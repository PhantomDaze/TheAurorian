# 同步 1.20.1 分支新修复至 1.19.2

- 日期: 2026-08-14
- 分支: 1.19.2 (HEAD bf467fd2)
- 目标: 把 1.20.1 分支在 bf467fd2 移植点之后的修复同步过来
- **状态: 已完成**(2026-08-14,代码移植 + 编译验证通过)

## 分析结论

1.19.2 HEAD `bf467fd2`(2026-08-09 19:27 "Port 1.20.1 fixes to 1.19.2")已移植 1.20.1 截至 08-09 的修复。
`git log 1.19.2..origin/1.20.1` 列出的 26 个提交中,时间在 bf467fd2 之后的新提交仅 4 个:

| 提交 | 内容 | 状态 |
|------|------|------|
| 2bb2cb04 (08-09 14:56) | tinkering 材料颜色格式 + 移至 assets | **已移植**,diff 为空,跳过 |
| e4ab1050 (08-11) | WeepingWillowLeavesBlock 默认 persistent=true | 待同步 |
| 00658fe5 (08-11) | DungeonSlime 碰撞箱 ~4x 过小修正 | 待同步 |
| 742d0fa2 (08-14) | weeping willow planks/stairs 配方 | 待同步 |

其余 22 个提交(结构/世界生成/附魔/装备/Patchouli/TConstruct 等)均在 bf467fd2 移植范围之内,
1.19.2 已包含(通过文件级 diff 抽查确认)。

## 执行步骤

1. WeepingWillowLeavesBlock: 构造函数注册默认状态 `PERSISTENT=true`
2. DungeonSlimeEntity + EntityRegistry: 新增 `BASE_SIZE = 0.5F/0.255F`,替换 `sized(0.52F, 0.52F)`
3. 新增 2 个配方 JSON:
   - `recipes/shaped/weeping_willow_stairs.json`
   - `recipes/shapeless/weeping_willow_planks.json`
4. 编译验证(compileJava / IDE build_project)
5. 提交(等用户确认,英文 message)

## 验证要点

- `weeping_willow_planks` / `weeping_willow_stairs` 均在 BlockRegistry 已注册(已确认)
- 1.19.2 配方目录结构 `recipes/shaped` / `recipes/shapeless` 与 1.20.1 一致(已确认)
- EntityRegistry.java 的 dungeon_slime 行上下文与 1.20.1 diff 完全对齐(已确认)

## 复查(结构相关提交,2026-08-14)

用户追问"更早的结构相关修复是否同步",逐一核实 6 个提交:

| 提交 | 内容 | 核实结果 |
|------|------|---------|
| 8398f679 | 地表结构湖泊/水上拒绝 | ✅ isWaterCovered 定义 + 3 处调用 + 5 个 JSON 全在 |
| 92494473 | darkstone boss 房统一楼层 + loot | ✅ Java bossRoomY 逻辑在;NBT 逐方块一致 |
| 55cd2edd | moon temple 双 loot 修复 + key fragments | ✅ looted Set 逻辑 + high.json fragment 在;NBT 一致 |
| f6adf569 | runestone tower 加 loot chests | ✅ NBT chest 数一致 |
| 80b443c8 | 重存 graveyard 模板 | ✅ NBT 逐方块一致 |
| 0ae73301 | ruined house 改 single_template | ⚠️ **遗漏**:type 已改,但 biome 列表移除 aurorian_lakes 未同步 |

**已修复遗漏**:从 `ruined_house.json` biome 列表删除 `aurorian_lakes`(与上游一致)。

**附带发现**:runestone/darkstone/moontemple/graveyard 模板的 NBT 二进制与 1.20.1 不同,
但逐方块 (pos+Name) 内容一致;唯一差异是 1.20.1 重存模板时给 chest palette 显式加了
`facing` 属性(1.19.2 无),loot 表、双箱 type 均已同步,不影响实质内容,未覆盖二进制。

## 未决事项

- (已闭合) 0ae73301 的 aurorian_lakes biome 遗漏 —— 2026-08-14 已修复
