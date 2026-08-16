package shiroroku.theaurorian.DataGen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;
import shiroroku.theaurorian.Registry.BlockRegistry;
import shiroroku.theaurorian.TheAurorian;

import java.util.concurrent.CompletableFuture;

public class DataGenBlocksTags extends BlockTagsProvider {

    public static final TagKey<Block> CERULEAN_ORE = BlockTags.create(ResourceLocation.fromNamespaceAndPath(TheAurorian.MODID, "cerulean_ore"));
    public static final TagKey<Block> DUNGEON_BRICKS = BlockTags.create(ResourceLocation.fromNamespaceAndPath(TheAurorian.MODID, "dungeon_bricks"));
    public static final TagKey<Block> DUNGEON_GATES = BlockTags.create(ResourceLocation.fromNamespaceAndPath(TheAurorian.MODID, "dungeon_gates"));
    public static final TagKey<Block> AURORIAN_STONES = BlockTags.create(ResourceLocation.fromNamespaceAndPath(TheAurorian.MODID, "aurorian_stones"));
    public static final TagKey<Block> MOONSTONE_ORE = BlockTags.create(ResourceLocation.fromNamespaceAndPath(TheAurorian.MODID, "moonstone_ore"));

    protected DataGenBlocksTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, TheAurorian.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        // --- Worldgen replaceables ---
        this.tag(BlockTags.DEEPSLATE_ORE_REPLACEABLES).add(BlockRegistry.aurorian_deepslate.get());
        this.tag(BlockTags.STONE_ORE_REPLACEABLES).add(BlockRegistry.aurorian_stone.get());

        // --- Ground / dirt-like ---
        this.tag(BlockTags.DIRT).add(BlockRegistry.aurorian_dirt.get());
        this.tag(BlockTags.DIRT).add(BlockRegistry.aurorian_grass.get());
        this.tag(BlockTags.DIRT).add(BlockRegistry.aurorian_grass_light.get());
        this.tag(BlockTags.SAND).add(BlockRegistry.moon_sand.get());
        this.tag(BlockTags.SMELTS_TO_GLASS).add(BlockRegistry.moon_sand.get());
        this.tag(Tags.Blocks.SANDS).add(BlockRegistry.moon_sand.get());
        this.tag(Tags.Blocks.SANDS_COLORLESS).add(BlockRegistry.moon_sand.get());

        // --- Trees: leaves / logs / planks / saplings / wood derivatives ---
        this.tag(BlockTags.LEAVES).add(BlockRegistry.silentwood_leaves.get());
        this.tag(BlockTags.LEAVES).add(BlockRegistry.weeping_willow_leaves.get());
        this.tag(BlockTags.LOGS_THAT_BURN).add(BlockRegistry.silentwood_log.get());
        this.tag(BlockTags.LOGS_THAT_BURN).add(BlockRegistry.weeping_willow_log.get());
        this.tag(BlockTags.PLANKS).add(BlockRegistry.silentwood_planks.get());
        this.tag(BlockTags.PLANKS).add(BlockRegistry.weeping_willow_planks.get());
        this.tag(BlockTags.SAPLINGS).add(BlockRegistry.silentwood_sapling.get());
        this.tag(BlockTags.SAPLINGS).add(BlockRegistry.weeping_willow_sapling.get());
        this.tag(BlockTags.WOODEN_FENCES).add(BlockRegistry.silentwood_fence.get());
        this.tag(BlockTags.FENCES).add(BlockRegistry.silentwood_fence.get());
        this.tag(BlockTags.WOODEN_SLABS).add(BlockRegistry.silentwood_slab.get());
        this.tag(BlockTags.WOODEN_STAIRS).add(BlockRegistry.silentwood_stairs.get());
        this.tag(BlockTags.WOODEN_STAIRS).add(BlockRegistry.weeping_willow_stairs.get());

