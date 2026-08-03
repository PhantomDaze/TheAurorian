package shiroroku.theaurorian.Items;

import net.minecraft.world.item.Item;

import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import shiroroku.theaurorian.Util.TooltipUtil;

import java.util.List;

public class BaseAurorianArmor extends ArmorItem {

    public BaseAurorianArmor(Holder<ArmorMaterial> pMaterial, ArmorItem.Type pType, Properties pProperties) {
        super(pMaterial, pType, pProperties);
    }

    @Override
    public void appendHoverText(ItemStack pStack, Item.TooltipContext pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, TooltipUtil.tryAddDesc(pStack, pTooltipComponents), pIsAdvanced);
    }

    @Override
    public int getBarColor(ItemStack pStack) {
        return TooltipUtil.getBarColor();
    }
}
