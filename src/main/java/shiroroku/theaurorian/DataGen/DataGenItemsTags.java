package shiroroku.theaurorian.DataGen;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;
import shiroroku.theaurorian.Registry.BlockRegistry;
import shiroroku.theaurorian.Registry.ItemRegistry;
import shiroroku.theaurorian.TheAurorian;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class DataGenItemsTags extends ItemTagsProvider {

    public static final TagKey<Item> ABSORPTION_ORB_REPAIRABLE = ItemTags.create(ResourceLocation.fromNamespaceAndPath(TheAurorian.MODID, "absorption_orb_repairable"));
    public static final TagKey<Item> AURORIAN_STONES = ItemTags.create(ResourceLocation.fromNamespaceAndPath(TheAurorian.MODID, "aurorian_stones"));
    public static final TagKey<Item> CERULEAN_ORE = ItemTags.create(ResourceLocation.fromNamespaceAndPath(TheAurorian.MODID, "cerulean_ore"));
    public static final TagKey<Item> CRYSTALLINE_PICKAXE_TREASURE = ItemTags.create(ResourceLocation.fromNamespaceAndPath(TheAurorian.MODID, "crystalline_treasure"));
    public static final TagKey<Item> CRYSTALLINE_SHIELD_REPAIRABLE = ItemTags.create(ResourceLocation.fromNamespaceAndPath(TheAurorian.MODID, "crystalline_shield_repairable"));
    public static final TagKey<Item> KEYS = ItemTags.create(ResourceLocation.fromNamespaceAndPath(TheAurorian.MODID, "keys"));
    public static final TagKey<Item> LIGHTNING_IMMUNE = ItemTags.create(ResourceLocation.fromNamespaceAndPath(TheAurorian.MODID, "lightning_immune"));
    public static final TagKey<Item> MOONSTONE_ORE = ItemTags.create(ResourceLocation.fromNamespaceAndPath(TheAurorian.MODID, "moonstone_ore"));
    public static final TagKey<Item> PORTAL_LIGHTERS = ItemTags.create(ResourceLocation.fromNamespaceAndPath(TheAurorian.MODID, "portal_lighters"));
    public static final TagKey<Item> SCRAP = ItemTags.create(ResourceLocation.fromNamespaceAndPath(TheAurorian.MODID, "scrap"));
    public static final TagKey<Item> SPECTRAL_ARMOR = ItemTags.create(ResourceLocation.fromNamespaceAndPath(TheAurorian.MODID, "spectral_armor"));
    public static final TagKey<Item> TEA = ItemTags.create(ResourceLocation.fromNamespaceAndPath(TheAurorian.MODID, "tea"));

    protected DataGenItemsTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagsProvider.TagLookup<Block>> blockTags, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, TheAurorian.MODID, existingFileHelper);
    }

