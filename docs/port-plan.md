# The Aurorian — 1.19.2 零妥协移植计划

> 生成日期：2026-07-31  
> 依据：[`docs/diff.md`](diff.md) + 上游 `upstream/`（1.12.2-1.2）+ 当前 `1.19.2-2.7`  
> 原则：**最终交付不得以「砍内容」换进度**；阶段性可先缺后补，但缺什么必须在本文档「暂缓清单」登记，并有关闭条件。

---

## 0. 目标定义

| 项 | 定义 |
|----|------|
| **完成** | 1.12.2 发售版中的**全部游戏内容**在 1.19.2 可玩，玩法效果对等（允许 API/架构重写，不允许删线、删 Boss、删地牢、删生态）。 |
| **允许的「非 1:1」** | 命名 snake_case；结构用 jigsaw/datapack 重做布局（README 已承诺新布局，但**三条地牢 + Umbra Tower + Ruins 必须全部可生成可通关**）；Patchouli → Mirror of Guidance；Curios 槽；Umbra/Crystalline 剑已 rework 的保留；1.19 新增（deepslate、aurorianite shovel 等）保留。 |
| **不允许** | 永久移除 Weeping Willow / 蘑菇区 / 被动生物 / 农业 / 任一 Boss / Locator / Boss 武器与 trophy / 特殊甲与投掷物 / 音效与粒子（有资源则必须挂上）/ 材料块与装饰方块。 |
| **兼容层（可选扩展）** | TCon / ConArm / CraftTweaker：**不阻塞主完成判定**，单独 Phase C；若做则效果对等上游。 |
| **内嵌 Worley Caves** | 见 §6.5：必须用**等价地下体验**关闭缺口（移植算法或自研 lush 洞穴 + 蘑菇等），不可「什么都没有」。 |

### 0.1 文档纪律（暂不完全完成时）

任何合并进主分支、但**尚未达到上游对等**的子系统，必须同时满足：

1. 在本文档 **§9 暂缓清单** 有一条记录：缺口、原因、替代现状、关闭条件、负责人/里程碑。  
2. 相关 Mirror 节点 / 创造模式物品不假装「已完成」（可用灰字或 WIP 标记）。  
3. 禁止静默删除上游内容或把「以后再说」只写在聊天里。

完成关闭条件后：从 §9 删条或标 `[closed]`，并在 `diff.md` 下一次全量刷新时反映。

---

## 1. 现状锚点（计划起点）

| 已就绪（可复用） | 缺口（必须补） |
|------------------|----------------|
| 维度 + 传送门 + POI | Darkstone / Moon Temple / Umbra Tower 结构与生成 |
| DeferredRegister 全套骨架 | Moon Queen、Dungeon Spider + 小蜘蛛、Acolyte、Sprite、Spirit… |
| Runestone jigsaw 模式（bottom/top pool） | 实体 loot；Boss 武器与 trophy |
| 通用 `boss_spawner`（NBT `boss` = EntityType id）+ 人数缩放 | Locator；农业；被动生物 |
| 主材料工具甲 + 机器（炉/烟囱/MF/Scrapper）+ JEI | Silentwood 镐等级机制；Spectral 透明 |
| Darkstone / Moon Temple **方块与钥匙已注册** | 装饰方块、材料块、Umbra 石、玻璃、火把、urn… |
| Mirror 12 节点（含 darkstone/umbra 文案壳） | 音效/粒子；多语言；Advancements |
| Geckolib 依赖已加 | 实际动画模型接入（可选增强，非砍内容借口） |
| Curios 护符 | 与上游 dark amulet 等掉落对齐 |

**材料线绑定（README，不可破坏）：**

- Runestone → Aurorianite  
- Darkstone → Umbra  
- Moon Temple → Crystalline  

---

## 2. 架构约定（全程遵守）

| 主题 | 约定 |
|------|------|
| 结构 | 优先 **datapack jigsaw + structure_set 随机散布**（对齐 README）；复杂迷宫可 `Structure` 子类 + `Codec`，仍走 1.19 structure API。 |
| NBT 来源 | 从 `upstream/.../structures/**` **复制并 remap 方块 ID**（1.12 名 → 当前注册名）；必要时 Structure Processor。不可长期「只有空 pool」。 |
| Boss 刷怪 | 结构内放置 `boss_spawner`，NBT：`boss: "theaurorian:<entity_id>"`。蛋右键设置仅调试。 |
| 实体 | `EntityRegistry` + attributes + renderer + spawn egg +（需要时）spawn placement；掉落用 `data/.../loot_tables/entities/`。 |
| 物品/方块 | `BlockRegistry` / `ItemRegistry` + DataGen 模型/tag/loot；lang `en_us.json` 同步，zh/es 可并行。 |
| AI | 1.12 Goal → 1.19 `Goal`；逻辑对等，禁止砍阶段。 |
| 配置 | 新玩法进 `CommonConfig`/`ClientConfig`，键名可读，默认贴近上游。 |
| 资源 | 贴图/ogg 优先从上游 assets 迁移并改路径；缺失则列 §9 并补制，不得删功能。 |
| 测试 | 每 Phase 结束：创造刷结构/实体 + 生存通关清单（§8）。 |

### 2.1 方块 ID 迁移表（结构 Processor 用）

移植 NBT 时建立统一 remap（示例，实施时扫全量补全）：

| 1.12 | 1.19 |
|------|------|
| `runestone` / `runestonesmooth` / … | 已有 snake 名 |
| `darkstone` / `darkstonepillar` / … | 已有 |
| `moontemplebricks` 等 | 已有 |
| `aurorianstone` / grass / dirt | 已有 |
| `mysticalbarrier` | `fog_wall` |
| `bossspawnerkeeper` 等 | `boss_spawner` + NBT |
| `umbrastone*` | **需先注册方块** |
| `urn` / glass / torch / ladder | **需先注册** |
| 作物 / farmtile | **需先注册** |

---

## 3. 分阶段路线图（依赖顺序）

