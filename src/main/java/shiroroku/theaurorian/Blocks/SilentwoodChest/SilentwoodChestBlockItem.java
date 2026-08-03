package shiroroku.theaurorian.Blocks.SilentwoodChest;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import shiroroku.theaurorian.Util.TooltipUtil;

import java.util.List;

/**
 * Block item for the silentwood chest. Uses the 3D chest renderer so the item
 * shows the actual chest model instead of a flat sprite.
 * <p>
 * Client BEWLR is registered via {@code RegisterClientExtensionsEvent}
 * (see {@link shiroroku.theaurorian.EventsClient}).
 */
public class SilentwoodChestBlockItem extends BlockItem {

    public SilentwoodChestBlockItem(Block pBlock, Properties pProperties) {
        super(pBlock, pProperties);
    }

    @Override
    public void appendHoverText(ItemStack pStack, Item.TooltipContext pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, TooltipUtil.tryAddDesc(pStack, pTooltipComponents), pIsAdvanced);
    }
}
