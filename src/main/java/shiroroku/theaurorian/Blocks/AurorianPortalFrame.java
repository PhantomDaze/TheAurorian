package shiroroku.theaurorian.Blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import shiroroku.theaurorian.DataGen.DataGenItemsTags;
import shiroroku.theaurorian.Portal.AurorianPortalShape;

import java.util.Optional;

public class AurorianPortalFrame extends Block {

    public AurorianPortalFrame(Properties properties) {
        super(properties);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack usedItem, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (usedItem.is(DataGenItemsTags.PORTAL_LIGHTERS)) {
            BlockPos placePos = pos.relative(hit.getDirection());
            Optional<AurorianPortalShape> optional = AurorianPortalShape.findEmptyPortalShape(level, placePos, Direction.Axis.X);
            if (optional.isPresent()) {
                level.playSound(player, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.4F + 0.8F);
                optional.get().createPortalBlocks();
                return ItemInteractionResult.sidedSuccess(level.isClientSide());
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
}
