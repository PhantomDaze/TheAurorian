package shiroroku.theaurorian.Items.AurorianSteel;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import shiroroku.theaurorian.Config.CommonConfig;
import shiroroku.theaurorian.Util.TooltipUtil;

import java.util.List;
import java.util.Optional;

public class AurorianSteel {

    public static List<Component> appendHoverText(List<Component> pTooltipComponents, ItemStack pStack) {
        // Use iteration or simple first enchant for tooltip (simplified from old map)
        Holder<Enchantment> selected = getFirstUpgradableEnchantment(pStack);
        if (selected != null) {
            int currentLevel = pStack.getEnchantmentLevel(selected);
            pTooltipComponents.add(Component.translatable("string.theaurorian.tooltip.aurorian_steel.level", getXP(pStack), (int) (100 * getMultiplier(pStack))).withStyle(ChatFormatting.GOLD));
            pTooltipComponents.add((Component.translatable("string.theaurorian.tooltip.aurorian_steel.next_enchant").append(Enchantment.getFullname(selected, currentLevel + 1))).withStyle(ChatFormatting.GOLD));
        }
        return TooltipUtil.shiftMoreInfo(pTooltipComponents, Component.translatable("string.theaurorian.tooltip.aurorian_steel").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
    }

    public static <T extends LivingEntity> int onItemDamage(ItemStack stack, T entity, int amount) {
        if (!stack.isEnchanted()) {
            return amount;
        }

        Holder<Enchantment> upgradable = getFirstUpgradableEnchantment(stack);
        if (upgradable == null) {
            return amount;
        }

        setXP(stack, getXP(stack) + 1);

        if (!isMaxXP(stack)) {
            return amount;
        }

        // level up the first upgradable
        int current = stack.getEnchantmentLevel(upgradable);
        int newLevel = current + 1;
        if (newLevel <= upgradable.value().getMaxLevel()) {
            // use update to bump level
            EnchantmentHelper.updateEnchantments(stack, mutable -> {
                mutable.set(upgradable, newLevel);
            });
            nextLevel(stack);
            Level lvl = entity.level();
            if (!lvl.isClientSide) {
                lvl.playSound(null, entity, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1, 1);
            }
        }

        return amount;
    }

    private static Holder<Enchantment> getFirstUpgradableEnchantment(ItemStack stack) {
        // scan using helper iteration for current gameplay enchants
        final Holder<Enchantment>[] found = new Holder[1];
        EnchantmentHelper.runIterationOnItem(stack, (holder, level) -> {
            if (found[0] == null && level < holder.value().getMaxLevel()) {
                found[0] = holder;
            }
        });
        return found[0];
    }

    /**
     * Clears level, and increases cost for next level up
     */
    private static void nextLevel(ItemStack aurorian_steel_item) {
        // level cost increases by 25% every time you enchant
        setMultiplier(aurorian_steel_item, (float) (getMultiplier(aurorian_steel_item) * CommonConfig.aurorian_steel_level_multiplier.get()));
        setXP(aurorian_steel_item, 0);
    }

    private static boolean isMaxXP(ItemStack aurorian_steel_item) {
        return getXP(aurorian_steel_item) >= CommonConfig.aurorian_steel_base_level.get() * getMultiplier(aurorian_steel_item);
    }

    private static void setMultiplier(ItemStack aurorian_steel_item, float amt) {
        CustomData.update(DataComponents.CUSTOM_DATA, aurorian_steel_item, tag -> tag.putFloat("multiplier", amt));
    }

    private static float getMultiplier(ItemStack aurorian_steel_item) {
        return Math.max(1, aurorian_steel_item.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getFloat("multiplier"));
    }

    private static void setXP(ItemStack aurorian_steel_item, int amt) {
        CustomData.update(DataComponents.CUSTOM_DATA, aurorian_steel_item, tag -> tag.putInt("xp", amt));
    }

    private static int getXP(ItemStack aurorian_steel_item) {
        return aurorian_steel_item.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getInt("xp");
    }

    public static int getBarColor() {
        return FastColor.ARGB32.color(255, 189, 168, 252);
    }
}
