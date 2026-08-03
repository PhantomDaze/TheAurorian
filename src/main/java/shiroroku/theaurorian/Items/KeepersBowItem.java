package shiroroku.theaurorian.Items;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Keeper boss bow. A fully pulled shot fires three arrows with high spread.
 */
public class KeepersBowItem extends BaseAurorianBow {

    public KeepersBowItem(Properties properties) {
        super(properties);
    }

    // Keep vanilla bow use duration (72000). A short duration (e.g. 40) ends the
    // use animation and restarts while the player still holds right-click, which
    // makes the pull model loop back to the undrawn frame after a full draw.

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entityLiving, int timeLeft) {
        if (!(entityLiving instanceof Player player)) {
            return;
        }
        ItemStack projectile = player.getProjectile(stack);
        if (projectile.isEmpty()) {
            return;
        }
        int charge = this.getUseDuration(stack, entityLiving) - timeLeft;
        charge = EventHooks.onArrowLoose(stack, level, player, charge, !projectile.isEmpty());
        if (charge < 0) {
            return;
        }
        float power = getPowerForTime(charge);
        if (power < 0.1F) {
            return;
        }
        List<ItemStack> drawn = draw(stack, projectile, player);
        if (level instanceof ServerLevel serverLevel && !drawn.isEmpty()) {
            // Triple-shot: fire three times with high inaccuracy (spread)
            for (int i = 0; i < 3; i++) {
                this.shoot(serverLevel, player, player.getUsedItemHand(), stack, drawn, power * 3.0F, 6.0F, power == 1.0F, null);
            }
        }
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F,
                1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + power * 0.5F);
        player.awardStat(Stats.ITEM_USED.get(this));
    }

    @Override
    protected void shootProjectile(LivingEntity shooter, Projectile projectile, int index,
                                   float velocity, float inaccuracy, float angle, @Nullable LivingEntity target) {
        // Extra base inaccuracy for the keeper triple-shot
        super.shootProjectile(shooter, projectile, index, velocity, inaccuracy + 5.0F, angle, target);
    }
}
