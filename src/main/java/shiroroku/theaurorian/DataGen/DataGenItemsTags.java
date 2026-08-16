package shiroroku.theaurorian.DataGen;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;
import shiroroku.theaurorian.Registry.BlockRegistry;
import shiroroku.theaurorian.Registry.ItemRegistry;
import shiroroku.theaurorian.TheAurorian;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class DataGenItemsTags extends ItemTagsProvider {

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
    // forge 命名空间下 mod 自定义材料的子 tag（forge 未提供常量，需手动声明）
    public static final TagKey<Item> INGOTS_AURORIAN_STEEL = ItemTags.create(new ResourceLocation("forge", "ingots/aurorian_steel"));
    public static final TagKey<Item> INGOTS_AURORIANITE = ItemTags.create(new ResourceLocation("forge", "ingots/aurorianite"));
    public static final TagKey<Item> INGOTS_CERULEAN = ItemTags.create(new ResourceLocation("forge", "ingots/cerulean"));
    public static final TagKey<Item> INGOTS_CRYSTALLINE = ItemTags.create(new ResourceLocation("forge", "ingots/crystalline"));
    public static final TagKey<Item> INGOTS_MOONSTONE = ItemTags.create(new ResourceLocation("forge", "ingots/moonstone"));
    public static final TagKey<Item> INGOTS_UMBRA = ItemTags.create(new ResourceLocation("forge", "ingots/umbra"));
    public static final TagKey<Item> NUGGETS_AURORIAN_STEEL = ItemTags.create(new ResourceLocation("forge", "nuggets/aurorian_steel"));
    public static final TagKey<Item> NUGGETS_CERULEAN = ItemTags.create(new ResourceLocation("forge", "nuggets/cerulean"));
    public static final TagKey<Item> NUGGETS_MOONSTONE = ItemTags.create(new ResourceLocation("forge", "nuggets/moonstone"));
    public static final TagKey<Item> STORAGE_BLOCKS_AURORIAN_STEEL = ItemTags.create(new ResourceLocation("forge", "storage_blocks/aurorian_steel"));
    public static final TagKey<Item> STORAGE_BLOCKS_CERULEAN = ItemTags.create(new ResourceLocation("forge", "storage_blocks/cerulean"));
    public static final TagKey<Item> STORAGE_BLOCKS_MOONSTONE = ItemTags.create(new ResourceLocation("forge", "storage_blocks/moonstone"));
    public static final TagKey<Item> ORES_CERULEAN = ItemTags.create(new ResourceLocation("forge", "ores/cerulean"));
    public static final TagKey<Item> ORES_MOONSTONE = ItemTags.create(new ResourceLocation("forge", "ores/moonstone"));
    public static final TagKey<Item> COAL = ItemTags.create(new ResourceLocation("forge", "coal"));
    public static final TagKey<Item> SEEDS_LAVENDER = ItemTags.create(new ResourceLocation("forge", "seeds/lavender"));
    public static final TagKey<Item> SEEDS_SILKBERRY = ItemTags.create(new ResourceLocation("forge", "seeds/silkberry"));
    public static final TagKey<Item> CROPS_LAVENDER = ItemTags.create(new ResourceLocation("forge", "crops/lavender"));
    public static final TagKey<Item> CROPS_SILKBERRY = ItemTags.create(new ResourceLocation("forge", "crops/silkberry"));

    protected DataGenItemsTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagsProvider.TagLookup<Block>> blockTags, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, TheAurorian.MODID, existingFileHelper);
    }

