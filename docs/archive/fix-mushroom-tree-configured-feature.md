# 修复 1.21.1 树缺失的 mushroom_tree configured feature

- 创建日期：2026-11-20
- 目标树：`/opt/DEV/MDEV/minecraft-mods/aurorian/Aurorian`
- 状态：已完成（2026-11-20）

## 背景

静态扫描称：`theaurorian:mushroom_tree` 在 1.21.1 树缺 configured_feature JSON，
但 Java 代码引用它，导致蘑菇树苗无法生长。1.19.2 树存在该文件。

## 复核要点与结论（待实测确认）

1. `AurorianTreeGrowers.MUSHROOM` 使用 `key("mushroom_tree")` →
   `ResourceKey<ConfiguredFeature<?,?>>` = `theaurorian:mushroom_tree`。
2. `BlockRegistry.mushroom_small` 是 `SaplingBlock`（properties 复制
   `Blocks.BROWN_MUSHROOM`），构造时传入 `AurorianTreeGrowers.MUSHROOM`。
3. `World/Feature/MushroomTreeGrower.java` 为 `@Deprecated` 空壳类，
   全仓库除自身外无引用，属死代码（本次不删除）。
4. `FeatureRegistry` 用 `DeferredRegister<Feature<?>>`（`Registries.FEATURE`）
   注册 `theaurorian:mushroom_tree` → `MushroomTreeFeature extends Feature<NoneFeatureConfiguration>`，
   构造用 `NoneFeatureConfiguration.CODEC`，故 JSON config 必须是空对象 `{}`。
5. 1.21.1 源码核对（`build/moddev/artifacts/neoforge-21.1.248-sources.jar`）：
   - `ConfiguredFeature.DIRECT_CODEC` 按 `type` dispatch，`Feature.configuredCodec`
     为 `codec.fieldOf("config")` → 顶层字段仍为 `type` + `config`。
   - `NoneFeatureConfiguration.CODEC = Codec.unit(INSTANCE)`，接受 `{}`。
   - 与 1.19.2 的 `{"type": "...", "config": {}}` schema 兼容。
   - `TreeGrower.growTree` 在 configured feature 不存在时 `getHolder` 返回 null，
     直接 `return false`（无日志），故表现为“树苗永远不长”。
6. 1.21.1 现有 worldgen 引用图：14 个 configured_feature / 14 个 placed_feature
   全部对得上，唯一缺失是 `mushroom_tree`（且没有被 placed_feature/biome 引用，
   只被 sapling 代码引用）。

## 修复步骤

1. 逐字节复制 1.19.2 的
   `worldgen/configured_feature/mushroom_tree.json` 到 1.21.1 树。
2. `git status --porcelain` 确认只新增该文件。
3. 构建并启动服务端（不修改 25565 上既有进程）：
   - run 目录复用 `Aurorian/run/`；
   - `server-port=25812`，`rcon.port=25813`，`broadcast-rcon-to-ops=false`；
   - `./gradlew runServer --offline` 后台运行，日志落盘；
   - 用 Python RCON 驱动命令。
4. 对照实测：
   - 未修复态（文件加入前）：`/place feature theaurorian:mushroom_tree` 预期失败，
     记录返回文本。
   - 修复态：`/place feature ...` 成功，用 `/execute if block` 校验
     `theaurorian:mushroom_stem` / `theaurorian:mushroom` / `theaurorian:mushroom_crystal`。
   - 树苗路径：`/setblock` 放 `theaurorian:mushroom_small`，用发射器 + 骨粉
     触发 `performBonemeal`（两次：stage 0→1→生长），校验生成蘑菇树方块。
5. 收集日志中 `theaurorian` ERROR/WARN、`Missing configured feature`/`Unknown feature`。
6. `/stop` 优雅关闭，确认进程退出与端口释放。

## 待办 / 搁置

- 不修其它 worldgen 文件（引用图已完整）。
- 不删除 `@Deprecated` 的 `MushroomTreeGrower.java`。
- `mushroom_tree` 目前没有 placed_feature / biome 引用，仅用于树苗生长；
  是否要加入世界自然生成不在本任务范围。

## 复现/测试记录

### 未修复态（基线）

- 服务端：`./gradlew runServer --offline`，`server-port=25812`，`rcon.port=25813`。
- RCON：`place feature theaurorian:mushroom_tree 0 120 0`
  → `There is no feature with type "theaurorian:mushroom_tree"`
- `execute if block 0 120 0 theaurorian:mushroom_stem` → `Test failed`
- 对照日志无 worldgen 解析错误（缺失条目是“静默”的，只有命令才显式报错）。

### 修复后

- 新增文件与 1.19.2 一致（sha256 `13be0199...65145c`，diff 为空）。
- RCON：`place feature theaurorian:mushroom_tree 0 120 0`
  → `Placed "theaurorian:mushroom_tree" at 0, 120, 0`
- 方块校验（本次高度=4）：
  - `0 120/121/122/123 0` = `theaurorian:mushroom_stem`（Test passed）
  - `0 123 2`、`0 124 1` = `theaurorian:mushroom`（Test passed）
- 树苗路径：
  - `setblock 10 100 10 theaurorian:mushroom_small`，`gamerule randomTickSpeed 0`；
  - 发射器（`10 100 11`，facing=north，64 骨灰）逐次脉冲；
  - 第 5 次脉冲后原树苗位置变为 `theaurorian:mushroom_stem`；
  - 校验（本次高度=5）：`10 100..104 10` 为 stem，`10 105 10`、
    `10 104 8`、`10 105 9/11` 为 `theaurorian:mushroom`，全部 Test passed；
  - 骨粉剩余 59（5 次脉冲各消耗 1），测试后 `randomTickSpeed` 恢复 3。
- 日志：`/ERROR\]` 0 条；`theaurorian` 相关 ERROR/WARN 0 条；
  无 `Missing configured feature` / `Unknown feature` / 解析失败字样。
- 关闭：RCON `stop` → `BUILD SUCCESSFUL`，devlaunch 进程退出，
  25812/25813 监听释放（1s 内）。

### 遗留说明

- `run/smoke_world` 中保留了本次测试方块（0,120,0 与 10,100,10 附近），未清理。
- 测试连接曾出现一次 RCON 首包超时，属客户端读取逻辑问题（Minecraft 命令响应无空包终止符），已修正，与服务端无关。
- 未验证：真实玩家用骨粉右键树苗的完整交互（用发射器等价触发 `performBonemeal` 验证）；
  未验证自然环境（非创造命令）下树苗的随机刻生长。
