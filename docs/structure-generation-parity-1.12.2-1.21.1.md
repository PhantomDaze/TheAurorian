# 1.12.2 与 1.21.1：带模板拼图结构的生成机制对照与符石塔对齐方案

> 目的：解释原版 The Aurorian 1.12.2 为什么能把 15×15 模板拼成符石塔，以及 1.21.1 NeoForge 移植为什么会出现“拼图错位、方向反了、跨区块缺半边”。本文同时作为后续修改 `RunestoneDungeonStructure` 和布局测试的基准。

## 1. 结论先行

1.12.2 的符石塔不是 Jigsaw，也不是一个模板自动连接出来的结构，而是 `AurorianChunkGenerator.populate` 在**每个区块**调用 `RunestoneTowerWorldGenerator.generate`，再由 `isValidChunkForGen(chunkX, chunkZ, offsetX, offsetZ)` 判断当前区块是否属于某座塔的四个源区块。

四个模板的文件名 TL/TR/BL/BR 表示源生成分支，而不是可以直接套用的屏幕象限名。以 BR 源区块为锚点，原始 1.12.2 的模板原点是：

```text
                 z - 16                 z + 1
x - 15       TL 源分支              TR 源分支
x            BL 源分支              BR 源分支
```

本对照结论不包含此前临时提出的象限交换；当前目标是恢复原模的 TL/TR/BL/BR 同名映射。

## 2. 1.12.2 原模机制

### 2.1 结构触发层

原模的 `AurorianChunkGenerator.populate(int x, int z)` 对每个已生成区块依次调用各个 `WorldGenerator`：

```java
if (RunestoneTowerWorldGenerator.GENERATE_TOWERS) {
    towergen.generate(world, random, blockpos);
}
```

`generate` 取得当前区块 `Chunk c`，然后在同一个调用里检查四个相邻的“源区块条件”。符石塔使用：

```java
(chunkX + offsetX) % Config_DungeonDensity == 0
&& (chunkZ + offsetZ) % Config_DungeonDensity == 0
```

默认 `Config_DungeonDensity=32`，所以原模是以 32 区块为周期的确定性网格，而不是 1.21.1 `random_spread` 的随机单元内选点。

`offsetX/offsetZ` 的含义是“为了找到塔的固定锚点，当前源区块要加多少偏移”，不是模板的世界坐标偏移。原模注释给出的四个源分支是：

```text
TL = (1, -1)
TR = (1,  0)
BL = (0, -1)
BR = (0,  0)
```

假设 BR 源区块的中心锚点为 `(x,z)`，则各分支当前区块中心分别是：

```text
TL 当前区块中心 = (x-16, z-16)
TR 当前区块中心 = (x-16, z)
BL 当前区块中心 = (x,    z-16)
BR 当前区块中心 = (x,    z)
```

### 2.2 模板实际原点

原模的 `generateTower` 使用：

```java
final int x = chunkX * 16 + 8;
final int z = chunkZ * 16 + 8;
```

逐分支模板原点如下：

| 源分支 | terrain/base/floor/top 原点 | 模板集合 |
|---|---:|---|
| TL | `(x-15, y, z-16)` | `*_tl` |
| TR | `(x-15, y, z+1)` | `*_tr` |
| BL | `(x, y, z-16)` | `*_bl` |
| BR | `(x, y, z+1)` | `*_br` |

推导示例：TL 当前区块的中心是 `(x-16,z-16)`，旧代码放置位置是 `(currentX+1,currentZ)`，因此得到 `(x-15,z-16)`。TR 是 `(currentX+1,currentZ+1)`，得到 `(x-15,z+1)`。

模板均为 15×15：

- X：`x-15..x-1` 与 `x..x+14`，两块正好相邻；
- **Z**：`z-16..z-2` 与 `z+1..z+15`，`z-1,z` 是两格中央空隙；
- 这两个方向性空隙来自原始模板保存方向，不能通过“把所有块旋转 180°”修正。

### 2.3 楼层循环和旋转

每个分支的逻辑相同：

1. terrain 从地表向下堆到 Y=50；
2. base 放在 `y`；
3. `floor` 放在 `y + 6*floor`；
4. 下一轮使用 `floor_2`，并将计数器增加 2；
5. 最终 top 根据 `alt` 状态使用普通 top 或 180° 的另一侧 top。

上游关键代码等价于：

```text
alt=true, floor=1
while floor <= FLOOR_COUNT:
  if alt:
    floor_quad @ (px, y+6*floor, pz), NONE
    floor += 1
  else:
    floor2_opposite_quad @ (px+14, y+6*floor, pz+14), CLOCKWISE_180
    floor += 2
  alt = !alt

bossY = y + 6*floor
if alt:
  top_quad @ (px,bossY,pz), NONE
else:
  top_opposite_quad @ (px+14,bossY,pz+14), CLOCKWISE_180
```

