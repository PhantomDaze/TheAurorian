package shiroroku.theaurorian.Items;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Weeping willow sap. Eating it converts active Poison into Slowness of the same
 * duration (upstream behaviour), and it burns as fuel.
 */
public class WeepingWillowSap extends Item {

    public WeepingWillowSap(Properties pProperties) {
        super(pProperties.food(new FoodProperties.Builder().nutrition(1).saturationMod(0.0F).build()));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack pStack, Level pLevel, LivingEntity pEntity) {
        ItemStack result = super.finishUsingItem(pStack, pLevel, pEntity);
        if (!pLevel.isClientSide) {
            MobEffectInstance poison = pEntity.getEffect(MobEffects.POISON);
            if (poison != null) {
                pEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, poison.getDuration(), poison.getAmplifier()));
                pEntity.removeEffect(MobEffects.POISON);
            }
        }
        return result;
    }

    @Override
    public int getBurnTime(ItemStack itemStack, net.minecraft.world.item.crafting.RecipeType<?> recipeType) {
        return 1600;
    }
}
