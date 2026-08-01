# Structure / Loot Asset Remap

> Phase 0 产物。上游 1.12.2 NBT / loot 中的方块与物品 ID → 当前 1.19.2 注册名。  
> 源：`upstream/src/main/resources/assets/theaurorian/`  
> 目标：`src/main/resources/data/theaurorian/`  
> 脚本：`scripts/remap_structure_nbt.py`

## 0. 状态

| 包 | NBT 已复制 | 方块 ID remap | Boss spawner BE 重写 | Loot 迁移 |
|----|------------|---------------|----------------------|-----------|
| runestone（现行 jigsaw） | bottom/top 已是 1.19 | 已完成（现行） | 现行 `boss_spawner` | chests/runestone/* |
| runestone/upstream_ref | 20 件参考 | 未批量 | 未 | — |
| darkstone | 14/14 | 脚本可跑（缺块仍旧名） | 待 Phase 3 | 待 Phase 3 |
| moontemple | 11/11 | 同上 | 待 Phase 4 | 待 Phase 4 |
| umbratower | 2/2 | 需 umbra 方块 | — | 待 Phase 6 |
| ruins | 3/3 | 需 urn 等 | — | 待 Phase 6 |
| weepingwillow | 5/5 | 需 willow 方块 | — | Phase 9 |

## 1. 方块 ID 映射（结构 Processor / 脚本）

### 1.1 已存在于 1.19，可立即 remap

| 1.12 registry | 1.19 registry | 备注 |
|---------------|---------------|------|
| `theaurorian:aurorianstone` | `theaurorian:aurorian_stone` | |
| `theaurorian:auroriandirt` | `theaurorian:aurorian_dirt` | |
| `theaurorian:auroriangrass` | `theaurorian:aurorian_grass` | |
| `theaurorian:auroriancobblestone` | `theaurorian:aurorian_cobblestone` | |
| `theaurorian:auroriancobblestonestairs` | `theaurorian:aurorian_cobblestone_stairs` | |
| `theaurorian:aurorianstonestairs` | *缺* → 暂 `aurorian_cobblestone_stairs` 或 Phase 1 补 brick stairs | 见 §2 |
| `theaurorian:aurorianstonebrick` | *缺* `aurorian_stone_brick` | Phase 1.B |
| `theaurorian:auroriantallgrass` | `theaurorian:aurorian_tallgrass` | |
| `theaurorian:aurorianfurnace` | `theaurorian:aurorian_furnace` | |
| `theaurorian:aurorianfurnacechimney` | `theaurorian:chimney` | |
| `theaurorian:silentwoodlog` | `theaurorian:silentwood_log` | |
| `theaurorian:silentwoodleaves` | `theaurorian:silentwood_leaves` | |
| `theaurorian:silentwoodplanks` | `theaurorian:silentwood_planks` | |
| `theaurorian:silentwoodstairs` | `theaurorian:silentwood_stairs` | |
| `theaurorian:silentwoodchest` | `theaurorian:silentwood_chest` | |
| `theaurorian:silentwoodladder` | *缺* | Phase 1.B |
| `theaurorian:silentwoodtorch` | *缺* | Phase 1.B |
| `theaurorian:darkstonebricks` | `theaurorian:darkstone` | |
| `theaurorian:darkstonefancy` | `theaurorian:darkstone_chipped` | 或 pillar；目视核对 |
| `theaurorian:darkstonegate` | `theaurorian:darkstone_gate` | |
| `theaurorian:darkstonegatekeyhole` | `theaurorian:darkstone_gate_keyhole` | |
| `theaurorian:darkstonelamp` | `theaurorian:darkstone_lamp` | |
| `theaurorian:darkstonelayers` | `theaurorian:darkstone_pillar` | 上游 layers ≈ 柱/分层；核对贴图 |
| `theaurorian:darkstonestairs` | `theaurorian:darkstone_stairs` | |
| `theaurorian:moontemplebricks` | `theaurorian:moon_temple_bricks` | |
| `theaurorian:moontemplebrickssmooth` | `theaurorian:moon_temple_bricks_smooth` | |
| `theaurorian:moontemplebars` | `theaurorian:moon_temple_bars` | |
| `theaurorian:moontemplegate` | `theaurorian:moon_temple_gate` | |
| `theaurorian:moontemplegatekeyhole` | `theaurorian:moon_temple_gate_keyhole` | |
| `theaurorian:moontemplecellgate` | `theaurorian:moon_temple_interior_gate` | |
| `theaurorian:moontemplecellgatekeyhole` | `theaurorian:moon_temple_interior_gate_keyhole` | |
| `theaurorian:moontemplelamp` | `theaurorian:moon_temple_lamp` | |
| `theaurorian:moontemplestairs` | `theaurorian:moon_temple_stairs` | |
| `theaurorian:moongem` | `theaurorian:moon_gem` | |
| `theaurorian:moonglass` | *缺* `moon_glass` | Phase 1.B |
| `theaurorian:moonglasspane` | *缺* | Phase 1.B |
| `theaurorian:aurorianglass` / `pane` | *缺* | Phase 1.B |
| `theaurorian:mysticalbarrier` | `theaurorian:fog_wall` | |
| `theaurorian:crystal` | `theaurorian:crystal` | 已同名 |
| `theaurorian:runestone` 等无下划线 | 已有 snake 现行 jigsaw | upstream_ref 需 remap |
| `theaurorian:lavenderplant` | `theaurorian:lavender_block` | |
| `theaurorian:petuniaplant` | `theaurorian:petunia` | |
| `theaurorian:silkberryplant` | `theaurorian:silkberry_block` | |
| `theaurorian:aurorianperidotite` | *缺* `peridotite` | Phase 1.B |
| `theaurorian:aurorianperidotitesmooth` | *缺* | Phase 1.B |
| `theaurorian:aurorianperidotitesmoothstairs` | *缺* | Phase 1.B |

### 1.2 Boss spawner（特殊）

| 1.12 方块 | 1.19 方块 | BlockEntity |
|-----------|-----------|-------------|
| `bossspawnerkeeper` | `boss_spawner` | `boss: "theaurorian:dungeon_keeper"` |
| `bossspawnerspider` | `boss_spawner` | `boss: "theaurorian:dungeon_spider"`（实体 Phase 2） |
| `bossspawnermoonqueen` | `boss_spawner` | `boss: "theaurorian:moon_queen"`（实体 Phase 2） |

脚本在替换方块名后写入 `palette`/`blocks[].nbt` 的 `boss` 字符串。详见 `docs/boss-spawner.md`。

### 1.3 Phase 1 注册后才可完整 remap

| 1.12 | 目标 1.19 | Phase |
|------|-----------|-------|
| `umbrastone` | `umbra_stone` | 1.A |
| `umbrastonecracked` | `umbra_stone_cracked` | 1.A |
| `umbrastonerooftiles` | `umbra_stone_roof_tiles` | 1.A |
| `umbrastoneroofstairs` | `umbra_stone_roof_stairs` | 1.A |
| `urn` | `urn` | 1.A |
| `auroriancoalblock` 等 | `aurorian_coal_block` 等 | 1.A |
| `aurorianfarmtile` | `aurorian_farm_tile` | 1.C / 7 |
| `lavendercrop` / `silkberrycrop` | crops | 1.C / 7 |
| `weepingwillow*` | `weeping_willow_*` | 9 |
| `mushroom*` | indigo mushroom 套 | 9 |
| `moonsand` / `moontorch` | `moon_sand` / `moon_torch` | 1.B |
| `auroriangrasslight` / `auroriantallgrasslight` | light 变体 | 1.B |
| `tamoonwater` | 流体块（若保留）或水 | 评估 / §9 |

### 1.4 原版 / 结构元块

| ID | 处理 |
|----|------|
| `minecraft:chest` | 可保留；或改 `silentwood_chest`（loot 表仍靠 marker/processor） |
| `minecraft:mob_spawner` | 1.19 为 `minecraft:spawner`；需写 SpawnData |
| `minecraft:web` | `minecraft:cobweb` |
| `minecraft:structure_block` | 放置时通常剥离；jigsaw 用 `jigsaw` |
| `minecraft:air` | 不变 |

### 1.5 实体 ID（spawner / 结构内）

| 1.12 | 1.19 |
|------|------|
| `theaurorian:undeadknight` | `theaurorian:undead_knight` |
| `theaurorian:aurorianslime` | `theaurorian:dungeon_slime` |
| `theaurorian:spiderling` | `theaurorian:spiderling`（待注册） |
| `theaurorian:spirit` | `theaurorian:spirit`（待注册） |

## 2. 物品 ID（loot JSON）

| 1.12 | 1.19 | 状态 |
|------|------|------|
| `keeperamulet` | `keepers_amulet` | 有 |
| `runestonelootkey` | `runestone_loot_key` | 有 |
| `trophykeeper` | `trophy_keeper` | **缺 Phase 5** |
| `trophymoonqueen` | `trophy_moon_queen` | 缺 |
| `trophyspider` | `trophy_spider` | 缺 |
| `darkamulet` | 待定 / curios | 缺 |
| `soullessflesh` | `soulless_flesh` | 缺 Phase 7/2 |
| `tealavender` 等 | `lavender_tea` 等 | 有 |
| `aurorianpork` | `aurorian_pork` | 缺 |
| `moontemplecellkeyfragment` | `moon_temple_key_fragment` | 有 |
| 无下划线材料名 | snake_case 现行 | 对照 ItemRegistry |

## 3. 文件映射清单（55 NBT）

### Darkstone → `data/.../structures/darkstone/`

| 上游文件 | 目标 |
|----------|------|
| darkstone_bossroom_{back,backleft,backright,front,frontleft,frontright}.nbt | 同名 |
| darkstone_{corner,cross,end,entrance,stairs,straight,straight_b,t}.nbt | 同名 |

### Moon Temple → `structures/moontemple/`

| 上游 | 目标 |
|------|------|
| moontemple_{island,path_straight,path_turn,terrain}.nbt | 同名 |
| moontemplev2_{center,courtyard,courtyardl,courtyardr,left,right,room}.nbt | 同名 |

### Umbra / Ruins / Willow / Runestone ref

见 `port-plan.md` 附录 A；已复制到对应目录；runestone 完整上游在 `structures/runestone/upstream_ref/`。

## 4. 运行 remap 脚本

```bash
# dry-run
python3 scripts/remap_structure_nbt.py --dry-run structures/darkstone

# 写入（仅替换 §1.1 中「已存在」映射；未知 ID 保留并列出）
python3 scripts/remap_structure_nbt.py src/main/resources/data/theaurorian/structures/darkstone
```

**注意：** 在 Phase 1 注册 umbra/urn/glass 前，完整 darkstone/temple 结构仍会有未知方块；脚本默认不删未知 ID。

## 5. 关闭条件（D30）

- [x] 目录与 55 NBT 复制  
- [x] 本 remap 表  
- [x] remap 脚本  
- [ ] 对 darkstone/moontemple 跑通且游戏内无空气洞（依赖 Phase 1 方块）  
- [ ] Boss spawner BE 批量重写验证  
- [ ] chest loot 路径从上游文件名迁到 `chests/{darkstone,moontemple}/`  

D30 在 Phase 1+3 验证前保持 `open`。


## 非交互同步（推荐）

本机 zsh 常把 `cp` 别名为 `cp -iv`，覆盖时会卡在确认提示。请用：

```bash
bash scripts/sync_structures.sh           # /usr/bin/cp -f 复制上游 NBT + remap
bash scripts/sync_structures.sh --dry-run # 只看计划
bash scripts/sync_structures.sh --remap-only
```

映射逻辑仍在 `scripts/remap_structure_nbt.py`。
