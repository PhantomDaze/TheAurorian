package shiroroku.theaurorian.DataGen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;
import shiroroku.theaurorian.Registry.BlockRegistry;
import shiroroku.theaurorian.TheAurorian;

import java.util.concurrent.CompletableFuture;

public class DataGenBlocksTags extends BlockTagsProvider {

    public static final TagKey<Block> CERULEAN_ORE = BlockTags.create(new ResourceLocation(TheAurorian.MODID, "cerulean_ore"));
    public static final TagKey<Block> DUNGEON_BRICKS = BlockTags.create(new ResourceLocation(TheAurorian.MODID, "dungeon_bricks"));
    public static final TagKey<Block> DUNGEON_GATES = BlockTags.create(new ResourceLocation(TheAurorian.MODID, "dungeon_gates"));
    public static final TagKey<Block> AURORIAN_STONES = BlockTags.create(new ResourceLocation(TheAurorian.MODID, "aurorian_stones"));
    public static final TagKey<Block> MOONSTONE_ORE = BlockTags.create(new ResourceLocation(TheAurorian.MODID, "moonstone_ore"));
    // forge 命名空间下 mod 自定义材料的子 tag（forge 未提供常量，需手动声明）
    public static final TagKey<Block> ORES_CERULEAN = BlockTags.create(new ResourceLocation("forge", "ores/cerulean"));
    public static final TagKey<Block> ORES_MOONSTONE = BlockTags.create(new ResourceLocation("forge", "ores/moonstone"));
    public static final TagKey<Block> STORAGE_BLOCKS_AURORIAN_STEEL = BlockTags.create(new ResourceLocation("forge", "storage_blocks/aurorian_steel"));
    public static final TagKey<Block> STORAGE_BLOCKS_CERULEAN = BlockTags.create(new ResourceLocation("forge", "storage_blocks/cerulean"));
    public static final TagKey<Block> STORAGE_BLOCKS_MOONSTONE = BlockTags.create(new ResourceLocation("forge", "storage_blocks/moonstone"));

    protected DataGenBlocksTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, TheAurorian.MODID, existingFileHelper);
    }

    @Override
