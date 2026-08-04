# 结构拼图破碎 + 刷怪笼不刷怪 — 修复计划

> **分支：** `1.21.1`（NeoForge 21.1）  
> **对照源：** `upstream/`（1.12 `WorldGenerator` + NBT）  
> **状态：** Phase 1–3 已在 worktree 实施（未合并）  
> **相关文档：** [`boss-spawner.md`](boss-spawner.md) · [`asset-remap.md`](asset-remap.md) · [`diff.md`](diff.md) · [`port-plan-1.21.1.md`](port-plan-1.21.1.md)

---

## 0. 现象与目标

| 玩家可见现象 | 根因类别 |
|--------------|----------|
| 地牢/神庙/塔「拼图」碎成条带、房间只剩一半、走廊对不上 | 自定义 `StructurePiece` 跨 chunk 放置被错误门控 |
| Runestone 塔不像上游、顶层单薄/错位、无地形柱 | jigsaw 半成品替代了 20 片象限拼装 |
| 进地牢刷怪笼不刷 / 只部分刷 | 结构 NBT 仍是 1.12～1.16 刷怪笼格式；部分 Boss spawner 未 remap |
| 宝箱空、structure_block 残留 | loot 扫了 `CHEST` 而 NBT 标记在 `structure_block` |

**完成定义（DoD）：**

1. `/locate` 三地牢 + Umbra + Ruins 后，**跨 chunk 完整**（16×16 房间四角均有实心块，无 8 格宽「半截」）。
2. Runestone 使用 `upstream_ref` 20 分片逻辑，楼梯通顶、Boss 可触发、terrain 不悬空。
3. 所有结构内 `minecraft:spawner` 为 **1.18+ / 1.21** NBT 形；靠近后能刷出对应实体。
4. 所有 `boss_spawner` 带合法 `boss` 字符串；玩家靠近后生成并销毁方块。
5. structure_block 宝箱标记能写入对应 loot table。
6. GameTest / 校验脚本覆盖放置与 spawner 形，防止回退。

---

## 1. 背景：上游 vs 移植

### 1.1 上游（1.12）

- **无 jigsaw**。全部 `WorldGenerator` + `Template.addBlocksToWorld`。
- **按 chunk 判定**「我是不是这座建筑的某个格子/象限」，只在本 chunk 坐标贴片。
- Runestone：4 象限 ×（terrain 柱 + base + 交替 floor/floor2 + top），模板 **15×15**，硬编码 `+14` / `CLOCKWISE_180`。
- Darkstone：5×5×2 字符地图 + entrance/stairs/boss 六片。
- Moon Temple：固定偏移的 center/left/right/courtyard/room + terrain + island + spiral path。
- 刷怪笼：`minecraft:mob_spawner` + 旧 `SpawnData.id` / `SpawnPotentials[].Entity`。
- Boss：三方块 `bossspawnerkeeper|spider|moonqueen`，BE `containedboss`。

### 1.2 当前移植（1.21.1）

| 结构 | 类型 | NBT | 主要问题 |
|------|------|-----|----------|
| Runestone | `minecraft:jigsaw` → `bottom`/`top` | 合成 32×48×32 + 薄顶；`upstream_ref` 未参与生成 | jigsaw 非法 pool 字段；无中间层/terrain；与上游布局不等价 |
| Darkstone | `theaurorian:darkstone_dungeon` | 14 片已 remap | **chunk 门控**；每片独立 surfaceY；loot 扫 CHEST |
| Moon Temple | `theaurorian:moon_temple` | 11 片已 remap | **chunk 门控**（螺旋路尤甚） |
| Ruins / Graveyard / Umbra | `theaurorian:single_template` | 有 | **chunk 门控**；Umbra 缺 terrain；loot 扫 CHEST |
| Ruined house | jigsaw 单件 | 小 | 无 jigsaw 块，可改 single_template |

自定义放置共性错误（Darkstone / Moon / SingleTemplate）：

