package shiroroku.theaurorian.Entities.Boss.AI;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
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

    /**
     * 校验从当前脚部到 hangingY 的整列是否可通行。
     */
    private boolean canReachHanging() {
        double x = this.entity.getX();
        double z = this.entity.getZ();
        double height = this.entity.getBbHeight();
        for (double y = this.entity.getY(); y <= this.hangingY; y += 0.5D) {
            AABB box = new AABB(x - 0.3D, y, z - 0.3D, x + 0.3D, y + height, z + 0.3D);
            if (!this.entity.level.noCollision(this.entity, box)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean canUse() {
        if (this.entity.getTarget() != null) {
            if (this.hangCooldown == 0) {
                double startY = this.entity.getY();
                hangingX = this.entity.getX();
                hangingZ = this.entity.getZ();
                hangingY = startY;
                // 起点至少高于自身高度，避免把脚下的方块当成天花板
                int start = Math.max(this.minDistance, Mth.ceil(this.entity.getBbHeight()) + 1);
                int max = Math.min(this.maxDistance, this.entity.level.getMaxBuildHeight() - Mth.floor(startY) - 1);
                BlockPos base = new BlockPos((int) hangingX, (int) startY, (int) hangingZ);
                for (int y = start; y <= max; y++) {
                    BlockPos up = base.above(y);
                    if (this.entity.level.getBlockState(up).isRedstoneConductor(this.entity.level, up)) {
                        // 脚部高度必须钳制在天花板下方，否则会穿顶窒息
                        double feetY = up.getY() - this.entity.getBbHeight() - 0.1D;
                        hangingY = Math.min(startY + y - 3, feetY);
                        // 沿整列校验可通行，不可通行则放弃悬挂
                        return this.canReachHanging();
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
            // 上升时钳制在 hangingY，并走碰撞移动，避免塞进天花板
            double target = Math.min(this.entity.getY() + 0.5D, hangingY);
            this.entity.move(MoverType.SELF, new Vec3(0.0D, target - this.entity.getY(), 0.0D));
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
