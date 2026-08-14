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
        // 柳树模板是 1.12 时代制作,存的是旧的 check_decay/decayable 叶子属性,现代
        // LeavesBlock 已不识别,加载后落到默认状态(persistent=false, distance=7),
        // 导致生成的每片叶子都可能腐烂——原版随机刻和快速落叶 mod 会把整片柳树林剥光。
        // 默认 persistent=true 只影响世界生成/模板放置(玩家放置的叶子经
        // LeavesBlock#getStateForPlacement 本来就是 persistent)。
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
