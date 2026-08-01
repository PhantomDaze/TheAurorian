package shiroroku.theaurorian.Blocks.SilentwoodChest;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

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
