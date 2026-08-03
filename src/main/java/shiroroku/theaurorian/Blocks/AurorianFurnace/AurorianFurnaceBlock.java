package shiroroku.theaurorian.Blocks.AurorianFurnace;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import shiroroku.theaurorian.Registry.BlockEntityRegistry;

public class AurorianFurnaceBlock extends AbstractFurnaceBlock {

    public static final MapCodec<AurorianFurnaceBlock> CODEC = simpleCodec(AurorianFurnaceBlock::new);

    public AurorianFurnaceBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends AbstractFurnaceBlock> codec() {
        return CODEC;
    }

    @Override
    protected void openContainer(Level level, BlockPos pos, Player player) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof MenuProvider provider) {
            player.openMenu(provider);
        }
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AurorianFurnaceBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createFurnaceTicker(level, type, BlockEntityRegistry.aurorian_furnace.get());
    }
}
