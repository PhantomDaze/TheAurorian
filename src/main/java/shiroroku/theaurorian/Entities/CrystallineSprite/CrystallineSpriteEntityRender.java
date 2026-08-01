package shiroroku.theaurorian.Entities.CrystallineSprite;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Quaternionf;
import org.joml.AxisAngle4f;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import shiroroku.theaurorian.TheAurorian;

/**
 * Renders the Crystalline Sprite as a flat, camera-facing billboard using the
 * animated crystallinesprite texture (8 frames, frametime 3, matching the
 * upstream item-sprite render which was scaled 4x and offset +0.25y).
 */
public class CrystallineSpriteEntityRender extends EntityRenderer<CrystallineSpriteEntity> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(TheAurorian.MODID, "textures/entity/crystalline_sprite.png");
    private static final int FRAME_COUNT = 8;

    public CrystallineSpriteEntityRender(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(CrystallineSpriteEntity pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        pPoseStack.pushPose();
        pPoseStack.translate(0.0D, 0.25D, 0.0D);
        pPoseStack.scale(4.0F, 4.0F, 4.0F);
        float yaw = Mth.lerp(pPartialTick, pEntity.yRotO, pEntity.getYRot()) + 180.0F;
        float pitch = Mth.lerp(pPartialTick, pEntity.xRotO, pEntity.getXRot());
        pPoseStack.mulPose(new Quaternionf(new AxisAngle4f((float)Math.toRadians(-yaw), 0.0F, 1.0F, 0.0F)));
        pPoseStack.mulPose(new Quaternionf(new AxisAngle4f((float)Math.toRadians(pitch), 1.0F, 0.0F, 0.0F)));

        int frame = (pEntity.tickCount / 3) % FRAME_COUNT;
        float v0 = frame / (float) FRAME_COUNT;
        float v1 = (frame + 1) / (float) FRAME_COUNT;

        VertexConsumer vc = pBuffer.getBuffer(RenderType.entityTranslucent(TEXTURE));
        PoseStack.Pose pose = pPoseStack.last();
        Matrix4f mat = pose.pose();
        Matrix3f normal = pose.normal();
        float half = 0.125F;
        vc.vertex(mat, -half, -half, 0).color(1.0F, 1.0F, 1.0F, 1.0F).uv(0.0F, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(pPackedLight).normal(normal, 0, 0, 1).endVertex();
        vc.vertex(mat, half, -half, 0).color(1.0F, 1.0F, 1.0F, 1.0F).uv(1.0F, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(pPackedLight).normal(normal, 0, 0, 1).endVertex();
        vc.vertex(mat, half, half, 0).color(1.0F, 1.0F, 1.0F, 1.0F).uv(1.0F, v0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(pPackedLight).normal(normal, 0, 0, 1).endVertex();
        vc.vertex(mat, -half, half, 0).color(1.0F, 1.0F, 1.0F, 1.0F).uv(0.0F, v0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(pPackedLight).normal(normal, 0, 0, 1).endVertex();
        pPoseStack.popPose();
        super.render(pEntity, pEntityYaw, pPartialTick, pPoseStack, pBuffer, pPackedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(CrystallineSpriteEntity pEntity) {
        return TEXTURE;
    }
}