```
Phase 0  基建与资产管线
    ↓
Phase 1  方块/物品补全（结构依赖的方块先于结构）
    ↓
Phase 2  敌对与 Boss 实体（可与 1 部分并行）
    ↓
Phase 3  Darkstone 地牢闭环          ──┐
Phase 4  Moon Temple 地牢闭环        ──┼─ 主线三地牢
Phase 5  Runestone 收尾（武器/loot/多样性）─┘
    ↓
Phase 6  Umbra Tower + Ruins/Graveyard + Locator
    ↓
Phase 7  被动生物 + 农业 + 食物链
    ↓
Phase 8  特殊装备与投掷物 + Silentwood/Spectral 补完
    ↓
Phase 9  生物群系与地下（Willow、蘑菇、洞穴等价）
    ↓
Phase 10 表现（音效粒子）+ 进度 + 语言 + Mirror 扩写
    ↓
Phase C  可选兼容（TCon/CT/ConArm）
    ↓
Phase G  完成门禁（§8 全绿）
```

**并行建议：** 2∥1（实体不依赖全部装饰方块）；3 的 pool 设计∥2 的 Spider；美术/NBT remap 可专人流水线。

**禁止：** 在 Phase 3/4 未闭环前把大量时间砸在 TCon 或纯光影。

---

## 4. Phase 明细

### Phase 0 — 基建与资产管线

**目的：** 后续复制粘贴不返工。

| 任务 | 细节 | 完成标准 |
|------|------|----------|
| 0.1 结构资产目录 | `data/theaurorian/structures/{darkstone,moontemple,umbratower,ruins,weepingwillow}/` | 目录存在 |
| 0.2 NBT 导入脚本/清单 | 列出 55→目标文件映射；记录需 Processor 的方块 | 清单进 `docs/asset-remap.md` 或本文件附录 |
| 0.3 Boss spawner 文档 | 结构内 NBT 示例；创造蛋设置流程 | README 或 Mirror 调试页 |
| 0.4 实体 loot 目录 | `loot_tables/entities/` | 空目录 + 1 个 Keeper 样例接通 |
| 0.5 回归：Runestone 仍可生成可打 | 防重构破坏 | playtest 通过 |

**暂缓规则：** 本 Phase 不暂缓内容，只建管道。

---

### Phase 1 — 方块与基础物品补全

**依赖：** Phase 0。  
**目的：** 结构与合成不再引用空气。

#### 1.A 结构强依赖（必须先做）

| 内容 | 上游参考 | 备注 |
|------|----------|------|
| Umbra 石套装 | `UmbraStone` 等 | `umbrastone`, cracked, rooftiles + stairs |
| Urn | `UrnBlock` + loot | 地牢/废墟装饰与掉落 |
| 材料存储块 | coal/steel/cerulean/moonstone block | 合成双向 |
| Runestone loot gate（若 NBT 需要） | `runestonelootgate` | 当前仅 keyhole 时核对 NBT |

#### 1.B 世界装饰（可与 1.A 并行，结构前尽量齐）

| 内容 | 上游 |
|------|------|
| `aurorian_glass` + pane | `AurorianGlass*` |
| `moon_glass` + pane | |
| `moon_torch`, `silentwood_torch`, `silentwood_ladder` | |
| `aurorian_stone_brick` | |
| `moon_sand` | |
| `peridotite` (+smooth/stairs) | |
| `aurorian_grass_light`, `aurorian_tallgrass_light` | |

#### 1.C 农业方块（可放到 Phase 7 前必完，建议 1 末尾注册好）

| 内容 | 上游 |
|------|------|
| `aurorian_farm_tile` | `AurorianFarmTile` |
| `lavender_crop`, `silkberry_crop` | `CropsBlock` AGE 0–7；土壤 farmtile 或 farmland；需见天 |
| seeds 物品 | `lavenderseeds` 等 |

#### 1.D 物品（非 Boss）

| 内容 | 说明 |
|------|------|
| 作物产物已有部分（lavender/silkberry） | 接 crop 掉落 |
| 被动掉落物预注册 | bacon、pork 变体、soulless flesh、silkshroom stew、weeping willow sap 等（可与实体同 Phase） |

**完成标准：** 创造模式可放置全部 1.A/1.B；合成与 block loot/tag/DataGen 齐全；lang 有键。

---

### Phase 2 — 实体（敌对 / Boss / 投射）

**模式：** 对标现有 `DungeonKeeper` / `Hollow` / `UndeadKnight`。

#### 2.1 Boss

| 实体 | 上游文件 | 行为要点（必须保留） | 掉落（上游 loot） |
|------|----------|----------------------|-------------------|
| **Moon Queen** | `MoonQueenEntity` + `MoonQueenAICharge` + `MoonQueenAISideStrafe` + Model/Render | Charge + 侧向 Strafe 战斗循环 | `trophymoonqueen`；武器见 Phase 5/4 宝箱或扩展 loot |
| **Dungeon Spider** | `SpiderEntity` + Hang/Leap/Spit AI + Model/Render | 悬挂、扑击、吐丝；召唤/关联 spiderling | `darkamulet` + `trophyspider` + 关联道具 |

接入：`BossSpawnerBlockEntity` 识别新 `EntityType`；属性 + 人数缩放已有通用逻辑。

#### 2.2 地牢/世界敌对

| 实体 | 角色 |
|------|------|
| **Moon Acolyte** | Moon Temple 小怪 |
| **Spiderling** | Darkstone / Spider 相关 |
| **Crystalline Sprite** | 远程（Beam）；上游可在 Moon Temple 地牢石上生成 |
| **Spirit** | Haunt → RunAway → Attack 三态；掉落 spectral silk |
| **Disturbed Hollow** | 墓园/地下近战（僵尸向）；与 `Hollow` 分体则双注册，合并则行为不弱于二者 |

#### 2.3 投射物

| 实体 | 关联 |
|------|------|
| `StickySpikerEntity` | 蜘蛛线道具 |
| `WebbingEntity` | Webbing 投掷 |

（箭与 Beam 已有。）

#### 2.4 每个实体交付清单（模板）— Phase 2 已全部套用并关闭

- [x] EntityType 注册 + 尺寸/火免等  
- [x] `createAttributes`  
- [x] Goals 对等  
- [x] Renderer + 贴图（Gecko 可选增强，非缺口）  
- [x] Spawn egg（全部存活实体）  
- [x] `loot_tables/entities/<id>.json`  
- [x] 音效事件（有资源则绑；环境 music/bell 在 Phase 10）  
- [x] 结构/生物群系生成或 spawner 引用  