```java
// 错误：只在模板原点所在 chunk 放置一次，且不设 BoundingBox
if (!new ChunkPos(s.pos).equals(chunkPos)) continue;
s.template.placeInWorld(level, s.pos, s.pos, settings, random, 2);
```

原点在 `chunkMin+8`、模板宽 16 时，方块落在 2 个 chunk；邻 chunk 生成时被 skip → **每个房间只剩约一半**。  
这与「拼图破碎」观感一致，且与是否 jigsaw **无关**。

---

## 2. 刷怪笼问题清单（已扫描全量 structure NBT）

### 2.1 格式统计（`data/theaurorian/structure/**/*.nbt`，不含 gametest）

| 计数 | 形态 | 1.21 是否可用 |
|------|------|----------------|
| 22 | `SpawnData.entity.id` + **空** `SpawnPotentials` + BE `minecraft:mob_spawner`（主要在 `runestone/bottom.nbt`） | 半可用：SpawnData 新形，但 BE id 旧、potentials 空，行为不稳 |
| 13 | `SpawnData.id` + `SpawnPotentials[{Entity,Weight}]` + BE `minecraft:spawner`（darkstone / moon / graveyard / `upstream_ref`） | **否** — 1.18+ 读的是 `SpawnData.entity` 与 `data.entity`/`weight` |
| 3 | `boss_spawner` + `boss: theaurorian:*`（bottom / darkstone boss / moon center） | 是 |
| 1 | `boss_spawner` + **`containedboss: runestonedungeonkeeper`**（`upstream_ref/.../top_brv2.nbt`） | **否** — remap 字典未覆盖该字符串 |

### 2.2 1.21 期望的 vanilla spawner 形

```nbt
{
  id: "minecraft:spawner",
  SpawnData: {
    entity: { id: "theaurorian:undead_knight" }
    // 可选: custom_spawn_rules, equipment 等
  },
  SpawnPotentials: [
    {
      weight: 1,
      data: {
        entity: { id: "theaurorian:undead_knight" }
      }
    }
  ],
  MinSpawnDelay: 200,
  MaxSpawnDelay: 800,
  SpawnCount: 4,
  MaxNearbyEntities: 6,
  RequiredPlayerRange: 16,
  SpawnRange: 4,
  Delay: 0
}
```

**禁止残留：**

- `SpawnData: { id: "..." }`（无 `entity` 包装）
- `SpawnPotentials: [{ Entity: {...}, Weight: N }]`（大写 Entity / Weight）
- BE `id: "minecraft:mob_spawner"`
- 空 `SpawnPotentials` 且依赖隐式（应用显式 potentials 与 SpawnData 一致）

### 2.3 Boss spawner

| 位置 | 现状 | 处理 |
|------|------|------|
| `runestone/bottom.nbt` | `boss=theaurorian:dungeon_keeper` | 保留 |
| `darkstone_bossroom_back.nbt` | `boss=theaurorian:dungeon_spider` | 保留 |
| `moontemplev2_center.nbt` | `boss=theaurorian:moon_queen` | 保留 |
| `upstream_ref/runestonetower_top_brv2.nbt` | `containedboss=runestonedungeonkeeper` | **必须** → `boss=theaurorian:dungeon_keeper` |

`scripts/remap_structure_nbt.py` 的 `BOSS_CONTAINED` 仅有：

```text
spider / moonqueen / keeper
```

**缺少** 1.12 全名：`runestonedungeonkeeper`、`moonqueen` 全路径别名等。  
dry-run 对该 top 文件报 `no changes`，说明 **upstream_ref 的 Boss 从未被脚本修掉**。

`BossSpawnerBlockEntity` 只读 NBT 键 **`boss`**；`containedboss` 加载后 `bossEntity == null` → 永远不刷。

### 2.4 与「结构破碎」的耦合

即便 NBT 修好，若 piece 只写半个 chunk，落在另一半的 spawner **根本不会进世界**。  
因此：**先修放置（P0），再修 NBT（P0/P1），最后 Runestone 改走 upstream_ref（P1）**。

