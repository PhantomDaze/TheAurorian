package shiroroku.theaurorian.Entities.Boss.AI;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;
import shiroroku.theaurorian.Entities.Boss.MoonQueenEntity;
import shiroroku.theaurorian.Registry.ItemRegistry;

import java.util.EnumSet;

/**
 * Moon Queen strafes around the player at mid range while blocking with a
 * shield, attacking when close.
 */
public class MoonQueenAISideStrafe extends Goal {

    private enum Direction {
        LEFT, RIGHT
    }

    private final MoonQueenEntity entity;
    private Direction strafeDirection;
    private final float strafeDistance = 8.0F;
    private final float strafeSpeed = 0.3F;
    private int strafeTimer;

    public MoonQueenAISideStrafe(MoonQueenEntity attacker) {
        this.entity = attacker;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public void stop() {
        this.entity.stopUsingItem();
        this.entity.getNavigation().stop();
        this.entity.zza = 0;
    }

    @Override
    public boolean canUse() {
        if (this.entity.getTarget() != null) {
            if (this.entity.distanceTo(this.entity.getTarget()) <= this.strafeDistance && !this.entity.isCharging()) {
                if (this.strafeTimer == 0) {
                    return true;
                } else if (this.strafeTimer > 0) {
                    this.strafeTimer--;
                }
            }
        }
        return false;
    }

    @Override
    public void start() {
        this.entity.getNavigation().stop();
        this.strafeDirection = this.getRandomDirection();
        this.strafeTimer = 10;
        this.entity.setItemInHand(InteractionHand.OFF_HAND, new ItemStack(ItemRegistry.moonstone_shield.get()));
        this.entity.startUsingItem(InteractionHand.OFF_HAND);
    }

    @Override
    public boolean canContinueToUse() {
        if (this.entity.getTarget() != null && !this.entity.isCharging()) {
            return this.entity.distanceTo(this.entity.getTarget()) <= this.strafeDistance && this.strafeDirection != null;
        }
        return false;
    }

    @Override
    public void tick() {
        LivingEntity target = this.entity.getTarget();
        if (target == null) {
            return;
        }
        this.entity.getLookControl().setLookAt(target, 40.0F, 40.0F);
        this.entity.zza = this.strafeDirection == Direction.RIGHT ? this.strafeSpeed : -this.strafeSpeed;

        if (this.entity.distanceTo(target) <= 2.5F) {
            this.entity.doHurtTarget(target);
            this.strafeDirection = null;
        }
    }

    private Direction getRandomDirection() {
        return this.entity.getRandom().nextInt(2) == 0 ? Direction.LEFT : Direction.RIGHT;
    }
}
