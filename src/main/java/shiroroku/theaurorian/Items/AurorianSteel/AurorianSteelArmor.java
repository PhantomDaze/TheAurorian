package shiroroku.theaurorian.Items.AurorianSteel;

import net.minecraft.world.item.Item;

import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import shiroroku.theaurorian.Items.BaseAurorianArmor;

import java.util.List;
import java.util.function.Consumer;

public class AurorianSteelArmor extends BaseAurorianArmor {

    public AurorianSteelArmor(Holder<ArmorMaterial> pMaterial, ArmorItem.Type pType, Properties pProperties) {
        super(pMaterial, pType, pProperties);
    }

    @Override
    public void appendHoverText(ItemStack pStack, Item.TooltipContext pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, AurorianSteel.appendHoverText(pTooltipComponents, pStack), pIsAdvanced);
    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<Item> onBroken) {
        return AurorianSteel.onItemDamage(stack, entity, amount);
    }
}