        // --- Mineable: axe (wood, mushroom blocks) ---
        this.tag(BlockTags.MINEABLE_WITH_AXE).add(BlockRegistry.silentwood_chest.get());
        this.tag(BlockTags.MINEABLE_WITH_AXE).add(BlockRegistry.silentwood_crafting_table.get());
        this.tag(BlockTags.MINEABLE_WITH_AXE).add(BlockRegistry.silentwood_fence.get());
        this.tag(BlockTags.MINEABLE_WITH_AXE).add(BlockRegistry.silentwood_log.get());
        this.tag(BlockTags.MINEABLE_WITH_AXE).add(BlockRegistry.silentwood_planks.get());
        this.tag(BlockTags.MINEABLE_WITH_AXE).add(BlockRegistry.silentwood_slab.get());
        this.tag(BlockTags.MINEABLE_WITH_AXE).add(BlockRegistry.silentwood_stairs.get());
        this.tag(BlockTags.MINEABLE_WITH_AXE).add(BlockRegistry.silentwood_ladder.get());
        this.tag(BlockTags.MINEABLE_WITH_AXE).add(BlockRegistry.weeping_willow_log.get());
        this.tag(BlockTags.MINEABLE_WITH_AXE).add(BlockRegistry.weeping_willow_planks.get());
        this.tag(BlockTags.MINEABLE_WITH_AXE).add(BlockRegistry.weeping_willow_stairs.get());
        this.tag(BlockTags.MINEABLE_WITH_AXE).add(BlockRegistry.mushroom.get());
        this.tag(BlockTags.MINEABLE_WITH_AXE).add(BlockRegistry.mushroom_stem.get());
        this.tag(BlockTags.MINEABLE_WITH_AXE).add(BlockRegistry.mushroom_small.get());

        // --- Mineable: hoe (leaves) ---
        this.tag(BlockTags.MINEABLE_WITH_HOE).add(BlockRegistry.silentwood_leaves.get());
        this.tag(BlockTags.MINEABLE_WITH_HOE).add(BlockRegistry.weeping_willow_leaves.get());

        // --- Mineable: shovel (dirt / sand / farmland) ---
        this.tag(BlockTags.MINEABLE_WITH_SHOVEL).add(BlockRegistry.aurorian_dirt.get());
        this.tag(BlockTags.MINEABLE_WITH_SHOVEL).add(BlockRegistry.aurorian_grass.get());
        this.tag(BlockTags.MINEABLE_WITH_SHOVEL).add(BlockRegistry.aurorian_farm_tile.get());
        this.tag(BlockTags.MINEABLE_WITH_SHOVEL).add(BlockRegistry.moon_sand.get());

