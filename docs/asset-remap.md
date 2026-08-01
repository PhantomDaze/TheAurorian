# Structure / Loot Asset Remap

> Phase 0 产物，**D30 / Phase G 已关闭**（2026-08-01）。  
> 上游 1.12.2 NBT / loot ID → 当前 1.19.2 注册名。  
> 源：`upstream/src/main/resources/assets/theaurorian/`  
> 目标：`src/main/resources/data/theaurorian/`  
> 脚本：`scripts/remap_structure_nbt.py` · 同步：`scripts/sync_structures.sh`

## 0. 状态

| 包 | NBT | 方块 ID remap | Boss spawner | Loot |
|----|-----|---------------|--------------|------|
| runestone（jigsaw bottom/top） | ✅ | ✅ | ✅ `boss_spawner` | ✅ `chests/runestone/*` |
| runestone/upstream_ref | ✅ 20 | ✅ 全量 | ✅ `containedboss`→`boss` | 参考件 |
| darkstone | ✅ 14/14 | ✅ 旧 ID 0 | ✅ spider | ✅ `chests/darkstone/{low,med,high}` |
| moontemple | ✅ 11/11 | ✅ | ✅ moon_queen | ✅ `chests/moontemple/{low,med,high}` |
| umbratower | ✅ 2/2 | ✅ | — | ✅ → `chests/ruins/common` |
| ruins | ✅ 3/3 | ✅ | spirit SpawnData | ✅ `chests/ruins/common` |
| weepingwillow | ✅ 5/5 | ✅ | — | 树 feature |
| gametest | ✅ 3 | n/a | n/a | GameTest |

**校验：** `bash scripts/sync_structures.sh` 后 remap `--dry-run` → `files_touched=0`；已知 1.12 旧 ID 扫描 0 命中。

## 1. 非交互同步（推荐）

本机 zsh 常把 `cp` 别名为 `cp -iv`，覆盖时会卡在确认提示。请用：

```bash
bash scripts/sync_structures.sh           # /usr/bin/cp -f 复制上游 NBT + remap
bash scripts/sync_structures.sh --dry-run
bash scripts/sync_structures.sh --remap-only
```

单独 remap：

```bash
python3 scripts/remap_structure_nbt.py --dry-run src/main/resources/data/theaurorian/structures/darkstone
python3 scripts/remap_structure_nbt.py src/main/resources/data/theaurorian/structures/darkstone
```

完整字典见 `scripts/remap_structure_nbt.py` 的 `BLOCK_REMAP`（及实体/loot 字符串）。

## 2. 关键映射摘要

| 1.12 | 1.19 |
|------|------|
| `darkstonebricks` / `fancy` / `layers` | `darkstone` / `darkstone_chipped` / `darkstone_pillar` |
| `moontemplebricks` / `cellgate*` | `moon_temple_bricks` / `moon_temple_interior_gate*` |
| `bossspawnerkeeper\|spider\|moonqueen` | `boss_spawner` + BE NBT `boss: theaurorian:<id>` |
| `containedboss` | `boss` |
| `runestonedungeonkeeper` / `undeadknight` / `moonqueen` | `dungeon_keeper` / `undead_knight` / `moon_queen` |
| `mysticalbarrier` | `fog_wall` |
| `umbrastone*` / `weepingwillow*` / `urn` / glass / torch | 均已 snake_case 注册 |

## 3. Loot 路径

| 上游概念 | 当前 datapack |
|----------|----------------|
| runestone chests | `loot_tables/chests/runestone/{common,uncommon,rare,epic}.json` |
| darkstone L/M/H | `loot_tables/chests/darkstone/{low,med,high}.json` |
| moontemple L/M/H | `loot_tables/chests/moontemple/{low,med,high}.json` |
| ruins | `loot_tables/chests/ruins/common.json` |
| entities | `loot_tables/entities/*.json`（含 disturbed_hollow） |

## 4. D30 关闭条件

- [x] 目录与 55+ NBT 复制  
- [x] remap 表 / 脚本字典  
- [x] remap 脚本（完整 NBT 编解码）  
- [x] darkstone/moontemple 全量 remap，旧 ID 清零  
- [x] Boss spawner BE：`containedboss`→`boss` + 通用 `boss_spawner`  
- [x] chest loot 路径 `chests/{darkstone,moontemple,runestone,ruins}/`  
- [x] 结构门禁：`validate_resources.py` + GameTest NBT 下限  

**状态：`closed`（与 `port-plan.md` D30 一致）。**  
游戏内 `/locate` 走图属 `port-plan.md` §8.8 可选手测，**不是**资产 remap 缺口。