### 2.5 实体 ID（注册侧，已正确）

| 结构用途 | 注册 ID |
|----------|---------|
| Runestone 杂兵 | `theaurorian:undead_knight` / `theaurorian:dungeon_slime` |
| Darkstone 杂兵 | `theaurorian:spiderling` |
| Moon 杂兵 | `theaurorian:dungeon_slime` |
| Graveyard | `theaurorian:spirit` |
| Boss | `dungeon_keeper` / `dungeon_spider` / `moon_queen` |

实体 ID remap 本身大体正确；问题在 **spawner 数据结构** 与 **boss 键名**，不是 ID 字符串写错成未注册名（`runestonedungeonkeeper` 除外）。

---

## 3. 修复方案分轨

### Track A — 结构完整放置（消「碎裂」）

#### A1. 统一 Piece 放置 API（P0，必做）

**文件：**

- `World/Structure/DarkstoneDungeonStructure.java`
- `World/Structure/MoonTempleStructure.java`
- `World/Structure/SingleTemplateStructure.java`

**改法（二选一，推荐 A1-b）：**

**A1-a 最小改动：** 在现有多 Slot 单 Piece 上：

```java
settings.setBoundingBox(chunkBB);  // 原版 TemplateStructurePiece 做法
// 删除: !new ChunkPos(origin).equals(chunkPos)
// 改为: template 旋转后 AABB 与 chunkBB 不相交则 skip
template.placeInWorld(level, pos, pivot, settings, random, 2);
```

注意：`placeInWorld` 在邻 chunk 未加载时对越界写入的行为需实测；设 `BoundingBox` 后原版会裁到当前 chunk 盒，结构生成管线会对每个相交 chunk 再调 `postProcess`。

**A1-b 更稳（推荐）：** 每个 Slot → 独立 `StructurePiece`（可继承 `TemplateStructurePiece` 或薄封装）：

- `findGenerationPoint` 里 `builder.addPiece` 多次。
- 原版按 piece BB 分 chunk 调度，loot/spawner BE 自然完整。
- Darkstone/Moon 的 Slot 列表已具备，迁移成本可控。

**SingleTemplate：** 去掉 origin-chunk 门控；Umbra 额外加 terrain piece（见 A4）。

#### A2. 整座建筑统一锚点 Y（P0）

- Darkstone：入口采样一次 `y0`，所有房间 `y = y0 - 14*(floor+1)`，**禁止**每 Slot `getBaseHeight`。
- Runestone（自定义后）：每象限可保留上游「按象限采样」或改为中心一次 Y + terrain 填缝；优先复刻上游再视悬空微调。
- Moon：保持绝对高度 `TEMPLE_HEIGHT=200`（已与上游一致）。

#### A3. Runestone 放弃残缺 jigsaw（P1）

1. 新增 `RunestoneDungeonStructure` + Piece(s)，逻辑移植自  
   `upstream/.../RunestoneTowerWorldGenerator.java`。
2. 模板根路径：`theaurorian:runestone/upstream_ref/<name>`（或生成时把分片迁到 `runestone/` 扁平目录并改引用）。
3. 覆盖：terrain 柱（y→50）、base、floor/floor2 交替、`FLOOR_COUNT` 偶数约束、top Boss 象限、chest structure_block。
4. `worldgen/structure/runestone_dungeon.json`：

   ```json
   { "type": "theaurorian:runestone_dungeon", "biomes": [...], "step": "surface_structures", "terrain_adaptation": "beard_thin" }
   ```

5. `StructureRegistry` 注册 type + piece。
6. 废弃：
   - `worldgen/template_pool/runestone_*.json`（或留空文件删除）
   - 合成件 `structure/runestone/bottom.nbt` / `top.nbt`（可移到 `dev_ref/` 或删除）
7. pool 里非法字段 `"rotation": "clockwise_90"` 随 jigsaw 删除一并消失。

**不推荐**短期内用 jigsaw 重做 15×15 象限树（pivot 历史包袱 + terrain 柱难表达）。若未来要 datapack 化，单独立项。

