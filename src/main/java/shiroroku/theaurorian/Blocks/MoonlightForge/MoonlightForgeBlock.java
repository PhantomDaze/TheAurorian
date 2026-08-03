package shiroroku.theaurorian.Blocks.MoonlightForge;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import shiroroku.theaurorian.Blocks.AbstractRotatingBlock;
import shiroroku.theaurorian.Registry.BlockEntityRegistry;
import shiroroku.theaurorian.Util.ModUtil;

public class MoonlightForgeBlock extends AbstractRotatingBlock {

    public static final MapCodec<MoonlightForgeBlock> CODEC = simpleCodec(MoonlightForgeBlock::new);
    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 10, 16);

    public MoonlightForgeBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends AbstractRotatingBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MoonlightForgeBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> blockEntity) {
        return blockEntity == BlockEntityRegistry.moonlight_forge.get() ? MoonlightForgeBlockEntity::updateCraft : null;
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            ModUtil.dropItemHandlerInWorld(level.getBlockEntity(pos));
            super.onRemove(state, level, pos, newState, movedByPiston);
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof MoonlightForgeBlockEntity block) {
            MenuProvider menuProvider = new MenuProvider() {
                @Override
                public Component getDisplayName() {
                    return Component.translatable("block.theaurorian.moonlight_forge");
                }

                @Override
                public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player p) {
                    return new MoonlightForgeMenu(id, pos, playerInventory, p);
                }
            };
            block.updateClient();
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.openMenu(menuProvider, buf -> buf.writeBlockPos(pos));
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }
}
