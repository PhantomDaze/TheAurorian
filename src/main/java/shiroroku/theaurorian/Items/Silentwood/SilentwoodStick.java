package shiroroku.theaurorian.Items.Silentwood;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import shiroroku.theaurorian.Items.BaseAurorianItem;
import shiroroku.theaurorian.Registry.ItemRegistry;

/**
 * Silentwood stick. Holding one in each hand and using it creates fire, or
 * lights the Aurorian portal when used on a frame. Also a registered
 * {@code portal_lighters} tag member so the frame lights it generically.
 */
public class SilentwoodStick extends BaseAurorianItem {

    public SilentwoodStick(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        Player player = pContext.getPlayer();
        if (player == null || pContext.getHand() != InteractionHand.MAIN_HAND || !player.getOffhandItem().is(ItemRegistry.silentwood_stick.get())) {
            return InteractionResult.FAIL;
        }
        ItemStack stack = pContext.getItemInHand();
        BlockPos pos = pContext.getClickedPos().relative(pContext.getClickedFace());
        Level level = pContext.getLevel();
        if (!player.mayUseItemAt(pos, pContext.getClickedFace(), stack)) {
            return InteractionResult.FAIL;
        }
        if (level.isEmptyBlock(pos)) {
            level.playSound(player, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.4F + 0.8F);
            level.setBlock(pos, Blocks.FIRE.defaultBlockState(), 11);
        }
        stack.shrink(1);
        player.getOffhandItem().shrink(1);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
