package shiroroku.theaurorian.Blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Simple torch with a configurable light level and flame particles.
 */
public class AurorianTorchBlock extends TorchBlock {

    public AurorianTorchBlock(Properties pProperties) {
        super(ParticleTypes.FLAME, pProperties);
    }

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
        double d0 = (double) pPos.getX() + 0.5D;
        double d1 = (double) pPos.getY() + 0.7D;
        double d2 = (double) pPos.getZ() + 0.5D;
        pLevel.addParticle(ParticleTypes.SMOKE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
        pLevel.addParticle(ParticleTypes.FLAME, d0, d1, d2, 0.0D, 0.0D, 0.0D);
    }

    @Override
    public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pMovedByPiston) {
        // keep wall/floor placement simple like vanilla torch
        super.onPlace(pState, pLevel, pPos, pOldState, pMovedByPiston);
    }
}
