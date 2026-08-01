package shiroroku.theaurorian.Entities.Boss.AI;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import shiroroku.theaurorian.Entities.Boss.DungeonSpiderEntity;
import shiroroku.theaurorian.Entities.Webbing.WebbingEntity;

import java.util.EnumSet;

/**
 * Spider Mother winds up and spits webbing at the target, slowing them.
 */
public class SpiderAISpit extends Goal {

    private final DungeonSpiderEntity entity;
    private int spitCooldown = 0;
    private int spitWindup = 0;
    private final float spitRange = 16.0F;
    private double targetX;
    private double targetY;
    private double targetZ;

    public SpiderAISpit(DungeonSpiderEntity attacker) {
        this.entity = attacker;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean isInterruptable() {
        return false;
    }

    @Override
    public boolean canUse() {
        if (this.entity.getTarget() != null) {
            float dist = this.entity.distanceTo(this.entity.getTarget());
            if (dist <= this.spitRange && this.entity.hasLineOfSight(this.entity.getTarget())) {
                if (this.spitCooldown == 0) {
                    return true;
                } else if (this.spitCooldown > 0) {
                    this.spitCooldown--;
                }
            }
        }
        return false;
    }

    @Override
    public void start() {
        this.entity.getNavigation().stop();
        this.entity.setSpitting(true);
        if (this.entity.getTarget() != null) {
            this.entity.getLookControl().setLookAt(this.entity.getTarget(), 60.0F, 60.0F);
        }
        this.startSpit();
    }

    @Override
    public boolean canContinueToUse() {
        if (!this.entity.getNavigation().isDone() || this.entity.getTarget() != null) {
            if (this.entity.getTarget() != null) {
                if (this.entity.hasLineOfSight(this.entity.getTarget())) {
                    if (this.spitCooldown == 0) {
                        return true;
                    }
                }
            } else {
                if (this.spitCooldown == 0) {
                    return true;
                }
            }
        }
        this.entity.setSpitting(false);
        return false;
    }

    @Override
    public void tick() {
        LivingEntity target = this.entity.getTarget();
        if (this.spitWindup == 1) {
            this.setSpitLocation(this.entity.getTarget());
        }
        if (this.spitWindup == 0) {
            this.entity.getLookControl().setLookAt(this.targetX, this.targetY + this.entity.getEyeHeight(), this.targetZ, 30.0F, 30.0F);
            this.entity.setWindingUpSpit(false);
            this.finishSpit(target);
        } else {
            if (target != null) {
                this.entity.getLookControl().setLookAt(target, 30.0F, 30.0F);
            }
            this.entity.setWindingUpSpit(true);
            this.spitWindup--;
        }
    }

    private void startSpit() {
        this.spitWindup = 20 - this.entity.getRandom().nextInt(10);
    }

    @Override
    public void stop() {
        this.entity.getNavigation().stop();
    }

    private void finishSpit(LivingEntity target) {
        this.spitCooldown = 30 - this.entity.getRandom().nextInt(10);
        if (target != null) {
            WebbingEntity web = new WebbingEntity(this.entity.level(), this.entity);
            double d0 = target.getX() - this.entity.getX();
            double d1 = target.getBoundingBox().minY + target.getBbHeight() / 3.0F - web.getY();
            double d2 = target.getZ() - this.entity.getZ();
            double d3 = Math.sqrt(d0 * d0 + d2 * d2);
            web.shoot(d0, d1 + d3 * 0.1D, d2, 1.0F, 0.0F);
            this.entity.level().playSound(null, this.entity.getX(), this.entity.getY(), this.entity.getZ(), SoundEvents.CAT_HISS, SoundSource.HOSTILE, 0.8F, 0.8F / (this.entity.getRandom().nextFloat() * 0.4F + 0.8F));
            this.entity.level().addFreshEntity(web);
        }
        this.entity.setSpitting(false);
    }

    private void setSpitLocation(LivingEntity target) {
        if (target != null) {
            this.targetX = target.getX();
            this.targetY = target.getY();
            this.targetZ = target.getZ();
        }
    }
}
