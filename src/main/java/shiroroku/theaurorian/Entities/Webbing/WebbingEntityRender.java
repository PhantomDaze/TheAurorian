package shiroroku.theaurorian.Entities.Webbing;

import com.mojang.blaze3d.vertex.PoseStack;
import org.joml.Quaternionf;
import org.joml.Quaternionf;
import org.joml.AxisAngle4f;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import shiroroku.theaurorian.Registry.ItemRegistry;

public class WebbingEntityRender extends EntityRenderer<WebbingEntity> {

    private final ItemRenderer itemRenderer;
    private final ItemStack item;

    public WebbingEntityRender(EntityRendererProvider.Context context) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
        this.item = new ItemStack(ItemRegistry.webbing.get());
    }

    @Override
    public void render(WebbingEntity pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        pPoseStack.pushPose();
        pPoseStack.scale(1.8F, 1.8F, 1.8F);
        pPoseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        pPoseStack.mulPose(new Quaternionf(new AxisAngle4f((float)Math.toRadians(180.0F), 0.0F, 1.0F, 0.0F)));
        this.itemRenderer.renderStatic(this.item, ItemDisplayContext.GROUND, pPackedLight, OverlayTexture.NO_OVERLAY, pPoseStack, pBuffer, null, 0);
        pPoseStack.popPose();
        super.render(pEntity, pEntityYaw, pPartialTick, pPoseStack, pBuffer, pPackedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(WebbingEntity pEntity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
