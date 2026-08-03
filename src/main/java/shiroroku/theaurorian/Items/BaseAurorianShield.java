package shiroroku.theaurorian.Items;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.common.ItemAbilities;

public class BaseAurorianShield extends BaseAurorianItem {

    private final Tier tier;

    public BaseAurorianShield(Tier pTier, Properties pProperties) {
        super(pProperties.durability(pTier.getUses()));
        tier = pTier;
        DispenserBlock.registerBehavior(this, ArmorItem.DISPENSE_ITEM_BEHAVIOR);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.BLOCK;
    }

    @Override
    public int getUseDuration(ItemStack pStack, net.minecraft.world.entity.LivingEntity entity) {
        return 72000;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
        pPlayer.startUsingItem(pHand);
        return InteractionResultHolder.consume(pPlayer.getItemInHand(pHand));
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ItemAbility toolAction) {
        return ItemAbilities.DEFAULT_SHIELD_ACTIONS.contains(toolAction);
    }

    @Override
    public boolean isValidRepairItem(ItemStack pToRepair, ItemStack pRepair) {
        return tier.getRepairIngredient().test(pRepair);
    }
}
