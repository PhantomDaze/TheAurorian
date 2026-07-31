package shiroroku.theaurorian.Items;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.ForgeEventFactory;

/**
 * Keeper boss bow. A fully pulled shot fires three arrows with high spread.
 */
public class KeepersBowItem extends BaseAurorianBow {

    public KeepersBowItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public int getUseDuration(ItemStack pStack) {
        return 40;
    }

    @Override
    public void releaseUsing(ItemStack pStack, Level pLevel, LivingEntity pEntityLiving, int pTimeLeft) {
        if (pEntityLiving instanceof Player player) {
            int i = this.getUseDuration(pStack) - pTimeLeft;
            i = ForgeEventFactory.onArrowLoose(pStack, pLevel, player, i, !player.getProjectile(pStack).isEmpty() || player.getAbilities().instabuild);
            if (i < 0) {
                return;
            }
            float f = getPowerForTime(i);
            if (f < 0.1D) {
                return;
            }
            for (int arrow = 0; arrow < 3; arrow++) {
                this.fireArrow(pStack, pLevel, player, f);
            }
        }
    }

    private void fireArrow(ItemStack pStack, Level pLevel, Player player, float f) {
        boolean flag = player.getAbilities().instabuild || EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, pStack) > 0;
        ItemStack itemstack = player.getProjectile(pStack);
        if (!itemstack.isEmpty() || flag) {
            if (itemstack.isEmpty()) {
                itemstack = new ItemStack(Items.ARROW);
            }
            boolean flag1 = player.getAbilities().instabuild || (itemstack.getItem() instanceof ArrowItem arrowitem && arrowitem.isInfinite(itemstack, pStack, player));
            if (!pLevel.isClientSide) {
                ArrowItem arrowitem1 = (ArrowItem) (itemstack.getItem() instanceof ArrowItem ? itemstack.getItem() : Items.ARROW);
                AbstractArrow abstractarrow = arrowitem1.createArrow(pLevel, itemstack, player);
                abstractarrow = this.customArrow(abstractarrow);
                abstractarrow.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, f * 3.0F, 6.0F);
                if (f == 1.0F) {
                    abstractarrow.setCritArrow(true);
                }
                int j = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, pStack);
                if (j > 0) {
                    abstractarrow.setBaseDamage(abstractarrow.getBaseDamage() + (double) j * 0.5D + 0.5D);
                }
                int k = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PUNCH_ARROWS, pStack);
                if (k > 0) {
                    abstractarrow.setKnockback(k);
                }
                if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FLAMING_ARROWS, pStack) > 0) {
                    abstractarrow.setSecondsOnFire(100);
                }
                pStack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(player.getUsedItemHand()));
                if (flag1 || player.getAbilities().instabuild && (itemstack.is(Items.SPECTRAL_ARROW) || itemstack.is(Items.TIPPED_ARROW))) {
                    abstractarrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                }
                pLevel.addFreshEntity(abstractarrow);
            }
            pLevel.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F / (pLevel.getRandom().nextFloat() * 0.4F + 1.2F) + f * 0.5F);
            if (!flag1 && !player.getAbilities().instabuild) {
                itemstack.shrink(1);
                if (itemstack.isEmpty()) {
                    player.getInventory().removeItem(itemstack);
                }
            }
            player.awardStat(Stats.ITEM_USED.get(this));
        }
    }
}