@SuppressWarnings("unchecked")
    @Override
    protected void addTags(HolderLookup.Provider provider) {
        // 从方块 tag 复制到物品 tag（木制品衍生）：logs/planks/slabs/stairs/walls/fences/leaves/saplings/flowers/small_flowers
        this.copy(BlockTags.LOGS, ItemTags.LOGS);
        this.copy(BlockTags.LOGS_THAT_BURN, ItemTags.LOGS_THAT_BURN);
        this.copy(BlockTags.PLANKS, ItemTags.PLANKS);
        this.copy(BlockTags.SLABS, ItemTags.SLABS);
        this.copy(BlockTags.STAIRS, ItemTags.STAIRS);
        this.copy(BlockTags.WALLS, ItemTags.WALLS);
        this.copy(BlockTags.FENCES, ItemTags.FENCES);
        this.copy(BlockTags.WOODEN_FENCES, ItemTags.WOODEN_FENCES);
        this.copy(BlockTags.WOODEN_SLABS, ItemTags.WOODEN_SLABS);
        this.copy(BlockTags.WOODEN_STAIRS, ItemTags.WOODEN_STAIRS);
        this.copy(BlockTags.LEAVES, ItemTags.LEAVES);
        this.copy(BlockTags.SAPLINGS, ItemTags.SAPLINGS);
        this.copy(BlockTags.FLOWERS, ItemTags.FLOWERS);
        this.copy(BlockTags.SMALL_FLOWERS, ItemTags.SMALL_FLOWERS);
        this.copy(BlockTags.SAND, ItemTags.SAND);
        this.copy(BlockTags.DIRT, ItemTags.DIRT);
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
        // 锭子标签（forge:ingots/<material>，与原版 forge 体系一致）
        this.tag(INGOTS_AURORIAN_STEEL).add(ItemRegistry.aurorian_steel_ingot.get());
        this.tag(INGOTS_AURORIANITE).add(ItemRegistry.aurorianite_ingot.get());
        this.tag(INGOTS_CERULEAN).add(ItemRegistry.cerulean_ingot.get());
        this.tag(INGOTS_CRYSTALLINE).add(ItemRegistry.crystalline_ingot.get());
        this.tag(INGOTS_MOONSTONE).add(ItemRegistry.moonstone_ingot.get());
        this.tag(INGOTS_UMBRA).add(ItemRegistry.umbra_ingot.get());
        // 父标签通过子标签引用，符合 forge 约定
        this.tag(Tags.Items.INGOTS).addTags(INGOTS_AURORIAN_STEEL, INGOTS_AURORIANITE, INGOTS_CERULEAN, INGOTS_CRYSTALLINE, INGOTS_MOONSTONE, INGOTS_UMBRA);
        // 粒子标签（forge:nuggets/<material>）
        this.tag(NUGGETS_AURORIAN_STEEL).add(ItemRegistry.aurorian_steel_nugget.get());
        this.tag(NUGGETS_CERULEAN).add(ItemRegistry.cerulean_nugget.get());
        this.tag(NUGGETS_MOONSTONE).add(ItemRegistry.moonstone_nugget.get());
        this.tag(Tags.Items.NUGGETS).addTags(NUGGETS_AURORIAN_STEEL, NUGGETS_CERULEAN, NUGGETS_MOONSTONE);
        this.tag(Tags.Items.NUGGETS).add(ItemRegistry.aurorian_coal_nugget.get());
        // 存储块子标签（forge:storage_blocks/<material>）+ 父标签引用
        this.tag(STORAGE_BLOCKS_AURORIAN_STEEL).add(BlockRegistry.aurorian_steel_block.get().asItem());
        this.tag(STORAGE_BLOCKS_CERULEAN).add(BlockRegistry.cerulean_block.get().asItem());
        this.tag(STORAGE_BLOCKS_MOONSTONE).add(BlockRegistry.moonstone_block.get().asItem());
        this.tag(Tags.Items.STORAGE_BLOCKS).addTags(STORAGE_BLOCKS_AURORIAN_STEEL, STORAGE_BLOCKS_CERULEAN, STORAGE_BLOCKS_MOONSTONE);
        this.tag(Tags.Items.STORAGE_BLOCKS).addTag(Tags.Items.STORAGE_BLOCKS_COAL);
        // 矿石：子标签 + 父标签引用（与现有 theaurorian:cerulean_ore 并存，二者均可被检索）
        this.tag(ORES_CERULEAN).add(BlockRegistry.cerulean_ore.get().asItem(), BlockRegistry.deepslate_cerulean_ore.get().asItem());
        this.tag(ORES_MOONSTONE).add(BlockRegistry.moonstone_ore.get().asItem(), BlockRegistry.deepslate_moonstone_ore.get().asItem());
        this.tag(Tags.Items.ORES).addTags(ORES_CERULEAN, ORES_MOONSTONE);
        this.tag(Tags.Items.ORES).add(BlockRegistry.geode.get().asItem(), BlockRegistry.aurorian_coal_ore.get().asItem());
        this.tag(Tags.Items.ORES_COAL).add(BlockRegistry.aurorian_coal_ore.get().asItem());
        this.tag(Tags.Items.RODS_WOODEN).add(ItemRegistry.silentwood_stick.get());
        this.tag(Tags.Items.SHEARS).add(ItemRegistry.aurorian_stone_sickle.get());
        this.tag(Tags.Items.SHEARS).add(ItemRegistry.moonstone_sickle.get());
        this.tag(Tags.Items.SHEARS).add(ItemRegistry.silentwood_sickle.get());
        this.tag(Tags.Items.STRING).add(ItemRegistry.plant_fiber.get());
        // 煤（forge:coal，便于合成袋/燃料配方识别）
        this.tag(COAL).add(ItemRegistry.aurorian_coal.get());
        // 玻璃 / 玻璃板
        this.tag(Tags.Items.GLASS).add(BlockRegistry.aurorian_glass.get().asItem(), BlockRegistry.moon_glass.get().asItem());
        this.tag(Tags.Items.GLASS_PANES).add(BlockRegistry.aurorian_glass_pane.get().asItem(), BlockRegistry.moon_glass_pane.get().asItem());
        // 存储 block 物品
        this.tag(Tags.Items.STORAGE_BLOCKS_COAL).add(BlockRegistry.aurorian_coal_block.get().asItem());
        // 矿石子标签 + 产出地层（item 侧，forge 命名空间）
        this.tag(ORES_CERULEAN).add(BlockRegistry.cerulean_ore.get().asItem(), BlockRegistry.deepslate_cerulean_ore.get().asItem());
        this.tag(ORES_MOONSTONE).add(BlockRegistry.moonstone_ore.get().asItem(), BlockRegistry.deepslate_moonstone_ore.get().asItem());
        this.tag(Tags.Items.ORES_IN_GROUND_STONE).add(BlockRegistry.aurorian_coal_ore.get().asItem(), BlockRegistry.cerulean_ore.get().asItem(), BlockRegistry.moonstone_ore.get().asItem());
        this.tag(Tags.Items.ORES_IN_GROUND_DEEPSLATE).add(BlockRegistry.deepslate_cerulean_ore.get().asItem(), BlockRegistry.deepslate_moonstone_ore.get().asItem());
        // 种子（forge:seeds/<crop>，便于动物繁殖/合成识别）
        this.tag(SEEDS_LAVENDER).add(ItemRegistry.lavender_seeds.get());
        this.tag(SEEDS_SILKBERRY).add(ItemRegistry.silkberry_seeds.get());
        // 作物（forge:crops/<crop>）
        this.tag(CROPS_LAVENDER).add(ItemRegistry.lavender.get());
        this.tag(CROPS_SILKBERRY).add(ItemRegistry.silkberry.get());
        this.tag(ItemTags.AXES).add(ItemRegistry.aurorian_steel_axe.get());
        this.tag(ItemTags.AXES).add(ItemRegistry.aurorian_stone_axe.get());
        this.tag(ItemTags.AXES).add(ItemRegistry.aurorianite_axe.get());
        this.tag(ItemTags.AXES).add(ItemRegistry.moonstone_axe.get());
        this.tag(ItemTags.AXES).add(ItemRegistry.silentwood_axe.get());
        this.tag(Tags.Items.TOOLS_BOWS).add(ItemRegistry.silentwood_bow.get());
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
    }
}
