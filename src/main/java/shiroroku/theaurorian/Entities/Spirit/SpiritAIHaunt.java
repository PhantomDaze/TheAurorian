package shiroroku.theaurorian.Entities.Spirit;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

/**
 * Strafes the target while it is not looking at the spirit; closes in, applies
 * blindness when close, and attacks within melee range (upstream behaviour).
 */
public class SpiritAIHaunt extends Goal {

    private enum Direction {
        LEFT, RIGHT
    }

    private final SpiritEntity entity;

    private Direction strafeDirection;
    private final float strafeDistance = 8F;
    private final float strafeSpeed = 0.3F;

    public SpiritAIHaunt(SpiritEntity attacker) {
        this.entity = attacker;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public void stop() {
        this.entity.getNavigation().stop();
        this.entity.zza = 0;
    }

    @Override
    public boolean canUse() {
        if (this.entity.getTarget() != null) {
            LivingEntity target = this.entity.getTarget();
            if (this.entity.distanceTo(target) <= strafeDistance && this.entity.hasLineOfSight(target)) {
                return !SpiritEntity.isLookingAt(this.entity, target, 0.1D);
            }
        }
        return false;
    }

    @Override
    public void start() {
        this.entity.getNavigation().stop();
        this.strafeDirection = getRandomDirection();
    }

    @Override
    public boolean canContinueToUse() {
        if (this.entity.getTarget() != null) {
            LivingEntity target = this.entity.getTarget();
            if (this.entity.distanceTo(target) <= strafeDistance && this.strafeDirection != null && this.entity.hasLineOfSight(target)) {
                return !SpiritEntity.isLookingAt(this.entity, target, 0.1D);
            }
        }
        return false;
    }

    @Override
    public void tick() {
        LivingEntity target = this.entity.getTarget();

        this.entity.getLookControl().setLookAt(target, 40F, 40F);
        if (this.strafeDirection == Direction.RIGHT) {
            this.entity.zza = strafeSpeed;
        } else {
            this.entity.zza = -strafeSpeed;
        }

        if (this.entity.distanceTo(target) >= 2F) {
            this.entity.getNavigation().moveTo(target, strafeSpeed * 3.5);
        } else if (this.entity.distanceTo(target) < 2F && this.entity.getNavigation().getPath() != null) {
            this.entity.getNavigation().stop();
        } else {
            target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 60));
        }

        if (this.entity.distanceTo(target) <= 1.75F) {
            this.entity.doHurtTarget(target);
            this.strafeDirection = null;
        }
    }

    private Direction getRandomDirection() {
        return this.entity.getRandom().nextInt(2) == 0 ? Direction.LEFT : Direction.RIGHT;
    }
}