#### A4. Umbra terrain + Ruined house（P2）

- Umbra：`umbratowerterrain` 垫层/柱 + `umbratower` 主体（自定义双 piece 或 single 内先 terrain）。
- `ruined_house`：改 `single_template`，去掉无 jigsaw 块的 jigsaw 定义。

#### A5. 宝箱 structure_block（P0，与放置同批）

统一流程（已有于 Moon Temple）：

1. `filterBlocks(..., STRUCTURE_BLOCK)`
2. 读 `metadata`：`chest` / `chest_low` / `chest_med` …
3. 清除 structure_block；对 `below()` 或同格的 `ChestBlockEntity` 设 loot。
4. Darkstone / SingleTemplate / Runestone 新 Structure **全部**走此路径；不要 `filterBlocks(CHEST)` 作为唯一手段（上游标记在 structure_block）。

Loot 表映射：

| 结构 | metadata | loot |
|------|----------|------|
| Darkstone floor0 | chest | `chests/darkstone/low` |
| Darkstone floor1 | chest | `chests/darkstone/med` |
| Darkstone boss | chest | `chests/darkstone/high` |
| Moon | chest_low / chest_med | `chests/moontemple/low|med` |
| Runestone | chest + 楼层 | `chests/runestone/common|uncommon|rare`（对齐旧 low/med/high） |
| Ruins/Graveyard/Umbra | chest | `chests/ruins/common` |

---

### Track B — 刷怪笼 NBT + Boss spawner（不刷怪）

#### B1. 扩展 `remap_structure_nbt.py`（P0）

**B1.1 Boss 字典补全**

```python
BOSS_CONTAINED = {
    "spider": "theaurorian:dungeon_spider",
    "moonqueen": "theaurorian:moon_queen",
    "keeper": "theaurorian:dungeon_keeper",
    # 1.12 全名 / 遗留
    "runestonedungeonkeeper": "theaurorian:dungeon_keeper",
    "moonqueenboss": "theaurorian:moon_queen",  # 若扫描到再加
    "spiderboss": "theaurorian:dungeon_spider",
}
```

对已是 `boss` 但值为短名/旧 id 的也做一次 `remap_str`。

**B1.2 Vanilla spawner 结构升级（新变换函数）**

对每个 block NBT 且 palette/Name 或 `id` 表明为 spawner：

1. `id: minecraft:mob_spawner` → `minecraft:spawner`
2. 若 `SpawnData` 仅有顶层 `id`（无 `entity`）：
   - 改为 `SpawnData: { entity: { id: <id> } }`（保留其他 SpawnData 字段若有）
3. 若 `SpawnPotentials` 元素含 `Entity`/`Weight`：
   - 改为 `{ weight: <Weight>, data: { entity: <Entity> } }`
4. 若 `SpawnPotentials` 为空但 `SpawnData.entity` 有 id：
   - 补一条 weight=1 的 potential，与 SpawnData 一致
5. 若实体 id 仍落在 `BLOCK_REMAP`/实体 remap 表，继续字符串 remap
6. `Delay` 可归一为 `0`（可选，避免结构自带长 delay 被误认为「不刷」）

**B1.3 跑全量**

```bash
bash scripts/sync_structures.sh --remap-only
# 或
python3 scripts/remap_structure_nbt.py src/main/resources/data/theaurorian/structure
python3 scripts/remap_structure_nbt.py --dry-run ...  # 期望二次 dry-run files_touched=0
```

**验收扫描（脚本或 validate 扩展）：**

- 0 个 `SpawnData` 直接含实体 `id` 而无 `entity`
- 0 个 `SpawnPotentials[].Entity` / `.Weight`
- 0 个 `minecraft:mob_spawner`
- 0 个 `containedboss`
- 每个 `boss_spawner` 的 `boss` 为 `namespace:path` 且实体已注册

#### B2. 放置时 BE 不丢（P0 验证项）

`StructureTemplate.placeInWorld` 默认应写入 block entity NBT。验证：

