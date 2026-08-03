package shiroroku.theaurorian.Items.Loot;

import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import shiroroku.theaurorian.Items.BaseAurorianItem;

import java.util.List;

public class StrangeMeat extends BaseAurorianItem {

    public StrangeMeat(Properties properties) {
        super(properties);
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 64;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity living) {
        level.playSound(null, living.getX(), living.getY(), living.getZ(), living.getEatingSound(stack), SoundSource.NEUTRAL, 1.0F, 1.0F + (level.random.nextFloat() - level.random.nextFloat()) * 0.4F);
        List<Holder<MobEffect>> effects = List.of(
                MobEffects.REGENERATION,
                MobEffects.FIRE_RESISTANCE,
                MobEffects.DAMAGE_BOOST,
                MobEffects.SATURATION,
                MobEffects.DIG_SPEED
        );
        living.addEffect(new MobEffectInstance(effects.get(living.getRandom().nextInt(effects.size())), 6000));
        stack.hurtAndBreak(1, living, EquipmentSlot.MAINHAND);
        if (living instanceof Player player) {
            player.getFoodData().eat(8, 0.8F);
        }
        return stack;
    }
}
