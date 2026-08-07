package shiroroku.theaurorian.Entities.CrystallineSprite;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import shiroroku.theaurorian.Registry.ItemRegistry;
import shiroroku.theaurorian.TheAurorian;

/**
 * Renders the Crystalline Sprite using the same ground item model as upstream.
 */
public class CrystallineSpriteEntityRender extends EntityRenderer<CrystallineSpriteEntity> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(TheAurorian.MODID, "textures/entity/crystalline_sprite.png");
    private final ItemRenderer itemRenderer;
    private final ItemStack item;

    public CrystallineSpriteEntityRender(EntityRendererProvider.Context context) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
        this.item = new ItemStack(ItemRegistry.crystalline_sprite.get());
    }

    @Override
    public void render(CrystallineSpriteEntity pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        pPoseStack.pushPose();
        pPoseStack.translate(0.0D, 0.25D, 0.0D);
        pPoseStack.scale(4.0F, 4.0F, 4.0F);
        float yaw = Mth.rotLerp(pPartialTick, pEntity.yHeadRotO, pEntity.yHeadRot);
        float pitch = Mth.lerp(pPartialTick, pEntity.xRotO, pEntity.getXRot());
        pPoseStack.mulPose(Axis.YP.rotationDegrees(-yaw));
        pPoseStack.mulPose(Axis.XP.rotationDegrees(pitch));
        pPoseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        this.itemRenderer.renderStatic(this.item, ItemDisplayContext.GROUND, pPackedLight, OverlayTexture.NO_OVERLAY, pPoseStack, pBuffer, null, 0);
        pPoseStack.popPose();
        super.render(pEntity, pEntityYaw, pPartialTick, pPoseStack, pBuffer, pPackedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(CrystallineSpriteEntity pEntity) {
        return TEXTURE;
    }
}
