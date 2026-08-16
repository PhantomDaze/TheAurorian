package shiroroku.theaurorian.DataGen;

import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;
import shiroroku.theaurorian.Registry.BlockRegistry;
import shiroroku.theaurorian.Registry.ItemRegistry;
import shiroroku.theaurorian.TheAurorian;

import java.util.function.Supplier;

public class DataGenItemsTags extends TagsProvider<Item> {

    public static final TagKey<Item> ABSORPTION_ORB_REPAIRABLE = ItemTags.create(new ResourceLocation(TheAurorian.MODID, "absorption_orb_repairable"));
    public static final TagKey<Item> AURORIAN_STONES = ItemTags.create(new ResourceLocation(TheAurorian.MODID, "aurorian_stones"));
    public static final TagKey<Item> CERULEAN_ORE = ItemTags.create(new ResourceLocation(TheAurorian.MODID, "cerulean_ore"));
    public static final TagKey<Item> CRYSTALLINE_PICKAXE_TREASURE = ItemTags.create(new ResourceLocation(TheAurorian.MODID, "crystalline_treasure"));
    public static final TagKey<Item> CRYSTALLINE_SHIELD_REPAIRABLE = ItemTags.create(new ResourceLocation(TheAurorian.MODID, "crystalline_shield_repairable"));
    public static final TagKey<Item> KEYS = ItemTags.create(new ResourceLocation(TheAurorian.MODID, "keys"));
    public static final TagKey<Item> LIGHTNING_IMMUNE = ItemTags.create(new ResourceLocation(TheAurorian.MODID, "lightning_immune"));
    public static final TagKey<Item> MOONSTONE_ORE = ItemTags.create(new ResourceLocation(TheAurorian.MODID, "moonstone_ore"));
    public static final TagKey<Item> PORTAL_LIGHTERS = ItemTags.create(new ResourceLocation(TheAurorian.MODID, "portal_lighters"));
    public static final TagKey<Item> SCRAP = ItemTags.create(new ResourceLocation(TheAurorian.MODID, "scrap"));
    public static final TagKey<Item> SPECTRAL_ARMOR = ItemTags.create(new ResourceLocation(TheAurorian.MODID, "spectral_armor"));
    public static final TagKey<Item> TEA = ItemTags.create(new ResourceLocation(TheAurorian.MODID, "tea"));

    // Forge 命名空间下 mod 自定义材料的 ingots/nuggets/storage_blocks 子 tag（统一由 DataGen 生成）
    public static final TagKey<Item> INGOTS_AURORIAN_STEEL = ItemTags.create(new ResourceLocation("forge", "ingots/aurorian_steel"));
    public static final TagKey<Item> INGOTS_CERULEAN = ItemTags.create(new ResourceLocation("forge", "ingots/cerulean"));
    public static final TagKey<Item> INGOTS_MOONSTONE = ItemTags.create(new ResourceLocation("forge", "ingots/moonstone"));
    public static final TagKey<Item> NUGGETS_AURORIAN_STEEL = ItemTags.create(new ResourceLocation("forge", "nuggets/aurorian_steel"));
    public static final TagKey<Item> NUGGETS_CERULEAN = ItemTags.create(new ResourceLocation("forge", "nuggets/cerulean"));
    public static final TagKey<Item> NUGGETS_MOONSTONE = ItemTags.create(new ResourceLocation("forge", "nuggets/moonstone"));
    public static final TagKey<Item> STORAGE_BLOCKS_AURORIAN_STEEL = ItemTags.create(new ResourceLocation("forge", "storage_blocks/aurorian_steel"));
    public static final TagKey<Item> STORAGE_BLOCKS_CERULEAN = ItemTags.create(new ResourceLocation("forge", "storage_blocks/cerulean"));
    public static final TagKey<Item> STORAGE_BLOCKS_MOONSTONE = ItemTags.create(new ResourceLocation("forge", "storage_blocks/moonstone"));

