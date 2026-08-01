package shiroroku.theaurorian.Items.Loot;

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

import java.util.Map;

/**
 * Spiked chestplate. While crouching it is treated as having Thorns III but
 * slows you down; stand up and the Thorns enchantment is removed again.
 */
public class SpikedChestplate extends BaseAurorianArmor {

    public SpikedChestplate(ArmorMaterial pMaterial, Properties pProperties) {
        super(pMaterial, ArmorItem.Type.CHESTPLATE, pProperties);
    }

    @Override
    public void onArmorTick(ItemStack stack, Level level, Player player) {
        Map<Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(stack);
        if (player.isCrouching()) {
            if (!enchants.containsKey(Enchantments.THORNS)) {
                enchants.put(Enchantments.THORNS, 3);
                EnchantmentHelper.setEnchantments(enchants, stack);
            }
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 10));
        } else if (enchants.containsKey(Enchantments.THORNS)) {
            enchants.remove(Enchantments.THORNS);
            EnchantmentHelper.setEnchantments(enchants, stack);
        }
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return enchantment != Enchantments.THORNS && super.canApplyAtEnchantingTable(stack, enchantment);
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return EnchantmentHelper.getEnchantments(book).entrySet().stream().noneMatch(e -> e.getKey() == Enchantments.THORNS) && super.isBookEnchantable(stack, book);
    }
}
