package shiroroku.theaurorian.Util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Matrix4f;

public class RenderUtil {

    public static void renderItem(ItemRenderer itemRenderer, PoseStack pPoseStack, Item item, int x, int y) {
        // Draw into the current PoseStack (used by Mirror of Guidance node graph).
        MultiBufferSource.BufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();
        pPoseStack.pushPose();
        pPoseStack.translate(x, y, 100);
        pPoseStack.scale(16f, -16f, 16f);
        itemRenderer.renderStatic(new ItemStack(item), net.minecraft.world.item.ItemDisplayContext.GUI,
                0xF000F0, net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY,
                pPoseStack, buffers, null, 0);
        buffers.endBatch();
        pPoseStack.popPose();
    }

    public static void renderItem(GuiGraphics graphics, Item item, int x, int y) {
        graphics.renderItem(new ItemStack(item), x - 8, y - 8);
    }

    public static void blit(PoseStack pPoseStack, ResourceLocation atlas, int x, int y, float pUOffset, float pVOffset, int pWidth, int pHeight, int pTextureWidth, int pTextureHeight) {
        RenderSystem.setShaderTexture(0, atlas);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        Matrix4f matrix = pPoseStack.last().pose();
        float u0 = pUOffset / (float) pTextureWidth;
        float u1 = (pUOffset + pWidth) / (float) pTextureWidth;
        float v0 = pVOffset / (float) pTextureHeight;
        float v1 = (pVOffset + pHeight) / (float) pTextureHeight;
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.vertex(matrix, (float) x, (float) y, 0).uv(u0, v0).endVertex();
        bufferbuilder.vertex(matrix, (float) x, (float) (y + pHeight), 0).uv(u0, v1).endVertex();
        bufferbuilder.vertex(matrix, (float) (x + pWidth), (float) (y + pHeight), 0).uv(u1, v1).endVertex();
        bufferbuilder.vertex(matrix, (float) (x + pWidth), (float) y, 0).uv(u1, v0).endVertex();
        BufferUploader.drawWithShader(bufferbuilder.end());
    }

    public static void blit(GuiGraphics graphics, ResourceLocation atlas, int x, int y, float pUOffset, float pVOffset, int pWidth, int pHeight, int pTextureWidth, int pTextureHeight) {
        graphics.blit(atlas, x, y, pUOffset, pVOffset, pWidth, pHeight, pTextureWidth, pTextureHeight);
    }

    public static void blitRepeating(ResourceLocation atlas, int x, int y, int w, int h, float u, float v) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        RenderSystem.setShaderTexture(0, atlas);
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.vertex(x, y, 0.0D).uv(u, v).endVertex();
        bufferbuilder.vertex(x, y + h, 0.0D).uv(u, v + 1).endVertex();
        bufferbuilder.vertex(x + w, y + h, 0.0D).uv(u + 1, v + 1).endVertex();
        bufferbuilder.vertex(x + w, y, 0.0D).uv(u + 1, v).endVertex();
        BufferUploader.drawWithShader(bufferbuilder.end());
    }

    public static boolean isMouseOver(int pX, int pY, int pWidth, int pHeight, double pMouseX, double pMouseY) {
        return pMouseX >= (pX - 1) && pMouseX < (pX + pWidth + 1) && pMouseY >= (pY - 1) && pMouseY < (pY + pHeight + 1);
    }

    public static void renderBlockOutline(PoseStack poseStack, MultiBufferSource bufferSource, BlockPos pos, Camera camera, float r, float g, float b, float a) {
        double pX = (double) pos.getX() - camera.getPosition().x;
        double pY = (double) pos.getY() - camera.getPosition().y;
        double pZ = (double) pos.getZ() - camera.getPosition().z;
        poseStack.pushPose();
        PoseStack.Pose last = poseStack.last();
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.lines());
        VoxelShape shape = Minecraft.getInstance().level.getBlockState(pos).getShape(Minecraft.getInstance().level, pos, CollisionContext.of(camera.getEntity()));
        shape.forAllEdges((x, y, z, x1, y1, z1) -> {
            float dx = (float) (x1 - x);
            float dy = (float) (y1 - y);
            float dz = (float) (z1 - z);
            float f3 = Mth.sqrt(dx * dx + dy * dy + dz * dz);
            dx /= f3;
            dy /= f3;
            dz /= f3;
            consumer.vertex(last.pose(), (float) (x + pX), (float) (y + pY), (float) (z + pZ)).color(r, g, b, a).normal(last.normal(), dx, dy, dz).endVertex();
            consumer.vertex(last.pose(), (float) (x1 + pX), (float) (y1 + pY), (float) (z1 + pZ)).color(r, g, b, a).normal(last.normal(), dx, dy, dz).endVertex();
        });
        poseStack.popPose();
    }
}