protected void addTags(HolderLookup.Provider provider) {
        this.tag(BlockTags.DEEPSLATE_ORE_REPLACEABLES).add(BlockRegistry.aurorian_deepslate.get());
        this.tag(BlockTags.DIRT).add(BlockRegistry.aurorian_dirt.get());
        this.tag(BlockTags.DIRT).add(BlockRegistry.aurorian_grass.get());
        this.tag(BlockTags.DIRT).add(BlockRegistry.aurorian_grass_light.get());
        this.tag(BlockTags.FLOWERS).add(BlockRegistry.bright_bulb.get());
        this.tag(BlockTags.FLOWERS).add(BlockRegistry.petunia.get());
        this.tag(BlockTags.FLOWERS).add(BlockRegistry.lavender_block.get());
        // 小型花：便于蜜蜂采集、剑高效、花盆等判定
        this.tag(BlockTags.SMALL_FLOWERS).add(BlockRegistry.petunia.get());
        this.tag(BlockTags.SMALL_FLOWERS).add(BlockRegistry.lavender_block.get());
        this.tag(BlockTags.LEAVES).add(BlockRegistry.silentwood_leaves.get());
        this.tag(BlockTags.LEAVES).add(BlockRegistry.weeping_willow_leaves.get());
        this.tag(BlockTags.LOGS_THAT_BURN).add(BlockRegistry.silentwood_log.get());
        this.tag(BlockTags.LOGS_THAT_BURN).add(BlockRegistry.weeping_willow_log.get());
        // logs 父标签：让 logs_that_burn 也归入 logs（影响掉落、配方、不能当燃料的判定等）
        this.tag(BlockTags.LOGS).addTag(BlockTags.LOGS_THAT_BURN);
        this.tag(BlockTags.MINEABLE_WITH_AXE).add(BlockRegistry.silentwood_chest.get());
        this.tag(BlockTags.MINEABLE_WITH_AXE).add(BlockRegistry.silentwood_crafting_table.get());
        this.tag(BlockTags.MINEABLE_WITH_AXE).add(BlockRegistry.silentwood_fence.get());
        this.tag(BlockTags.MINEABLE_WITH_AXE).add(BlockRegistry.silentwood_log.get());
        this.tag(BlockTags.MINEABLE_WITH_AXE).add(BlockRegistry.silentwood_planks.get());
        this.tag(BlockTags.MINEABLE_WITH_AXE).add(BlockRegistry.silentwood_slab.get());
        this.tag(BlockTags.MINEABLE_WITH_AXE).add(BlockRegistry.silentwood_stairs.get());
        this.tag(BlockTags.MINEABLE_WITH_AXE).add(BlockRegistry.silentwood_ladder.get());
        this.tag(BlockTags.MINEABLE_WITH_AXE).add(BlockRegistry.silentwood_sapling.get());
        this.tag(BlockTags.MINEABLE_WITH_AXE).add(BlockRegistry.silentwood_torch.get());
        this.tag(BlockTags.CLIMBABLE).add(BlockRegistry.silentwood_ladder.get());
        this.tag(BlockTags.MINEABLE_WITH_AXE).add(BlockRegistry.weeping_willow_log.get());
        this.tag(BlockTags.MINEABLE_WITH_AXE).add(BlockRegistry.weeping_willow_planks.get());
        this.tag(BlockTags.MINEABLE_WITH_AXE).add(BlockRegistry.weeping_willow_stairs.get());
        this.tag(BlockTags.MINEABLE_WITH_AXE).add(BlockRegistry.weeping_willow_sapling.get());
        // 蘑菇方块（与原版 brown/red_mushroom、mushroom_stem 一致，用斧）
        this.tag(BlockTags.MINEABLE_WITH_AXE).add(BlockRegistry.mushroom_small.get());
        this.tag(BlockTags.MINEABLE_WITH_AXE).add(BlockRegistry.mushroom.get());
        this.tag(BlockTags.MINEABLE_WITH_AXE).add(BlockRegistry.mushroom_stem.get());
        // 树叶用锄更高效（与原版一致）
        this.tag(BlockTags.MINEABLE_WITH_HOE).add(BlockRegistry.silentwood_leaves.get());
        this.tag(BlockTags.MINEABLE_WITH_HOE).add(BlockRegistry.weeping_willow_leaves.get());
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
        // 水晶/装饰玻璃类也用镐
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BlockRegistry.crystal.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BlockRegistry.mushroom_crystal.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BlockRegistry.urn.get());
        this.tag(BlockTags.MINEABLE_WITH_SHOVEL).add(BlockRegistry.aurorian_dirt.get());
        this.tag(BlockTags.MINEABLE_WITH_SHOVEL).add(BlockRegistry.aurorian_grass.get());
        this.tag(BlockTags.MINEABLE_WITH_SHOVEL).add(BlockRegistry.aurorian_grass_light.get());
        this.tag(BlockTags.MINEABLE_WITH_SHOVEL).add(BlockRegistry.aurorian_farm_tile.get());
        this.tag(BlockTags.MINEABLE_WITH_SHOVEL).add(BlockRegistry.moon_sand.get());
        this.tag(BlockTags.NEEDS_STONE_TOOL).add(BlockRegistry.cerulean_ore.get());
        this.tag(BlockTags.NEEDS_STONE_TOOL).add(BlockRegistry.deepslate_cerulean_ore.get());
        this.tag(BlockTags.NEEDS_STONE_TOOL).add(BlockRegistry.geode.get());
        this.tag(BlockTags.NEEDS_STONE_TOOL).add(BlockRegistry.moonstone_ore.get());
        this.tag(BlockTags.NEEDS_STONE_TOOL).add(BlockRegistry.deepslate_moonstone_ore.get());
        this.tag(BlockTags.PLANKS).add(BlockRegistry.silentwood_planks.get());
        this.tag(BlockTags.PLANKS).add(BlockRegistry.weeping_willow_planks.get());
        this.tag(BlockTags.REPLACEABLE).add(BlockRegistry.aurorian_tallgrass.get(), BlockRegistry.aurorian_tallgrass_light.get(), BlockRegistry.bright_bulb.get(), BlockRegistry.lavender_block.get(), BlockRegistry.petunia.get(), BlockRegistry.silkberry_block.get());
        this.tag(BlockTags.SAND).add(BlockRegistry.moon_sand.get());
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
        this.tag(BlockTags.STAIRS).add(BlockRegistry.darkstone_stairs.get());
        this.tag(BlockTags.STAIRS).add(BlockRegistry.moon_temple_stairs.get());
        this.tag(BlockTags.STAIRS).add(BlockRegistry.umbra_stone_roof_stairs.get());
        this.tag(BlockTags.STONE_ORE_REPLACEABLES).add(BlockRegistry.aurorian_stone.get());
        this.tag(BlockTags.WALLS).add(BlockRegistry.aurorian_cobblestone_wall.get());
        this.tag(BlockTags.WALLS).add(BlockRegistry.aurorian_deepslate_wall.get());
        this.tag(BlockTags.WOODEN_FENCES).add(BlockRegistry.silentwood_fence.get());
        this.tag(BlockTags.FENCES).add(BlockRegistry.silentwood_fence.get());
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
        this.tag(Tags.Blocks.ORES).addTag(ORES_CERULEAN);
        this.tag(Tags.Blocks.ORES).addTag(ORES_MOONSTONE);
        this.tag(Tags.Blocks.ORES_COAL).add(BlockRegistry.aurorian_coal_ore.get());
        // 玻璃类（forge:glass / forge:glass_panes，便于其它 mod 配方识别）
        this.tag(Tags.Blocks.GLASS).add(BlockRegistry.aurorian_glass.get(), BlockRegistry.moon_glass.get());
        this.tag(Tags.Blocks.GLASS_PANES).add(BlockRegistry.aurorian_glass_pane.get(), BlockRegistry.moon_glass_pane.get());
        // 砂（forge:sand）
        this.tag(Tags.Blocks.SAND).add(BlockRegistry.moon_sand.get());
        // 矿石子标签 + 产出地层
        this.tag(ORES_CERULEAN).add(BlockRegistry.cerulean_ore.get(), BlockRegistry.deepslate_cerulean_ore.get());
        this.tag(ORES_MOONSTONE).add(BlockRegistry.moonstone_ore.get(), BlockRegistry.deepslate_moonstone_ore.get());
        this.tag(Tags.Blocks.ORES_IN_GROUND_STONE).add(BlockRegistry.aurorian_coal_ore.get(), BlockRegistry.cerulean_ore.get(), BlockRegistry.moonstone_ore.get());
        this.tag(Tags.Blocks.ORES_IN_GROUND_DEEPSLATE).add(BlockRegistry.deepslate_cerulean_ore.get(), BlockRegistry.deepslate_moonstone_ore.get());
        // 存储块子标签 + 父标签引用
        this.tag(STORAGE_BLOCKS_AURORIAN_STEEL).add(BlockRegistry.aurorian_steel_block.get());
        this.tag(STORAGE_BLOCKS_CERULEAN).add(BlockRegistry.cerulean_block.get());
        this.tag(STORAGE_BLOCKS_MOONSTONE).add(BlockRegistry.moonstone_block.get());
        this.tag(Tags.Blocks.STORAGE_BLOCKS).addTags(STORAGE_BLOCKS_AURORIAN_STEEL, STORAGE_BLOCKS_CERULEAN, STORAGE_BLOCKS_MOONSTONE);
        this.tag(Tags.Blocks.STORAGE_BLOCKS_COAL).add(BlockRegistry.aurorian_coal_block.get());
        this.tag(Tags.Blocks.STORAGE_BLOCKS).addTag(Tags.Blocks.STORAGE_BLOCKS_COAL);
        // 圆石（forge:cobblestone/normal，便于合成等识别）
        this.tag(Tags.Blocks.COBBLESTONE_NORMAL).add(BlockRegistry.aurorian_cobblestone.get());
    }
}