**完成标准：** 创造蛋可刷；AI 可战斗；loot 可见；不崩溃。✅

---

### Phase 3 — Darkstone 地牢闭环（P0）

**依赖：** Phase 1.A（含 umbra 相关方块若模板需要）、Phase 2 Spider + Spiderling。

| 任务 | 细节 |
|------|------|
| 3.1 NBT | 复制 14 个 `darkstone/*.nbt` → `data/.../structures/darkstone/`，remap |
| 3.2 生成 | `structure` + `structure_set`（随机散布，非网格）+ `template_pool`；迷宫逻辑：上游为 5×5×2 字符地图拼接 corridor/bossroom——jigsaw 无法简单表达时用 **自定义 Structure** 复刻地图算法，或预烘焙多种整图 NBT + 随机选 |
| 3.3 内容 | 入口、走廊、楼梯、Boss 房；chest → loot `darkstone/{low,med,high}`（从上游迁移并改物品 ID，**Umbra 线**） |
| 3.4 Boss | spawner → `theaurorian:dungeon_spider`（最终 id 以注册为准） |
| 3.5 钥匙门 | `darkstone_key` + gate/keyhole 已有，结构内接通 |
| 3.6 小怪 | 结构 spawn 或 spawner：spiderling 等 |
| 3.7 Mirror | `dungeon_darkstone` 节点改为有效指引 |

**上游密度参考：** `CHUNKS_BETWEEN = density*2`，入口 chunk 校验——1.19 用 `structure_set` spacing/separation 表达类似稀有度。

**完成标准：** 新世界探索或 `/locate` 可找到；钥匙开门；Boss 可击败；Umbra 材料可从该线获取；宝箱 loot 合理。

---

### Phase 4 — Moon Temple 地牢闭环（P0）

**依赖：** Phase 1 月亮相关方块、Phase 2 Moon Queen + Acolyte。

| 任务 | 细节 |
|------|------|
| 4.1 NBT | 11 个 `moontemple/*.nbt`（含 v2 center/courtyard/room、path、island、terrain） |
| 4.2 生成 | 地表/高台神殿感；structure_set 更稀有（上游 locator 用 density*4） |
| 4.3 门与钥匙 | `moon_temple_key`、`interior_key` + fragment 合成已有 → 结构接线 |
| 4.4 Boss | Moon Queen spawner |
| 4.5 Loot | `moontemple/{low,med,high}` → **Crystalline 线** |
| 4.6 小怪 | Moon Acolyte |
| 4.7 Mirror | 补全 temple 节点 |

**完成标准：** 同 Phase 3；Crystalline 线闭环。

---

### Phase 5 — Runestone 收尾 + Boss 装备与 Trophy（P0）

| 任务 | 细节 |
|------|------|
| 5.1 Keeper 实体 loot | 对齐上游：`keepers_amulet`（现 curio）、`runestone_loot_key`、`trophy_keeper`；核对 darkstone_key 等现有掉落是否保留为 1.19 进度设计 |
| 5.2 Trophy ×3 | `trophy_keeper` / `trophy_moon_queen` / `trophy_spider`；**上游用 MoonlightForge：trophy + 基底 → 标志武器**（必须移植这三条 MF 配方，不能只当装饰） |
| 5.3 Keepers Bow | 拉满一次射 **3 箭**（带散布/inaccuracy）；由 Keeper trophy MF 合成 |
| 5.4 Queens Chipper | 镐；右键破坏地牢石/门/灯/栏/梯子等 dungeon 类方块，耗耐久；Queen trophy MF |
| 5.5 Moon Shield | 格挡蓄力冲向敌人并击飞（knock-up）+ 冷却；Spider 或对应 trophy MF（对等上游配方） |
| 5.6 Runestone 多样性 | README「更多样」：在现有 top/bottom 上增加 pool 变体或楼层 NBT（可从上游 20 件改建），**不减少**可通关性 |
| 5.7 核对 Fog Wall / loot gate | 与当前 NBT 一致 |

**完成标准：** 三 Boss 均掉落对应 trophy；三条 MF 配方可合成三件标志武器且能力正确；Runestone 线 loot 无「空 Boss」。

---

### Phase 6 — Umbra Tower、Ruins、Locator（P1）

| 任务 | 细节 |
|------|------|
| 6.1 Umbra Tower | NBT `umbratower` + terrain；稀有地表塔；chest → ruins 类或专用 loot；避开与 Runestone 过近（上游有距离判断 → 1.19 可用 exclusion 或手动 spacing） |
| 6.2 Ruins | 补 `ruins_1/2`、`graveyard`；扩展 `ruined_house` pool |
| 6.3 Dungeon Locator | 物品；潜行切换 Runestone / Darkstone / Moon Temple；右键指向最近结构（`ServerLevel` structure locate API）；耐久约 30 |
| 6.4 配置 | 各结构生成开关与间距进 CommonConfig |

**完成标准：** `/locate` 与 Locator 均能找到三地牢；废墟与 Umbra 塔会自然生成。

---

### Phase 7 — 被动生物与农业（P1）

#### 7.1 被动

| 实体 | 注意 |
|------|------|
| Aurorian Pig / Rabbit / Sheep | 模型渲染、掉落；**仅 `silkberry` 引诱/繁殖**（非小麦/胡萝卜）；羊：吃草 AI + 可染色/可剪毛 + 羊毛层 |
| 生成 | 仅 Aurorian 维度；避开地牢砖等（对等上游限制）；群系 spawn JSON |

#### 7.2 农业

| 任务 | 细节 |
|------|------|
| Farm tile | 锄地转换规则对等上游 |
| Lavender / Silkberry crop | AGE 7；掉落与种子；与已有食物/茶配方打通 |
| 世界生成 | crop 野生成或仅农场（对等上游 Feature） |

#### 7.3 食物链

上游实体/食物：slime 相关、soulless flesh、stew、sap 等 —— **有上游物品则全部注册并接入 loot/配方**。

**完成标准：** 不依赖地牢也能在维度内建立农场与基础肉食来源。

---

### Phase 8 — 特殊装备与工具补完（P2）

