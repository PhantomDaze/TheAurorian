package shiroroku.theaurorian.Items.Silentwood;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import shiroroku.theaurorian.Items.BaseAurorianAxe;
import shiroroku.theaurorian.Registry.BlockRegistry;
import shiroroku.theaurorian.Util.ModUtil;

/**
 * Silentwood axe. Breaking a silentwood log has a 75% chance to repair one
 * durability instead of consuming it.
 */
public class SilentwoodAxe extends BaseAurorianAxe {

    public SilentwoodAxe(Tier pTier, float pAttackDamageModifier, float pAttackSpeedModifier, Properties pProperties, int burnTime) {
        super(pTier, pAttackDamageModifier, pAttackSpeedModifier, pProperties, burnTime);
    }

    @Override
    public boolean mineBlock(ItemStack pStack, Level pLevel, BlockState pState, BlockPos pPos, LivingEntity pEntityLiving) {
        if (!pLevel.isClientSide && pState.getDestroySpeed(pLevel, pPos) != 0.0F) {
            if (pState.is(BlockRegistry.silentwood_log.get())) {
                if (ModUtil.randomChanceOf(pLevel.getRandom(), 0.75D)) {
                    if (pStack.getDamageValue() > 0) {
                        pStack.setDamageValue(pStack.getDamageValue() - 1);
                    }
                } else {
                    pStack.hurtAndBreak(1, pEntityLiving, (player) -> player.broadcastBreakEvent(pEntityLiving.getUsedItemHand()));
                }
            } else {
                pStack.hurtAndBreak(1, pEntityLiving, (player) -> player.broadcastBreakEvent(pEntityLiving.getUsedItemHand()));
            }
        }
        return true;
    }
}
