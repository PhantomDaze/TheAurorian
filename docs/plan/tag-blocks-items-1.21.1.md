# 方块/物品 Tag 补全（1.21.1）

## 目标
对照 1.21.1 原版 + NeoForge `c:` 常见 tag，为 mod 内所有方块/物品按名称与用途补齐合适的 tag。

## 完成情况（已实现）
### DataGenBlocksTags.java（方块 tag）
- mineable/pickaxe 补齐：runestone 系 8、darkstone 系 7、moon_temple 系 9、crystal、fog_wall、mushroom_crystal、boss_spawner
- mineable/axe 补齐：mushroom、mushroom_stem、mushroom_small（对应原版蘑菇块为斧）
- mineable/hoe 补齐：silentwood_leaves、weeping_willow_leaves
- needs_stone_tool 补齐：aurorian_coal_ore、deepslate_cerulean_ore、deepslate_moonstone_ore、aurorian_steel_block、cerulean_block、moonstone_block
- 功能 tag：fences、stairs（darkstone/moon_temple）、sand、smelts_to_glass、coal_ores、impermeable、crops、maintains_farmland、small_flowers、flowers（补 2）、replaceable（补 3）、replaceable_by_trees、sword_efficient、guarded_by_piglins、beacon_base_blocks、wall_post_override（双火把）
- 常见 c: tag：fences、glass_blocks(+colorless)、glass_panes(+colorless)、cobblestones(+normal)、stones、sands(+colorless)、storage_blocks(+coal)、ores_in_ground(stone/deepslate)、ore_rates/singular
- dungeon_bricks 补 moon_temple_stairs

### DataGenItemsTags.java（物品 tag）
- 木系物品 tag：planks、logs_that_burn、leaves、saplings、wooden_slabs、wooden_stairs、wooden_fences、fences
- 材料镜像：storage_blocks(+coal)、glass_blocks(+colorless)、glass_panes(+colorless)、cobblestones(+normal)、stones、sands(+colorless)、dirt、sand、coal_ores、ores_in_ground(stone/deepslate)、ore_rates/singular
- 作物/种子/蘑菇：crops、seeds、villager_plantable_seeds、mushrooms
- 食物：foods（15 项）、foods/berry
- 工具子类：melee_weapon（13）、mining_tool（21）、ranged_weapon（2）

### 生成结果
- `runData` 成功，tag 文件 73 → 127；`validate_resources.py` 0 warning。
- 删除的旧 c: tag（c:shears、c:string、c:armors/*、c:tools/bows、c:tools/shields）为 1.19.2 陈旧路径，NeoForge 21.1.248 对应新路径已生成，无引用。

## 顺带修复（datagen 原本无法运行）
- DataGenBlocksLoot.java：boss_spawner / aurorian_portal / fog_wall 补空 loot table（原版 spawner 风格）。
- moon_temple_stairs 的 dropSelf loot table 也因 datagen 此前失败而缺失，本次一并生成。
- urn 从 getKnownBlocks() 排除——它用手写的 `src/main/resources/data/theaurorian/loot_table/blocks/urn.json`（加权废墟掉落），datagen 生成会覆盖它。
- scripts/validate_resources.py：c:shears 检查改为 c:tools/shear（1.21.1 路径）。

## 验证
- compileJava / runData / validate_resources / validateRunestoneLayout 全部通过。

## 搁置项 / 后续
- **~~镰刀剪取行为~~（已修复）**：叶子/草/花的剪取条件现在显式匹配 `minecraft:shears` + 3 把镰刀（`DataGenBlocksLoot` 用实例 `HAS_SHEARS`，并重写 `createSilkTouchOrShearsDispatchTable` / 自建 `leavesDrops`，让树叶的叶子块池与木棍池共用同一条件）。原 `createShearsOnlyDrop`/`createLeavesDrops` 用父类静态 `minecraft:shears`，镰刀不生效；且 tag 在 datagen 时未含 mod 物品，无法用 tag 表达，故显式列物品。
- **~~花草掉落~~（已改）**：按用户要求改为原版式——
  - 花（bright_bulb/petunia/lavender_block/silkberry_block）：`createSingleItemTable` 原样掉落（bright_bulb/petunia/lavender/silkberry），不再要求剪刀。
  - 草（aurorian_tallgrass/_light）：剪刀/镰刀必掉 plant_fiber；否则 12.5% 概率掉 plant_fiber（带时运加成），对齐原版 short_grass 掉小麦种子。
  - 注：lang 里 petunia/silkberry 的 "Obtained with a Sickle or Shears" 与 advancement "Craft a Sickle to harvest Lavender and Silkberries" 现在略过时（花已自然掉落）；镰刀仍用于草（必掉）与树叶（原样采集）。未改 lang/advancement，留待确认。
- `minecraft:portals`：AurorianPortal 已实现 Portal 接口，未加 tag（无必要）。
- `bee_growables`：原版引用 `#crops`，作物已入 crops，自动覆盖。
- `c:tools` 本体经 `#minecraft:swords/axes/...` 已自动覆盖。
