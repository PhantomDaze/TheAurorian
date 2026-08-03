package shiroroku.theaurorian.Renderers;

import com.mojang.math.Vector3f;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.world.phys.Vec3;

/**
 * Aurorian dimension client effects, ported from 1.12 {@code AurorianWorldProvider}:
 * <ul>
 *   <li>constant moonlight feel ({@code getSunBrightness = 0.75})</li>
 *   <li>deep blue sky ({@code (0, 0.15, 0.30)})</li>
 *   <li>high cloud layer (255)</li>
 *   <li>brighter star field (×1.85 applied via ClientLevel mixin)</li>
 * </ul>
 * There was no separate GLSL post-shader in 1.12 — lighting came from the world provider.
 */
public class AurorianDimensionSpecialEffects extends DimensionSpecialEffects {

    /** 1.12 forced sun brightness factor. */
    public static final float MOONLIGHT_BRIGHTNESS = 0.75F;
    /** 1.12 getSkyColor * muli. */
    public static final Vec3 SKY_COLOR = new Vec3(0.0D, 0.15D, 0.30D);

    public AurorianDimensionSpecialEffects() {
        // cloudLevel=255, hasGround, NORMAL sky, no end-style forced bright map, constant ambient shading
        super(255.0F, true, SkyType.NORMAL, false, true);
    }

    @Override
    public Vec3 getBrightnessDependentFogColor(Vec3 fogColor, float brightness) {
        // Never collapse fog to near-black at "night" — floor brightness like 1.12 moonlight.
        float b = Math.max(brightness, MOONLIGHT_BRIGHTNESS);
        return fogColor.multiply(b * 0.94F + 0.06F, b * 0.94F + 0.06F, b * 0.91F + 0.09F);
    }

    @Override
    public boolean isFoggyAt(int x, int y) {
        return false;
    }

    @Override
    public float[] getSunriseColor(float timeOfDay, float partialTicks) {
        // Permanent night / no sunrise band (fixed moon feel).
        return null;
    }

    /**
     * Lift lightmap sky contribution so outdoor areas match ~0.75 sunlight instead of midnight black.
     */
    @Override
    public void adjustLightmapColors(ClientLevel level, float partialTicks, float skyDarken, float blockLightRedFlicker,
                                     float skyLight, int blockLightIndex, int skyLightIndex, Vector3f colors) {
        float lift = Math.max(0.0F, MOONLIGHT_BRIGHTNESS - skyDarken);
        if (lift <= 0.0F) {
            return;
        }
        float skyBase = LightTexture.getBrightness(level.dimensionType(), skyLightIndex);
        float add = skyBase * lift;
        // Cool moonlight cast (slightly blue), matching the 1.12 sky palette
        colors.add(add * 0.55F, add * 0.68F, add * 0.95F);
    }
}
