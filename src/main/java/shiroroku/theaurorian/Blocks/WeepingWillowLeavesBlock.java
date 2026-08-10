package shiroroku.theaurorian.Blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import shiroroku.theaurorian.Registry.ParticleRegistry;

/**
 * Weeping willow leaves. Occasionally drips water particles from their
 * undersides, and their loot table drops weeping willow sap like the 1.12
 * {@code dropApple}.
 */
public class WeepingWillowLeavesBlock extends LeavesBlock {

    public WeepingWillowLeavesBlock(Properties pProperties) {
        super(pProperties);
        // The willow templates were authored in 1.12 with the old check_decay/decayable leaf
        // properties, which no longer exist on modern leaves. Those drop to the default state
        // (persistent=false, distance=7) when loaded, so every generated leaf is eligible for
        // decay - vanilla random ticks and fast-leaf-decay mods strip the whole forest. Default
        // the leaves to persistent so generated trees are stable. Player-placed leaves are already
        // persistent via LeavesBlock#getStateForPlacement.
        this.registerDefaultState(this.defaultBlockState().setValue(PERSISTENT, true));
    }

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
        super.animateTick(pState, pLevel, pPos, pRandom);
        if (pLevel.isClientSide && pLevel.getBlockState(pPos.below()).isAir() && pRandom.nextFloat() < 0.01F) {
            double x = pPos.getX() + pRandom.nextDouble();
            double y = pPos.getY() - 0.2D;
            double z = pPos.getZ() + pRandom.nextDouble();
            pLevel.addParticle(ParticleRegistry.WEEPING_WILLOW_DRIP.get(), x, y, z, 0, 0, 0);
        }
    }
}
