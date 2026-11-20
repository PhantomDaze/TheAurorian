# 修复 mushroom_tree configured feature 缺失（1.20.1f）

## 状态
已完成（2026-11-20）

## 实测结果
- 修复前：`/place feature theaurorian:mushroom_tree 0 100 0` → `There is no feature with type "theaurorian:mushroom_tree"`（复现成功）。
- `/reload` 不会重新扫描 mod 数据包（放回文件后仍失败），需重启服务端。
- 修复后：`Placed "theaurorian:mushroom_tree" at 0, 100, 0`；扫描到 stem/cap/crystal。
- 骨粉生长：`setblock theaurorian:mushroom_small` + 发射器骨粉，骨粉 64→59，长出 stem/cap/crystal（坐标 10 100 10 一带）。
- 服务端日志无 theaurorian 相关 ERROR/WARN，无 Missing/Unknown feature。
- RCON `/stop` 优雅退出，25810/25811 端口释放。

## 复核结论
- `MushroomTreeGrower` 引用 `theaurorian:mushroom_tree`（Registries.CONFIGURED_FEATURE）。
- `FeatureRegistry.MUSHROOM_TREE` 注册名 `mushroom_tree`，类型 `Feature<NoneFeatureConfiguration>`，codec 为 `NoneFeatureConfiguration.CODEC`。
- `mushroom_small` 是 `SaplingBlock(new MushroomTreeGrower(), ...)`。
- 1.19.2 的 JSON 为 `{"type":"theaurorian:mushroom_tree","config":{}}`；1.20.1 树中 `mushroom_cave.json`、`weeping_willow_tree.json` 使用同格式，schema 兼容，可逐字节复制。

## 计划
1. 复制 1.19.2 的 mushroom_tree.json 到 1.20.1 资源目录。
2. 未修复状态下尝试 `/place feature theaurorian:mushroom_tree` 作为对照（若流程允许）。
3. 构建并启动 runServer（端口 25811，RCON）。
4. 修复后 `/place feature` + `/execute if block` 验证产出的 mushroom/mushroom_stem/mushroom_crystal。
5. `/setblock` 蘑菇树苗，说明骨粉生长命令限制。
6. 优雅关闭，记录日志与 git status。

## 未决/待查
- 无
