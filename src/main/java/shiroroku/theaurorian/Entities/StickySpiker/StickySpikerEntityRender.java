package shiroroku.theaurorian.Entities.StickySpiker;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import shiroroku.theaurorian.Registry.ItemRegistry;

public class StickySpikerEntityRender extends EntityRenderer<StickySpikerEntity> {

    private final ItemRenderer itemRenderer;
    private final ItemStack item;

    public StickySpikerEntityRender(EntityRendererProvider.Context context) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
        this.item = new ItemStack(ItemRegistry.sticky_spiker.get());
    }

    @Override
    public void render(StickySpikerEntity pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        pPoseStack.pushPose();
        pPoseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        pPoseStack.mulPose(new Quaternion(new Vector3f(0.0F, 1.0F, 0.0F), 180.0F, true));
        this.itemRenderer.renderStatic(this.item, ItemTransforms.TransformType.GROUND, pPackedLight, OverlayTexture.NO_OVERLAY, pPoseStack, pBuffer, 0);
        pPoseStack.popPose();
        super.render(pEntity, pEntityYaw, pPartialTick, pPoseStack, pBuffer, pPackedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(StickySpikerEntity pEntity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
