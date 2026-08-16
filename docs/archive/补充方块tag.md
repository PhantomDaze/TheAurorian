# 为方块/物品补充合适 tag

## 背景
- 1.19.2 Forge 项目。tag 通过 DataGen 生成（`DataGenBlocksTags` / `DataGenItemsTags`），运行 `runData` 输出到 `src/generated/resources/data`。
- 已有手写的 `data/forge/tags/items/{ingots,nuggets,storage_blocks}/{aurorian_steel,cerulean,moonstone}.json`，但 DataGen 未生成 storage_blocks（需迁移到 DataGen 统一来源）。
- 现有 DataGen 已覆盖大量 tag，但仍有**遗漏的方块/物品**和**缺失的类别 tag**。

## 方案：在 DataGen 里补全 tag 分配

### A. DataGenBlocksTags 补充
1. **mineable/pickaxe** 缺失：
   - runestone 全系列（runestone, runestone_bars, runestone_smooth, runestone_lamp, runestone_gate, runestone_gate_keyhole, runestone_gate_loot_keyhole）
   - darkstone 全系列（darkstone, darkstone_chipped, darkstone_gate, darkstone_gate_keyhole, darkstone_lamp, darkstone_pillar）
   - moon_temple 全系列（bricks, bricks_smooth, bars, gate, gate_keyhole, interior_gate, interior_gate_keyhole, lamp）
   - aurorian_glass, moon_glass, aurorian_glass_pane, moon_glass_pane（玻璃 → pickaxe，与 vanilla 一致）
   - crystal, moon_gem, mushroom_crystal（玻璃/晶体类）
   - chimney（石头机器）
   - moon_sand 的 stairs/与 peridotite 已有，umbra roof 已有
2. **mineable/axe** 缺失：
   - silentwood_leaves（leaves 用 axe）
   - weeping_willow_leaves
   - silentwood_sapling, weeping_willow_sapling
   - mushroom, mushroom_stem, mushroom_small（木/菌类 → axe）
   - urn（玻璃属性但作为容器/装饰，按 vanilla glass 也归 pickaxe；此处 urn 复制自 GLASS → pickaxe 更合适，见下）
3. **mineable/shovel**：aurorian_grass_light（复制 GRASS_BLOCK，归 shovel）
4. **mineable/hoe**：lavender_crop, silkberry_crop（作物 → hoe，vanilla 标准实践）+ mushroom_small（菌丝，hoe 可加速）
   - 注：lavender_block/petunia/silkberry_block/aurorian_tallgrass/bright_bulb 是 REPLACEABLE_PLANTS，vanilla grass 用 hoe — 但保持与现有做法一致（未加 hoe 也行）。决定：补充 hoe 给 small plants 以对齐 vanilla。
5. **needs_tool**：
   - moonstone_ore / cerulean_ore / deepslate_cerulean_ore / deepslate_moonstone_ore / geode → NEEDS_STONE_TOOL（已有部分）
   - umbra_stone（strength 5）→ NEEDS_STONE_TOOL
   - peridotite（strength 5）→ NEEDS_STONE_TOOL
   - aurorian_steel_block（复制 iron_block）→ NEEDS_STONE_TOOL
   - aurorian_stone_brick（strength 2）→ 无须额外（stone 工具即可，vanilla stone_bricks 无 needs tag）
6. **Forge Tags.Blocks**：
   - `STORAGE_BLOCKS`: aurorian_coal_block, aurorian_steel_block, cerulean_block, moonstone_block
   - `GLASS` / `GLASS_COLORLESS`: aurorian_glass, moon_glass
   - `GLASS_PANES` / `GLASS_PANES_COLORLESS`: aurorian_glass_pane, moon_glass_pane
   - `SAND`: moon_sand
   - `COBBLESTONE` / `COBBLESTONE_NORMAL`: aurorian_cobblestone
   - `COBBLESTONE_DEEPSLATE`: aurorian_deepslate
   - `STONE`: aurorian_stone, peridotite
   - `ORES_IN_GROUND_STONE`: aurorian_coal_ore, cerulean_ore, moonstone_ore, geode
   - `ORES_IN_GROUND_DEEPSLATE`: deepslate_cerulean_ore, deepslate_moonstone_ore
   - `ORE_BEARING_GROUND_STONE`: aurorian_stone
   - `ORE_BEARING_GROUND_DEEPSLATE`: aurorian_deepslate
