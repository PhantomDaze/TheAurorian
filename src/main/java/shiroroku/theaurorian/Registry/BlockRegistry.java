package shiroroku.theaurorian.Registry;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.ColoredFallingBlock;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.Nullable;
import shiroroku.theaurorian.Blocks.*;
import shiroroku.theaurorian.Blocks.AurorianFurnace.AurorianFurnaceBlock;
import shiroroku.theaurorian.Blocks.AurorianFurnace.ChimneyBlock;
import shiroroku.theaurorian.Blocks.BossSpawner.BossSpawnerBlock;
import shiroroku.theaurorian.Blocks.Crystal.CrystalBlock;
import shiroroku.theaurorian.Blocks.MoonlightForge.MoonlightForgeBlock;
import shiroroku.theaurorian.Blocks.Scrapper.ScrapperBlock;
import shiroroku.theaurorian.Blocks.SilentwoodChest.SilentwoodChestBlock;
import shiroroku.theaurorian.Blocks.SilentwoodChest.SilentwoodChestBlockItem;
import shiroroku.theaurorian.Blocks.SilentwoodCraftingTable.SilentwoodCraftingTableBlock;
import shiroroku.theaurorian.TheAurorian;
import shiroroku.theaurorian.Util.TooltipUtil;
import shiroroku.theaurorian.World.Feature.AurorianTreeGrowers;

import java.util.List;
import java.util.function.Supplier;

