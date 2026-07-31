package shiroroku.theaurorian.Blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Weeping willow leaves. Occasionally drips water particles from their
 * undersides, and their loot table drops weeping willow sap like the 1.12
 * {@code dropApple}.
 */
public class WeepingWillowLeavesBlock extends LeavesBlock {

    public WeepingWillowLeavesBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
        super.animateTick(pState, pLevel, pPos, pRandom);
        if (pLevel.isClientSide && pLevel.getBlockState(pPos.below()).isAir() && pRandom.nextFloat() < 0.01F) {
            double x = pPos.getX() + pRandom.nextDouble();
            double y = pPos.getY() - 0.2D;
            double z = pPos.getZ() + pRandom.nextDouble();
            pLevel.addParticle(ParticleTypes.DRIPPING_WATER, x, y, z, 0, 0, 0);
        }
    }
}
