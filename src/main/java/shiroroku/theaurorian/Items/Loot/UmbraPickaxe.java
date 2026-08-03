package shiroroku.theaurorian.Items.Loot;

import net.minecraft.world.entity.EquipmentSlot;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.registries.BuiltInRegistries;
import shiroroku.theaurorian.Config.CommonConfig;
import shiroroku.theaurorian.Items.BaseAurorianPickaxe;

import javax.annotation.Nullable;
import java.util.List;

public class UmbraPickaxe extends BaseAurorianPickaxe {

    public UmbraPickaxe(Tier pTier, int pAttackDamageModifier, float pAttackSpeedModifier, Properties pProperties) {
        super(pTier, pAttackDamageModifier, pAttackSpeedModifier, pProperties);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        if (pContext.getPlayer().isShiftKeyDown()) {
            clearSelectedBlock(pContext.getItemInHand());
        } else {
            setSelectedBlock(pContext.getItemInHand(), pContext.getLevel().getBlockState(pContext.getClickedPos()).getBlock(), pContext.getPlayer(), pContext.getHand());
        }
        return super.useOn(pContext);
    }

    @Override
    public void appendHoverText(ItemStack pStack, Item.TooltipContext pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        Block selectedBlock = getSelectedBlock(pStack);
        if (selectedBlock != Blocks.AIR) {
            pTooltipComponents.add(Component.translatable("item.theaurorian.umbra_pickaxe.selected", Component.translatable(selectedBlock.getDescriptionId())).withStyle(ChatFormatting.GOLD));
        }
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }

    @Override
    public float getDestroySpeed(ItemStack pStack, BlockState pState) {
        Block selected = getSelectedBlock(pStack);
        if (selected != null && selected != Blocks.AIR && pState.is(selected)) {
            return (float) (this.getTier().getSpeed() * CommonConfig.umbra_pickaxe_speed_multiplier.get());
        }
        return super.getDestroySpeed(pStack, pState);
    }

    @Nullable
    public static Block getSelectedBlock(ItemStack stack) {
        String id = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getString("selected_block");
        if (id == null || id.isEmpty()) return Blocks.AIR;
        ResourceLocation rl = ResourceLocation.tryParse(id);
        return rl == null ? Blocks.AIR : BuiltInRegistries.BLOCK.get(rl);
    }

    private static void clearSelectedBlock(ItemStack stack) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.remove("selected_block"));
    }

    private static void setSelectedBlock(ItemStack stack, Block block, Player player, InteractionHand hand) {
        if (getSelectedBlock(stack) != block) {
            final String key = BuiltInRegistries.BLOCK.getKey(block).toString();
            CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putString("selected_block", key));
            stack.hurtAndBreak(CommonConfig.umbra_pickaxe_selection_cost.get(), player, EquipmentSlot.MAINHAND);
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1F, 2F);
        }
    }
}