`+14` 不是额外的世界平移设计，而是因为 15×15 模板按 1.12 的旋转枢轴需要把旋转后的负坐标抵消回原模板方框。

### 2.4 Y 高度的历史细节

原模不是简单地用模板原点当高度采样点。每个源分支分别调用 `getDungeonBaseHeight`；展开 `part` 偏移后，四次最终采样坐标是：

```text
TL、BL： (x+15, z-16)
TR、BR： (x+15, z+16)
```

模板原点仍然是上一节的 `z-16 / z+1`。也就是说，原模故意把高度探针和模板原点分开 17 格；这两个坐标表不能合并。旧方法从世界高度向下跳过空气、静默木树叶、静默木原木和灌木，返回实体表面上方一格。BR 分支的 terrain 循环是 `place y=i`，另外三个分支是 `place y=i-1`，这是原模的一个不对称历史 quirk。

### 2.5 1.12 模板放置和方块实体

原模直接调用 `Template.addBlocksToWorld`。调用发生在源区块 populate 阶段，模板写入可以跨越区块；宝箱的 `structure_block` data marker 被 `getDataBlocks` 找到，marker 清掉后给下方 ChestBlockEntity 设置 loot table。刷怪笼和 BossSpawner 的 NBT 也随模板直接写入。

## 3. 1.21.1 当前机制

### 3.1 结构触发层变化

1.21.1 使用三层数据/代码管线：

```text
worldgen/structure/runestone_dungeon.json
        ↓ 结构类型与生物群系
worldgen/structure_set/major_dungeons.json
        ↓ random_spread 选择起始区块
Structure.findGenerationPoint(context)
        ↓ 生成 StructureStart / StructurePiece
StructurePiece.postProcess(..., chunkBox, chunkPos)
        ↓ 每个相交区块裁剪放置模板
```

这和 1.12 的“每个区块主动询问四个 offset”不是同一个触发模型。1.21 只选择一个结构起始点；所有分片必须在一个 `StructureStart` 内列出，并由 Minecraft 根据 StructurePiece 的包围盒在每个相交区块调用 `postProcess`。

当前 `runestone_dungeon.json` 已使用自定义 `theaurorian:runestone_dungeon`，不是遗留 Jigsaw pool。`RunestoneDungeonPiece` 将 20 个模板类别的 Slot 存入一个 piece，并用 `StructurePlaceSettings.setBoundingBox(chunkBox)` 裁剪到当前区块，避免只在原点区块放置而丢失跨区块部分。

### 3.2 1.21 模板旋转的等价关系

1.21 `StructureTemplate.placeInWorld(offset, pos, settings, ...)` 先将模板局部坐标按 settings 的 rotation/pivot 变换，再加 `offset`。`StructurePlaceSettings` 默认 rotation pivot 是 `BlockPos.ZERO`。

因此上游的：

```text
template at (baseX+14, baseY, baseZ+14), CLOCKWISE_180
```

在 1.21 中仍应使用：

```java
.setRotation(Rotation.CLOCKWISE_180)
placeInWorld(level, new BlockPos(baseX+14, baseY, baseZ+14), ...)
```

不能再叠加一个全塔 180°，也不能把 `+14` 删除。模板的 block state 朝向会由 `placeInWorld` 的 rotation 一并旋转。

### 3.3 当前移植与原模的差异

| 项目 | 1.12.2 | 1.21.1 初始/错误移植 | 对齐规则 |
|---|---|---|---|
| 分布 | 默认 32 区块确定性网格 | `random_spread spacing=24,separation=18` | `spacing=32,separation=31`，每 32 区块单元内偏移固定为 0 |
| 分支映射 | 无交换：TL/TR/BL/BR 同名 | 曾混入临时全局旋转或模板集合交换 | 保持原模同名模板集合与局部 floor2/top 旋转 |
| terrain | BR 用 `i`，其它用 `i-1` | 逻辑已保留，但曾把 BR quirk 与错误物理位置绑定 | quirk 绑定物理 BR 源位置，而不是模板文件名 |
| 跨区块 | 源区块直接写世界 | 自定义 piece 若无 chunk BB 会重复/漏写 | 每次 postProcess 设置当前 chunk BoundingBox |
| loot marker | `getDataBlocks` + 下方箱子 | 1.21 需 `filterBlocks(STRUCTURE_BLOCK)` | 保留 metadata 过滤、清 marker、设置 loot |
| NBT | 旧 spawner / containedboss | 需 1.21 SpawnData 形和 `boss` 键 | remap 后门禁扫描全量 NBT |

## 4. 对齐实现方案

### 4.1 符石塔 Slot 表

