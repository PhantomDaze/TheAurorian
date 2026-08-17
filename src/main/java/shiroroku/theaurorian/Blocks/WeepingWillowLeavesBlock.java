package shiroroku.theaurorian.Blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import shiroroku.theaurorian.Registry.ParticleRegistry;

/**
 * Weeping willow leaves. Occasionally drips water particles from their
 * undersides, and their loot table drops weeping willow sap like the 1.12
 * {@code dropApple}.
 */
public class WeepingWillowLeavesBlock extends LeavesBlock {

    public WeepingWillowLeavesBlock(Properties pProperties) {
        super(pProperties);
        // 柳树结构模板是用 1.12 的 check_decay/decayable 叶子属性保存的，现代版本已无此属性，
        // 加载时回落到默认状态（persistent=false, distance=7），导致所有生成的叶子都会衰减。
        // vanilla 随机刻 + 快速叶子衰减 mod 会把整片柳林剥光。默认设为 persistent，让生成的
        // 树稳定。玩家放置的叶子通过 LeavesBlock#getStateForPlacement 已经是 persistent。
        this.registerDefaultState(this.defaultBlockState().setValue(BlockStateProperties.PERSISTENT, true));
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
