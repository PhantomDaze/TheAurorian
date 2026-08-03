package shiroroku.theaurorian.Items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.Nullable;
import shiroroku.theaurorian.Util.TooltipUtil;

import java.util.List;

public class BaseAurorianShovel extends ShovelItem {

    private int burnTime = 0;

    public BaseAurorianShovel(Tier tier, float attackDamageModifier, float attackSpeedModifier, Properties properties, int burnTime) {
        super(tier, properties.attributes(ShovelItem.createAttributes(tier, attackDamageModifier, attackSpeedModifier)));
        this.burnTime = burnTime;
    }

    public BaseAurorianShovel(Tier tier, float attackDamageModifier, float attackSpeedModifier, Properties properties) {
        this(tier, attackDamageModifier, attackSpeedModifier, properties, 0);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag flag) {
        super.appendHoverText(stack, context, TooltipUtil.tryAddDesc(stack, tooltipComponents), flag);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return TooltipUtil.getBarColor();
    }

    @Override
    public int getBurnTime(ItemStack itemStack, @Nullable RecipeType<?> recipeType) {
        return burnTime == 0 ? super.getBurnTime(itemStack, recipeType) : burnTime;
    }
}
