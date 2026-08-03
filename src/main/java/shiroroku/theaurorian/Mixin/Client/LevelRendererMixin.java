package shiroroku.theaurorian.Mixin.Client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import shiroroku.theaurorian.Config.ClientConfig;
import shiroroku.theaurorian.Renderers.AuroraRenderer;
import shiroroku.theaurorian.TheAurorian;
import shiroroku.theaurorian.Util.ModUtil;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {

    @Shadow
    private ClientLevel level;

    @Shadow
    private int ticks;

    @Final
    @Shadow
    private Minecraft minecraft;

    @Shadow
    private VertexBuffer skyBuffer;

    @Shadow
    private VertexBuffer starBuffer;

    @Shadow
    private VertexBuffer darkBuffer;

    @Final
    @Shadow
    private static ResourceLocation MOON_LOCATION;

    @Shadow
    private boolean doesMobEffectBlockSky(Camera camera) {
        return false;
    }

    @Inject(at = @At("HEAD"), method = "renderSky", cancellable = true)
    public void renderSky(PoseStack pPoseStack, Matrix4f pProjectionMatrix, float pPartialTick, Camera pCamera, boolean pFog, Runnable pSkyFogSetup, CallbackInfo ci) {
        if (level.dimension() != TheAurorian.the_aurorian) {
            return; // Only override aurorian sky renderer
        }
        pSkyFogSetup.run();
        if (!pFog) {
            FogType fogtype = pCamera.getFluidInCamera();
            if (fogtype != FogType.POWDER_SNOW && fogtype != FogType.LAVA && !this.doesMobEffectBlockSky(pCamera)) {

                BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
                // texture unit always enabled in 1.20+
                FogRenderer.levelFogColor();
                RenderSystem.depthMask(false);
                Vec3 skyColor = this.level.getSkyColor(this.minecraft.gameRenderer.getMainCamera().getPosition(), pPartialTick);
                RenderSystem.setShaderColor((float) skyColor.x, (float) skyColor.y, (float) skyColor.z, 1.0F);
                ShaderInstance shaderinstance = RenderSystem.getShader();

                this.skyBuffer.bind();
                this.skyBuffer.drawWithShader(pPoseStack.last().pose(), pProjectionMatrix, shaderinstance);
                VertexBuffer.unbind();
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                float[] sunriseColor = this.level.effects().getSunriseColor(this.level.getTimeOfDay(pPartialTick), pPartialTick);
                if (sunriseColor != null) {
                    RenderSystem.setShader(GameRenderer::getPositionColorShader);
                    // texture unit always enabled in 1.20+
                    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                    pPoseStack.pushPose();
                    pPoseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
                    pPoseStack.mulPose(Axis.ZP.rotationDegrees(Mth.sin(this.level.getSunAngle(pPartialTick)) < 0.0F ? 180.0F : 0.0F));
                    pPoseStack.mulPose(Axis.ZP.rotationDegrees(90.0F));
                    Matrix4f matrix4f = pPoseStack.last().pose();
                    bufferbuilder.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);
                    bufferbuilder.vertex(matrix4f, 0.0F, 100.0F, 0.0F).color(sunriseColor[0], sunriseColor[1], sunriseColor[2], sunriseColor[3]).endVertex();
                    for (int j = 0; j <= 16; ++j) {
                        float f7 = (float) j * ((float) Math.PI * 2F) / 16.0F;
                        float f8 = Mth.sin(f7);
                        float f9 = Mth.cos(f7);
                        bufferbuilder.vertex(matrix4f, f8 * 120.0F, f9 * 120.0F, -f9 * 40.0F * sunriseColor[3]).color(sunriseColor[0], sunriseColor[1], sunriseColor[2], 0.0F).endVertex();
                    }
                    BufferUploader.drawWithShader(bufferbuilder.end());
                    pPoseStack.popPose();
                }

                // texture unit always enabled in 1.20+
                RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                pPoseStack.pushPose();
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                float skyRotationSpeed = (this.level.getGameTime() + pPartialTick) * 0.02f;
                pPoseStack.mulPose(Axis.YP.rotationDegrees(skyRotationSpeed));
                pPoseStack.mulPose(Axis.XP.rotationDegrees(140 + ModUtil.wave(skyRotationSpeed, 0.05f, 60)));
                Matrix4f matrix4f1 = pPoseStack.last().pose();
                float moonSize = 40.0F;
                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                RenderSystem.setShaderTexture(0, MOON_LOCATION);
                int moonPhase = this.level.getMoonPhase();
                int l = moonPhase % 4;
                int i1 = moonPhase / 4 % 2;
                float u1 = (float) (l) / 4.0F;
                float v1 = (float) (i1) / 2.0F;
                float u2 = (float) (l + 1) / 4.0F;
                float v2 = (float) (i1 + 1) / 2.0F;
                bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
                bufferbuilder.vertex(matrix4f1, -moonSize, -100.0F, moonSize).uv(u2, v2).endVertex();
                bufferbuilder.vertex(matrix4f1, moonSize, -100.0F, moonSize).uv(u1, v2).endVertex();
                bufferbuilder.vertex(matrix4f1, moonSize, -100.0F, -moonSize).uv(u1, v1).endVertex();
                bufferbuilder.vertex(matrix4f1, -moonSize, -100.0F, -moonSize).uv(u2, v1).endVertex();
                BufferUploader.drawWithShader(bufferbuilder.end());
                // texture unit always enabled in 1.20+

                float starBrightness = this.level.getStarBrightness(pPartialTick);
                RenderSystem.setShaderColor(starBrightness, starBrightness, starBrightness, starBrightness);
                FogRenderer.setupNoFog();
                this.starBuffer.bind();
                this.starBuffer.drawWithShader(pPoseStack.last().pose(), pProjectionMatrix, GameRenderer.getPositionShader());
                VertexBuffer.unbind();
                pSkyFogSetup.run();

                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                RenderSystem.disableBlend();
                pPoseStack.popPose();

                if (ClientConfig.enable_auroras.get()) {
                    AuroraRenderer.renderSky(level, pPoseStack, pProjectionMatrix, pPartialTick);
                }

                // Void/horizon disc (below sea level). Must not leak this color into terrain.
                RenderSystem.setShaderColor(0.0F, 0.0F, 0.0F, 1.0F);
                double d0 = this.minecraft.player.getEyePosition(pPartialTick).y - this.level.getLevelData().getHorizonHeight(this.level);
                if (d0 < 0.0D) {
                    pPoseStack.pushPose();
                    pPoseStack.translate(0.0D, 12.0D, 0.0D);
                    this.darkBuffer.bind();
                    this.darkBuffer.drawWithShader(pPoseStack.last().pose(), pProjectionMatrix, shaderinstance);
                    VertexBuffer.unbind();
                    pPoseStack.popPose();
                }
            }
        }

        // Always restore after cancelling vanilla sky — including fog/fluid early-outs.
        // 1.20 block shaders multiply ColorModulator; a leftover sky tint crushes outdoor lighting.
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);

        ci.cancel(); // Dont render vanilla sky after
    }
}
