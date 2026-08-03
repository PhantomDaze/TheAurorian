package shiroroku.theaurorian.Items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import shiroroku.theaurorian.Util.TooltipUtil;

import java.util.List;

public class BaseAurorianItem extends Item {

    public BaseAurorianItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag flag) {
        super.appendHoverText(stack, context, TooltipUtil.tryAddDesc(stack, tooltipComponents), flag);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return TooltipUtil.getBarColor();
    }
}
