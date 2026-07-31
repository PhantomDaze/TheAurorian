package shiroroku.theaurorian.Entities.Spirit;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

/**
 * Runs away when the target is looking directly at the spirit (upstream
 * behaviour).
 */
public class SpiritAIRunAway extends Goal {

    private final SpiritEntity entity;
    private Path pathaway;

    public SpiritAIRunAway(SpiritEntity attacker) {
        this.entity = attacker;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public void stop() {
        this.entity.getNavigation().stop();
    }

    @Override
    public boolean canUse() {
        if (this.entity.getTarget() != null) {
            LivingEntity target = this.entity.getTarget();
            if (SpiritEntity.isLookingAt(this.entity, target, 0.1D) && this.entity.hasLineOfSight(target)) {
                Vec3 vec3d = DefaultRandomPos.getPosAway(this.entity, 16, 10, new Vec3(target.getX(), target.getY(), target.getZ()));
                if (vec3d != null) {
                    if (!(target.distanceToSqr(vec3d.x, vec3d.y, vec3d.z) < target.distanceToSqr(this.entity))) {
                        this.pathaway = this.entity.getNavigation().createPath(vec3d.x, vec3d.y, vec3d.z, 0);
                        return this.pathaway != null;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void start() {
        this.entity.getNavigation().stop();
        this.entity.getNavigation().moveTo(this.pathaway, 2D);
    }

    @Override
    public boolean canContinueToUse() {
        if (this.entity.getTarget() != null) {
            return !this.entity.getNavigation().isDone();
        }
        return false;
    }

    @Override
    public void tick() {
    }
}