7. **vanilla BlockTags**：
   - `DRAGON_IMMUNE`: runestone, runestone_bars（strength -1，不可破坏，与 bedrock 类似）— 可选，但符合"地牢核心方块不可破坏"语义。决定：加。
   - `WITHER_IMMUNE`: 同上 runestone（防凋灵破坏地牢墙体）。决定：加 runestone 系列。
   - `FEATURES_CANNOT_REPLACE`: runestone / darkstone / moon_temple_bricks（地牢结构方块不应被世界生成覆盖）— 1.19.2 存在 `BlockTags.FEATURES_CANNOT_REPLACE`。决定：加地牢核心方块。
   - `MUSHROOM_GROW_BLOCK`: mushroom（IndigoMushroomBlock，蘑菇可生长其上）— 决定：加 mushroom, mushroom_stem。
   - `STRIDER_WARM_BLOCKS`? 不相关。
   - `SNOW_CANNOT_SURVIVE_ON`? 不相关。

### B. DataGenItemsTags 补充
1. **Forge Tags.Items.STORAGE_BLOCKS**: 4 个材料块物品 → 替代手写 JSON
2. **Forge Tags.Items.GLASS / GLASS_PANES**: aurorian_glass, moon_glass, aurorian_glass_pane, moon_glass_pane
3. **Forge Tags.Items.SAND**: moon_sand
4. **Forge Tags.Items.COBBLESTONE**: aurorian_cobblestone, aurorian_deepslate
5. **Forge Tags.Items.STONE**: aurorian_stone, peridotite
6. **Forge Tags.Items.SEEDS**: lavender_seeds, silkberry_seeds
7. **Forge Tags.Items.RODS**: silentwood_stick（已加 RODS_WOODEN，再加总称）
8. **Forge Tags.Items.ORES_IN_GROUND_***: 矿石物品
9. **vanilla ItemTags**:
   - `SAPLINGS`（item 端）: silentwood_sapling, weeping_willow_sapling, mushroom_small
   - `LEAVES`: silentwood_leaves, weeping_willow_leaves
   - `FLOWERS`(item): bright_bulb, petunia
   - `SIGNS`? 无。
10. storage_blocks/ingots/nuggets 的 forge 子 tag：已有手写 aurorian_steel/cerulean/moonstone，但 DataGenItemsTags 未生成对应 forge tag。补充 DataGen 生成 → 删除手写 JSON（避免来源不一致）。

### C. 删除手写 JSON，统一由 DataGen 生成
- 删 `data/forge/tags/items/ingots/{aurorian_steel,cerulean,moonstone}.json`
- 删 `data/forge/tags/items/nuggets/{aurorian_steel,cerulean,moonstone}.json`
- 删 `data/forge/tags/items/storage_blocks/{aurorian_steel,cerulean,moonstone}.json`
- 在 DataGenItemsTags 中用 `Tags.Items.INGOTS_IRON` 等子类 tag？不 — 这些是 mod 自定义材料（aurorian_steel/cerulean/moonstone），对应 forge 命名空间下 `forge:ingots/aurorian_steel` 等。需用 `ItemTags.create(new ResourceLocation("forge", "ingots/aurorian_steel"))`。决定：在 DataGenItemsTags 里为这些自定义命名空间 tag 创建 TagKey 并填充。

## 验证
- `export JAVA_HOME=/usr/lib/jvm/21`（1.19.2 实际需 17）。查 ENV。
- 运行 `./gradlew runData` 生成资源。
- 检查 `src/generated/resources/data` 下新 tag 文件是否齐全。
- 编译验证：`./gradlew compileJava`。

## 搁置/待办
- runestone 等 strength(-1) 方块已加 DRAGON_IMMUNE/WITHER_IMMUNE/FEATURES_CANNOT_REPLACE（仅 runestone 系列，不含 darkstone/moon_temple 等可破坏地牢砖），不影响 boss 机制（boss 战不破坏墙体本就是设计意图）。

## 状态：已完成 ✅
- compileJava + runData 全部 BUILD SUCCESSFUL。
- 102 个方块中 92 个有工具 tag；剩余 7 个（aurorian_portal/boss_spawner/fog_wall/lavender_crop/silkberry_crop/moon_torch/silentwood_torch）按 vanilla 语义无需 mineable tag。
- 顺带修复既存冲突：删除 main 手写 `crystalline_sprite.json`（与 DataGen 输出重复）。

