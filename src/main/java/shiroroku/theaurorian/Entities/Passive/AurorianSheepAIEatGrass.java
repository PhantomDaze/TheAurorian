package shiroroku.theaurorian.Entities.Passive;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import shiroroku.theaurorian.Registry.BlockRegistry;

import java.util.EnumSet;

public class AurorianSheepAIEatGrass extends Goal {

    private final AurorianSheepEntity sheep;
    private final Level level;
    int eatingGrassTimer;

    public AurorianSheepAIEatGrass(AurorianSheepEntity sheep) {
        this.sheep = sheep;
        this.level = sheep.level();
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        if (this.sheep.getRandom().nextInt(this.sheep.isBaby() ? 50 : 1000) != 0) {
            return false;
        } else {
            BlockPos blockpos = this.sheep.blockPosition();
            return this.sheep.level().getBlockState(blockpos.below()).is(BlockRegistry.aurorian_grass.get());
        }
    }

    @Override
    public void start() {
        this.eatingGrassTimer = 40;
        this.sheep.level().broadcastEntityEvent(this.sheep, (byte) 10);
        this.sheep.getNavigation().stop();
    }

    @Override
    public void stop() {
        this.eatingGrassTimer = 0;
    }

    @Override
    public boolean canContinueToUse() {
        return this.eatingGrassTimer > 0;
    }

    public int getEatingGrassTimer() {
        return this.eatingGrassTimer;
    }

    @Override
    public void tick() {
        this.eatingGrassTimer = Math.max(0, this.eatingGrassTimer - 1);
        if (this.eatingGrassTimer == 4) {
            BlockPos blockpos = this.sheep.blockPosition();
            BlockPos below = blockpos.below();
            BlockState state = this.sheep.level().getBlockState(below);
            if (state.is(BlockRegistry.aurorian_grass.get())) {
                if (net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.sheep.level(), this.sheep)) {
                    this.sheep.level().levelEvent(2001, below, Block.getId(state));
                    this.sheep.level().setBlock(below, BlockRegistry.aurorian_dirt.get().defaultBlockState(), 2);
                }
                this.sheep.ate();
            }
        }
    }
}
