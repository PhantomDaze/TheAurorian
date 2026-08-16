package shiroroku.theaurorian.DataGen;

import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;
import shiroroku.theaurorian.Registry.BlockRegistry;
import shiroroku.theaurorian.TheAurorian;

public class DataGenBlocksTags extends TagsProvider<Block> {

    public static final TagKey<Block> CERULEAN_ORE = BlockTags.create(new ResourceLocation(TheAurorian.MODID, "cerulean_ore"));
    public static final TagKey<Block> DUNGEON_BRICKS = BlockTags.create(new ResourceLocation(TheAurorian.MODID, "dungeon_bricks"));
    public static final TagKey<Block> DUNGEON_GATES = BlockTags.create(new ResourceLocation(TheAurorian.MODID, "dungeon_gates"));
    public static final TagKey<Block> AURORIAN_STONES = BlockTags.create(new ResourceLocation(TheAurorian.MODID, "aurorian_stones"));
    public static final TagKey<Block> MOONSTONE_ORE = BlockTags.create(new ResourceLocation(TheAurorian.MODID, "moonstone_ore"));

    @SuppressWarnings("deprecation")
    protected DataGenBlocksTags(DataGenerator pGenerator, @Nullable ExistingFileHelper existingFileHelper) {
        super(pGenerator, Registry.BLOCK, TheAurorian.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        this.tag(BlockTags.DEEPSLATE_ORE_REPLACEABLES).add(BlockRegistry.aurorian_deepslate.get());
        this.tag(BlockTags.DIRT).add(BlockRegistry.aurorian_dirt.get());
        this.tag(BlockTags.DIRT).add(BlockRegistry.aurorian_grass.get());
        // 地牢核心方块不可被世界生成覆盖、不可被龙/凋灵破坏
        this.tag(BlockTags.FEATURES_CANNOT_REPLACE).add(
                BlockRegistry.runestone.get(),
                BlockRegistry.runestone_smooth.get(),
                BlockRegistry.runestone_bars.get(),
                BlockRegistry.darkstone.get(),
                BlockRegistry.moon_temple_bricks.get(),
                BlockRegistry.moon_temple_bricks_smooth.get(),
                BlockRegistry.moon_temple_bars.get(),
                BlockRegistry.umbra_stone.get());
        this.tag(BlockTags.DRAGON_IMMUNE).add(
                BlockRegistry.runestone.get(),
                BlockRegistry.runestone_smooth.get(),
                BlockRegistry.runestone_bars.get());
        this.tag(BlockTags.WITHER_IMMUNE).add(
                BlockRegistry.runestone.get(),
                BlockRegistry.runestone_smooth.get(),
                BlockRegistry.runestone_bars.get());
        this.tag(BlockTags.FLOWERS).add(BlockRegistry.bright_bulb.get());
        this.tag(BlockTags.FLOWERS).add(BlockRegistry.petunia.get());
        this.tag(BlockTags.LEAVES).add(BlockRegistry.silentwood_leaves.get());
        this.tag(BlockTags.LEAVES).add(BlockRegistry.weeping_willow_leaves.get());
        this.tag(BlockTags.LOGS_THAT_BURN).add(BlockRegistry.silentwood_log.get());
        this.tag(BlockTags.LOGS_THAT_BURN).add(BlockRegistry.weeping_willow_log.get());
        // 蘑菇可种植其上
        this.tag(BlockTags.MUSHROOM_GROW_BLOCK).add(BlockRegistry.mushroom.get(), BlockRegistry.mushroom_stem.get());
        this.tag(BlockTags.MINEABLE_WITH_AXE).add(BlockRegistry.silentwood_chest.get());
        this.tag(BlockTags.MINEABLE_WITH_AXE).add(BlockRegistry.silentwood_crafting_table.get());
        this.tag(BlockTags.MINEABLE_WITH_AXE).add(BlockRegistry.silentwood_fence.get());
        this.tag(BlockTags.MINEABLE_WITH_AXE).add(BlockRegistry.silentwood_log.get());
        this.tag(BlockTags.MINEABLE_WITH_AXE).add(BlockRegistry.silentwood_planks.get());
        this.tag(BlockTags.MINEABLE_WITH_AXE).add(BlockRegistry.silentwood_slab.get());
        this.tag(BlockTags.MINEABLE_WITH_AXE).add(BlockRegistry.silentwood_stairs.get());
        this.tag(BlockTags.MINEABLE_WITH_AXE).add(BlockRegistry.silentwood_ladder.get());
        this.tag(BlockTags.MINEABLE_WITH_AXE).add(BlockRegistry.silentwood_leaves.get());
        this.tag(BlockTags.MINEABLE_WITH_AXE).add(BlockRegistry.silentwood_sapling.get());
        this.tag(BlockTags.MINEABLE_WITH_AXE).add(BlockRegistry.weeping_willow_leaves.get());
        this.tag(BlockTags.MINEABLE_WITH_AXE).add(BlockRegistry.weeping_willow_sapling.get());
        // 菌类方块按木头/菌柄处理，用斧加速
        this.tag(BlockTags.MINEABLE_WITH_AXE).add(BlockRegistry.mushroom.get());
        this.tag(BlockTags.MINEABLE_WITH_AXE).add(BlockRegistry.mushroom_stem.get());
        this.tag(BlockTags.MINEABLE_WITH_AXE).add(BlockRegistry.mushroom_small.get());
        this.tag(BlockTags.MINEABLE_WITH_HOE).add(BlockRegistry.mushroom_small.get());
        this.tag(BlockTags.MINEABLE_WITH_HOE).add(
                BlockRegistry.aurorian_tallgrass.get(),
                BlockRegistry.aurorian_tallgrass_light.get(),
                BlockRegistry.bright_bulb.get(),
                BlockRegistry.lavender_block.get(),
                BlockRegistry.petunia.get(),
                BlockRegistry.silkberry_block.get());
        this.tag(BlockTags.MINEABLE_WITH_HOE).add(BlockRegistry.silentwood_leaves.get());
        this.tag(BlockTags.MINEABLE_WITH_HOE).add(BlockRegistry.weeping_willow_leaves.get());
        this.tag(BlockTags.CLIMBABLE).add(BlockRegistry.silentwood_ladder.get());
        this.tag(BlockTags.MINEABLE_WITH_AXE).add(BlockRegistry.weeping_willow_log.get());
        this.tag(BlockTags.MINEABLE_WITH_AXE).add(BlockRegistry.weeping_willow_planks.get());
        this.tag(BlockTags.MINEABLE_WITH_AXE).add(BlockRegistry.weeping_willow_stairs.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BlockRegistry.aurorian_coal_block.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BlockRegistry.aurorian_coal_ore.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BlockRegistry.aurorian_steel_block.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BlockRegistry.aurorian_stone_brick.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BlockRegistry.aurorian_stone_brick_stairs.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BlockRegistry.aurorian_stone_stairs.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BlockRegistry.aurorian_grass_light.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BlockRegistry.cerulean_block.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BlockRegistry.moonstone_block.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BlockRegistry.peridotite.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BlockRegistry.peridotite_smooth.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BlockRegistry.peridotite_smooth_stairs.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BlockRegistry.umbra_stone.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BlockRegistry.umbra_stone_cracked.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BlockRegistry.umbra_stone_roof_tiles.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BlockRegistry.umbra_stone_roof_stairs.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BlockRegistry.aurorian_cobblestone.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BlockRegistry.aurorian_cobblestone_slab.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BlockRegistry.aurorian_cobblestone_stairs.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BlockRegistry.aurorian_cobblestone_wall.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BlockRegistry.aurorian_deepslate.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BlockRegistry.aurorian_deepslate_slab.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BlockRegistry.aurorian_deepslate_stairs.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BlockRegistry.aurorian_deepslate_wall.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BlockRegistry.aurorian_furnace.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BlockRegistry.aurorian_portal_frame.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BlockRegistry.aurorian_stone.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BlockRegistry.cerulean_ore.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BlockRegistry.chimney.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BlockRegistry.deepslate_cerulean_ore.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BlockRegistry.deepslate_moonstone_ore.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BlockRegistry.geode.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BlockRegistry.moon_gem.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BlockRegistry.moonlight_forge.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BlockRegistry.moonstone_ore.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BlockRegistry.scrapper.get());
        // Runestone 系列（不可破坏墙体，但仍归镐）
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(
                BlockRegistry.runestone.get(),
                BlockRegistry.runestone_smooth.get(),
                BlockRegistry.runestone_bars.get(),
                BlockRegistry.runestone_gate.get(),
                BlockRegistry.runestone_gate_keyhole.get(),
                BlockRegistry.runestone_gate_loot_keyhole.get(),
                BlockRegistry.runestone_lamp.get(),
                BlockRegistry.runestone_stairs.get());
        // Darkstone 系列
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(
                BlockRegistry.darkstone.get(),
                BlockRegistry.darkstone_chipped.get(),
                BlockRegistry.darkstone_gate.get(),
                BlockRegistry.darkstone_gate_keyhole.get(),
                BlockRegistry.darkstone_lamp.get(),
                BlockRegistry.darkstone_pillar.get(),
                BlockRegistry.darkstone_stairs.get());
        // Moon Temple 系列
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(
                BlockRegistry.moon_temple_bricks.get(),
                BlockRegistry.moon_temple_bricks_smooth.get(),
                BlockRegistry.moon_temple_bars.get(),
                BlockRegistry.moon_temple_gate.get(),
                BlockRegistry.moon_temple_gate_keyhole.get(),
                BlockRegistry.moon_temple_interior_gate.get(),
                BlockRegistry.moon_temple_interior_gate_keyhole.get(),
                BlockRegistry.moon_temple_lamp.get(),
                BlockRegistry.moon_temple_stairs.get());
        // 玻璃/玻璃板（vanilla 玻璃归镐）
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(
                BlockRegistry.aurorian_glass.get(),
                BlockRegistry.moon_glass.get(),
                BlockRegistry.aurorian_glass_pane.get(),
                BlockRegistry.moon_glass_pane.get());
        // 晶体/宝石/晶体蘑菇
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(
                BlockRegistry.crystal.get(),
                BlockRegistry.mushroom_crystal.get());
        // 陶罐（玻璃属性，归镐）
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BlockRegistry.urn.get());
        this.tag(BlockTags.MINEABLE_WITH_SHOVEL).add(BlockRegistry.aurorian_dirt.get());
        this.tag(BlockTags.MINEABLE_WITH_SHOVEL).add(BlockRegistry.aurorian_grass.get());
        this.tag(BlockTags.MINEABLE_WITH_SHOVEL).add(BlockRegistry.aurorian_farm_tile.get());
        this.tag(BlockTags.MINEABLE_WITH_SHOVEL).add(BlockRegistry.moon_sand.get());
        // 草地方块（复制自 GRASS_BLOCK）归铲
        this.tag(BlockTags.MINEABLE_WITH_SHOVEL).add(BlockRegistry.aurorian_grass_light.get());
        this.tag(BlockTags.NEEDS_STONE_TOOL).add(BlockRegistry.cerulean_ore.get());
        this.tag(BlockTags.NEEDS_STONE_TOOL).add(BlockRegistry.geode.get());
        this.tag(BlockTags.NEEDS_STONE_TOOL).add(BlockRegistry.moonstone_ore.get());
        this.tag(BlockTags.NEEDS_STONE_TOOL).add(BlockRegistry.deepslate_cerulean_ore.get());
        this.tag(BlockTags.NEEDS_STONE_TOOL).add(BlockRegistry.deepslate_moonstone_ore.get());
        // 高强度石质方块（strength 5）需石镐
        this.tag(BlockTags.NEEDS_STONE_TOOL).add(BlockRegistry.umbra_stone.get());
        this.tag(BlockTags.NEEDS_STONE_TOOL).add(BlockRegistry.peridotite.get());
        // 钢块复制自铁块，需石镐
        this.tag(BlockTags.NEEDS_STONE_TOOL).add(BlockRegistry.aurorian_steel_block.get());
        this.tag(BlockTags.CROPS).add(BlockRegistry.lavender_crop.get(), BlockRegistry.silkberry_crop.get());
        this.tag(BlockTags.PLANKS).add(BlockRegistry.silentwood_planks.get());
        this.tag(BlockTags.PLANKS).add(BlockRegistry.weeping_willow_planks.get());
        this.tag(BlockTags.REPLACEABLE_PLANTS).add(
                BlockRegistry.aurorian_tallgrass.get(),
                BlockRegistry.aurorian_tallgrass_light.get(),
                BlockRegistry.bright_bulb.get(),
                BlockRegistry.lavender_block.get(),
                BlockRegistry.petunia.get(),
                BlockRegistry.silkberry_block.get());
        this.tag(BlockTags.SAPLINGS).add(BlockRegistry.silentwood_sapling.get());
        this.tag(BlockTags.SAPLINGS).add(BlockRegistry.weeping_willow_sapling.get());
        this.tag(BlockTags.SLABS).add(BlockRegistry.aurorian_cobblestone_slab.get());
        this.tag(BlockTags.SLABS).add(BlockRegistry.aurorian_deepslate_slab.get());
        this.tag(BlockTags.STAIRS).add(BlockRegistry.aurorian_cobblestone_stairs.get());
        this.tag(BlockTags.STAIRS).add(BlockRegistry.aurorian_deepslate_stairs.get());
        this.tag(AURORIAN_STONES).add(BlockRegistry.aurorian_stone.get());
        this.tag(AURORIAN_STONES).add(BlockRegistry.aurorian_cobblestone.get());
        this.tag(AURORIAN_STONES).add(BlockRegistry.aurorian_deepslate.get());
        this.tag(AURORIAN_STONES).add(BlockRegistry.peridotite.get());
        this.tag(AURORIAN_STONES).add(BlockRegistry.peridotite_smooth.get());
        this.tag(BlockTags.STAIRS).add(BlockRegistry.aurorian_stone_brick_stairs.get());
        this.tag(BlockTags.STAIRS).add(BlockRegistry.aurorian_stone_stairs.get());
        this.tag(BlockTags.STAIRS).add(BlockRegistry.peridotite_smooth_stairs.get());
        this.tag(BlockTags.STAIRS).add(BlockRegistry.runestone_stairs.get());
        this.tag(BlockTags.STAIRS).add(BlockRegistry.umbra_stone_roof_stairs.get());
        this.tag(BlockTags.STONE_ORE_REPLACEABLES).add(BlockRegistry.aurorian_stone.get());
        this.tag(BlockTags.WALLS).add(BlockRegistry.aurorian_cobblestone_wall.get());
        this.tag(BlockTags.WALLS).add(BlockRegistry.aurorian_deepslate_wall.get());
        this.tag(BlockTags.WOODEN_FENCES).add(BlockRegistry.silentwood_fence.get());
        this.tag(BlockTags.WOODEN_SLABS).add(BlockRegistry.silentwood_slab.get());
        this.tag(BlockTags.WOODEN_STAIRS).add(BlockRegistry.silentwood_stairs.get());
        this.tag(BlockTags.WOODEN_STAIRS).add(BlockRegistry.weeping_willow_stairs.get());
        this.tag(CERULEAN_ORE).add(BlockRegistry.cerulean_ore.get());
        this.tag(CERULEAN_ORE).add(BlockRegistry.deepslate_cerulean_ore.get());
        this.tag(DUNGEON_BRICKS).add(BlockRegistry.darkstone.get());
        this.tag(DUNGEON_BRICKS).add(BlockRegistry.darkstone_chipped.get());
        this.tag(DUNGEON_BRICKS).add(BlockRegistry.darkstone_gate.get());
        this.tag(DUNGEON_BRICKS).add(BlockRegistry.darkstone_gate_keyhole.get());
        this.tag(DUNGEON_BRICKS).add(BlockRegistry.darkstone_lamp.get());
        this.tag(DUNGEON_BRICKS).add(BlockRegistry.darkstone_pillar.get());
        this.tag(DUNGEON_BRICKS).add(BlockRegistry.darkstone_stairs.get());
        this.tag(DUNGEON_BRICKS).add(BlockRegistry.moon_temple_bars.get());
        this.tag(DUNGEON_BRICKS).add(BlockRegistry.umbra_stone.get());
        this.tag(DUNGEON_BRICKS).add(BlockRegistry.umbra_stone_cracked.get());
        this.tag(DUNGEON_BRICKS).add(BlockRegistry.umbra_stone_roof_tiles.get());
        this.tag(DUNGEON_BRICKS).add(BlockRegistry.umbra_stone_roof_stairs.get());
        this.tag(DUNGEON_BRICKS).add(BlockRegistry.moon_temple_bricks.get());
        this.tag(DUNGEON_BRICKS).add(BlockRegistry.moon_temple_bricks_smooth.get());
        this.tag(DUNGEON_BRICKS).add(BlockRegistry.moon_temple_gate.get());
        this.tag(DUNGEON_BRICKS).add(BlockRegistry.moon_temple_gate_keyhole.get());
        this.tag(DUNGEON_BRICKS).add(BlockRegistry.moon_temple_interior_gate.get());
        this.tag(DUNGEON_BRICKS).add(BlockRegistry.moon_temple_interior_gate_keyhole.get());
        this.tag(DUNGEON_BRICKS).add(BlockRegistry.moon_temple_lamp.get());
        this.tag(DUNGEON_BRICKS).add(BlockRegistry.runestone.get());
        this.tag(DUNGEON_BRICKS).add(BlockRegistry.runestone_bars.get());
        this.tag(DUNGEON_BRICKS).add(BlockRegistry.runestone_gate.get());
        this.tag(DUNGEON_BRICKS).add(BlockRegistry.runestone_gate_keyhole.get());
        this.tag(DUNGEON_BRICKS).add(BlockRegistry.runestone_gate_loot_keyhole.get());
        this.tag(DUNGEON_BRICKS).add(BlockRegistry.runestone_lamp.get());
        this.tag(DUNGEON_BRICKS).add(BlockRegistry.runestone_smooth.get());
        this.tag(DUNGEON_BRICKS).add(BlockRegistry.runestone_stairs.get());
        this.tag(DUNGEON_GATES).add(BlockRegistry.darkstone_gate.get());
        this.tag(DUNGEON_GATES).add(BlockRegistry.moon_temple_gate.get());
        this.tag(DUNGEON_GATES).add(BlockRegistry.moon_temple_interior_gate.get());
        this.tag(DUNGEON_GATES).add(BlockRegistry.runestone_gate.get());
        this.tag(MOONSTONE_ORE).add(BlockRegistry.deepslate_moonstone_ore.get());
        this.tag(MOONSTONE_ORE).add(BlockRegistry.moonstone_ore.get());
        this.tag(Tags.Blocks.CHESTS).add(BlockRegistry.silentwood_chest.get());
        this.tag(Tags.Blocks.CHESTS_WOODEN).add(BlockRegistry.silentwood_chest.get());
        this.tag(Tags.Blocks.FENCES_WOODEN).add(BlockRegistry.silentwood_fence.get());
        this.tag(Tags.Blocks.ORES).add(BlockRegistry.aurorian_coal_ore.get());
        this.tag(Tags.Blocks.ORES).add(BlockRegistry.geode.get());
        this.tag(Tags.Blocks.ORES).addTag(CERULEAN_ORE);
        this.tag(Tags.Blocks.ORES).addTag(MOONSTONE_ORE);
        this.tag(Tags.Blocks.ORES_COAL).add(BlockRegistry.aurorian_coal_ore.get());
        // 矿石产出地层（与原版铁/煤矿同义）
        this.tag(Tags.Blocks.ORE_BEARING_GROUND_STONE).add(BlockRegistry.aurorian_stone.get());
        this.tag(Tags.Blocks.ORE_BEARING_GROUND_DEEPSLATE).add(BlockRegistry.aurorian_deepslate.get());
        this.tag(Tags.Blocks.ORES_IN_GROUND_STONE).add(
                BlockRegistry.aurorian_coal_ore.get(),
                BlockRegistry.cerulean_ore.get(),
                BlockRegistry.moonstone_ore.get(),
                BlockRegistry.geode.get());
        this.tag(Tags.Blocks.ORES_IN_GROUND_DEEPSLATE).add(
                BlockRegistry.deepslate_cerulean_ore.get(),
                BlockRegistry.deepslate_moonstone_ore.get());
        // 矿石产出密度
        this.tag(Tags.Blocks.ORE_RATES_SINGULAR).add(
                BlockRegistry.cerulean_ore.get(),
                BlockRegistry.deepslate_cerulean_ore.get(),
                BlockRegistry.moonstone_ore.get(),
                BlockRegistry.deepslate_moonstone_ore.get());
        // 石质类别
        this.tag(Tags.Blocks.STONE).add(BlockRegistry.aurorian_stone.get(), BlockRegistry.peridotite.get());
        this.tag(Tags.Blocks.COBBLESTONE).add(BlockRegistry.aurorian_cobblestone.get());
        this.tag(Tags.Blocks.COBBLESTONE_NORMAL).add(BlockRegistry.aurorian_cobblestone.get());
        this.tag(Tags.Blocks.COBBLESTONE_DEEPSLATE).add(BlockRegistry.aurorian_deepslate.get());
        // 沙子
        this.tag(Tags.Blocks.SAND).add(BlockRegistry.moon_sand.get());
        // 玻璃/玻璃板
        this.tag(Tags.Blocks.GLASS).add(BlockRegistry.aurorian_glass.get(), BlockRegistry.moon_glass.get());
        this.tag(Tags.Blocks.GLASS_COLORLESS).add(BlockRegistry.aurorian_glass.get(), BlockRegistry.moon_glass.get());
        this.tag(Tags.Blocks.GLASS_PANES).add(BlockRegistry.aurorian_glass_pane.get(), BlockRegistry.moon_glass_pane.get());
        this.tag(Tags.Blocks.GLASS_PANES_COLORLESS).add(BlockRegistry.aurorian_glass_pane.get(), BlockRegistry.moon_glass_pane.get());
        // 储物方块
        this.tag(Tags.Blocks.STORAGE_BLOCKS).add(
                BlockRegistry.aurorian_coal_block.get(),
                BlockRegistry.aurorian_steel_block.get(),
                BlockRegistry.cerulean_block.get(),
                BlockRegistry.moonstone_block.get());
        this.tag(Tags.Blocks.STORAGE_BLOCKS_COAL).add(BlockRegistry.aurorian_coal_block.get());
        this.tag(Tags.Blocks.STORAGE_BLOCKS_IRON).add(BlockRegistry.aurorian_steel_block.get());
    }
}