    @SuppressWarnings("deprecation")
    protected DataGenItemsTags(DataGenerator pGenerator, @Nullable ExistingFileHelper existingFileHelper) {
        super(pGenerator, Registry.ITEM, TheAurorian.MODID, existingFileHelper);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void addTags() {
        ItemRegistry.ITEMS_GEN_SHIELD.getEntries().stream().map(Supplier::get).forEach(shield -> this.tag(Tags.Items.TOOLS_SHIELDS).add(shield));
        ItemRegistry.ITEMS_GEN_KEY.getEntries().stream().map(Supplier::get).forEach(key -> this.tag(KEYS).add(key));
        this.tag(ABSORPTION_ORB_REPAIRABLE).addTags(Tags.Items.TOOLS, Tags.Items.ARMORS, Tags.Items.SHEARS);
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
        this.tag(CRYSTALLINE_SHIELD_REPAIRABLE).addTags(Tags.Items.TOOLS, Tags.Items.ARMORS, Tags.Items.SHEARS);
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
        this.tag(Tags.Items.ARMORS_BOOTS).add(ItemRegistry.cerulean_boots.get(), ItemRegistry.spectral_boots.get(), ItemRegistry.aurorian_steel_boots.get(), ItemRegistry.slime_boots.get());
        this.tag(Tags.Items.ARMORS_CHESTPLATES).add(ItemRegistry.cerulean_chestplate.get(), ItemRegistry.spectral_chestplate.get(), ItemRegistry.umbra_chestplate.get(), ItemRegistry.aurorian_steel_chestplate.get(), ItemRegistry.spiked_chestplate.get());
        this.tag(Tags.Items.ARMORS_HELMETS).add(ItemRegistry.cerulean_helmet.get(), ItemRegistry.spectral_helmet.get(), ItemRegistry.spectral_helmet.get());
        this.tag(Tags.Items.ARMORS_LEGGINGS).add(ItemRegistry.cerulean_leggings.get(), ItemRegistry.spectral_leggings.get(), ItemRegistry.aurorian_steel_leggings.get());
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
        // 矿石产出地层（物品端）
        this.tag(Tags.Items.ORE_BEARING_GROUND_STONE).add(BlockRegistry.aurorian_stone.get().asItem());
        this.tag(Tags.Items.ORE_BEARING_GROUND_DEEPSLATE).add(BlockRegistry.aurorian_deepslate.get().asItem());
        this.tag(Tags.Items.ORES_IN_GROUND_STONE).add(
                BlockRegistry.aurorian_coal_ore.get().asItem(),
                BlockRegistry.cerulean_ore.get().asItem(),
                BlockRegistry.moonstone_ore.get().asItem(),
                BlockRegistry.geode.get().asItem());
        this.tag(Tags.Items.ORES_IN_GROUND_DEEPSLATE).add(
                BlockRegistry.deepslate_cerulean_ore.get().asItem(),
                BlockRegistry.deepslate_moonstone_ore.get().asItem());
        // Forge 自定义材料子 tag（替代手写 JSON）
        this.tag(INGOTS_AURORIAN_STEEL).add(ItemRegistry.aurorian_steel_ingot.get());
        this.tag(INGOTS_CERULEAN).add(ItemRegistry.cerulean_ingot.get());
        this.tag(INGOTS_MOONSTONE).add(ItemRegistry.moonstone_ingot.get());
        this.tag(NUGGETS_AURORIAN_STEEL).add(ItemRegistry.aurorian_steel_nugget.get());
        this.tag(NUGGETS_CERULEAN).add(ItemRegistry.cerulean_nugget.get());
        this.tag(NUGGETS_MOONSTONE).add(ItemRegistry.moonstone_nugget.get());
        this.tag(STORAGE_BLOCKS_AURORIAN_STEEL).add(BlockRegistry.aurorian_steel_block.get().asItem());
        this.tag(STORAGE_BLOCKS_CERULEAN).add(BlockRegistry.cerulean_block.get().asItem());
        this.tag(STORAGE_BLOCKS_MOONSTONE).add(BlockRegistry.moonstone_block.get().asItem());
        this.tag(Tags.Items.STORAGE_BLOCKS).add(
                BlockRegistry.aurorian_coal_block.get().asItem(),
                BlockRegistry.aurorian_steel_block.get().asItem(),
                BlockRegistry.cerulean_block.get().asItem(),
                BlockRegistry.moonstone_block.get().asItem());
        this.tag(Tags.Items.STORAGE_BLOCKS_COAL).add(BlockRegistry.aurorian_coal_block.get().asItem());
        this.tag(Tags.Items.STORAGE_BLOCKS_IRON).add(BlockRegistry.aurorian_steel_block.get().asItem());
        // 石/圆石/沙/玻璃
        this.tag(Tags.Items.STONE).add(BlockRegistry.aurorian_stone.get().asItem(), BlockRegistry.peridotite.get().asItem());
        this.tag(Tags.Items.COBBLESTONE).add(BlockRegistry.aurorian_cobblestone.get().asItem());
        this.tag(Tags.Items.COBBLESTONE_NORMAL).add(BlockRegistry.aurorian_cobblestone.get().asItem());
        this.tag(Tags.Items.COBBLESTONE_DEEPSLATE).add(BlockRegistry.aurorian_deepslate.get().asItem());
        this.tag(Tags.Items.SAND).add(BlockRegistry.moon_sand.get().asItem());
        this.tag(Tags.Items.GLASS).add(BlockRegistry.aurorian_glass.get().asItem(), BlockRegistry.moon_glass.get().asItem());
        this.tag(Tags.Items.GLASS_COLORLESS).add(BlockRegistry.aurorian_glass.get().asItem(), BlockRegistry.moon_glass.get().asItem());
        this.tag(Tags.Items.GLASS_PANES).add(BlockRegistry.aurorian_glass_pane.get().asItem(), BlockRegistry.moon_glass_pane.get().asItem());
        this.tag(Tags.Items.GLASS_PANES_COLORLESS).add(BlockRegistry.aurorian_glass_pane.get().asItem(), BlockRegistry.moon_glass_pane.get().asItem());
        // 种子（Forge）+ 原版 SAPLINGS/LEAVES/FLOWERS 物品端
        this.tag(Tags.Items.SEEDS).add(ItemRegistry.lavender_seeds.get(), ItemRegistry.silkberry_seeds.get());
        this.tag(ItemTags.SAPLINGS).add(
                BlockRegistry.silentwood_sapling.get().asItem(),
                BlockRegistry.weeping_willow_sapling.get().asItem(),
                BlockRegistry.mushroom_small.get().asItem());
        this.tag(ItemTags.LEAVES).add(
                BlockRegistry.silentwood_leaves.get().asItem(),
                BlockRegistry.weeping_willow_leaves.get().asItem());
        this.tag(ItemTags.FLOWERS).add(
                BlockRegistry.bright_bulb.get().asItem(),
                BlockRegistry.petunia.get().asItem());
        // 木棍总称
        this.tag(Tags.Items.RODS).add(ItemRegistry.silentwood_stick.get());
        this.tag(Tags.Items.RODS_WOODEN).add(ItemRegistry.silentwood_stick.get());
        this.tag(Tags.Items.SHEARS).add(ItemRegistry.aurorian_stone_sickle.get());
        this.tag(Tags.Items.SHEARS).add(ItemRegistry.moonstone_sickle.get());
        this.tag(Tags.Items.SHEARS).add(ItemRegistry.silentwood_sickle.get());
        this.tag(Tags.Items.STRING).add(ItemRegistry.plant_fiber.get());
        this.tag(Tags.Items.TOOLS_AXES).add(ItemRegistry.aurorian_steel_axe.get());
        this.tag(Tags.Items.TOOLS_AXES).add(ItemRegistry.aurorian_stone_axe.get());
        this.tag(Tags.Items.TOOLS_AXES).add(ItemRegistry.aurorianite_axe.get());
        this.tag(Tags.Items.TOOLS_AXES).add(ItemRegistry.moonstone_axe.get());
        this.tag(Tags.Items.TOOLS_AXES).add(ItemRegistry.silentwood_axe.get());
        this.tag(Tags.Items.TOOLS_BOWS).add(ItemRegistry.silentwood_bow.get());
        this.tag(Tags.Items.TOOLS_HOES).add(ItemRegistry.aurorian_steel_hoe.get());
        this.tag(Tags.Items.TOOLS_HOES).add(ItemRegistry.aurorian_stone_hoe.get());
        this.tag(Tags.Items.TOOLS_HOES).add(ItemRegistry.moonstone_hoe.get());
        this.tag(Tags.Items.TOOLS_HOES).add(ItemRegistry.silentwood_hoe.get());
        this.tag(Tags.Items.TOOLS_PICKAXES).add(ItemRegistry.aurorian_steel_pickaxe.get());
        this.tag(Tags.Items.TOOLS_PICKAXES).add(ItemRegistry.aurorian_stone_pickaxe.get());
        this.tag(Tags.Items.TOOLS_PICKAXES).add(ItemRegistry.aurorianite_pickaxe.get());
        this.tag(Tags.Items.TOOLS_PICKAXES).add(ItemRegistry.crystalline_pickaxe.get());
        this.tag(Tags.Items.TOOLS_PICKAXES).add(ItemRegistry.moonstone_pickaxe.get());
        this.tag(Tags.Items.TOOLS_PICKAXES).add(ItemRegistry.silentwood_pickaxe.get());
        this.tag(Tags.Items.TOOLS_PICKAXES).add(ItemRegistry.umbra_pickaxe.get());
        this.tag(Tags.Items.TOOLS_SHOVELS).add(ItemRegistry.aurorian_steel_shovel.get());
        this.tag(Tags.Items.TOOLS_SHOVELS).add(ItemRegistry.aurorian_stone_shovel.get());
        this.tag(Tags.Items.TOOLS_SHOVELS).add(ItemRegistry.moonstone_shovel.get());
        this.tag(Tags.Items.TOOLS_SHOVELS).add(ItemRegistry.silentwood_shovel.get());
        this.tag(Tags.Items.TOOLS_SHOVELS).add(ItemRegistry.aurorianite_shovel.get());
        this.tag(Tags.Items.TOOLS_SWORDS).add(ItemRegistry.aurorian_steel_sword.get());
        this.tag(Tags.Items.TOOLS_SWORDS).add(ItemRegistry.aurorian_stone_sword.get());
        this.tag(Tags.Items.TOOLS_SWORDS).add(ItemRegistry.aurorianite_sword.get());
        this.tag(Tags.Items.TOOLS_SWORDS).add(ItemRegistry.crystalline_sword.get());
        this.tag(Tags.Items.TOOLS_SWORDS).add(ItemRegistry.moonstone_sword.get());
        this.tag(Tags.Items.TOOLS_SWORDS).add(ItemRegistry.silentwood_sword.get());
        this.tag(Tags.Items.TOOLS_SWORDS).add(ItemRegistry.umbra_greatsword.get());
    }
}
