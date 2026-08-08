package shiroroku.theaurorian.Items.Loot;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import shiroroku.theaurorian.Items.BaseAurorianSword;
import shiroroku.theaurorian.Registry.ItemRegistry;

public class UmbraGreatsword extends BaseAurorianSword {

    private static final int EFFECT_DURATION = 120;

    public UmbraGreatsword(Tier pTier, int pAttackDamageModifier, float pAttackSpeedModifier, Properties pProperties) {
        super(pTier, pAttackDamageModifier, pAttackSpeedModifier, pProperties);
    }

    @Override
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        if (!(pEntity instanceof Player player)) {
            return;
        }
        if (player.getMainHandItem().is(ItemRegistry.umbra_greatsword.get()) || player.getOffhandItem().is(ItemRegistry.umbra_greatsword.get())) {
            player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, EFFECT_DURATION, 0, false, false, true));
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, EFFECT_DURATION, 0, false, false, true));
            player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, EFFECT_DURATION, 0, false, false, true));
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, EFFECT_DURATION, 1, false, false, true));
        }
    }
}