        // --- Mineable: pickaxe (stone, ores, metal, dungeon brickwork) ---
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
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BlockRegistry.boss_spawner.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BlockRegistry.crystal.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BlockRegistry.fog_wall.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BlockRegistry.mushroom_crystal.get());
        // Runestone (unbreakable dungeon brickwork, still pickaxe-classed)
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BlockRegistry.runestone.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BlockRegistry.runestone_bars.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BlockRegistry.runestone_gate.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BlockRegistry.runestone_gate_keyhole.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BlockRegistry.runestone_gate_loot_keyhole.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BlockRegistry.runestone_lamp.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BlockRegistry.runestone_smooth.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BlockRegistry.runestone_stairs.get());
        // Darkstone
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BlockRegistry.darkstone.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BlockRegistry.darkstone_chipped.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BlockRegistry.darkstone_gate.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BlockRegistry.darkstone_gate_keyhole.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BlockRegistry.darkstone_lamp.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BlockRegistry.darkstone_pillar.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BlockRegistry.darkstone_stairs.get());
        // Moon temple
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BlockRegistry.moon_temple_bricks.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BlockRegistry.moon_temple_bars.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BlockRegistry.moon_temple_bricks_smooth.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BlockRegistry.moon_temple_gate.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BlockRegistry.moon_temple_gate_keyhole.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BlockRegistry.moon_temple_interior_gate.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BlockRegistry.moon_temple_interior_gate_keyhole.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BlockRegistry.moon_temple_lamp.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BlockRegistry.moon_temple_stairs.get());

        // --- Harvest levels (ores + metal storage blocks) ---
        this.tag(BlockTags.NEEDS_STONE_TOOL).add(BlockRegistry.cerulean_ore.get());
        this.tag(BlockTags.NEEDS_STONE_TOOL).add(BlockRegistry.geode.get());
        this.tag(BlockTags.NEEDS_STONE_TOOL).add(BlockRegistry.moonstone_ore.get());
        this.tag(BlockTags.NEEDS_STONE_TOOL).add(BlockRegistry.aurorian_coal_ore.get());
        this.tag(BlockTags.NEEDS_STONE_TOOL).add(BlockRegistry.deepslate_cerulean_ore.get());
        this.tag(BlockTags.NEEDS_STONE_TOOL).add(BlockRegistry.deepslate_moonstone_ore.get());
        this.tag(BlockTags.NEEDS_STONE_TOOL).add(BlockRegistry.aurorian_steel_block.get());
        this.tag(BlockTags.NEEDS_STONE_TOOL).add(BlockRegistry.cerulean_block.get());
        this.tag(BlockTags.NEEDS_STONE_TOOL).add(BlockRegistry.moonstone_block.get());

        // --- Ores ---
        this.tag(BlockTags.COAL_ORES).add(BlockRegistry.aurorian_coal_ore.get());
        this.tag(Tags.Blocks.ORES).add(BlockRegistry.aurorian_coal_ore.get());
        this.tag(Tags.Blocks.ORES).add(BlockRegistry.geode.get());
        this.tag(Tags.Blocks.ORES).addTag(CERULEAN_ORE);
        this.tag(Tags.Blocks.ORES).addTag(MOONSTONE_ORE);
        this.tag(Tags.Blocks.ORES_COAL).add(BlockRegistry.aurorian_coal_ore.get());
        this.tag(Tags.Blocks.ORES_IN_GROUND_STONE).add(BlockRegistry.aurorian_coal_ore.get());
        this.tag(Tags.Blocks.ORES_IN_GROUND_STONE).add(BlockRegistry.cerulean_ore.get());
        this.tag(Tags.Blocks.ORES_IN_GROUND_STONE).add(BlockRegistry.geode.get());
        this.tag(Tags.Blocks.ORES_IN_GROUND_STONE).add(BlockRegistry.moonstone_ore.get());
        this.tag(Tags.Blocks.ORES_IN_GROUND_DEEPSLATE).add(BlockRegistry.deepslate_cerulean_ore.get());
        this.tag(Tags.Blocks.ORES_IN_GROUND_DEEPSLATE).add(BlockRegistry.deepslate_moonstone_ore.get());
        this.tag(Tags.Blocks.ORE_RATES_SINGULAR).add(BlockRegistry.aurorian_coal_ore.get());
        this.tag(Tags.Blocks.ORE_RATES_SINGULAR).add(BlockRegistry.cerulean_ore.get());
        this.tag(Tags.Blocks.ORE_RATES_SINGULAR).add(BlockRegistry.deepslate_cerulean_ore.get());
        this.tag(Tags.Blocks.ORE_RATES_SINGULAR).add(BlockRegistry.deepslate_moonstone_ore.get());
        this.tag(Tags.Blocks.ORE_RATES_SINGULAR).add(BlockRegistry.geode.get());
        this.tag(Tags.Blocks.ORE_RATES_SINGULAR).add(BlockRegistry.moonstone_ore.get());

        // --- Storage blocks / beacon bases ---
        this.tag(Tags.Blocks.STORAGE_BLOCKS).add(BlockRegistry.aurorian_coal_block.get());
        this.tag(Tags.Blocks.STORAGE_BLOCKS).add(BlockRegistry.aurorian_steel_block.get());
        this.tag(Tags.Blocks.STORAGE_BLOCKS).add(BlockRegistry.cerulean_block.get());
        this.tag(Tags.Blocks.STORAGE_BLOCKS).add(BlockRegistry.moonstone_block.get());
        this.tag(Tags.Blocks.STORAGE_BLOCKS_COAL).add(BlockRegistry.aurorian_coal_block.get());
        this.tag(BlockTags.BEACON_BASE_BLOCKS).add(BlockRegistry.aurorian_steel_block.get());
        this.tag(BlockTags.BEACON_BASE_BLOCKS).add(BlockRegistry.cerulean_block.get());
        this.tag(BlockTags.BEACON_BASE_BLOCKS).add(BlockRegistry.moonstone_block.get());

        // --- Glass ---
        this.tag(BlockTags.IMPERMEABLE).add(BlockRegistry.aurorian_glass.get());
        this.tag(BlockTags.IMPERMEABLE).add(BlockRegistry.moon_glass.get());
        this.tag(Tags.Blocks.GLASS_BLOCKS).add(BlockRegistry.aurorian_glass.get());
        this.tag(Tags.Blocks.GLASS_BLOCKS).add(BlockRegistry.moon_glass.get());
        this.tag(Tags.Blocks.GLASS_BLOCKS_COLORLESS).add(BlockRegistry.aurorian_glass.get());
        this.tag(Tags.Blocks.GLASS_BLOCKS_COLORLESS).add(BlockRegistry.moon_glass.get());
        this.tag(Tags.Blocks.GLASS_PANES).add(BlockRegistry.aurorian_glass_pane.get());
        this.tag(Tags.Blocks.GLASS_PANES).add(BlockRegistry.moon_glass_pane.get());
        this.tag(Tags.Blocks.GLASS_PANES_COLORLESS).add(BlockRegistry.aurorian_glass_pane.get());
        this.tag(Tags.Blocks.GLASS_PANES_COLORLESS).add(BlockRegistry.moon_glass_pane.get());

        // --- Stone / cobblestone ---
        this.tag(Tags.Blocks.STONES).add(BlockRegistry.aurorian_stone.get());
        this.tag(Tags.Blocks.STONES).add(BlockRegistry.aurorian_deepslate.get());
        this.tag(Tags.Blocks.STONES).add(BlockRegistry.aurorian_stone_brick.get());
        this.tag(Tags.Blocks.STONES).add(BlockRegistry.peridotite.get());
        this.tag(Tags.Blocks.STONES).add(BlockRegistry.peridotite_smooth.get());
        this.tag(Tags.Blocks.STONES).add(BlockRegistry.umbra_stone.get());
        this.tag(Tags.Blocks.STONES).add(BlockRegistry.umbra_stone_cracked.get());
        this.tag(Tags.Blocks.COBBLESTONES).add(BlockRegistry.aurorian_cobblestone.get());
        this.tag(Tags.Blocks.COBBLESTONES_NORMAL).add(BlockRegistry.aurorian_cobblestone.get());

        // --- Plants ---
        this.tag(BlockTags.REPLACEABLE).add(BlockRegistry.aurorian_tallgrass.get(), BlockRegistry.aurorian_tallgrass_light.get(), BlockRegistry.bright_bulb.get(), BlockRegistry.lavender_block.get(), BlockRegistry.petunia.get(), BlockRegistry.silkberry_block.get());
        this.tag(BlockTags.REPLACEABLE).add(BlockRegistry.mushroom_small.get(), BlockRegistry.lavender_crop.get(), BlockRegistry.silkberry_crop.get());
        this.tag(BlockTags.REPLACEABLE_BY_TREES).add(BlockRegistry.aurorian_tallgrass.get(), BlockRegistry.aurorian_tallgrass_light.get());
        this.tag(BlockTags.REPLACEABLE_BY_TREES).add(BlockRegistry.bright_bulb.get(), BlockRegistry.lavender_block.get(), BlockRegistry.petunia.get(), BlockRegistry.silkberry_block.get());
        this.tag(BlockTags.REPLACEABLE_BY_TREES).add(BlockRegistry.mushroom_small.get());
        this.tag(BlockTags.SWORD_EFFICIENT).add(BlockRegistry.aurorian_tallgrass.get());
        this.tag(BlockTags.SWORD_EFFICIENT).add(BlockRegistry.aurorian_tallgrass_light.get());
        this.tag(BlockTags.SWORD_EFFICIENT).add(BlockRegistry.mushroom_small.get());
        this.tag(BlockTags.FLOWERS).add(BlockRegistry.bright_bulb.get());
        this.tag(BlockTags.FLOWERS).add(BlockRegistry.petunia.get());
        this.tag(BlockTags.FLOWERS).add(BlockRegistry.lavender_block.get());
        this.tag(BlockTags.FLOWERS).add(BlockRegistry.silkberry_block.get());
        this.tag(BlockTags.SMALL_FLOWERS).add(BlockRegistry.bright_bulb.get());
        this.tag(BlockTags.SMALL_FLOWERS).add(BlockRegistry.lavender_block.get());
        this.tag(BlockTags.SMALL_FLOWERS).add(BlockRegistry.petunia.get());
        this.tag(BlockTags.SMALL_FLOWERS).add(BlockRegistry.silkberry_block.get());

        // --- Crops / farmland ---
        this.tag(BlockTags.CROPS).add(BlockRegistry.lavender_crop.get());
        this.tag(BlockTags.CROPS).add(BlockRegistry.silkberry_crop.get());
        this.tag(BlockTags.MAINTAINS_FARMLAND).add(BlockRegistry.lavender_crop.get());
        this.tag(BlockTags.MAINTAINS_FARMLAND).add(BlockRegistry.silkberry_crop.get());

        // --- Fences / chests / misc behaviour ---
        this.tag(Tags.Blocks.CHESTS).add(BlockRegistry.silentwood_chest.get());
        this.tag(Tags.Blocks.CHESTS_WOODEN).add(BlockRegistry.silentwood_chest.get());
        this.tag(Tags.Blocks.FENCES_WOODEN).add(BlockRegistry.silentwood_fence.get());
        this.tag(Tags.Blocks.FENCES).add(BlockRegistry.silentwood_fence.get());
        this.tag(BlockTags.GUARDED_BY_PIGLINS).add(BlockRegistry.silentwood_chest.get());
        // LadderBlock alone does not make a custom ladder climbable — must be in the tag.
        this.tag(BlockTags.CLIMBABLE).add(BlockRegistry.silentwood_ladder.get());
        this.tag(BlockTags.FALL_DAMAGE_RESETTING).add(BlockRegistry.silentwood_ladder.get());
        // Torches: keep wall/fence posts from poking through, like vanilla torches.
        this.tag(BlockTags.WALL_POST_OVERRIDE).add(BlockRegistry.silentwood_torch.get());
        this.tag(BlockTags.WALL_POST_OVERRIDE).add(BlockRegistry.moon_torch.get());

        // --- Slabs / stairs / walls ---
        this.tag(BlockTags.SLABS).add(BlockRegistry.aurorian_cobblestone_slab.get());
        this.tag(BlockTags.SLABS).add(BlockRegistry.aurorian_deepslate_slab.get());
        this.tag(BlockTags.STAIRS).add(BlockRegistry.aurorian_cobblestone_stairs.get());
        this.tag(BlockTags.STAIRS).add(BlockRegistry.aurorian_deepslate_stairs.get());
        this.tag(BlockTags.STAIRS).add(BlockRegistry.aurorian_stone_brick_stairs.get());
        this.tag(BlockTags.STAIRS).add(BlockRegistry.aurorian_stone_stairs.get());
        this.tag(BlockTags.STAIRS).add(BlockRegistry.peridotite_smooth_stairs.get());
        this.tag(BlockTags.STAIRS).add(BlockRegistry.runestone_stairs.get());
        this.tag(BlockTags.STAIRS).add(BlockRegistry.umbra_stone_roof_stairs.get());
        this.tag(BlockTags.STAIRS).add(BlockRegistry.darkstone_stairs.get());
        this.tag(BlockTags.STAIRS).add(BlockRegistry.moon_temple_stairs.get());
        this.tag(BlockTags.WALLS).add(BlockRegistry.aurorian_cobblestone_wall.get());
        this.tag(BlockTags.WALLS).add(BlockRegistry.aurorian_deepslate_wall.get());

        // --- Mod-specific tags ---
        this.tag(AURORIAN_STONES).add(BlockRegistry.aurorian_stone.get());
        this.tag(AURORIAN_STONES).add(BlockRegistry.aurorian_cobblestone.get());
        this.tag(AURORIAN_STONES).add(BlockRegistry.aurorian_deepslate.get());
        this.tag(AURORIAN_STONES).add(BlockRegistry.peridotite.get());
        this.tag(AURORIAN_STONES).add(BlockRegistry.peridotite_smooth.get());
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
        this.tag(DUNGEON_BRICKS).add(BlockRegistry.moon_temple_bricks.get());
        this.tag(DUNGEON_BRICKS).add(BlockRegistry.moon_temple_bricks_smooth.get());
        this.tag(DUNGEON_BRICKS).add(BlockRegistry.moon_temple_gate.get());
        this.tag(DUNGEON_BRICKS).add(BlockRegistry.moon_temple_gate_keyhole.get());
        this.tag(DUNGEON_BRICKS).add(BlockRegistry.moon_temple_interior_gate.get());
        this.tag(DUNGEON_BRICKS).add(BlockRegistry.moon_temple_interior_gate_keyhole.get());
        this.tag(DUNGEON_BRICKS).add(BlockRegistry.moon_temple_lamp.get());
        this.tag(DUNGEON_BRICKS).add(BlockRegistry.moon_temple_stairs.get());
        this.tag(DUNGEON_BRICKS).add(BlockRegistry.runestone.get());
        this.tag(DUNGEON_BRICKS).add(BlockRegistry.runestone_bars.get());
        this.tag(DUNGEON_BRICKS).add(BlockRegistry.runestone_gate.get());
        this.tag(DUNGEON_BRICKS).add(BlockRegistry.runestone_gate_keyhole.get());
        this.tag(DUNGEON_BRICKS).add(BlockRegistry.runestone_gate_loot_keyhole.get());
        this.tag(DUNGEON_BRICKS).add(BlockRegistry.runestone_lamp.get());
        this.tag(DUNGEON_BRICKS).add(BlockRegistry.runestone_smooth.get());
        this.tag(DUNGEON_BRICKS).add(BlockRegistry.runestone_stairs.get());
        this.tag(DUNGEON_BRICKS).add(BlockRegistry.umbra_stone.get());
        this.tag(DUNGEON_BRICKS).add(BlockRegistry.umbra_stone_cracked.get());
        this.tag(DUNGEON_BRICKS).add(BlockRegistry.umbra_stone_roof_tiles.get());
        this.tag(DUNGEON_BRICKS).add(BlockRegistry.umbra_stone_roof_stairs.get());
        this.tag(DUNGEON_GATES).add(BlockRegistry.darkstone_gate.get());
        this.tag(DUNGEON_GATES).add(BlockRegistry.moon_temple_gate.get());
        this.tag(DUNGEON_GATES).add(BlockRegistry.moon_temple_interior_gate.get());
        this.tag(DUNGEON_GATES).add(BlockRegistry.runestone_gate.get());
        this.tag(MOONSTONE_ORE).add(BlockRegistry.deepslate_moonstone_ore.get());
        this.tag(MOONSTONE_ORE).add(BlockRegistry.moonstone_ore.get());
    }
}
