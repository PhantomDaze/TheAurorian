# Boss Spawner（1.19）

## 行为（当前代码）

- 方块：`theaurorian:boss_spawner`（`BossSpawnerBlock` + `BossSpawnerBlockEntity`）
- BE 字段：`bossEntity`；NBT 键 **`boss`** = 实体注册 ID 字符串，例如 `theaurorian:dungeon_keeper`
- 每 tick（服务端）：若 `boss` 已设且 `spawnDistance`（16）内有非创造/观察者玩家，则在 spawner **上方** `MobSpawnType.STRUCTURE` 生成，并 **销毁** spawner
- 人数缩放：附近玩家数 > 1 时，对 MAX_HEALTH / ATTACK_DAMAGE / MOVEMENT_SPEED 乘以 `(n * config_per_player + 1)`（见 `CommonConfig`）
- 创造调试：手持任意 **Spawn Egg** 右键 spawner → `setBoss(eggType)`
- 客户端：已设置 boss 时旋转渲染 spawner 模型

## 结构内写入方式

### 1. 结构 NBT（推荐最终）

palette 中方块为 `theaurorian:boss_spawner`，对应 `blocks[]` 条目带：

```nbt
{ boss: "theaurorian:dungeon_keeper" }
```

`scripts/remap_structure_nbt.py` 可将 1.12 的 `bossspawnerkeeper|spider|moonqueen` 转为上述形式。

### 2. 放置后指令（调试）

```mcfunction
# 先放方块，再写 BE
setblock ~ ~ ~ theaurorian:boss_spawner
data merge block ~ ~ ~ {boss:"theaurorian:dungeon_keeper"}
```

### 3. 创造

1. 取出 `boss_spawner`  
2. 取出目标 Boss 的 spawn egg  
3. 右键 spawner  

## 实体 ID 约定（移植）

| Boss | `boss` 值 | 注册 Phase |
|------|-----------|------------|
| Runestone Keeper | `theaurorian:dungeon_keeper` | 已有 |
| Darkstone Spider | `theaurorian:dungeon_spider` | 2 |
| Moon Queen | `theaurorian:moon_queen` | 2 |

新 Boss **只需** EntityType 注册 + 结构写对 ID；无需新 spawner 方块。

## 与上游差异

| 上游 | 1.19 |
|------|------|
| 三方块 `bossspawnerkeeper` 等 | 单方块 + NBT |
| 无人数缩放 | 有 |
| 网格地牢内固定 | jigsaw / 自定义 Structure 内放置 |

## 测试清单

1. 创造：蛋设置 → 走近生成 → spawner 消失  
2. 两人以上：属性高于单人  
3. 未设置 boss：不生成、不崩  
4. 结构生成后 BE 仍带 `boss` 字符串（进世界 `/data get block`）
