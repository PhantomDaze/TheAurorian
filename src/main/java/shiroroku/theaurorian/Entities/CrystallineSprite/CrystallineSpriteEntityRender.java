package shiroroku.theaurorian.Entities.CrystallineSprite;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
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
        pPoseStack.mulPose(new Quaternion(new Vector3f(0.0F, 1.0F, 0.0F), -yaw, true));
        pPoseStack.mulPose(new Quaternion(new Vector3f(1.0F, 0.0F, 0.0F), pitch, true));
        pPoseStack.mulPose(new Quaternion(new Vector3f(0.0F, 1.0F, 0.0F), 180.0F, true));
        this.itemRenderer.renderStatic(this.item, ItemTransforms.TransformType.GROUND, pPackedLight, OverlayTexture.NO_OVERLAY, pPoseStack, pBuffer, 0);
        pPoseStack.popPose();
        super.render(pEntity, pEntityYaw, pPartialTick, pPoseStack, pBuffer, pPackedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(CrystallineSpriteEntity pEntity) {
        return TEXTURE;
    }
}
