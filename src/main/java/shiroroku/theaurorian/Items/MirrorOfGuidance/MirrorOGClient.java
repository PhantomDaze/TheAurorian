package shiroroku.theaurorian.Items.MirrorOfGuidance;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Client-only helpers for Mirror of Guidance. Kept separate so the item class
 * does not classload {@link Minecraft} on dedicated/GameTest servers.
 */
@OnlyIn(Dist.CLIENT)
public final class MirrorOGClient {

    private MirrorOGClient() {
    }

    public static void openScreen() {
        Minecraft.getInstance().setScreen(new MirrorOGScreen());
    }
}
