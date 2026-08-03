package shiroroku.theaurorian.Mixin.Client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import shiroroku.theaurorian.Renderers.AurorianDimensionSpecialEffects;
import shiroroku.theaurorian.TheAurorian;

/**
 * Ports 1.12 {@code AurorianWorldProvider} client lighting hooks:
 * sky darkness floor (sun brightness 0.75), fixed blue sky, brighter stars.
 */
@Mixin(ClientLevel.class)
public class ClientLevelMixin {

    @Inject(method = "getSkyDarken", at = @At("RETURN"), cancellable = true)
    private void theaurorian$moonlightSkyDarken(float partialTick, CallbackInfoReturnable<Float> cir) {
        ClientLevel self = (ClientLevel) (Object) this;
        if (self.dimension() == TheAurorian.the_aurorian) {
            cir.setReturnValue(Math.max(cir.getReturnValue(), AurorianDimensionSpecialEffects.MOONLIGHT_BRIGHTNESS));
        }
    }

    @Inject(method = "getSkyColor", at = @At("RETURN"), cancellable = true)
    private void theaurorian$aurorianSkyColor(Vec3 pos, float partialTick, CallbackInfoReturnable<Vec3> cir) {
        ClientLevel self = (ClientLevel) (Object) this;
        if (self.dimension() == TheAurorian.the_aurorian) {
            cir.setReturnValue(AurorianDimensionSpecialEffects.SKY_COLOR);
        }
    }

    @Inject(method = "getStarBrightness", at = @At("RETURN"), cancellable = true)
    private void theaurorian$brighterStars(float partialTick, CallbackInfoReturnable<Float> cir) {
        ClientLevel self = (ClientLevel) (Object) this;
        if (self.dimension() == TheAurorian.the_aurorian) {
            // 1.12: getStarBrightnessBody * 1.85
            cir.setReturnValue(Math.min(1.0F, cir.getReturnValue() * 1.85F));
        }
    }

    @Inject(method = "getCloudColor", at = @At("RETURN"), cancellable = true)
    private void theaurorian$cloudColor(float partialTick, CallbackInfoReturnable<Vec3> cir) {
        ClientLevel self = (ClientLevel) (Object) this;
        if (self.dimension() == TheAurorian.the_aurorian) {
            // Soft blue-grey clouds under permanent moonlight
            cir.setReturnValue(new Vec3(0.35D, 0.40D, 0.55D));
        }
    }
}