1. 生成后 `/data get block <spawner>` 可见 `SpawnData.entity.id` 或 `boss`。
2. 若自定义 processor / `BlockIgnoreProcessor` 误伤 spawner，排除之。
3. `IgnoreBlockStructureProcessor` 只跳过 `aurorian_stone`，不应跳过 spawner。

#### B3. BossSpawner 加载兼容（P1，防御性）

`BossSpawnerBlockEntity.loadAdditional`：

```java
// 读 boss；若无则尝试 containedboss + 短名/全名映射表
// BuiltInRegistries.ENTITY_TYPE.get(id) 若为默认 pig/null，打 log 并保持 null
```

避免以后漏 remap 的 NBT 静默失效。可选：`getOptional` + 日志。

#### B4. 与结构破碎修复的顺序

```text
B1 脚本修 NBT（可先做，不依赖 Java）
A1 放置修复（否则 spawner 仍可能落不全）
A3 Runestone 改 upstream_ref 后，再 B1 扫一遍 upstream_ref
进游戏验证刷怪
```

---

## 4. 实施阶段与任务拆分

### Phase 0 — 诊断固化（0.5d，可与文档同步完成）

- [x] 对比 upstream 生成器 vs 移植 Structure/jigsaw
- [x] 全量 NBT 扫描 jigsaw / structure_block / spawner 形态
- [x] 本修复计划文档
- [ ] 在 `docs/diff.md` / `asset-remap.md` 加「已知缺陷」链接指向本文（实施时勾）

### Phase 1 — P0 放置 + spawner NBT（优先可玩）

| # | 任务 | 文件/范围 | 估计 |
|---|------|-----------|------|
| 1.1 | A1 修复 Darkstone/Moon/SingleTemplate 放置 | 3× Structure Java | M |
| 1.2 | A2 Darkstone 统一 y0 | DarkstoneDungeonStructure | S |
| 1.3 | A5 宝箱改 structure_block 路径 | Darkstone + SingleTemplate | S |
| 1.4 | B1.1 + B1.2 remap 脚本 | `scripts/remap_structure_nbt.py` | M |
| 1.5 | B1.3 全量 remap + dry-run 清零 | `structure/**` | S |
| 1.6 | validate 增加 spawner/boss 形检查 | `scripts/validate_resources.py` | S |
| 1.7 | 新世界手测 darkstone/moon/umbra/ruins 完整度 + 刷怪 | 客户端 | M |

**Phase 1 出口：** 非 Runestone 结构不再半截；darkstone/moon/graveyard 刷怪笼可刷；三 Boss spawner（含将用的 upstream top）键正确。

### Phase 2 — P1 Runestone 还原

| # | 任务 | 文件/范围 | 估计 |
|---|------|-----------|------|
| 2.1 | `RunestoneDungeonStructure` 移植上游坐标/旋转/楼层 | 新 Java + Registry | L |
| 2.2 | datapack `runestone_dungeon.json` 改 type | worldgen/structure | S |
| 2.3 | 删除/停用 jigsaw pool 与 bottom/top 依赖 | template_pool + 可选 NBT | S |
| 2.4 | chest + spawner 随 piece 放置验证 | 手测 + remap 已覆盖 upstream_ref | M |
| 2.5 | Boss：top 象限 `boss_spawner` + 人数缩放 | 已有 BE 逻辑 | S |
| 2.6 | Config `enable_runestone` / 楼层数对接 | CommonConfig | S |

**Phase 2 出口：** `/locate theaurorian:runestone_dungeon` 得完整四象限塔；Keeper 可触发；杂兵笼可刷。

### Phase 3 — P2 打磨与回归

