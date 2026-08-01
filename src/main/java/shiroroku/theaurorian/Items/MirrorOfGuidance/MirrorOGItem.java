package shiroroku.theaurorian.Items.MirrorOfGuidance;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import shiroroku.theaurorian.Items.BaseAurorianItem;

public class MirrorOGItem extends BaseAurorianItem {

    public MirrorOGItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        if (world.isClientSide() && MirrorDataLoader.loaded) {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> MirrorOGClient::openScreen);
        }
        return super.use(world, player, hand);
    }
}
