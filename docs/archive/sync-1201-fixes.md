# 同步 1.20.1 分支新修复到 1.21.1

日期：2026-08-14
分支：`1.21.1`（目标），来源 `origin/1.20.1`
状态：**已完成**（4 个 commit：e88993a3 / cd938d7c / e57d0587 / 1ed51fcf）

## 背景

1.20.1 分支 08-09 有 7 个提交尚未同步到 1.21.1（1.21.1 最后一次同步 `c0d9bc65` 只覆盖到 08-07/08-08 的内容）。
1.21.1 与 1.20.1 存在 API 差异（1.21.1 用 `ResourceLocation.parse`/Holder 化、结构 NBT 路径 `structure/` vs `structures/`），
因此不能直接 merge，需按逻辑手动移植。

## 待同步提交清单（最终确认）

| 提交 | 内容 | 1.21.1 现状 | 同步方式 |
|------|------|------------|---------|
| `8398f679` | 表面结构不在 lakes 生物群系/水面上生成 | 无 `StructurePlacementChecks` | 新增该类 + 3 结构类加检查 + 5 json 移除 aurorian_lakes |
| `0ae73301` | ruined_house 改用 single_template 结构 | 类型已改，仍残留 `aurorian_lakes` | json 移除 aurorian_lakes |
| `92494473` | darkstone boss 房六块共享一层 | boss 房仍各 piece 独立探高度 | 只改 `DarkstoneDungeonStructure`（NBT 不需） |
| `55cd2edd` | moon temple `chest_high`→high + key fragments | 1.21.1 `chest_high` 仍错映射 med；无 key fragments | 改 `MoonTempleStructure` + high.json + left/right NBT 加 4 structure_block |
| `f6adf569` | runestone 塔 NBT 双箱子化 | 1.21.1 已用 `connectChests` 等价实现 | **不需同步** |
| `80b443c8` | graveyard 重存（spawner id 1.20.1 用 mob_spawner） | 1.21.1 用 `minecraft:spawner` 正确 | **不需同步** |
| `2bb2cb04` | tinkering material 移到 assets + 颜色改 hex | 仍 data/ + int 格式 | 移动 5 json（TConstruct 1.21.1 未发布，仅保持一致） |

## NBT 分析结论（已用 python 解析器确认）

- **graveyard/corner/straight_b/bossroom_back/end**：1.20.1 修改仅为 `minecraft:spawner`→`minecraft:mob_spawner`（1.20.1 特有）或 chest 双箱子化（palette 加 type/waterlogged，blocks/pos 不变）。1.21.1 版本正确，**不覆盖**。
- **runestone 12 NBT**：f6adf569 仅 palette 加双箱子状态（blocks/sb/chest 数不变）。1.21.1 `connectChests` 等价实现，**不覆盖**。
- **moontemple center/room**：仅双箱子化，**不覆盖**（connectChests 处理）。
- **moontemple left/right**：1.20.1 新增 4 个 `chest_high` structure_block（left: (9,21,12)/(10,21,12)，right: (5,21,12)/(6,21,12)），**需合并进 1.21.1**，保留 1.21.1 的 `minecraft:spawner` 与单箱子状态（connectChests 自动连接）。

## 执行步骤

1. [x] 分析差异，定位待同步提交
2. [x] 新增 `StructurePlacementChecks.java` + 3 结构类加 `isWaterCovered` 检查
3. [x] 6 个结构 json 移除 `aurorian_lakes`（graveyard/ruins_1/ruins_2/runestone_dungeon/umbra_tower/ruined_house）
4. [x] `DarkstoneDungeonStructure` boss 房共享一层（92494473）
5. [x] `MoonTempleStructure`：`chest_high`→high loot（55cd2edd）
6. [x] `moontemple/high.json` 加 key fragments
7. [x] moontemple left/right NBT 合并 4 个 structure_block（用类型感知 NBT 编码器插入，验证：非 blocks 字段全一致、原 blocks 全保留、spawner 保持 minecraft:spawner）
8. [x] tinkering materials 移到 assets + hex 颜色
9. [x] 编译验证（`./gradlew compileJava` BUILD SUCCESSFUL）
10. [x] 资源校验（`validate_resources.py` 0 error 0 warning）+ 结构布局测试（runClientStructureLayout：3 个 boss 结构全部 OK，无崩溃）
11. [ ] 分批 commit，英文 message（待用户确认）

## 备注

- 1.21.1 的 `MoonTempleStructure` 无 CHEST fallback 循环（比 1.20.1 简化），故无需 `looted` 集合。
- tinkering materials：1.21.1 `tconstruct_version` 为空，移动 assets 仅保持一致。