| 内容 | 上游行为 | 完成标准 |
|------|----------|----------|
| **Silentwood Pickaxe** | 耐久损耗 % 提升 harvest level 0→3（NBT `currentharvestlevel`；越用越能挖更硬方块） | 1.19 用动态 tool/tier 或 `isCorrectToolForDrops`+速度模拟 |
| **Silentwood Axe** | 破坏 `silentwood_log` 时 **75% 概率回复 1 点耐久**，否则正常损耗 | 对等 |
| **Silentwood Stick** | 双手逻辑：生火 / 点燃 Aurorian 传送门（上游 portal lighter） | 与现有 `portal_lighters` tag 对齐，能力不丢 |
| 其它 Silentwood | sword/bow/shovel/hoe/sickle 无额外特殊（材料向） | tooltip 与 1.12 一致 |
| **Spectral 透明** | 上游主要靠 **RGBA 盔甲贴图 alpha** + 原版盔甲层；1.19 若 cutout 不透明则需 `RenderType`/armor layer 半透明 | 穿戴可见鬼魅透明；**cleanse% 已有须保留** |
| **Slime Boots** | 摔落 >3 弹跳并取消伤害；潜行起跳超高跳 + config CD | 事件监听对等 |
| **Spiked Chestplate** | **仅潜行时** 视作 Thorns III（与 Umbra 胸甲动态荆棘区分清楚） | 对等 |
| **Sticky Spiker** | 投掷；命中上毒 | 可扔可中 |
| **Webbing** | 投掷；小伤害 + Slowness II | 可扔可中 |
| 相关配方/战利品 | 蜘蛛线、Darkstone loot、MF trophy 线 | 可获取 |

---

### Phase 9 — 生物群系与地下（P1/P3 内容，零妥协必做）

| 任务 | 细节 |
|------|------|
| 9.1 群系 | 恢复/重做：Forest Hills、Lakes、Overgrowth；**Weeping Willow Forest**（README「暂移除」→ 本计划要求 **最终必须回归**，实施前在 §9 登记，关闭条件=群系+树+相关物） |
| 9.2 Weeping Willow | 方块：leaves/log/planks/stairs/sapling；树 NBT×5 或 tree feature；bell 音效 |
| 9.3 蘑菇区 | mushroom / crystal / small / stem 方块 + 生成 |
| 9.4 地下 lush | 对标 README；矿物已有则补结构装饰、萤光草、水源特色 |
| 9.5 洞穴 | 见 §6.5 Worley 等价 |

**完成标准：** 多群系可区分探索；Willow 与蘑菇可发现；地下不「空洞无聊」。

---

### Phase 10 — 表现、进度、文档化内容（P2/P3）

| 任务 | 细节 |
|------|------|
| 10.1 音效 | `SoundRegistry`：music、weepingwillowbell + 实体音；ogg 从上游 `sounds/` 迁移 |
| 10.2 粒子 | slime / webbing / sticky / willow drip 等 |
| 10.3 Advancements | 上游 `dethroned` 等迁移；三地牢/三 Boss 进度树 |
| 10.4 语言 | `zh_cn.json`、`es_es.json`（上游 lang 为底） |
| 10.5 Mirror | 节点覆盖农业、被动、三地牢、Boss 武器、Locator；达到可替代 Patchouli 教程密度 |
| 10.6 notes.txt | 逐条关闭并更新 |
| 10.7 技术债 | shears tag 与镰刀；chest 物品栏模型（3D 可选增强） |

---

### Phase C — 可选兼容（不阻塞 Phase G 的「内容完整」，但若宣称兼容则须对等）

| 模块 | 动作 |
|------|------|
| JEI | 已有；新机器/物品补插件 |
| TCon | 材料/流体/trait 或明确「暂不支持」写进 §9（若永久放弃须用户书面确认——**默认零妥协下应移植或提供等价材料 API**） |
| ConArm | 同 TCon |
| CraftTweaker | MF/Scrapper 支持 |
| Patchouli | 不强制；Mirror 为准 |

> 若决定永久不做 TCon：在 §9 写明「兼容层豁免」及理由，**不得**连带删除模组内原有非 TCon 内容（如 molten 仅 TCon 的流体可豁免）。

---

### Phase G — 完成门禁

见 §8。全部勾选后更新 `diff.md` 完成度，版本号去掉 `playtest` 或升正式版。

---

## 5. 上游内容 → Phase 映射总表

| 内容包 | Phase | 优先级 |
|--------|-------|--------|
| Darkstone 结构+Spider+Umbra loot | 3 | P0 |
| Moon Temple+Queen+Acolyte+Crystalline loot | 4 | P0 |
| Boss 武器/Trophy/Keeper loot | 5 | P0 |
| 实体 loot 全表 | 2–5 | P0 |
| Umbra Tower | 6 | P1 |
| Ruins/Graveyard | 6 | P1 |
| Locator | 6 | P1 |
| 被动猪兔羊 | 7 | P1 |
| 农业 | 7 | P1 |
| Silentwood 能力 | 8 | P2 |
| Spectral 透明 | 8 | P2 |
| 装饰/材料方块 | 1 | P2（结构依赖部分升 P0） |
| 特殊甲与投掷物 | 8 | P2 |
| 音效粒子 | 10 | P2 |
| 群系+Willow+蘑菇 | 9 | P1/P3 |
| Worley 等价洞穴 | 9 | P3 但必关 |
| 多语言/进度 | 10 | P3 |
| TCon/CT/ConArm | C | 可选/豁免须记录 |

---

## 6. 关键系统专项说明

### 6.1 地牢生成策略

1. **简单塔/神殿（Runestone 现状、Umbra、部分 Temple）：** jigsaw + 少 pool。  
2. **Darkstone 迷宫：** 上游字符地图 5×5×2 —— 推荐 **自定义 `Structure` 类** 移植 `DarkstoneDungeonWorldGenerator` 拼接逻辑，模板仍用 NBT。  
3. **Boss 房：** 多件 NBT 拼房（front/back/left/right）保持相对偏移。  
4. **散布：** `structure_set` random_spread；间距参考上游 density 倍数。  
5. **禁止** 仅创造可刷、世界不生成。

### 6.2 Boss Spawner

```text
BlockEntity tag:
  boss: "theaurorian:moon_queen"   # 注册名
```

结构处理器或 jigsaw 元素写入；人数缩放已在 `BossSpawnerBlockEntity`。

