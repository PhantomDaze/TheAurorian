package shiroroku.theaurorian.Entities.Boss.AI;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import shiroroku.theaurorian.Entities.Boss.DungeonSpiderEntity;
import shiroroku.theaurorian.Entities.Spiderling.SpiderlingEntity;

import java.util.EnumSet;

/**
 * Spider Mother climbs to a ceiling above her position, hangs, and drops
 * spiderlings onto the target.
 */
public class SpiderAIHang extends Goal {

    private final DungeonSpiderEntity entity;
    private int hangCooldown = 0;
    private int hangTime = 0;
    private final int minDistance = 8;
    private final int maxDistance = 50;
    private double hangingX;
    private double hangingY;
    private double hangingZ;

    public SpiderAIHang(DungeonSpiderEntity attacker) {
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
            if (this.hangCooldown == 0) {
                hangingX = this.entity.getX();
                hangingY = this.entity.getY();
                hangingZ = this.entity.getZ();
                for (int y = minDistance; y <= maxDistance; y++) {
                    BlockPos up = new BlockPos((int) hangingX, (int) hangingY, (int) hangingZ).above(y);
                    if (this.entity.level.getBlockState(up).isRedstoneConductor(this.entity.level, up)) {
                        hangingY = hangingY + y - 3;
                        return true;
                    }
                }
            } else if (this.hangCooldown > 0) {
                this.hangCooldown--;
            }
        }
        return false;
    }

    @Override
    public void start() {
        this.entity.getNavigation().stop();
        this.entity.setHanging(true);
        if (this.entity.getTarget() != null) {
            this.entity.getLookControl().setLookAt(this.entity.getTarget(), 30.0F, 30.0F);
        }
        hangTime = 200;
        this.entity.setNoGravity(true);
    }

    @Override
    public boolean canContinueToUse() {
        if (hangTime != 0) {
            return true;
        }
        this.entity.setHanging(false);
        this.entity.setNoGravity(false);
        this.hangCooldown = 200;
        return false;
    }

    @Override
    public void tick() {
        if (this.entity.getY() <= hangingY) {
            this.entity.setPos(hangingX, this.entity.getY() + 0.5D, hangingZ);
        }
        if (hangTime == 100 || hangTime == 150) {
            for (int i = 0; i <= this.entity.getRandom().nextInt(5); i++) {
                SpiderlingEntity spiderling = new SpiderlingEntity(this.entity.level);
                spiderling.setPos(this.entity.blockPosition().getX() - 0.5F, this.entity.blockPosition().getY() - 0.5F, this.entity.blockPosition().getZ() - 0.5F);
                spiderling.setTarget(this.entity.getTarget());
                this.entity.level.addFreshEntity(spiderling);
            }
        }
        this.hangTime--;
    }
}
