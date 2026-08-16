# 同步 1.20.1 最近 10 次提交到 1.21.1

## 1.20.1 最近 10 次提交核对
| commit | 主题 | 1.21.1 状态 |
|---|---|---|
| 68b8b23f | Migrate Patchouli guide to resource-pack data | ✅ 已同步（book.json `use_resource_pack:true`，条目在 `assets/`，version 20，且已做 1.21 进一步适配 `sortnum` 数字化/`smelting` 类型） |
| 71f0d14c | Align dungeon entity scale and hitboxes | ✅ 已同步（Spider/Keeper scale 2.0，moon_queen sized 0.6×1.95） |
| 03a75abe | Add throwable dungeon loot and rework item behavior | ✅ 已同步（KeepersBow onUseTick 连发、StickySpiker/Webbing 投掷、UmbraGreatsword 四效果、UmbraShield 过热冷却、en_us/zh_cn lang） |
| c8f60d9f | Rebalance Tinkers handle durability modifiers | ✅ 已同步（5 材料 handle.durability 全部对齐：0.35/-0.20/0.0/-0.15/0.20） |
| 008443b6 | Match Crystalline Sprite rendering to upstream | ✅ 已同步（ground item 模型渲染、texture/item 资源齐全） |
| 0cc70e4b | Fix Strange Meat consumption by maids | ✅ 已同步（`instanceof Player` 守卫已在） |
| 95af3630 | Port TConstruct/CraftTweaker/Patchouli integrations to 1.20.1 | ⚠️ 部分不适用：TConstruct/CT 不适用 1.21.1（gradle.properties `tconstruct_version=` 为空，TConstruct 无 1.21.1 版本，Compat 包构建时排除）；Patchouli 指南 + 粒子 sprite 修复已同步 |
| 902a2357 | Port 1.21.1 fixes to 1.20.1: worldgen, structures, recipes | ✅ 源自 1.21.1，本分支已有（surface rule 陆地草/水底 moon_sand、major_dungeons structure_set 合并、furnace 配方 moon_sand/charcoal/cooked_aurorian_pork、weeping willow 粒子 sprites.get(0,1)） |
| c82536dc | Fix advancements, restore key recipes, complete zh_cn, and plant collision | ✅ 已同步（advancement `items[]` 数组形式、darkstone_key/moon_temple_key/sticky_spiker 配方含 moon_gem、moon_temple_interior_key 要求 moon_gem）；植物碰撞在 1.21.1 不存在该 bug（见下） |
| 01649dd2 | Merge: LevelRendererMixin sky renderer 冲突 | ⚠️ 1.20.1 专属 mixin 冲突解决，不适用 1.21.1（1.21.1 有自己的 LevelRendererMixin，渲染管线不同） |

## 植物碰撞（c82536dc）深度分析
1.20.1 修复前：`copy(Blocks.TALL_GRASS)`，1.20.x 的 `TALL_GRASS`（DoublePlantBlock）`hasCollision=true` → 碰撞 bug。
1.20.1 修复：改 `copy(Blocks.GRASS).noCollission().replaceable()` + 用 `TallGrassBlock`。
1.21.1 现状：`ofFullCopy(Blocks.TALL_GRASS)` + `FlowerBlock`。
- 关键差异：1.21.1 的 vanilla `TALL_GRASS` 构造**显式调用了 `noCollission().replaceable()`**（已从 Blocks.class 字节码确认，offset 19865/19868）。
- `ofFullCopy` → `ofLegacyCopy` 会复制 `hasCollision`（已从 Properties.class 字节码确认，getfield hasCollision）。
- 因此 1.21.1 的 mod 植物复制后 `hasCollision=false`，`replaceable=true`，**无碰撞 bug**。
- 1.21.1 用 `FlowerBlock`（vanilla 花基类，蜜蜂授粉/花盆/可疑 stew）比 1.20.1 的 `TallGrassBlock` 更合适，无需改。
- 结论：**1.21.1 无需同步植物碰撞修复**，bug 是 1.20.x 特有的。

## 漏掉的项
- `.gitignore` 缺 `/libs/`（1.20.1 有，1.21.1 漏）。已补上（commit `1f7b0f08`，前一轮）。

## 不属于本次同步范围的改动
- `gradle.properties` 的 `mod_version` 由 1.0.5 → 1.0.6（非本次会话改动，来源不明；未处理，留待确认）。

## 验证
- `./gradlew check`（含 JUnit 内容校验 + validate_resources + validateRunestoneLayout）全部通过。
- 结论：1.20.1 最近 10 次提交中适用于 1.21.1 的修复全部已同步，无遗漏。
