package shiroroku.theaurorian.Items.Loot;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import shiroroku.theaurorian.Items.BaseAurorianArmor;

/**
 * Spiked chestplate. While crouching it is treated as having Thorns III but
 * slows you down; stand up and the Thorns enchantment is removed again.
 */
public class SpikedChestplate extends BaseAurorianArmor {

    public SpikedChestplate(Holder<ArmorMaterial> material, Properties properties) {
        super(material, ArmorItem.Type.CHESTPLATE, properties);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, net.minecraft.world.entity.Entity entity, int slotId, boolean isSelected) {
        if (!(entity instanceof Player player) || level.isClientSide || !(level instanceof ServerLevel sl)) {
            return;
        }
        // only when worn in chest slot roughly - inventoryTick fires always; check armor slot
        if (player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.CHEST) != stack) {
            return;
        }
        Holder<Enchantment> thorns = sl.registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(Enchantments.THORNS);
        int hasThorns = stack.getEnchantmentLevel(thorns);
        if (player.isCrouching()) {
            if (hasThorns < 3) {
                EnchantmentHelper.updateEnchantments(stack, m -> m.set(thorns, 3));
            }
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 10));
        } else if (hasThorns > 0) {
            EnchantmentHelper.updateEnchantments(stack, m -> m.removeIf(h -> h.is(Enchantments.THORNS)));
        }
    }
}
