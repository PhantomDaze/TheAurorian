package shiroroku.theaurorian.Entities.Spiderling;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

/**
 * Leaps at the target when it is far away (>= 20 blocks squared).
 */
public class SpiderlingAILeap extends Goal {

    private final SpiderlingEntity entity;
    private LivingEntity target;

    private final float leapVelocity = 0.5f;
    private final int minDistance = 20;

    public SpiderlingAILeap(SpiderlingEntity attacker) {
        this.entity = attacker;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        this.target = this.entity.getTarget();
        if (this.target == null) {
            return false;
        }
        double d0 = this.entity.distanceToSqr(this.target);
        return d0 >= minDistance && this.entity.onGround();
    }

    @Override
    public void tick() {
        this.entity.getLookControl().setLookAt(this.target, this.entity.getMaxHeadYRot(), this.entity.getMaxHeadXRot());
    }

    @Override
    public boolean canContinueToUse() {
        return !this.entity.onGround();
    }

    @Override
    public void start() {
        double d0 = this.target.getX() - this.entity.getX();
        double d1 = this.target.getZ() - this.entity.getZ();
        float f = (float) Math.sqrt(d0 * d0 + d1 * d1);

        double val1 = 1;
        double val2 = 1;
        Vec3 motion = this.entity.getDeltaMovement();

        if (f >= 1.0E-4F) {
            this.entity.setDeltaMovement(motion.x + d0 / f * 0.5D * val1 + motion.x * val2, motion.y, motion.z + d1 / f * 0.5D * val1 + motion.z * val2);
        }
        this.entity.setDeltaMovement(this.entity.getDeltaMovement().x, this.leapVelocity, this.entity.getDeltaMovement().z);
    }
}
