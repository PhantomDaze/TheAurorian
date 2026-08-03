package shiroroku.theaurorian.Registry;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.SimpleTier;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import shiroroku.theaurorian.TheAurorian;

import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;

public class MaterialTiers {

    public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS = DeferredRegister.create(Registries.ARMOR_MATERIAL, TheAurorian.MODID);

    // Tool tiers updated for 1.21 SimpleTier(tag, uses, speed, attackDmgBonus, enchantVal, repair)
    public static final SimpleTier AURORIANITE = new SimpleTier(BlockTags.NEEDS_DIAMOND_TOOL, 500, 8f, 3f, 20, () -> Ingredient.of(ItemRegistry.aurorianite_ingot.get()));
    public static final SimpleTier AURORIAN_STEEL = new SimpleTier(BlockTags.NEEDS_DIAMOND_TOOL, 1500, 8.5f, 3.5f, 10, () -> Ingredient.of(ItemRegistry.aurorian_steel_ingot.get()));
    public static final SimpleTier AURORIAN_STONE = new SimpleTier(BlockTags.NEEDS_STONE_TOOL, 131, 4.5f, 1.5f, 14, () -> Ingredient.of(BlockRegistry.aurorian_cobblestone.get()));
    public static final SimpleTier CERULEAN = new SimpleTier(BlockTags.NEEDS_IRON_TOOL, 150, 7, 2.5f, 20, () -> Ingredient.of(ItemRegistry.cerulean_ingot.get()));
    public static final SimpleTier CRYSTALLINE = new SimpleTier(BlockTags.NEEDS_DIAMOND_TOOL, 500, 8f, 3f, 20, () -> Ingredient.of(ItemRegistry.crystalline_ingot.get()));
    public static final SimpleTier MOONSTONE = new SimpleTier(BlockTags.NEEDS_IRON_TOOL, 300, 7, 2.5f, 14, () -> Ingredient.of(ItemRegistry.moonstone_ingot.get()));
    public static final SimpleTier MOON_SHIELD = new SimpleTier(BlockTags.NEEDS_IRON_TOOL, 512, 7, 2.5f, 14, () -> Ingredient.of(ItemRegistry.moonstone_ingot.get()));
    public static final SimpleTier SILENTWOOD = new SimpleTier(Tags.Blocks.NEEDS_WOOD_TOOL, 59, 3, 0, 20, () -> Ingredient.of(BlockRegistry.silentwood_planks.get()));
    public static final SimpleTier UMBRA = new SimpleTier(BlockTags.NEEDS_DIAMOND_TOOL, 500, 8f, 3f, 20, () -> Ingredient.of(ItemRegistry.umbra_ingot.get()));

    // Armor materials now records registered via DeferredRegister
    // Durability factors kept for use in item properties: .durability( Type.getDurability(factor) )
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> AURORIAN_STEEL_ARMOR = ARMOR_MATERIALS.register("aurorian_steel", () -> makeArmorMaterial("aurorian_steel", new int[]{4, 7, 8, 4}, 10, SoundEvents.ARMOR_EQUIP_NETHERITE, 1.0F, 0.1F, () -> Ingredient.of(ItemRegistry.aurorian_steel_ingot.get())));
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> CERULEAN_ARMOR = ARMOR_MATERIALS.register("cerulean", () -> makeArmorMaterial("cerulean", new int[]{3, 6, 5, 3}, 20, SoundEvents.ARMOR_EQUIP_DIAMOND, 1.0F, 0.0F, () -> Ingredient.of(ItemRegistry.cerulean_ingot.get())));
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> KNIGHT_ARMOR = ARMOR_MATERIALS.register("knight", () -> makeArmorMaterial("knight", new int[]{3, 6, 5, 3}, 5, SoundEvents.ARMOR_EQUIP_IRON, 0.0F, 0.0F, () -> Ingredient.EMPTY));
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> SPECTRAL_ARMOR = ARMOR_MATERIALS.register("spectral", () -> makeArmorMaterial("spectral", new int[]{4, 6, 6, 4}, 25, SoundEvents.ARMOR_EQUIP_LEATHER, 0.0F, 0.0F, () -> Ingredient.of(ItemRegistry.spectral_silk.get())));
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> SLIME_ARMOR = ARMOR_MATERIALS.register("aurorian_slime", () -> makeArmorMaterial("aurorian_slime", new int[]{1, 2, 3, 1}, 20, Holder.direct(SoundEvents.SLIME_SQUISH), 0.0F, 0.0F, () -> Ingredient.of(ItemRegistry.aurorian_slime_ball.get())));
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> SPIKED_ARMOR = ARMOR_MATERIALS.register("spiked", () -> makeArmorMaterial("spiked", new int[]{3, 6, 5, 3}, 15, SoundEvents.ARMOR_EQUIP_IRON, 1.0F, 0.0F, () -> Ingredient.of(ItemRegistry.umbra_ingot.get())));
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> UMBRA_ARMOR = ARMOR_MATERIALS.register("umbra", () -> makeArmorMaterial("umbra", new int[]{4, 6, 6, 4}, 20, SoundEvents.ARMOR_EQUIP_DIAMOND, 0.0F, 0.2F, () -> Ingredient.of(ItemRegistry.umbra_ingot.get())));

    // Durability multipliers (for reference when setting item durability)
    public static final int AURORIAN_STEEL_ARMOR_DURABILITY = 33;
    public static final int CERULEAN_ARMOR_DURABILITY = 20;
    public static final int KNIGHT_ARMOR_DURABILITY = 20;
    public static final int SPECTRAL_ARMOR_DURABILITY = 20;
    public static final int SLIME_ARMOR_DURABILITY = 120;
    public static final int SPIKED_ARMOR_DURABILITY = 65;
    public static final int UMBRA_ARMOR_DURABILITY = 20;

    private static ArmorMaterial makeArmorMaterial(String pName, int[] pSlotProtections, int pEnchantmentValue, Holder<SoundEvent> pSound, float pToughness, float pKnockbackResistance, Supplier<Ingredient> pRepairIngredient) {
        // pSlotProtections order: boots, legs, chest, helmet (matching historical)
        EnumMap<ArmorItem.Type, Integer> defense = new EnumMap<>(ArmorItem.Type.class);
        defense.put(ArmorItem.Type.BOOTS, pSlotProtections[0]);
        defense.put(ArmorItem.Type.LEGGINGS, pSlotProtections[1]);
        defense.put(ArmorItem.Type.CHESTPLATE, pSlotProtections[2]);
        defense.put(ArmorItem.Type.HELMET, pSlotProtections[3]);

        List<ArmorMaterial.Layer> layers = List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(TheAurorian.MODID, pName)));

        return new ArmorMaterial(defense, pEnchantmentValue, pSound, pRepairIngredient, layers, pToughness, pKnockbackResistance);
    }
}
