package shiroroku.theaurorian.Registry;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.ForgeTier;
import net.minecraftforge.common.Tags;
import shiroroku.theaurorian.TheAurorian;

import java.util.function.Supplier;

public class MaterialTiers {

    public static final ForgeTier AURORIANITE = new ForgeTier(3, 500, 8f, 3f, 20, BlockTags.NEEDS_DIAMOND_TOOL, () -> Ingredient.of(ItemRegistry.aurorianite_ingot.get()));
    public static final ForgeTier AURORIAN_STEEL = new ForgeTier(3, 1500, 8.5f, 3.5f, 10, BlockTags.NEEDS_DIAMOND_TOOL, () -> Ingredient.of(ItemRegistry.aurorian_steel_ingot.get()));
    public static final ForgeTier AURORIAN_STONE = new ForgeTier(1, 131, 4.5f, 1.5f, 14, BlockTags.NEEDS_STONE_TOOL, () -> Ingredient.of(BlockRegistry.aurorian_cobblestone.get()));
    public static final ForgeTier CERULEAN = new ForgeTier(2, 150, 7, 2.5f, 20, BlockTags.NEEDS_IRON_TOOL, () -> Ingredient.of(ItemRegistry.cerulean_ingot.get()));
    public static final ForgeTier CRYSTALLINE = new ForgeTier(3, 500, 8f, 3f, 20, BlockTags.NEEDS_DIAMOND_TOOL, () -> Ingredient.of(ItemRegistry.crystalline_ingot.get()));
    public static final ForgeTier MOONSTONE = new ForgeTier(2, 300, 7, 2.5f, 14, BlockTags.NEEDS_IRON_TOOL, () -> Ingredient.of(ItemRegistry.moonstone_ingot.get()));
    public static final ForgeTier MOON_SHIELD = new ForgeTier(2, 512, 7, 2.5f, 14, BlockTags.NEEDS_IRON_TOOL, () -> Ingredient.of(ItemRegistry.moonstone_ingot.get()));
    public static final ForgeTier SILENTWOOD = new ForgeTier(0, 59, 3, 0, 20, Tags.Blocks.NEEDS_WOOD_TOOL, () -> Ingredient.of(BlockRegistry.silentwood_planks.get()));
    public static final ForgeTier UMBRA = new ForgeTier(3, 500, 8f, 3f, 20, BlockTags.NEEDS_DIAMOND_TOOL, () -> Ingredient.of(ItemRegistry.umbra_ingot.get()));

    public static final ArmorMaterial AURORIAN_STEEL_ARMOR = armorBuilder(AURORIAN_STEEL, "aurorian_steel", 33, new int[]{4, 7, 8, 4}, SoundEvents.ARMOR_EQUIP_NETHERITE, 1.0F, 0.1F);
    public static final ArmorMaterial CERULEAN_ARMOR = armorBuilder(CERULEAN, "cerulean", 20, new int[]{3, 6, 5, 3}, SoundEvents.ARMOR_EQUIP_DIAMOND, 1.0F, 0.0F);
    public static final ArmorMaterial KNIGHT_ARMOR = armorBuilder("knight", 20, new int[]{3, 6, 5, 3}, 5, SoundEvents.ARMOR_EQUIP_IRON, 0.0F, 0.0F, () -> Ingredient.EMPTY);
    public static final ArmorMaterial SPECTRAL_ARMOR = armorBuilder("spectral", 20, new int[]{4, 6, 6, 4}, 25, SoundEvents.ARMOR_EQUIP_LEATHER, 0.0F, 0.0F, () -> Ingredient.of(ItemRegistry.spectral_silk.get()));
    public static final ArmorMaterial SLIME_ARMOR = armorBuilder("aurorian_slime", 120, new int[]{1, 2, 3, 1}, 20, SoundEvents.SLIME_SQUISH, 0.0F, 0.0F, () -> Ingredient.of(ItemRegistry.aurorian_slime_ball.get()));
    public static final ArmorMaterial SPIKED_ARMOR = armorBuilder("spiked", 65, new int[]{3, 6, 5, 3}, 15, SoundEvents.ARMOR_EQUIP_IRON, 1.0F, 0.0F, () -> Ingredient.of(ItemRegistry.umbra_ingot.get()));
    public static final ArmorMaterial UMBRA_ARMOR = armorBuilder(UMBRA, "umbra", 20, new int[]{4, 6, 6, 4}, SoundEvents.ARMOR_EQUIP_DIAMOND, 0.0F, 0.2F);

    // order: BOOTS, LEGGINGS, CHESTPLATE, HELMET (ArmorItem.Type ordinal)
    private static final int[] ARMOR_BASE_DURABILITIES = new int[]{13, 15, 16, 11};

    private static ArmorMaterial armorBuilder(ForgeTier pTier, String pName, int pDurabilityMultiplier, int[] pSlotProtections, SoundEvent pSound, float pToughness, float pKnockbackResistance) {
        return armorBuilder(pName, pDurabilityMultiplier, pSlotProtections, pTier.getEnchantmentValue(), pSound, pToughness, pKnockbackResistance, pTier::getRepairIngredient);
    }

    private static ArmorMaterial armorBuilder(String pName, int pDurabilityMultiplier, int[] pSlotProtections, int pEnchantmentValue, SoundEvent pSound, float pToughness, float pKnockbackResistance, Supplier<Ingredient> pRepairIngredient) {
        // pSlotProtections is boots,legs,chest,helmet matching EquipmentSlot indices 0-3 historically
        // ArmorItem.Type: HELMET=0, CHESTPLATE=1, LEGGINGS=2, BOOTS=3 for getDurability / defense arrays in vanilla
        // Our array is [boots, legs, chest, helmet] = EquipmentSlot index order
        return new ArmorMaterial() {
            private int index(ArmorItem.Type type) {
                return switch (type) {
                    case BOOTS -> 0;
                    case LEGGINGS -> 1;
                    case CHESTPLATE -> 2;
                    case HELMET -> 3;
                };
            }

            @Override
            public int getDurabilityForType(ArmorItem.Type type) {
                return ARMOR_BASE_DURABILITIES[index(type)] * pDurabilityMultiplier;
            }

            @Override
            public int getDefenseForType(ArmorItem.Type type) {
                return pSlotProtections[index(type)];
            }

            @Override
            public int getEnchantmentValue() {
                return pEnchantmentValue;
            }

            @Override
            public SoundEvent getEquipSound() {
                return pSound;
            }

            @Override
            public Ingredient getRepairIngredient() {
                return pRepairIngredient.get();
            }

            @Override
            public String getName() {
                return TheAurorian.MODID + ":" + pName;
            }

            @Override
            public float getToughness() {
                return pToughness;
            }

            @Override
            public float getKnockbackResistance() {
                return pKnockbackResistance;
            }
        };
    }
}