以 BR 密度锚点区块中心 `(x,z)` 为原点，把四个 1.12 源分支的放置坐标折叠进同一个 `StructureStart`。模板文件名与上游分支同名，**不做**“径向边朝中心”的二次交换——那种映射会把实心 L 边朝内，结果变成四个彼此分离的四分之一圆。

上游 `generateTower` 相对 BR 锚点的放置原点：

```text
                 z + 1                  z + 16
x - 15       TR（NW，实心 N+W）      TL（SW，实心 S+W）
x            BR（NE，实心 N+E）      BL（SE，实心 S+E）
```

floor y=1 占用掩码确认：每块的两条满边是外圈 L 边，弧边朝塔心；BL/BR 的 `runestone_gate` 落在东侧两半的接缝上，构成入口。

```java
// 高度探针：原模四次采样在扁平世界可合并为一点
int y0 = surfaceY(context, x + 15, z + 16);
// 楼层数：原模把奇数配置 +1 变成偶数
if ((floors & 1) != 0) floors += 1;

addQuadrant(slots, x - 15, y0, z + 1,  "tr", false, floors);
addQuadrant(slots, x - 15, y0, z + 16, "tl", false, floors);
addQuadrant(slots, x,      y0, z + 1,  "br", true,  floors); // BR terrain y=i
addQuadrant(slots, x,      y0, z + 16, "bl", false, floors);
```

所有四分之一圆模板在这一层都保持 `Rotation.NONE`；`floor_2` 与交替 top 的局部 180° 规则仍由 `addQuadrant` 内的 opposite template +14 pivot 实现。


### 4.2 生成 Piece 的安全要求

- `StructurePiece` 的包围盒必须覆盖所有 Slot 的实际变换后 AABB；允许略大，但不能小；
- `postProcess` 每次使用当前 `box` 设置 `StructurePlaceSettings.setBoundingBox(box)`；
- Slot 的模板 AABB 与当前 chunk box 不相交时跳过；
- 通过 `placeInWorld` 放置 block entity NBT，不手动复制普通方块；
- chest marker 用旋转后的 `filterBlocks(slot.pos, settings, STRUCTURE_BLOCK)` 查询；
- terrain pillar 也必须按当前 chunk box 裁剪，否则邻区块会重复写入或跨 chunk 漏掉。

### 4.3 分布差异的处理边界

1.21.1 现已使用共享的 `major_dungeons` 结构集，以 `random_spread spacing=32,separation=31` 为三座大型结构提供统一候选网格。由于 `separation=spacing-1`，每个 32 区块单元内没有可选随机偏移，且一个候选点只会按权重选择 Runestone、Darkstone 或 Moon Temple 其中一座；salt 只影响单元哈希，不改变这个零偏移结论。

布局测试使用 `Structure.generate` 在固定 `ChunkPos(0,0)` 直接放置，是为了隔离模板几何；自然生成则由上面的 32 区块结构集控制。

## 5. 验证矩阵

### 自动验证

```bash
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk
export PATH="$JAVA_HOME/bin:$PATH"
python3 scripts/validate_runestone_layout.py
./gradlew test
```

布局门禁必须检查：

- 20 个 upstream_ref 模板都能解析；
- 每个模板尺寸是 15×15，类别高度分别为 terrain=1、base/floor=6、floor_2=12、top=15；
- 物理 Slot 原点使用上游同名表：`TR/BR @ z+1`、`TL/BL @ z+16`，`x-15/x`；
- 实心 L 边朝外（tr=N+W, tl=S+W, br=N+E, bl=S+E），禁止径向朝中心的反转映射；
- 楼层数按原模把奇数配置抬成偶数；
- floor2/topAlt 仍是 opposite template + 180° +14 pivot compensation；
- Java 中不存在错误的 `x+17`、`z-16` 原点、额外全局旋转或 bl/br/tl/tr 朝中心交换。

### 客户端布局测试

```bash
./scripts/run_structure_layout_test.sh
```

脚本启动前删除 `run/saves/ta_structure_layout`，客户端自动创建固定种子的超平坦世界，并通过 `Structure.generate + StructureStart.placeInChunk` 放置 runestone、darkstone、moon temple；游戏保持打开供检查。符石塔的结构起始包围盒应覆盖 X 连续的 30 格模板宽度和 Z 方向含中央两格空隙的 32 格历史 footprint。

## 6. 修改记录原则

以后若玩家反馈“方向错”，先记录具体方块（门、楼梯、BossSpawner）所在的物理 Slot，再判断是：

1. 模板集合交换错误；
2. 该 Slot 的 180° floor2/topAlt 变换错误；
3. 原点坐标错误；
4. `StructureTemplate` 的 chunk clipping/包围盒错误；
5. 仅仅是 random_spread 锚点与旧模网格不同。

不要用“所有部件统一旋转 180°”作为第一修复手段，因为这会同时破坏楼梯 block state、门洞方向、loot marker 和原模已经正确的 floor2 局部旋转。
