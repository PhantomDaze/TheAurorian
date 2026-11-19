package shiroroku.theaurorian.Blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 可贴墙放置的火把，粒子按 FACING 偏移（对齐原版 WallTorchBlock）。
 */
public class AurorianWallTorchBlock extends WallTorchBlock {

    public AurorianWallTorchBlock(Properties pProperties) {
        super(pProperties, ParticleTypes.FLAME);
    }

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
        Direction direction = pState.getValue(FACING);
        double d0 = (double) pPos.getX() + 0.5D;
        double d1 = (double) pPos.getY() + 0.7D;
        double d2 = (double) pPos.getZ() + 0.5D;
        Direction opposite = direction.getOpposite();
        double dx = 0.27D * (double) opposite.getStepX();
        double dz = 0.27D * (double) opposite.getStepZ();
        pLevel.addParticle(ParticleTypes.SMOKE, d0 + dx, d1 + 0.22D, d2 + dz, 0.0D, 0.0D, 0.0D);
        pLevel.addParticle(ParticleTypes.FLAME, d0 + dx, d1 + 0.22D, d2 + dz, 0.0D, 0.0D, 0.0D);
    }
}