| # | 任务 | 说明 |
|---|------|------|
| 3.1 | Umbra terrain | A4 |
| 3.2 | ruined_house → single_template | A4 |
| 3.3 | B3 BossSpawner 兼容加载 | 防御 |
| 3.4 | GameTest：3×3 chunk 放置后四角实心；spawner NBT 断言（若框架允许读 BE） | 防回归 |
| 3.5 | Demo `/ta demo` 增加「结构切片」可选 | 可选 |
| 3.6 | 更新 `diff.md` / `asset-remap.md` / `boss-spawner.md` / README 过时「jigsaw bottom/top」表述 | 文档 |
| 3.7 | `port-plan-1.21.1.md` 将 N11 从 closed 改为 reopened→再 closed，或新增 Nxx | 追踪 |

---

## 5. 详细设计笔记

### 5.1 Darkstone 放置伪代码（A1-b）

```text
findGenerationPoint:
  y0 = surface(center) // 单次
  slots = entrance, stairs@y0-14, map cells @ y0-14*(f+1), boss @ y0-28
  for slot in slots:
    builder.addPiece(TemplatePiece(slot.template, slot.pos, slot.rot, slot.lootMeta))

TemplatePiece.postProcess: // 或 super TemplateStructurePiece
  place with chunk BB
  if structure_block metadata matches loot → fill chest
```

旋转 +15 偏移表已与上游一致，**不要改**字符→模板映射，只改放置与高度锚点。

### 5.2 Runestone 象限偏移（自上游注释）

```text
TL = chunk offset (1, -1)   TR = (1, 0)
BL = (0, -1)               BR = (0, 0)
floor 高 6；floor2 高 12 且 180° + (14,14) 枢轴补偿
FLOOR_COUNT 强制偶数
terrain: for y in surface..50: place terrain_* 
```

配置：`CommonConfig` 中 runestone 楼层与 enable 开关（若已有则接线，若无则从上游 Config 名迁移）。

### 5.3 Spawner remap 伪代码

```text
function upgrade_spawner(nbt):
  if nbt.id == "minecraft:mob_spawner": nbt.id = "minecraft:spawner"
  sd = nbt.SpawnData
  if sd is compound and "entity" not in sd and "id" in sd:
    sd = { entity: { id: sd.id, ...other non-id keys? } }
  for each pot in SpawnPotentials:
    if pot has Entity:
      pot = { weight: pot.Weight or 1, data: { entity: pot.Entity } }
  if SpawnPotentials empty and SpawnData.entity.id:
    SpawnPotentials = [{ weight: 1, data: { entity: copy(SpawnData.entity) } }]
  remap all entity id strings through ENTITY_REMAP
```

### 5.4 风险与缓解

| 风险 | 缓解 |
|------|------|
| `placeInWorld` + BoundingBox 仍丢 BE | 对照原版 `TemplateStructurePiece`；必要时每 chunk 手动 `setBlockEntity` |
| 15×15 模板旋转后缝 1 格缝 | 严格复刻上游 +14 偏移；世界坐标打印 debug |
| remap 写坏 NBT | dry-run + git diff；先备份；validate gzip/parse |
| 旧世界已生成碎结构 | **必须新区块/新世界**；changelog 注明 |
| jigsaw Runestone 存档定位 | 改 type 后旧定位失效，可接受 |
| 刷怪过密 | 保持上游 Delay/Count；平衡另项 |

### 5.5 明确不在本计划范围

- 生物群系自然刷新权重（见 `entity-parity-fix-plan.md` P3）
- 新地牢布局设计（以还原上游可通关为先）
- TCon / 维度噪声大改
- 把 Darkstone/Moon 也改成 jigsaw（无必要）

---

## 6. 验证矩阵

### 6.1 自动化

```bash
./gradlew test                 # validate_resources 含新 spawner 规则
./gradlew runGameTestServer    # 现有 boss_spawner×3 + 新增放置测试（若加）
python3 scripts/remap_structure_nbt.py --dry-run src/main/resources/data/theaurorian/structure
# 期望：无 containedboss / 无 legacy SpawnData / files_touched=0
```

### 6.2 手测（新世界）

