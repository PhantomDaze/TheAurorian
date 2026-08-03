package shiroroku.theaurorian.Blocks.SilentwoodChest;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

@OnlyIn(Dist.CLIENT)
public final class SilentwoodChestClientExt implements IClientItemExtensions {

    public static final SilentwoodChestClientExt INSTANCE = new SilentwoodChestClientExt();

    private SilentwoodChestClientExt() {
    }

    @Override
    public BlockEntityWithoutLevelRenderer getCustomRenderer() {
        return SilentwoodChestItemRenderer.INSTANCE;
    }
}
