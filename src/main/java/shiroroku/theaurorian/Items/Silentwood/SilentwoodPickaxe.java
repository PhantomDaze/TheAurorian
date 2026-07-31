package shiroroku.theaurorian.Items.Silentwood;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.Tier;
import org.jetbrains.annotations.Nullable;
import shiroroku.theaurorian.Items.BaseAurorianPickaxe;

import java.util.List;

/**
 * Silentwood pickaxe. The more it wears, the higher its harvest level climbs
 * (0 → 3), letting it mine progressively harder blocks. Replicates upstream
 * {@code currentharvestlevel} NBT behaviour.
 */
public class SilentwoodPickaxe extends BaseAurorianPickaxe {

    public SilentwoodPickaxe(Tier pTier, int pAttackDamageModifier, float pAttackSpeedModifier, Properties pProperties, int burnTime) {
        super(pTier, pAttackDamageModifier, pAttackSpeedModifier, pProperties, burnTime);
    }

    @Override
    public boolean mineBlock(ItemStack pStack, Level pLevel, BlockState pState, BlockPos pPos, LivingEntity pEntityLiving) {
        boolean result = super.mineBlock(pStack, pLevel, pState, pPos, pEntityLiving);
        updateHarvestLevel(pStack);
        return result;
    }

    public static int getHarvestLevel(ItemStack pStack) {
        return pStack.getOrCreateTag().getInt("currentharvestlevel");
    }

    private static void updateHarvestLevel(ItemStack pStack) {
        double damagedPercent = (float) pStack.getDamageValue() / (float) pStack.getMaxDamage();
        int level;
        if (damagedPercent < 0.25D) {
            level = 0;
        } else if (damagedPercent < 0.5D) {
            level = 1;
        } else if (damagedPercent < 0.75D) {
            level = 2;
        } else {
            level = 3;
        }
        pStack.getOrCreateTag().putInt("currentharvestlevel", level);
    }

    private static int requiredLevel(BlockState pState) {
        if (pState.is(BlockTags.NEEDS_DIAMOND_TOOL)) {
            return 3;
        }
        if (pState.is(BlockTags.NEEDS_IRON_TOOL)) {
            return 2;
        }
        if (pState.is(BlockTags.NEEDS_STONE_TOOL)) {
            return 1;
        }
        return 0;
    }

    @Override
    public float getDestroySpeed(ItemStack pStack, BlockState pState) {
        if (pState.is(BlockTags.MINEABLE_WITH_PICKAXE) && getHarvestLevel(pStack) >= requiredLevel(pState)) {
            return this.speed;
        }
        return super.getDestroySpeed(pStack, pState);
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack pStack, BlockState pState) {
        return pState.is(BlockTags.MINEABLE_WITH_PICKAXE) && getHarvestLevel(pStack) >= requiredLevel(pState);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(Component.translatable("item.theaurorian.silentwood_pickaxe.desc2", getHarvestLevel(pStack)).withStyle(ChatFormatting.AQUA));
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }
}