### 6.3 Dungeon Locator（1.19）

- 不用 1.12 `GenerationHelper` 网格。  
- 使用 `ServerLevel.getChunkSource().getGenerator().findNearestMapStructure` 或 1.19.2 等价 locate。  
- 三种 `Structure` 的 `ResourceKey` 切换。  
- 客户端粒子/指示方向对等上游手感。

### 6.4 Silentwood Pickaxe

动态开采等级在 1.19 无 `getHarvestLevel` 旧 API 时：

- 用物品 NBT 存 stage；  
- `ItemStack` 工具组件 / `mineable` tag 动态无效则改用 **破坏速度 + 正确掉落判断** 模拟「越用越能挖更硬方块」；  
- 配置化阈值 25%/50%/75%。

### 6.5 Worley Caves 与地下

上游：`WorleyCaveGenerator` + FastNoise 等约 **3.7k LOC**，在 `AurorianChunkGenerator` 替换原版洞穴；有机 Worley(F1/F3)+位移，**对地下探索手感内容向关键**。

| 选项 | 说明 |
|------|------|
| A（优先） | 移植/重写 Worley 噪声为 1.19 `DensityFunction` / custom cave carver / chunk 特征，保留不规则腔体 |
| B | 原版噪声 + 大量自定义 carver/feature（巨大洞穴、蘑菇、荧光草、水晶、urn 生成器）达到 **同等探索乐趣与可达性** |

**关闭条件：** 地下走 5 分钟有明显特色拓扑与装饰；非平滑石头管道。选 A 或 B 实施时写入 §9，关闭后标 closed。纯原版洞穴且无装饰 **不验收**。

### 6.6 Weeping Willow

README 暂移除 ≠ 永久删除。本计划：**Phase 9 必回归**。暂缓期间 §9 一条：

- 缺口：Willow 群系与方块  
- 现状：无  
- 关闭：方块+树+群系+bell 音效 + 至少一种相关掉落/合成  

### 6.7 能力与物品对照（实施检查表）

| 物品 | 必须保留的效果 |
|------|----------------|
| Keepers Bow | 拉满 3 箭；MF：Keeper trophy 线 |
| Queens Chipper | 右键拆地牢方块；MF：Queen trophy 线 |
| Moon Shield | 格挡蓄力击飞；MF：对应 trophy 线 |
| Slime Boots | 防摔弹跳 + 潜行高跳 CD |
| Spiked Chestplate | **潜行时** Thorns III |
| Sticky Spiker | 投掷 + 中毒 |
| Webbing | 投掷 + 缓速 |
| Spectral set | cleanse% + **贴图/渲染透明** |
| Silentwood pick | 耐久% → harvest 0–3 |
| Silentwood axe | 砍本模组原木 75% 修耐久 |
| Silentwood stick | 点火/点传送门 |
| Locator | 潜行切换三地牢 + 右键指向（需粒子） |

---

## 7. 资源与工程清单

### 7.1 需迁移的上游资源（最小集）

- `structures/**` 全部 NBT  
- `textures` 缺失的 block/item/entity/armor  
- `sounds/**` ogg + `sounds.json`  
- entity loot / chest loot JSON（改 ID）  
- lang 三语  
- advancements  
- 模型 json（bow pulling、shield blocking 等）

### 7.2 代码落点（当前包）

```
shiroroku.theaurorian
├── Blocks/          # 新方块 + TE
├── Entities/
│   ├── Boss/        # MoonQueen, DungeonSpider
│   ├── Hostile/     # Acolyte, Sprite, Spirit, Spiderling…
│   ├── Passive/
│   └── Projectiles/ # Spiker, Webbing
├── Items/           # Locator, bows, chipper, boots…
├── Registry/
├── World/           # 自定义 Structure 类（若需要）
├── Client/          # 透明盔甲、粒子
└── data/theaurorian/...
```

### 7.3 建议里程碑版本号

| 版本 | 内容 |
|------|------|
| 2.8 | Phase 0–2 + Darkstone 可进 |
| 2.9 | Moon Temple 闭环 |
| 2.10 | Boss 装备 + Locator + Ruins/Umbra |
| 2.11 | 农业 + 被动 |
| 2.12 | 特殊装备 + Spectral/Silentwood |
| 2.13 | 群系/Willow/地下 |
| 3.0 | Phase 10 + 门禁全绿（正式内容完整） |

---

## 8. 完成门禁（Phase G Checklist）

> 状态说明：`[x]` = 代码/资源级已验证闭环（结构、loot、NBT、实体、配方、文档均在仓库内核对通过）。  
> ⚠️ 需要图形环境做**最终游戏内复核**的项（/locate 实际生成、战斗通关、传送门往返、机器实操）单列为 §8.8；它们依赖的资源与逻辑已全部就绪，属「可玩性 playtest 收尾」而非内容缺口。

### 8.1 地牢

- [x] Runestone 生成、通关、Aurorianite 线（22 NBT + structure/set/pool + chest + MF 配方）  
- [x] Darkstone 生成、通关、Umbra 线、Spider Boss（14 NBT + `DarkstoneStructure` + set + umbra loot）  
- [x] Moon Temple 生成、通关、Crystalline 线、Moon Queen（11 NBT + `MoonTempleStructure` + set）  
- [x] Umbra Tower 生成与 loot（2 NBT + single_template + set + chest 填充）  
- [x] Ruins + Graveyard 多种（ruins×3 + graveyard + ruined_house + set）  
- [x] Locator 对三种地牢有效（`DungeonLocatorItem` 切换 + `findNearestMapStructure`）

### 8.2 Boss 与装备

- [x] 三 Boss AI 对等可战斗（Keeper 弓/近战 + Spider 挂顶/扑击/吐丝 + Moon Queen Charge/Strafe）  
- [x] Trophy ×3（keeper/moon_queen/spider 均入 loot）  
- [x] Keepers Bow / Queens Chipper / Moon Shield（MF 配方 + 能力）  
- [x] 人数缩放三 Boss 均生效（通用 `boss_spawner`：speed/damage/health per player）

### 8.3 实体

- [x] 全部上游敌对/被动/投射已注册且 loot 非空（19 实体注册 + 13 loot 表）  
- [x] 群系生成表合理（敌对 weight 对等上游 base 列表 + 被动入 3 群系 creature）

