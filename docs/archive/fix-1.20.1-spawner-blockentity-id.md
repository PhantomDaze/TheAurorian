# 任务：补齐 1.20.1 刷怪笼方块实体 id 修复 + 全树清扫

状态：**已完成**（2026-11-15）

## 背景
1.19.2 / 1.21.1 已修复「结构 nbt 中刷怪笼方块实体 id 写成 minecraft:spawner」的缺陷；
1.20.1 的修复清单漏掉了 `runestone/upstream_ref/`（该目录未被上一批清单覆盖）。

## 缺陷
- 方块实体类型注册名应为 `minecraft:mob_spawner`（1.13+），写成 `minecraft:spawner` 会
  导致放置结构时不创建 MobSpawnerBlockEntity → 玩家看到空刷怪笼。
- 修法：仅改 `blocks[*].nbt.id`；palette 的方块 Name 保持 `minecraft:spawner`；
  其它字段一律不动。

## 结果
### 全树清扫（61 文件/线）
| 线 | 坏 id | 正确 mob_spawner | palette spawner | 原版箱子 | 静木箱 | boss_spawner |
|---|---|---|---|---|---|---|
| 1.21.1 修复前 | 0 | 35 | 13 | 20 | 79 | 4 |
| 1.20.1 修复前 | **8** | 27 | 13 | 20 | 79 | 4 |
| 1.19.2 修复前 | 0 | 35 | 13 | 20 | 79 | 4 |
| 1.20.1 修复后 | 0 | 35 | 13 | 20 | 79 | 4 |

8 处坏 id 全部在 `runestone/upstream_ref/` 的 7 个文件，与给定清单完全一致。
三线逐文件统计除上述 bad/good 外完全相同（已脚本断言）。

### 修改文件（7 个，共 8 处）
- runestonetower_base_brv2.nbt       1 处（block 860）
- runestonetower_base_tlv2.nbt       1 处（869）
- runestonetower_floor_2_blv2.nbt    1 处（1499）
- runestonetower_floor_2_brv2.nbt    2 处（1523、1526）
- runestonetower_floor_2_tlv2.nbt    1 处（1481）
- runestonetower_floor_blv2.nbt      1 处（786）
- runestonetower_floor_trv2.nbt      1 处（767）

逐文件深度等价校验（HEAD 树 vs 工作树）全部 PASS：
- 唯一差异均为 `$.blocks[i].nbt.id`：`minecraft:spawner`(17) → `minecraft:mob_spawner`(21)；
- gzip 解压长度增量 = 4×改动数；
- 改后坏 id=0，palette 中 `minecraft:spawner` 仍为 1。

### 引用确认
`RunestoneDungeonStructure.java:45` 定义 `REF = "theaurorian:runestone/upstream_ref/"`，
并在 :114-120 使用 terrain/base/floor/floor_2/top 模板名 → **被引用，修复必要**。
世界生成已注册：`worldgen/structure/runestone_dungeon.json` +
`worldgen/structure_set/major_dungeons.json`，配置默认 `enable_runestone_dungeon=true`。

### 残留原版箱子（未修）
三线 upstream_ref 均为 12 文件 / 20 个 `minecraft:chest` 方块实体。
`RunestoneDungeonStructure.populateChests()` 同时遍历 `Blocks.CHEST` 与
`BlockRegistry.silentwood_chest`，战利品注入仍生效；差别仅为外观（原版箱 vs 静木箱）。
不影响玩家可见战利品结果，本次按任务要求不修改。

## 未决事项
- 保留原版箱子（20 处）作为后续外观统一事项，待单独任务处理。
- 工具脚本放在 /tmp（/tmp/nbttool.py、/tmp/sweep.py、/tmp/fix_spawner.py、
  /tmp/verify_spawner.py），未进仓库。