| 步骤 | 期望 |
|------|------|
| `/locate structure theaurorian:darkstone_dungeon` 传送 | 5×5 走廊连通；无半截 16 房间；入口→楼梯→Boss |
| 靠近 spiderling 笼 | 延迟后刷出 `spiderling` |
| Boss 房 spawner | 靠近刷 `dungeon_spider`，方块消失 |
| `/locate ... moon_temple` | 主体完整；螺旋路连续到地表附近 |
| Moon 笼 / Queen | slime 可刷；Queen 可触发 |
| `/locate ... runestone_dungeon`（Phase 2 后） | 四象限对齐；terrain 贴地；Keeper 触发 |
| undead_knight / dungeon_slime 笼 | 可刷 |
| Umbra / ruins / graveyard | 完整 footprint；graveyard spirit 笼可刷；宝箱有 loot |
| 双人靠近 Boss | 属性缩放（已有逻辑，回归） |

### 6.3 调试命令

```mcfunction
/data get block <x y z>
# spawner: 应见 SpawnData.entity.id
# boss_spawner: 应见 boss:"theaurorian:..."
```

---

## 7. 文件变更预览（实施时）

```text
src/main/java/shiroroku/theaurorian/World/Structure/
  DarkstoneDungeonStructure.java     # A1 A2 A5
  MoonTempleStructure.java           # A1
  SingleTemplateStructure.java       # A1 A5
  RunestoneDungeonStructure.java     # 新建 A3
src/main/java/.../Registry/StructureRegistry.java
src/main/resources/data/theaurorian/worldgen/structure/runestone_dungeon.json
src/main/resources/data/theaurorian/worldgen/template_pool/runestone_*.json  # 删或废
src/main/resources/data/theaurorian/structure/**/*.nbt  # B1 remap
scripts/remap_structure_nbt.py       # B1
scripts/validate_resources.py        # spawner/boss 断言
docs/structure-spawn-fix-plan.md    # 本文
docs/boss-spawner.md                 # 补 containedboss 全名与 1.21 spawner 形
docs/asset-remap.md                  # Runestone 不再写 jigsaw bottom/top 为正式方案
docs/diff.md                         # 缺陷→已修
```

---

## 8. 建议实施顺序（一句话）

1. **脚本修 spawner/boss NBT**（立刻受益、低风险）  
2. **Java 放置 A1 + 宝箱 A5 + Darkstone y0**（碎裂主因）  
3. **Runestone 自定义 Structure 吃 upstream_ref**（拼图/塔还原）  
4. **Umbra terrain、校验、GameTest、文档**  

---

## 9. 决策记录

| 决策 | 选择 | 理由 |
|------|------|------|
| Runestone 是否继续 jigsaw | **否**（短期） | 上游 15×15 象限+terrain 不适合半套 jigsaw；合成 bottom/top 已证明不足 |
| 多模板结构 Piece 模型 | **每 Slot 一 Piece（推荐）** 或 setBoundingBox | 对齐原版，避免再犯 origin-chunk 门控 |
| Spawner 修复位置 | **NBT 预 remap + validate** | 运行时 processor 可做但难审计；资源侧一次修好更清晰 |
| Boss 键 | 保持 **`boss` 字符串** | 已有文档与 GameTest；补 remap 与加载兼容即可 |

---

## 10. 进度勾选（实施时更新）

- [x] Phase 1.1 A1 放置（BoundingBox clip，去掉 origin-chunk 门控）
- [x] Phase 1.2 A2 y0（Darkstone 统一锚点）
- [x] Phase 1.3 A5 宝箱（structure_block metadata + CHEST fallback）
- [x] Phase 1.4–1.5 spawner remap（14 文件；dry-run files_touched=0）
- [x] Phase 1.6 validate（spawner/boss/runestone type 断言）
- [ ] Phase 1.7 手测非 Runestone（需新世界客户端）
- [x] Phase 2 Runestone Structure（upstream_ref 象限 + 弃用 jigsaw pool）
- [x] Phase 3 部分：Umbra terrain、ruined_house→single_template、BossSpawner containedboss 兼容、validate

**当前：** worktree `worktree-structure-spawn-fix` @ 1.21.1；`compileJava` + `validate_resources` 通过；**未合并**。手测待做。