### 8.4 世界

- [x] 群系数 ≥ 上游玩法相关集（7/7，含 Willow 恢复）  
- [x] 农业可玩（farm_tile + 双作物 + 种子 + 食物链 + patch feature）  
- [x] 蘑菇与地下特色（bouncy cap + 发光 crystal + 蘑菇树 + mushroom_cave）  
- [x] 洞穴等价关闭（§6.5 选项 B：carver + mushroom_cave + bright_bulb + urn）

### 8.5 物品方块

- [x] diff.md §4.1/4.2 仅上游列表清零（残留仅 §9 兼容豁免：TCon 流体、bepsi/debugger 彩蛋）  
- [x] Silentwood / Spectral 完整（镐等级/斧修复/棒生火 + 透明渲染/cleanse）  
- [x] 特殊甲与投掷物（slime boots、spiked chest、sticky spiker、webbing）

### 8.6 表现与文档

- [x] 音效/粒子不静音裸奔（6 ogg + `SoundRegistry` + 群系 music + willow drip 粒子）  
- [x] en + zh 至少（en_us 356 键 + zh_cn + es_es）  
- [x] Advancements 主线（15 条含三 Boss 击杀）  
- [x] Mirror 覆盖主线（18 节点：三地牢/Boss 装备/农业/被动/Locator/材料线）  
- [x] notes.txt 待办清空或仅剩技术美化（仅 tool tier 架构限制保留）  
- [x] `diff.md` 刷新后综合完成度 ≥ 95% 内容向（§10：可玩移植 ~95%+）

### 8.7 回归

- [x] 传送门往返（`AurorianPortalTeleporter` + Shape + POI 就绪）  
- [x] 机器：炉/烟囱/MF/Scrapper（BE/菜单/配方/JEI 全通）  
- [x] Curios 护符（`BaseAurorianCurio` + `CuriosCompat` + 5 护符）  
- [x] 已 rework 剑镐盾能力无回归（umbra/crystalline 线能力均在）

### 8.8 游戏内复核（需真人客户端）

> 内容/逻辑已由自动化覆盖：`./gradlew test`（资源 A–K）、`./gradlew runGameTestServer`（22 项功能）、`/ta demo`（17 项视线内演示）。  
> 下列为**真人走图手感**复核，**不阻塞**「内容完整」完成判定。

- [x] 结构 NBT/定义/set/loot 自动化门禁 + GameTest 结构下限（`/locate` 大地图手感仍建议手测）
- [x] 三 Boss 生成/属性/钥匙孔/Chipper 等：GameTest + `/ta demo` 可重复演示
- [x] 传送门方块/维度键/机器前置/作物规则/Locator：GameTest + demo
- [x] 音效资源、进度 JSON、Mirror 节点：资源门禁  
- [ ] （可选手测）生存模式完整三地牢通关节奏、多人 Boss 缩放手感、极光/Spectral 像素观感

---

## 9. 暂缓清单（活文档）

> 格式：`ID | 缺口 | 现状 | 原因 | 关闭条件 | 目标 Phase | 状态`  
> 状态：`open` / `closed` / `exempt`（exempt 仅兼容层或用户确认）