public class BlockRegistry {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, TheAurorian.MODID); // no datagen
    public static final DeferredRegister<Block> BLOCKS_GEN = DeferredRegister.create(Registries.BLOCK, TheAurorian.MODID); // basic block, all sided model which drops itself
    public static final DeferredRegister<Block> BLOCKS_GEN_NL = DeferredRegister.create(Registries.BLOCK, TheAurorian.MODID); // nl = generates model without loot table
    public static final DeferredRegister<Block> BLOCKS_GEN_NL_PLANT = DeferredRegister.create(Registries.BLOCK, TheAurorian.MODID); // plants, blocks with cross model

    // Runestone
    public static final DeferredHolder<Block, Block> runestone = regBlockItem(BLOCKS_GEN, "runestone", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).strength(-1.0F, 3600000.0F)));
    public static final DeferredHolder<Block, Block> runestone_bars = regBlockItem(BLOCKS, "runestone_bars", () -> new IronBarsBlock(BlockBehaviour.Properties.ofFullCopy(runestone.get())));
    public static final DeferredHolder<Block, Block> runestone_gate = regBlockItem(BLOCKS_GEN, "runestone_gate", () -> new Block(BlockBehaviour.Properties.ofFullCopy(runestone.get())));
    public static final DeferredHolder<Block, Block> runestone_gate_keyhole = regBlockItem(BLOCKS_GEN, "runestone_gate_keyhole", () -> new DungeonGateKeyHole(ItemRegistry.runestone_key, BlockBehaviour.Properties.ofFullCopy(runestone.get()), true));
    public static final DeferredHolder<Block, Block> runestone_gate_loot_keyhole = regBlockItem(BLOCKS_GEN, "runestone_gate_loot_keyhole", () -> new DungeonGateKeyHole(ItemRegistry.runestone_loot_key, BlockBehaviour.Properties.ofFullCopy(runestone.get())));
    public static final DeferredHolder<Block, Block> runestone_lamp = regBlockItem(BLOCKS_GEN, "runestone_lamp", () -> new Block(BlockBehaviour.Properties.ofFullCopy(runestone.get()).lightLevel((state) -> 15)));
    public static final DeferredHolder<Block, Block> runestone_smooth = regBlockItem(BLOCKS_GEN, "runestone_smooth", () -> new Block(BlockBehaviour.Properties.ofFullCopy(runestone.get())));
    public static final DeferredHolder<Block, Block> runestone_stairs = regBlockItem(BLOCKS, "runestone_stairs", () -> new StairBlock(runestone.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(runestone.get())));

    // Darkstone
    public static final DeferredHolder<Block, Block> darkstone = regBlockItem(BLOCKS_GEN, "darkstone", () -> new Block(BlockBehaviour.Properties.ofFullCopy(runestone.get())));
    public static final DeferredHolder<Block, Block> darkstone_chipped = regBlockItem(BLOCKS_GEN, "darkstone_chipped", () -> new Block(BlockBehaviour.Properties.ofFullCopy(darkstone.get())));
    public static final DeferredHolder<Block, Block> darkstone_gate = regBlockItem(BLOCKS_GEN, "darkstone_gate", () -> new Block(BlockBehaviour.Properties.ofFullCopy(darkstone.get())));
    public static final DeferredHolder<Block, Block> darkstone_gate_keyhole = regBlockItem(BLOCKS_GEN, "darkstone_gate_keyhole", () -> new DungeonGateKeyHole(ItemRegistry.darkstone_key, BlockBehaviour.Properties.ofFullCopy(darkstone.get())));
    public static final DeferredHolder<Block, Block> darkstone_lamp = regBlockItem(BLOCKS_GEN, "darkstone_lamp", () -> new Block(BlockBehaviour.Properties.ofFullCopy(darkstone.get()).lightLevel((state) -> 15)));
    public static final DeferredHolder<Block, Block> darkstone_pillar = regBlockItem(BLOCKS_GEN, "darkstone_pillar", () -> new Block(BlockBehaviour.Properties.ofFullCopy(darkstone.get())));
    public static final DeferredHolder<Block, Block> darkstone_stairs = regBlockItem(BLOCKS, "darkstone_stairs", () -> new StairBlock(darkstone.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(darkstone.get())));

    // Moon Temple
    public static final DeferredHolder<Block, Block> moon_temple_bricks = regBlockItem(BLOCKS_GEN, "moon_temple_bricks", () -> new Block(BlockBehaviour.Properties.ofFullCopy(runestone.get())));
    public static final DeferredHolder<Block, Block> moon_temple_bars = regBlockItem(BLOCKS, "moon_temple_bars", () -> new IronBarsBlock(BlockBehaviour.Properties.ofFullCopy(moon_temple_bricks.get())));
    public static final DeferredHolder<Block, Block> moon_temple_bricks_smooth = regBlockItem(BLOCKS_GEN, "moon_temple_bricks_smooth", () -> new Block(BlockBehaviour.Properties.ofFullCopy(moon_temple_bricks.get())));
    public static final DeferredHolder<Block, Block> moon_temple_gate = regBlockItem(BLOCKS_GEN, "moon_temple_gate", () -> new Block(BlockBehaviour.Properties.ofFullCopy(moon_temple_bricks.get())));
    public static final DeferredHolder<Block, Block> moon_temple_gate_keyhole = regBlockItem(BLOCKS_GEN, "moon_temple_gate_keyhole", () -> new DungeonGateKeyHole(ItemRegistry.moon_temple_key, BlockBehaviour.Properties.ofFullCopy(moon_temple_bricks.get())));
    public static final DeferredHolder<Block, Block> moon_temple_interior_gate = regBlockItem(BLOCKS_GEN, "moon_temple_interior_gate", () -> new Block(BlockBehaviour.Properties.ofFullCopy(moon_temple_bricks.get())));
    public static final DeferredHolder<Block, Block> moon_temple_interior_gate_keyhole = regBlockItem(BLOCKS_GEN, "moon_temple_interior_gate_keyhole", () -> new DungeonGateKeyHole(ItemRegistry.moon_temple_interior_key, BlockBehaviour.Properties.ofFullCopy(moon_temple_bricks.get())));
    public static final DeferredHolder<Block, Block> moon_temple_lamp = regBlockItem(BLOCKS_GEN, "moon_temple_lamp", () -> new Block(BlockBehaviour.Properties.ofFullCopy(moon_temple_bricks.get()).lightLevel((state) -> 15)));
    public static final DeferredHolder<Block, Block> moon_temple_stairs = regBlockItem(BLOCKS, "moon_temple_stairs", () -> new StairBlock(moon_temple_bricks.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(moon_temple_bricks.get())));

    // Natural
    public static final DeferredHolder<Block, Block> aurorian_cobblestone = regBlockItem(BLOCKS_GEN, "aurorian_cobblestone", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.COBBLESTONE)));
    public static final DeferredHolder<Block, Block> aurorian_cobblestone_slab = regBlockItem(BLOCKS, "aurorian_cobblestone_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(BlockRegistry.aurorian_cobblestone.get())));
    public static final DeferredHolder<Block, Block> aurorian_cobblestone_stairs = regBlockItem(BLOCKS, "aurorian_cobblestone_stairs", () -> new StairBlock(aurorian_cobblestone.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(aurorian_cobblestone.get())));
    public static final DeferredHolder<Block, Block> aurorian_cobblestone_wall = regBlockItem(BLOCKS, "aurorian_cobblestone_wall", () -> new WallBlock(BlockBehaviour.Properties.ofFullCopy(BlockRegistry.aurorian_cobblestone.get())));
    public static final DeferredHolder<Block, Block> aurorian_deepslate = regBlockItem(BLOCKS_GEN, "aurorian_deepslate", () -> new AurorianDeepslateBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE).randomTicks()));
    public static final DeferredHolder<Block, Block> aurorian_deepslate_slab = regBlockItem(BLOCKS, "aurorian_deepslate_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(BlockRegistry.aurorian_deepslate.get())));
    public static final DeferredHolder<Block, Block> aurorian_deepslate_stairs = regBlockItem(BLOCKS, "aurorian_deepslate_stairs", () -> new StairBlock(Blocks.DEEPSLATE.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(aurorian_deepslate.get())));
    public static final DeferredHolder<Block, Block> aurorian_deepslate_wall = regBlockItem(BLOCKS, "aurorian_deepslate_wall", () -> new WallBlock(BlockBehaviour.Properties.ofFullCopy(BlockRegistry.aurorian_deepslate.get())));
    public static final DeferredHolder<Block, Block> aurorian_dirt = regBlockItem(BLOCKS_GEN, "aurorian_dirt", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.DIRT)));
    public static final DeferredHolder<Block, Block> aurorian_grass = regBlockItem(BLOCKS, "aurorian_grass", () -> new AurorianGrassBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GRASS_BLOCK)));
    public static final DeferredHolder<Block, Block> aurorian_stone = regBlockItem(BLOCKS_GEN_NL, "aurorian_stone", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)));
    public static final DeferredHolder<Block, Block> silentwood_fence = regBlockItemWithBurntime(BLOCKS, "silentwood_fence", () -> new FenceBlock(BlockBehaviour.Properties.ofFullCopy(BlockRegistry.silentwood_planks.get())), 300);
    public static final DeferredHolder<Block, Block> silentwood_leaves = regBlockItem(BLOCKS_GEN_NL, "silentwood_leaves", () -> new LeavesBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_LEAVES)));
    public static final DeferredHolder<Block, Block> silentwood_log = regBlockItemWithBurntime(BLOCKS, "silentwood_log", () -> new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_LOG)), 300);
    public static final DeferredHolder<Block, Block> silentwood_planks = regBlockItemWithBurntime(BLOCKS_GEN, "silentwood_planks", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_PLANKS)), 300);
    public static final DeferredHolder<Block, Block> silentwood_sapling = regBlockItemWithBurntime(BLOCKS_GEN_NL_PLANT, "silentwood_sapling", () -> new SaplingBlock(AurorianTreeGrowers.SILENTWOOD, BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_SAPLING)), 100);
    public static final DeferredHolder<Block, Block> silentwood_slab = regBlockItemWithBurntime(BLOCKS, "silentwood_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(BlockRegistry.silentwood_planks.get())), 150);
    public static final DeferredHolder<Block, Block> silentwood_stairs = regBlockItemWithBurntime(BLOCKS, "silentwood_stairs", () -> new StairBlock(silentwood_planks.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(silentwood_planks.get())), 300);

    // Weeping willow (Phase 9)
    public static final DeferredHolder<Block, Block> weeping_willow_leaves = regBlockItem(BLOCKS, "weeping_willow_leaves", () -> new WeepingWillowLeavesBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.JUNGLE_LEAVES)));
    public static final DeferredHolder<Block, Block> weeping_willow_log = regBlockItemWithBurntime(BLOCKS, "weeping_willow_log", () -> new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.JUNGLE_LOG)), 300);
    public static final DeferredHolder<Block, Block> weeping_willow_planks = regBlockItemWithBurntime(BLOCKS_GEN, "weeping_willow_planks", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.JUNGLE_PLANKS)), 300);
    public static final DeferredHolder<Block, Block> weeping_willow_sapling = regBlockItemWithBurntime(BLOCKS_GEN_NL_PLANT, "weeping_willow_sapling", () -> new SaplingBlock(AurorianTreeGrowers.WEEPING_WILLOW, BlockBehaviour.Properties.ofFullCopy(Blocks.JUNGLE_SAPLING)), 100);
    public static final DeferredHolder<Block, Block> weeping_willow_stairs = regBlockItemWithBurntime(BLOCKS, "weeping_willow_stairs", () -> new StairBlock(weeping_willow_planks.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(weeping_willow_planks.get())), 300);

    // Plants
    public static final DeferredHolder<Block, Block> aurorian_tallgrass = regBlockItem(BLOCKS_GEN_NL_PLANT, "aurorian_tallgrass", () -> new TallGrassBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.TALL_GRASS)));
    public static final DeferredHolder<Block, Block> bright_bulb = regBlockItem(BLOCKS_GEN_NL_PLANT, "bright_bulb", () -> new FlowerBlock(MobEffects.LUCK, 0.0F, BlockBehaviour.Properties.ofFullCopy(Blocks.TALL_GRASS).lightLevel((state) -> 10)));
    public static final DeferredHolder<Block, Block> lavender_block = regBlockItem(BLOCKS_GEN_NL_PLANT, "lavender_block", () -> new FlowerBlock(MobEffects.LUCK, 0.0F, BlockBehaviour.Properties.ofFullCopy(Blocks.TALL_GRASS)));
    public static final DeferredHolder<Block, Block> petunia = regBlockItem(BLOCKS_GEN_NL_PLANT, "petunia", () -> new FlowerBlock(MobEffects.LUCK, 0.0F, BlockBehaviour.Properties.ofFullCopy(Blocks.TALL_GRASS)));
    public static final DeferredHolder<Block, Block> silkberry_block = regBlockItem(BLOCKS_GEN_NL_PLANT, "silkberry_block", () -> new FlowerBlock(MobEffects.LUCK, 0.0F, BlockBehaviour.Properties.ofFullCopy(Blocks.TALL_GRASS)));

    // Ores
    public static final DeferredHolder<Block, Block> aurorian_coal_ore = regBlockItem(BLOCKS_GEN_NL, "aurorian_coal_ore", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.COAL_ORE)));
    public static final DeferredHolder<Block, Block> cerulean_ore = regBlockItem(BLOCKS_GEN, "cerulean_ore", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_ORE)));
    public static final DeferredHolder<Block, Block> deepslate_cerulean_ore = regBlockItem(BLOCKS_GEN, "deepslate_cerulean_ore", () -> new Block(BlockBehaviour.Properties.ofFullCopy(cerulean_ore.get())));
    public static final DeferredHolder<Block, Block> moonstone_ore = regBlockItem(BLOCKS_GEN, "moonstone_ore", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_ORE)));
    public static final DeferredHolder<Block, Block> deepslate_moonstone_ore = regBlockItem(BLOCKS_GEN, "deepslate_moonstone_ore", () -> new Block(BlockBehaviour.Properties.ofFullCopy(moonstone_ore.get())));
    public static final DeferredHolder<Block, Block> geode = regBlockItem(BLOCKS_GEN_NL, "geode", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_ORE)));

    // Machines
    public static final DeferredHolder<Block, Block> aurorian_furnace = regBlockItem(BLOCKS, "aurorian_furnace", () -> new AurorianFurnaceBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.FURNACE)));
    public static final DeferredHolder<Block, Block> boss_spawner = regBlockItem(BLOCKS, "boss_spawner", () -> new BossSpawnerBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SPAWNER)));
    public static final DeferredHolder<Block, Block> moonlight_forge = regBlockItem(BLOCKS, "moonlight_forge", () -> new MoonlightForgeBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion()));
    public static final DeferredHolder<Block, Block> scrapper = regBlockItem(BLOCKS, "scrapper", () -> new ScrapperBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)));
    public static final DeferredHolder<Block, Block> silentwood_chest = regBlockItemCustom(BLOCKS, "silentwood_chest", () -> new SilentwoodChestBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CHEST)), SilentwoodChestBlockItem::new);
    public static final DeferredHolder<Block, Block> silentwood_crafting_table = regBlockItem(BLOCKS, "silentwood_crafting_table", () -> new SilentwoodCraftingTableBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CRAFTING_TABLE)));

    // Other
    public static final DeferredHolder<Block, Block> aurorian_portal = regBlockItem(BLOCKS, "aurorian_portal", () -> new AurorianPortal(BlockBehaviour.Properties.ofFullCopy(Blocks.NETHER_PORTAL)));
    public static final DeferredHolder<Block, Block> aurorian_portal_frame = regBlockItem(BLOCKS_GEN, "aurorian_portal_frame", () -> new AurorianPortalFrame(BlockBehaviour.Properties.ofFullCopy(BlockRegistry.aurorian_cobblestone.get())));
    public static final DeferredHolder<Block, Block> chimney = regBlockItem(BLOCKS, "chimney", () -> new ChimneyBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion()));
    public static final DeferredHolder<Block, Block> crystal = regBlockItem(BLOCKS, "crystal", () -> new CrystalBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion().lightLevel((state) -> 15).sound(SoundType.GLASS)));
    public static final DeferredHolder<Block, Block> fog_wall = regBlockItem(BLOCKS, "fog_wall", () -> new FogWallBlock(BlockBehaviour.Properties.ofFullCopy(runestone.get()).noCollission().noOcclusion().lightLevel((state) -> 10)));
    public static final DeferredHolder<Block, Block> moon_gem = regBlockItem(BLOCKS, "moon_gem", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion()));

    // Phase 1: Umbra stone set (Darkstone line)
    public static final DeferredHolder<Block, Block> umbra_stone = regBlockItem(BLOCKS_GEN, "umbra_stone", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).strength(5.0F)));
    public static final DeferredHolder<Block, Block> umbra_stone_cracked = regBlockItem(BLOCKS_GEN, "umbra_stone_cracked", () -> new Block(BlockBehaviour.Properties.ofFullCopy(umbra_stone.get())));
    public static final DeferredHolder<Block, Block> umbra_stone_roof_tiles = regBlockItem(BLOCKS_GEN, "umbra_stone_roof_tiles", () -> new Block(BlockBehaviour.Properties.ofFullCopy(umbra_stone.get())));
    public static final DeferredHolder<Block, Block> umbra_stone_roof_stairs = regBlockItem(BLOCKS, "umbra_stone_roof_stairs", () -> new StairBlock(umbra_stone_roof_tiles.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(umbra_stone_roof_tiles.get())));

    // Phase 1: Urn (ruins/dungeon decor, drops block loot)
    public static final DeferredHolder<Block, Block> urn = regBlockItem(BLOCKS_GEN_NL, "urn", () -> new UrnBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)));

    // Mushrooms (Phase 9): small mushroom grows into a full mushroom tree
    public static final DeferredHolder<Block, Block> mushroom_small = regBlockItem(BLOCKS_GEN_NL_PLANT, "mushroom_small", () -> new SaplingBlock(AurorianTreeGrowers.MUSHROOM, BlockBehaviour.Properties.ofFullCopy(Blocks.BROWN_MUSHROOM)));
    public static final DeferredHolder<Block, Block> mushroom = regBlockItem(BLOCKS, "mushroom", () -> new IndigoMushroomBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.MUSHROOM_STEM).strength(1.0F).sound(SoundType.SLIME_BLOCK)));
    public static final DeferredHolder<Block, Block> mushroom_crystal = regBlockItem(BLOCKS_GEN, "mushroom_crystal", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS).strength(0.5F).sound(SoundType.STONE).lightLevel((state) -> 15)));
    public static final DeferredHolder<Block, Block> mushroom_stem = regBlockItem(BLOCKS, "mushroom_stem", () -> new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.MUSHROOM_STEM).strength(1.0F).sound(SoundType.SLIME_BLOCK)));

    // Phase 1: Material storage blocks
    public static final DeferredHolder<Block, Block> aurorian_coal_block = regBlockItem(BLOCKS_GEN, "aurorian_coal_block", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.COAL_BLOCK)));
    public static final DeferredHolder<Block, Block> aurorian_steel_block = regBlockItem(BLOCKS_GEN, "aurorian_steel_block", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)));
    public static final DeferredHolder<Block, Block> cerulean_block = regBlockItem(BLOCKS_GEN, "cerulean_block", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)));
    public static final DeferredHolder<Block, Block> moonstone_block = regBlockItem(BLOCKS_GEN, "moonstone_block", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)));

    // Phase 1: Glass (no normal drops, silk touch only — datagen block loot)
    public static final DeferredHolder<Block, Block> aurorian_glass = regBlockItem(BLOCKS_GEN_NL, "aurorian_glass", () -> new HalfTransparentBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)));
    public static final DeferredHolder<Block, Block> moon_glass = regBlockItem(BLOCKS_GEN_NL, "moon_glass", () -> new HalfTransparentBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)));
    public static final DeferredHolder<Block, Block> aurorian_glass_pane = regBlockItem(BLOCKS, "aurorian_glass_pane", () -> new net.minecraft.world.level.block.IronBarsBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS_PANE)));
    public static final DeferredHolder<Block, Block> moon_glass_pane = regBlockItem(BLOCKS, "moon_glass_pane", () -> new IronBarsBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS_PANE)));

    // Phase 1: Torches + ladder
    public static final DeferredHolder<Block, Block> silentwood_torch = regBlockItem(BLOCKS, "silentwood_torch", () -> new AurorianTorchBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.TORCH).lightLevel((state) -> 15)));
    public static final DeferredHolder<Block, Block> moon_torch = regBlockItem(BLOCKS, "moon_torch", () -> new AurorianTorchBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.TORCH).lightLevel((state) -> 15)));
    public static final DeferredHolder<Block, Block> silentwood_ladder = regBlockItem(BLOCKS, "silentwood_ladder", () -> new LadderBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.LADDER)));

    // Phase 1: Moonsand + Peridotite
    public static final DeferredHolder<Block, Block> moon_sand = regBlockItem(BLOCKS_GEN, "moon_sand", () -> new ColoredFallingBlock(new net.minecraft.util.ColorRGBA(0xDBD3A0), BlockBehaviour.Properties.ofFullCopy(Blocks.SAND)));
    public static final DeferredHolder<Block, Block> peridotite = regBlockItem(BLOCKS_GEN, "peridotite", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).strength(5.0F)));
    public static final DeferredHolder<Block, Block> peridotite_smooth = regBlockItem(BLOCKS_GEN, "peridotite_smooth", () -> new Block(BlockBehaviour.Properties.ofFullCopy(peridotite.get())));
    public static final DeferredHolder<Block, Block> peridotite_smooth_stairs = regBlockItem(BLOCKS, "peridotite_smooth_stairs", () -> new StairBlock(peridotite_smooth.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(peridotite_smooth.get())));

    // Phase 1: Aurorian stone brick
    public static final DeferredHolder<Block, Block> aurorian_stone_brick = regBlockItem(BLOCKS_GEN, "aurorian_stone_brick", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS).strength(2.0F)));
    public static final DeferredHolder<Block, Block> aurorian_stone_brick_stairs = regBlockItem(BLOCKS, "aurorian_stone_brick_stairs", () -> new StairBlock(aurorian_stone_brick.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(aurorian_stone_brick.get())));
    public static final DeferredHolder<Block, Block> aurorian_stone_stairs = regBlockItem(BLOCKS, "aurorian_stone_stairs", () -> new StairBlock(aurorian_stone.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(aurorian_stone.get())));

    // Phase 1: Light grass variants
    public static final DeferredHolder<Block, Block> aurorian_grass_light = regBlockItem(BLOCKS, "aurorian_grass_light", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.GRASS_BLOCK).lightLevel((state) -> 5)));
    public static final DeferredHolder<Block, Block> aurorian_tallgrass_light = regBlockItem(BLOCKS_GEN_NL_PLANT, "aurorian_tallgrass_light", () -> new TallGrassBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.TALL_GRASS)));

    // Phase 1: Farm tile + Phase 7 crops (registered early so loot/remap stay valid).
    // Crops are registered WITHOUT a block item — the seed item plants them.
    public static final DeferredHolder<Block, Block> aurorian_farm_tile = regBlockItem(BLOCKS_GEN, "aurorian_farm_tile", () -> new AurorianFarmTile(BlockBehaviour.Properties.ofFullCopy(Blocks.FARMLAND)));
    public static final DeferredHolder<Block, Block> lavender_crop = BLOCKS_GEN_NL_PLANT.register("lavender_crop", () -> new AurorianCropBlock(() -> ItemRegistry.lavender_seeds.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT)));
    public static final DeferredHolder<Block, Block> silkberry_crop = BLOCKS_GEN_NL_PLANT.register("silkberry_crop", () -> new AurorianCropBlock(() -> ItemRegistry.silkberry_seeds.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT)));

    public static void register(IEventBus bus) {
        // The order of these matter because some blocks copy from previous register.
        // BLOCKS must fire before BLOCKS_GEN_NL_PLANT because the sapling's
        // SilentwoodTreeFeature resolves silentwood_log/silentwood_leaves at class load,
        // and after BLOCKS_GEN_NL because e.g. aurorian_stone_stairs copies aurorian_stone.
        BLOCKS_GEN.register(bus);
        BLOCKS_GEN_NL.register(bus);
        BLOCKS.register(bus);
        BLOCKS_GEN_NL_PLANT.register(bus);
    }

    /**
     * Registers a BlockItem while registering the block.
     */
    private static <I extends Block> DeferredHolder<Block, I> regBlockItem(DeferredRegister<Block> registry, final String id, final Supplier<? extends I> supplier) {
        return regBlockItemWithBurntime(registry, id, supplier, 0);
    }

    /**
     * Registers a BlockItem built by the given factory (e.g. a subclass with a custom client renderer).
     */
    private static <I extends Block> DeferredHolder<Block, I> regBlockItemCustom(DeferredRegister<Block> registry, final String id, final Supplier<? extends I> supplier, java.util.function.BiFunction<Block, Item.Properties, Item> itemFactory) {
        DeferredHolder<Block, I> createdBlock = registry.register(id, supplier);
        ItemRegistry.ITEMS.register(id, () -> itemFactory.apply(createdBlock.get(), ItemRegistry.defaultProp()));
        return createdBlock;
    }

    private static <I extends Block> DeferredHolder<Block, I> regBlockItemWithBurntime(DeferredRegister<Block> registry, final String id, final Supplier<? extends I> supplier, int burnTime) {
        DeferredHolder<Block, I> createdBlock = registry.register(id, supplier);
        ItemRegistry.ITEMS.register(id, () -> new BlockItem(createdBlock.get(), ItemRegistry.defaultProp()) {
            @Override
            public int getBurnTime(ItemStack itemStack, @Nullable RecipeType<?> recipeType) {
                return burnTime == 0 ? super.getBurnTime(itemStack, recipeType) : burnTime;
            }

            @Override
            public void appendHoverText(ItemStack pStack, Item.TooltipContext pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
                super.appendHoverText(pStack, pLevel, TooltipUtil.tryAddDesc(pStack, pTooltipComponents), pIsAdvanced);
            }
        });
        return createdBlock;
    }
}