@SuppressWarnings("unchecked")
    @Override
    protected void addTags(HolderLookup.Provider provider) {
        ItemRegistry.ITEMS_GEN_SHIELD.getEntries().stream().map(Supplier::get).forEach(shield -> this.tag(Tags.Items.TOOLS_SHIELD).add(shield));
        ItemRegistry.ITEMS_GEN_KEY.getEntries().stream().map(Supplier::get).forEach(key -> this.tag(KEYS).add(key));
        this.tag(ABSORPTION_ORB_REPAIRABLE).addTags(Tags.Items.TOOLS, Tags.Items.ARMORS, Tags.Items.TOOLS_SHEAR);
        this.tag(AURORIAN_STONES).add(BlockRegistry.aurorian_cobblestone.get().asItem());
        this.tag(AURORIAN_STONES).add(BlockRegistry.aurorian_deepslate.get().asItem());
        this.tag(CERULEAN_ORE).add(BlockRegistry.cerulean_ore.get().asItem());
        this.tag(CERULEAN_ORE).add(BlockRegistry.deepslate_cerulean_ore.get().asItem());
        this.tag(CRYSTALLINE_PICKAXE_TREASURE).add(BlockRegistry.cerulean_ore.get().asItem());
        this.tag(CRYSTALLINE_PICKAXE_TREASURE).add(BlockRegistry.crystal.get().asItem());
        this.tag(CRYSTALLINE_PICKAXE_TREASURE).add(BlockRegistry.moonstone_ore.get().asItem());
        this.tag(CRYSTALLINE_PICKAXE_TREASURE).add(Items.AMETHYST_SHARD);
        this.tag(CRYSTALLINE_PICKAXE_TREASURE).add(Items.FLINT);
        this.tag(CRYSTALLINE_PICKAXE_TREASURE).add(Items.RAW_GOLD);
        this.tag(CRYSTALLINE_PICKAXE_TREASURE).add(Items.RAW_IRON);
        this.tag(CRYSTALLINE_SHIELD_REPAIRABLE).addTags(Tags.Items.TOOLS, Tags.Items.ARMORS, Tags.Items.TOOLS_SHEAR);
        this.tag(ItemTags.ARROWS).add(ItemRegistry.cerulean_arrow.get(), ItemRegistry.crystal_arrow.get());
        this.tag(ItemTags.COALS).add(ItemRegistry.aurorian_coal.get());
        this.tag(LIGHTNING_IMMUNE).add(Items.LEATHER_BOOTS, Items.LEATHER_CHESTPLATE, Items.LEATHER_HELMET, Items.LEATHER_LEGGINGS);
        this.tag(MOONSTONE_ORE).add(BlockRegistry.deepslate_moonstone_ore.get().asItem());
        this.tag(MOONSTONE_ORE).add(BlockRegistry.moonstone_ore.get().asItem());
        this.tag(PORTAL_LIGHTERS).add(Items.FLINT_AND_STEEL);
        this.tag(PORTAL_LIGHTERS).add(ItemRegistry.silentwood_stick.get());
        this.tag(SCRAP).add(ItemRegistry.aurorianite_scrap.get());
        this.tag(SCRAP).add(ItemRegistry.crystalline_scrap.get());
        this.tag(SCRAP).add(ItemRegistry.umbra_scrap.get());
        this.tag(SPECTRAL_ARMOR).add(ItemRegistry.spectral_helmet.get(), ItemRegistry.spectral_chestplate.get(), ItemRegistry.spectral_leggings.get(), ItemRegistry.spectral_boots.get());
        this.tag(TEA).add(ItemRegistry.bright_bulb_tea.get(), ItemRegistry.lavender_tea.get(), ItemRegistry.petunia_tea.get(), ItemRegistry.silkberry_tea.get());
        this.tag(net.minecraft.tags.ItemTags.FOOT_ARMOR).add(ItemRegistry.cerulean_boots.get(), ItemRegistry.spectral_boots.get(), ItemRegistry.aurorian_steel_boots.get(), ItemRegistry.slime_boots.get());
        this.tag(net.minecraft.tags.ItemTags.CHEST_ARMOR).add(ItemRegistry.cerulean_chestplate.get(), ItemRegistry.spectral_chestplate.get(), ItemRegistry.umbra_chestplate.get(), ItemRegistry.aurorian_steel_chestplate.get(), ItemRegistry.spiked_chestplate.get());
        this.tag(net.minecraft.tags.ItemTags.HEAD_ARMOR).add(ItemRegistry.cerulean_helmet.get(), ItemRegistry.spectral_helmet.get(), ItemRegistry.spectral_helmet.get());
        this.tag(net.minecraft.tags.ItemTags.LEG_ARMOR).add(ItemRegistry.cerulean_leggings.get(), ItemRegistry.spectral_leggings.get(), ItemRegistry.aurorian_steel_leggings.get());
        this.tag(Tags.Items.CHESTS).add(BlockRegistry.silentwood_chest.get().asItem());
        this.tag(Tags.Items.CHESTS_WOODEN).add(BlockRegistry.silentwood_chest.get().asItem());
        this.tag(Tags.Items.GEMS).add(BlockRegistry.crystal.get().asItem());
        this.tag(Tags.Items.INGOTS).add(ItemRegistry.aurorian_steel_ingot.get());
        this.tag(Tags.Items.INGOTS).add(ItemRegistry.aurorianite_ingot.get());
        this.tag(Tags.Items.INGOTS).add(ItemRegistry.cerulean_ingot.get());
        this.tag(Tags.Items.INGOTS).add(ItemRegistry.crystalline_ingot.get());
        this.tag(Tags.Items.INGOTS).add(ItemRegistry.moonstone_ingot.get());
        this.tag(Tags.Items.INGOTS).add(ItemRegistry.umbra_ingot.get());
        this.tag(Tags.Items.NUGGETS).add(ItemRegistry.aurorian_coal_nugget.get());
        this.tag(Tags.Items.NUGGETS).add(ItemRegistry.cerulean_nugget.get());
        this.tag(Tags.Items.NUGGETS).add(ItemRegistry.moonstone_nugget.get());
        this.tag(Tags.Items.NUGGETS).add(ItemRegistry.aurorian_steel_nugget.get());
        this.tag(Tags.Items.ORES).add(BlockRegistry.geode.get().asItem(), BlockRegistry.aurorian_coal_ore.get().asItem());
        this.tag(Tags.Items.ORES).addTags(CERULEAN_ORE, MOONSTONE_ORE);
        this.tag(Tags.Items.RODS_WOODEN).add(ItemRegistry.silentwood_stick.get());
        this.tag(Tags.Items.TOOLS_SHEAR).add(ItemRegistry.aurorian_stone_sickle.get());
        this.tag(Tags.Items.TOOLS_SHEAR).add(ItemRegistry.moonstone_sickle.get());
        this.tag(Tags.Items.TOOLS_SHEAR).add(ItemRegistry.silentwood_sickle.get());
        this.tag(Tags.Items.STRINGS).add(ItemRegistry.plant_fiber.get());
        this.tag(ItemTags.AXES).add(ItemRegistry.aurorian_steel_axe.get());
        this.tag(ItemTags.AXES).add(ItemRegistry.aurorian_stone_axe.get());
        this.tag(ItemTags.AXES).add(ItemRegistry.aurorianite_axe.get());
        this.tag(ItemTags.AXES).add(ItemRegistry.moonstone_axe.get());
        this.tag(ItemTags.AXES).add(ItemRegistry.silentwood_axe.get());
        this.tag(Tags.Items.TOOLS_BOW).add(ItemRegistry.silentwood_bow.get());
        this.tag(ItemTags.HOES).add(ItemRegistry.aurorian_steel_hoe.get());
        this.tag(ItemTags.HOES).add(ItemRegistry.aurorian_stone_hoe.get());
        this.tag(ItemTags.HOES).add(ItemRegistry.moonstone_hoe.get());
        this.tag(ItemTags.HOES).add(ItemRegistry.silentwood_hoe.get());
        this.tag(ItemTags.PICKAXES).add(ItemRegistry.aurorian_steel_pickaxe.get());
        this.tag(ItemTags.PICKAXES).add(ItemRegistry.aurorian_stone_pickaxe.get());
        this.tag(ItemTags.PICKAXES).add(ItemRegistry.aurorianite_pickaxe.get());
        this.tag(ItemTags.PICKAXES).add(ItemRegistry.crystalline_pickaxe.get());
        this.tag(ItemTags.PICKAXES).add(ItemRegistry.moonstone_pickaxe.get());
        this.tag(ItemTags.PICKAXES).add(ItemRegistry.silentwood_pickaxe.get());
        this.tag(ItemTags.PICKAXES).add(ItemRegistry.umbra_pickaxe.get());
        this.tag(ItemTags.SHOVELS).add(ItemRegistry.aurorian_steel_shovel.get());
        this.tag(ItemTags.SHOVELS).add(ItemRegistry.aurorian_stone_shovel.get());
        this.tag(ItemTags.SHOVELS).add(ItemRegistry.moonstone_shovel.get());
        this.tag(ItemTags.SHOVELS).add(ItemRegistry.silentwood_shovel.get());
        this.tag(ItemTags.SHOVELS).add(ItemRegistry.aurorianite_shovel.get());
        this.tag(ItemTags.SWORDS).add(ItemRegistry.aurorian_steel_sword.get());
        this.tag(ItemTags.SWORDS).add(ItemRegistry.aurorian_stone_sword.get());
        this.tag(ItemTags.SWORDS).add(ItemRegistry.aurorianite_sword.get());
        this.tag(ItemTags.SWORDS).add(ItemRegistry.crystalline_sword.get());
        this.tag(ItemTags.SWORDS).add(ItemRegistry.moonstone_sword.get());
        this.tag(ItemTags.SWORDS).add(ItemRegistry.silentwood_sword.get());
        this.tag(ItemTags.SWORDS).add(ItemRegistry.umbra_greatsword.get());

        // --- Wood family item tags (mirror block tags for crafting compat) ---
        this.tag(ItemTags.PLANKS).add(BlockRegistry.silentwood_planks.get().asItem(), BlockRegistry.weeping_willow_planks.get().asItem());
        this.tag(ItemTags.LOGS_THAT_BURN).add(BlockRegistry.silentwood_log.get().asItem(), BlockRegistry.weeping_willow_log.get().asItem());
        this.tag(ItemTags.LEAVES).add(BlockRegistry.silentwood_leaves.get().asItem(), BlockRegistry.weeping_willow_leaves.get().asItem());
        this.tag(ItemTags.SAPLINGS).add(BlockRegistry.silentwood_sapling.get().asItem(), BlockRegistry.weeping_willow_sapling.get().asItem());
        this.tag(ItemTags.WOODEN_SLABS).add(BlockRegistry.silentwood_slab.get().asItem());
        this.tag(ItemTags.WOODEN_STAIRS).add(BlockRegistry.silentwood_stairs.get().asItem(), BlockRegistry.weeping_willow_stairs.get().asItem());
        this.tag(ItemTags.WOODEN_FENCES).add(BlockRegistry.silentwood_fence.get().asItem());
        this.tag(ItemTags.FENCES).add(BlockRegistry.silentwood_fence.get().asItem());

        // --- Stone / sand / dirt block items ---
        this.tag(Tags.Items.STONES).add(BlockRegistry.aurorian_stone.get().asItem(), BlockRegistry.aurorian_deepslate.get().asItem(), BlockRegistry.aurorian_stone_brick.get().asItem(), BlockRegistry.peridotite.get().asItem(), BlockRegistry.peridotite_smooth.get().asItem(), BlockRegistry.umbra_stone.get().asItem(), BlockRegistry.umbra_stone_cracked.get().asItem());
        this.tag(Tags.Items.COBBLESTONES).add(BlockRegistry.aurorian_cobblestone.get().asItem());
        this.tag(Tags.Items.COBBLESTONES_NORMAL).add(BlockRegistry.aurorian_cobblestone.get().asItem());
        this.tag(Tags.Items.SANDS).add(BlockRegistry.moon_sand.get().asItem());
        this.tag(Tags.Items.SANDS_COLORLESS).add(BlockRegistry.moon_sand.get().asItem());
        this.tag(ItemTags.SAND).add(BlockRegistry.moon_sand.get().asItem());
        this.tag(ItemTags.DIRT).add(BlockRegistry.aurorian_dirt.get().asItem(), BlockRegistry.aurorian_grass.get().asItem(), BlockRegistry.aurorian_grass_light.get().asItem());

        // --- Glass block items ---
        this.tag(Tags.Items.GLASS_BLOCKS).add(BlockRegistry.aurorian_glass.get().asItem(), BlockRegistry.moon_glass.get().asItem());
        this.tag(Tags.Items.GLASS_BLOCKS_COLORLESS).add(BlockRegistry.aurorian_glass.get().asItem(), BlockRegistry.moon_glass.get().asItem());
        this.tag(Tags.Items.GLASS_PANES).add(BlockRegistry.aurorian_glass_pane.get().asItem(), BlockRegistry.moon_glass_pane.get().asItem());
        this.tag(Tags.Items.GLASS_PANES_COLORLESS).add(BlockRegistry.aurorian_glass_pane.get().asItem(), BlockRegistry.moon_glass_pane.get().asItem());

        // --- Storage block items ---
        this.tag(Tags.Items.STORAGE_BLOCKS).add(BlockRegistry.aurorian_coal_block.get().asItem(), BlockRegistry.aurorian_steel_block.get().asItem(), BlockRegistry.cerulean_block.get().asItem(), BlockRegistry.moonstone_block.get().asItem());
        this.tag(Tags.Items.STORAGE_BLOCKS_COAL).add(BlockRegistry.aurorian_coal_block.get().asItem());

        // --- Ore item categorisation ---
        this.tag(ItemTags.COAL_ORES).add(BlockRegistry.aurorian_coal_ore.get().asItem());
        this.tag(Tags.Items.ORES_IN_GROUND_STONE).add(BlockRegistry.aurorian_coal_ore.get().asItem(), BlockRegistry.cerulean_ore.get().asItem(), BlockRegistry.geode.get().asItem(), BlockRegistry.moonstone_ore.get().asItem());
        this.tag(Tags.Items.ORES_IN_GROUND_DEEPSLATE).add(BlockRegistry.deepslate_cerulean_ore.get().asItem(), BlockRegistry.deepslate_moonstone_ore.get().asItem());
        this.tag(Tags.Items.ORE_RATES_SINGULAR).add(BlockRegistry.aurorian_coal_ore.get().asItem(), BlockRegistry.cerulean_ore.get().asItem(), BlockRegistry.deepslate_cerulean_ore.get().asItem(), BlockRegistry.deepslate_moonstone_ore.get().asItem(), BlockRegistry.geode.get().asItem(), BlockRegistry.moonstone_ore.get().asItem());

        // --- Crops / seeds / mushrooms ---
        this.tag(Tags.Items.CROPS).add(ItemRegistry.lavender.get(), ItemRegistry.silkberry.get());
        this.tag(Tags.Items.SEEDS).add(ItemRegistry.lavender_seeds.get(), ItemRegistry.silkberry_seeds.get());
        this.tag(ItemTags.VILLAGER_PLANTABLE_SEEDS).add(ItemRegistry.lavender_seeds.get(), ItemRegistry.silkberry_seeds.get());
        this.tag(Tags.Items.MUSHROOMS).add(BlockRegistry.mushroom.get().asItem(), BlockRegistry.mushroom_stem.get().asItem(), BlockRegistry.mushroom_crystal.get().asItem(), BlockRegistry.mushroom_small.get().asItem());

        // --- Foods ---
        this.tag(Tags.Items.FOODS).add(ItemRegistry.lavender_bread.get(), ItemRegistry.silkberry.get(), ItemRegistry.silkberry_jam.get(), ItemRegistry.silkberry_jam_sandwich.get(), ItemRegistry.strange_meat.get(), ItemRegistry.aurorian_pork.get(), ItemRegistry.aurorian_bacon.get(), ItemRegistry.cooked_aurorian_pork.get(), ItemRegistry.aurorian_slime_ball.get(), ItemRegistry.silkshroom_stew.get(), ItemRegistry.soulless_flesh.get(), ItemRegistry.bright_bulb_tea.get(), ItemRegistry.lavender_tea.get(), ItemRegistry.petunia_tea.get(), ItemRegistry.silkberry_tea.get());
        this.tag(Tags.Items.FOODS_BERRY).add(ItemRegistry.silkberry.get());

        // --- Tool subclass tags (c:tools is already covered via #minecraft:swords/axes/... but these lists are hand-written) ---
        this.tag(Tags.Items.MELEE_WEAPON_TOOLS).add(ItemRegistry.aurorian_steel_sword.get(), ItemRegistry.aurorian_stone_sword.get(), ItemRegistry.aurorianite_sword.get(), ItemRegistry.crystalline_sword.get(), ItemRegistry.moonstone_sword.get(), ItemRegistry.silentwood_sword.get(), ItemRegistry.umbra_greatsword.get(), ItemRegistry.queens_chipper.get());
        this.tag(Tags.Items.MELEE_WEAPON_TOOLS).add(ItemRegistry.aurorian_steel_axe.get(), ItemRegistry.aurorian_stone_axe.get(), ItemRegistry.aurorianite_axe.get(), ItemRegistry.moonstone_axe.get(), ItemRegistry.silentwood_axe.get());
        this.tag(Tags.Items.MINING_TOOL_TOOLS).add(ItemRegistry.aurorian_steel_pickaxe.get(), ItemRegistry.aurorian_stone_pickaxe.get(), ItemRegistry.aurorianite_pickaxe.get(), ItemRegistry.crystalline_pickaxe.get(), ItemRegistry.moonstone_pickaxe.get(), ItemRegistry.silentwood_pickaxe.get(), ItemRegistry.umbra_pickaxe.get());
        this.tag(Tags.Items.MINING_TOOL_TOOLS).add(ItemRegistry.aurorian_steel_shovel.get(), ItemRegistry.aurorian_stone_shovel.get(), ItemRegistry.moonstone_shovel.get(), ItemRegistry.silentwood_shovel.get(), ItemRegistry.aurorianite_shovel.get());
        this.tag(Tags.Items.MINING_TOOL_TOOLS).add(ItemRegistry.aurorian_steel_hoe.get(), ItemRegistry.aurorian_stone_hoe.get(), ItemRegistry.moonstone_hoe.get(), ItemRegistry.silentwood_hoe.get());
        this.tag(Tags.Items.MINING_TOOL_TOOLS).add(ItemRegistry.aurorian_steel_axe.get(), ItemRegistry.aurorian_stone_axe.get(), ItemRegistry.aurorianite_axe.get(), ItemRegistry.moonstone_axe.get(), ItemRegistry.silentwood_axe.get());
        this.tag(Tags.Items.RANGED_WEAPON_TOOLS).add(ItemRegistry.silentwood_bow.get(), ItemRegistry.keepers_bow.get());
    }
}