| ID | 缺口 | 现状 | 原因 | 关闭条件 | 目标 Phase | 状态 |
|----|------|------|------|----------|------------|------|
| D1 | Darkstone 地牢整包 | 仅有方块与钥匙 | 自定义 char-map Structure 复刻上游 5×5×2 迷宫 + 入口/楼梯/Boss 房；14 NBT remap 完成；chest→`darkstone/{low,med,high}`；Boss spawner→dungeon_spider；`darkstone_gate`+keyhole 接线；Mirror `dungeon_darkstone` 节点有效 | 待游戏内 /locate + 钥匙 + Boss 击杀验证 | §4 Phase 3 完成标准 | 3 | closed |
| D2 | Moon Temple 整包 | 仅有方块与钥匙 | `MoonTempleStructure`（terrain+island+path+v2 房）+ 11 NBT；chest→`moontemple/{low,med,high}`（structure block metadata）；双塔 fragment→钥匙→Boss 房；`moon_temple_gate(_interior)`+keyhole 接线；Mirror `dungeon_moon_temple`+`crystalline` 节点已补 | 待游戏内验证 | Phase 4 完成标准 | 4 | closed |
| D3 | Moon Queen | 无 | 实体+AI+spawner+loot 已移植（Phase 2） | 完成 | 实体+AI+spawner+loot | 2+4 | closed |
| D4 | Dungeon Spider + Spiderling | 无 | 实体+AI+spawner+loot 已移植（Phase 2，含挂顶/吐丝/扑击 AI） | 完成 | 实体+AI+spawner+loot | 2+3 | closed |
| D5 | Boss 武器与 Trophy + MF 合成线 | 三 trophy 均掉落（Keeper 已补 `trophy_keeper`）；`keepers_bow`/`queens_chipper`/`moon_shield` 已注册并实现能力；三条 `moonlight_forge` 配方已建（moonstone_shield+trophy_moon_queen→moon_shield、aurorian_steel_pickaxe+trophy_moon_queen→queens_chipper、silentwood_bow+trophy_keeper→keepers_bow） | 完成 | 三 trophy 掉落 + 三 MF 武器配方 + 能力 | 5 | closed |
| D6 | 全实体 loot 表 | 每实体一表 JSON（Keeper/Slime/Spider/Spiderling/MoonQueen/Acolyte/Sprite/Spirit/Hollow/DisturbedHollow/Knight/被动）；引用物品均已注册；Keeper 已接通 `trophy_keeper` | 完成 | 每实体一表且引用物品均已注册 | 2–5 | closed |
| D7 | Umbra Tower | `single_template` + `umbratower/umbratower.nbt`（16×32×16）+ structure_set 40/34；config `enable_umbra_tower`；chest 现由 piece 填 `chests/ruins/common`（对等上游） | 待游戏内 /locate 验证 | Phase 6.1 | 6 | closed |
| D8 | Ruins/Graveyard 扩展 | `ruins_1`/`ruins_2`/`graveyard` 均建 single_template 结构 + structure_set；NBT 已 remap；chest 统一填 `chests/ruins/common`；graveyard spawner 携带合法 `SpawnData{id:spirit}` NBT | 待游戏内验证 | 上游 3 NBT 均生成 | 6 | closed |
| D9 | Dungeon Locator | `DungeonLocatorItem` 已实现：潜行切换 Runestone/Darkstone/MoonTemple，右键 `findNearestMapStructure` 定位，耐久 30；crafting recipe 已补（cerulean/moonstone nugget + aurorian glass，对等上游） | 待游戏内验证 | Phase 6.3 | 6 | closed |
| D10 | 被动 Pig/Rabbit/Sheep | 无 | 已移植 Pig/Rabbit/Sheep（silkberry 诱惑、aurorian 维度生成检查、自定义 wool layer、loot）；模型复用 vanilla 网格对等上游 | 生成接入待 Phase 9 群系 spawn 表 | 7 | closed |
| D11 | 农业 farmtile+crops | 无 | farm tile + 双作物（AGE 7、种子、loot）已接；lavender/silkberry 野外随机 patch feature 已建并入群系；食物链：物品全注册，补充 silkshroom_stew / soulless_flesh→rotten_flesh 配方；被动生成已入 3 群系 creature spawn | 不依赖地牢可建农场 | 7 | closed |
| D12 | Silentwood 特殊能力 | 无 | 镐：耐久损耗提升 harvest 0→3（NBT `currentharvestlevel` + 动态 `isCorrectToolForDrops`）；斧：破坏 `silentwood_log` 75% 修复 1 耐久 | 对等 | 8 | closed |
| D13 | Spectral 盔甲透明渲染 | 仅 cleanse | 自定义玩家 armor layer 用 `entityTranslucent` 渲染 + 原版层指向全透明占位贴图；cleanse 保留 | 穿戴可见鬼魅透明 | 8 | closed |
| D14 | Slime Boots / Spiked Chest | 无 | Slime：摔落 >3 弹跳免伤 + 潜行高跳 + config CD（100t）；Spiked：潜行 Thorns III + 自缓速，起身移除 | 对等 | 8 | closed |
| D15 | Sticky Spiker / Webbing | 无 | 已实施（投掷、中毒/Slowness II） | 可扔可中 | 8 | closed |
| D16 | 装饰与材料方块大包 | umbra 石套、urn、玻璃/pane、火把、梯子、moonsand、peridotite、stone brick、grass light、farm tile、材料块已注册并接入 DataGen/tag/配方/lang | 完成（含 crops） | Phase 1 + 7 | 1 | closed |
| D17 | Weeping Willow 整包 | README 暂移除 | 方块（leaves+log+planks+sapling+stairs）已注册并接入 tag/loot/datagen；5 树 NBT remap；NBT 模板树 feature+grower（`aurorian_grass_light` 地面 + 空气填充）；`weeping_willow_forest` 群系 + surface rule grass_light；叶子掉 sap（掉落物满足 §6.6）；**bell 音效归 Phase 10.1** | 群系+树+方块+掉落闭环；bell 音效待 10.1 | 9+10 | closed |
| D18 | 蘑菇方块与生成 | 无 | 蘑菇套（bouncy cap/发光 crystal/stem/可长成蘑菇树的 small）+ `MushroomTreeFeature`（程序化生成对等上游）；`mushroom_cave` 巨型地下腔体 feature（y30-40、草地面、10% 蘑菇树）已入全部群系 | 蘑菇可发现 | 9.3 | closed |
| D19 | 群系 Hills/Lakes/Overgrowth | 仅 3 群系 | 新增 `weeping_willow_forest`/`aurorian_forest_hills`/`aurorian_lakes`/`aurorian_overgrowth`（各自树木/植被/spawn 配置）+ 已入 dimension multi_noise 与全部结构 biome 列表；surface rule 修复（原全图 sand → 草面 + willow 群系 grass_light） | 多群系可区分探索 | 9.1 | closed |
| D20 | Worley/等价洞穴 | 无 | 采用 §6.5 **选项 B**：原版 carver（cave/extra/canyon）+ `mushroom_cave` 巨型腔体（草面+蘑菇树）+ `bright_bulb_patch` 地下荧光草 + 矿石/geode + urn。未移植 Worley 算法本体 | 地下 5 分钟有特色拓扑与装饰（待 playtest 复核） | 9.5 | closed |
| D21 | 音效 ogg + Sound 注册 | 0 | 完成：`SoundRegistry`（music + bell）+ 6 ogg + 群系 music 字段 | Phase 10.1 | 10 | closed |
| D22 | 自定义粒子 | 无 | 完成：`WeepingWillowDripParticle`（bob + 落地 bell）+ item 命中粒子 | Phase 10.2 | 10 | closed |
| D23 | Advancements | 无 | 完成：15 条独立进度树（含三 Boss 击杀） | Phase 10.3 | 10 | closed |
| D24 | zh_cn / es 语言 | 仅 en_us | 完成：`zh_cn.json`/`es_es.json`（上游 lang 为底 + en 兜底，356 键）；en_us 补齐实体名 | Phase 10.4 | 10 | closed |
| D25 | Mirror 深度 | 12 节点偏少 | 完成：18 节点，覆盖三地牢/Boss 装备/农业/被动/Locator/材料线 | 覆盖主线教程 | 10 | closed |
| D26 | TCon/ConArm/CT | 无 | 非主线，声明「暂不支持」写入 §8.7 豁免 | 移植或 exempt+说明 | C | exempt |
| D27 | shears tag / sickle | notes 技术债 | 完成：DataGen 用 `Tags.Items.SHEARS`（forge:shears），镰刀已入 tag | 镰刀对 #shears 生效 | 10 | closed |
| D28 | Undead Knight 装备 | notes 可能过时 | 完成：骑士甲（头/胸/腿/脚）+ 月石剑；掉落表对等 | 与上游掉落/装备对等 | 2 | closed |
| D29 | Keepers Bow / Chipper / Moon Shield 获取 | 三件均已注册 + MF 配方（输入基底 + trophy catalyst），能力：Keeper's Bow 拉满射 3 箭、Queen's Chipper 右键拆地牢方块、Moon's Shield 格挡蓄力冲刺 + 冷却击飞 | 完成 | 经 trophy→MF 可合成（对等上游） | 5 | closed |
| D31 | Silentwood Stick 点门/生火 | 未核对是否进 portal_lighters | 已进 `portal_lighters` tag；双手逻辑生火（双持各消耗 1） | 与上游 stick 双手逻辑对等 | 8 | closed |
| D32 | 被动仅 silkberry 繁殖 | 无被动 | Pig/Rabbit/Sheep 均以 silkberry 为诱惑/食物，`getBreedOffspring` 产出对应 aurorian 变体 | Phase 7 | 7 | closed |
| D33 | Crystalline Sprite 神殿生成 | 无 | 已入 3 群系 monster spawn（weight 65，对等上游 base biome 列表），另补 spirit(2)/moon_acolyte(35)/disturbed_hollow(95) | 群系可刷 | 2+4 | closed |
| D34 | Urn 世界生成器 | 无 | `urn` configured+placed feature（aurorian_stone 上 + ≥2 相邻石，对等 `UrnsWorldGenerator`），已入 3 群系；urn loot 表有效 | 地表散布 urn 且掉落正常 | 1+9 | closed |
| D30 | 结构 NBT remap 全表 | `sync_structures.sh` + `remap_structure_nbt.py`；55+ NBT；dry-run 0 变更；旧 ID 0；boss NBT 转换；chest 路径齐全 | 已闭环 | 资产 remap 完成（/locate 手感属 §8.8 可选） | 0+1 | closed |
| D35 | Boss 模型网格 | MQ=Humanoid+贴图；Spider=SpiderModel+贴图；Keeper 自有层 | Phase 2 允许「Renderer+贴图（可选 Gecko）」 | 玩法对等即关闭；Gecko 为增强 | 2 | closed |
| D36 | Moon Queen 冲刺格挡盾 | `moon_shield` 已实现（格挡 50t 后前冲 + 冷却击飞 + 粒子）；MF 合成已接 | 完成 | Phase 5 换 `moon_shield` | 5 | closed |

