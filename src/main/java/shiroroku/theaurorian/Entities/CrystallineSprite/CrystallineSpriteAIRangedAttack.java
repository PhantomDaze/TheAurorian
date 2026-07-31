package shiroroku.theaurorian.Entities.CrystallineSprite;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

/**
 * Ranged attack goal for the Crystalline Sprite. Only opens fire while the
 * target is further than 5 blocks away (upstream behaviour).
 */
public class CrystallineSpriteAIRangedAttack extends Goal {

    private final CrystallineSpriteEntity entityHost;
    private LivingEntity attackTarget;

    private int rangedAttackTime;
    private final double entityMoveSpeed;
    private int seeTime;
    private final int attackIntervalMin;
    private final int maxRangedAttackTime;
    private final float attackRadius;
    private final float maxAttackDistance;

    public CrystallineSpriteAIRangedAttack(CrystallineSpriteEntity attacker, double movespeed, int maxAttackTime, float maxAttackDistanceIn) {
        this(attacker, movespeed, maxAttackTime, maxAttackTime, maxAttackDistanceIn);
    }

    public CrystallineSpriteAIRangedAttack(CrystallineSpriteEntity attacker, double movespeed, int attackIntervalMin, int maxAttackTime, float maxAttackDistanceIn) {
        this.rangedAttackTime = -1;
        this.entityHost = attacker;
        this.entityMoveSpeed = movespeed;
        this.attackIntervalMin = attackIntervalMin;
        this.maxRangedAttackTime = maxAttackTime;
        this.attackRadius = maxAttackDistanceIn;
        this.maxAttackDistance = maxAttackDistanceIn * maxAttackDistanceIn;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity livingentity = this.entityHost.getTarget();
        if (livingentity == null) {
            return false;
        }
        if (livingentity.distanceTo(this.entityHost) > 5) {
            this.attackTarget = livingentity;
            return true;
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return this.canUse() || !this.entityHost.getNavigation().isDone();
    }

    @Override
    public void stop() {
        this.attackTarget = null;
        this.seeTime = 0;
        this.rangedAttackTime = -1;
    }

    @Override
    public void tick() {
        double d0 = this.entityHost.distanceToSqr(this.attackTarget.getX(), this.attackTarget.getBoundingBox().minY, this.attackTarget.getZ());
        boolean flag = this.entityHost.hasLineOfSight(this.attackTarget);
        if (flag) {
            ++this.seeTime;
        } else {
            this.seeTime = 0;
        }
        if (d0 <= (double) this.maxAttackDistance && this.seeTime >= 20) {
            this.entityHost.getNavigation().stop();
        } else {
            this.entityHost.getNavigation().moveTo(this.attackTarget, this.entityMoveSpeed);
        }
        this.entityHost.getLookControl().setLookAt(this.attackTarget, 30.0F, 30.0F);
        if (--this.rangedAttackTime == 0) {
            if (!flag) {
                return;
            }
            float f = Mth.sqrt((float) d0) / this.attackRadius;
            float f1 = Mth.clamp(f, 0.1F, 1.0F);
            this.entityHost.performRangedAttack(this.attackTarget, f1);
            this.rangedAttackTime = Mth.floor(f * (float) (this.maxRangedAttackTime - this.attackIntervalMin) + (float) this.attackIntervalMin);
        } else if (this.rangedAttackTime < 0) {
            float f2 = Mth.sqrt((float) d0) / this.attackRadius;
            this.rangedAttackTime = Mth.floor(f2 * (float) (this.maxRangedAttackTime - this.attackIntervalMin) + (float) this.attackIntervalMin);
        }
    }
}
