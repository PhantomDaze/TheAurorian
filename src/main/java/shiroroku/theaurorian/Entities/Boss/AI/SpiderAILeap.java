package shiroroku.theaurorian.Entities.Boss.AI;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;
import shiroroku.theaurorian.Entities.Boss.DungeonSpiderEntity;

import java.util.EnumSet;

/**
 * Leaps at targets more than 20 blocks away.
 */
public class SpiderAILeap extends Goal {

    private final DungeonSpiderEntity entity;
    private LivingEntity target;
    private final float leapVelocity = 0.7f;
    private final int minDistance = 20;

    public SpiderAILeap(DungeonSpiderEntity attacker) {
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
    public boolean canContinueToUse() {
        return !this.entity.onGround();
    }

    @Override
    public void start() {
        if (this.target != null) {
            // S3: face target when leaping
            this.entity.getLookControl().setLookAt(this.target, 30.0F, 30.0F);
        }
        double dx = this.target.getX() - this.entity.getX();
        double dz = this.target.getZ() - this.entity.getZ();
        float f = (float) Math.sqrt(dx * dx + dz * dz);
        Vec3 motion = this.entity.getDeltaMovement();
        double val1 = 2;
        double val2 = 4;
        if (f >= 1.0E-4F) {
            this.entity.setDeltaMovement(
                    motion.x + dx / f * 0.5D * val1 + motion.x * val2,
                    this.leapVelocity,
                    motion.z + dz / f * 0.5D * val1 + motion.z * val2);
        } else {
            this.entity.setDeltaMovement(motion.x, this.leapVelocity, motion.z);
        }
        this.entity.hasImpulse = true;
    }

    @Override
    public void tick() {
        // S3: keep facing target mid-air
        if (this.target != null) {
            this.entity.getLookControl().setLookAt(this.target, 30.0F, 30.0F);
        }
    }
}