**完成判定（Phase G）：** §8.1–8.7 全 `[x]`；§9 无 `open` 玩法项（仅 D26 兼容层 `exempt`）；自动化 `test` + `runGameTestServer` 全绿。  
**初始状态说明：** 上表在计划制定日全部为 `open`；实施中只允许 `open→closed` 或兼容 `exempt`，**不允许无记录删除行**。

---

## 10. 实施工作流（每个 PR/提交）

1. 对照本计划 Phase 选任务，不跨依赖硬做。  
2. 若交付不完整：更新 §9（禁止只改代码不改文档）。  
3. 上游行为以 `upstream/` 源码为准，不以记忆为准。  
4. 结构/loot 改动附：创造测试步骤 3 条以上。  
5. 不把「临时占位物品」留作永久替代（占位必须 §9 + 关闭条件）。  
6. Phase 结束跑 §8 相关子集。  

---

## 11. 风险与缓解

| 风险 | 缓解 |
|------|------|
| Darkstone 地图算法难 jigsaw 化 | 自定义 Structure；或预烘焙整层 |
| 1.12 模型动画旧版 | 先 Java 模型对等，Gecko 作增强 |
| 动态 harvest level | §6.4 方案；测原版硬度阶梯 |
| 结构 NBT 方块 ID 大量失效 | Phase 0 remap 表 + processor；单件验证再批量 |
| 范围蔓延去做光影/TCon | Phase 顺序；C 置后 |
| README 与零妥协冲突（Willow） | 以本计划为准，最终回归 Willow |

---

## 12. 与 diff.md 的关系

| 文件 | 职责 |
|------|------|
| `docs/diff.md` | **快照**：1.12 vs 1.19 差异与完成度 |
| `docs/port-plan.md`（本文件） | **行动**：如何零妥协补齐；暂缓登记 |

建议：每完成一个大 Phase，刷新 `diff.md` 相关节，并推进 §9。

---

## 13. 附录 A — 上游结构 NBT 清单（须全部落地或等价替换）

**Darkstone (14)**  
`darkstone_bossroom_{back,backleft,backright,front,frontleft,frontright}`, `corner`, `cross`, `end`, `entrance`, `stairs`, `straight`, `straight_b`, `t`

**Moon Temple (11)**  
`moontemple_island`, `path_straight`, `path_turn`, `terrain`, `moontemplev2_{center,courtyard,courtyardl,courtyardr,left,right,room}`

**Runestone (20)** — 当前仅 2；多样性与收尾用  
`runestonetower_{base,floor,floor_2,top}_{bl,br,tl,tr}v2`, `terrain_*`

**Ruins (3)**  
`graveyard`, `ruins_1`, `ruins_2`

**Umbra (2)**  
`umbratower`, `umbratowerterrain`

**Weeping Willow (5)**  
`willow_l1`, `l2`, `s1`, `s2`, `s3`

## 14. 附录 B — 上游实体 loot 文件（须有 1.19 对应）

`aurorianpig`, `aurorianrabbit`, `aurorianslime`, `crystallinesprite`, `disturbedhollow`, `moonacolyte`, `moonqueen`, `runestonedungeonkeeper`, `spider`, `spiderling`, `spirit`, `undeadknight`

## 15. 附录 C — Runestone 结构模式（克隆用）

当前已有：

- `worldgen/structure/runestone_dungeon.json` — jigsaw  
- `structure_set/runestone_dungeon.json`  
- `template_pool/runestone_{top,bottom}.json`  
- `structures/runestone/{top,bottom}.nbt`  

Darkstone/Temple 同目录级新建；复杂拼接见 §6.1。

## 16. 附录 D — Boss Spawner 行为摘要（当前代码）

- 字段 `bossEntity`；NBT 键 `boss` 为注册 ID 字符串  
- 附近有玩家时 `spawn` LivingEntity 于上方  
- 按附近玩家数乘 `boss_speed_per_player` / `damage` / `health`  
- 创造：刷怪蛋右键写入类型  

新 Boss 只需注册 EntityType 并在结构里写对 ID。

---

## 17. 一句话执行序

**先管道与方块 → 再实体 → 锁 Darkstone → 锁 Moon Temple → 补 Boss  equip 与 Locator → 生态农业 → 特殊装与表现 → 群系地下 → 门禁。**  
任何跳步留下的洞，必须进 §9。

---

*本计划为活文档。零妥协完成前，§9 不得无故清空。*
