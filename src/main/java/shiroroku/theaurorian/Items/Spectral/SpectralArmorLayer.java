package shiroroku.theaurorian.Items.Spectral;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import shiroroku.theaurorian.TheAurorian;

/**
 * Renders Spectral armor pieces with a translucent render type so their RGBA
 * ghost texture is actually see-through (vanilla armor uses an opaque cutout
 * render type). The vanilla armor layer is neutralised for these pieces by
 * pointing their armor texture at a fully transparent placeholder.
 */
public class SpectralArmorLayer<T extends LivingEntity, M extends PlayerModel<T>> extends RenderLayer<T, M> {

    private final HumanoidModel<T> innerModel;
    private final HumanoidModel<T> outerModel;

    public SpectralArmorLayer(RenderLayerParent<T, M> pRenderer, HumanoidModel<T> pInnerModel, HumanoidModel<T> pOuterModel) {
        super(pRenderer);
        this.innerModel = pInnerModel;
        this.outerModel = pOuterModel;
    }

    @Override
    public void render(PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, T pEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTick, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        this.renderPiece(pPoseStack, pBufferSource, pPackedLight, pEntity, EquipmentSlot.HEAD, this.innerModel);
        this.renderPiece(pPoseStack, pBufferSource, pPackedLight, pEntity, EquipmentSlot.CHEST, this.outerModel);
        this.renderPiece(pPoseStack, pBufferSource, pPackedLight, pEntity, EquipmentSlot.LEGS, this.innerModel);
        this.renderPiece(pPoseStack, pBufferSource, pPackedLight, pEntity, EquipmentSlot.FEET, this.outerModel);
    }

    private void renderPiece(PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, T pEntity, EquipmentSlot pSlot, HumanoidModel<T> pModel) {
        ItemStack stack = pEntity.getItemBySlot(pSlot);
        if (stack.getItem() instanceof SpectralArmor) {
            this.getParentModel().copyPropertiesTo(pModel);
            pModel.setAllVisible(false);
            this.setPartVisibility(pModel, pSlot);
            ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(TheAurorian.MODID, "textures/models/armor/spectral_layer_" + (pSlot == EquipmentSlot.LEGS ? 2 : 1) + ".png");
            pModel.renderToBuffer(pPoseStack, pBufferSource.getBuffer(RenderType.entityTranslucent(texture)), pPackedLight, OverlayTexture.NO_OVERLAY, net.minecraft.util.FastColor.ARGB32.color((int)(1.0*255), (int)(1.0*255), (int)(1.0*255), (int)(1.0*255)));
        }
    }

    private void setPartVisibility(HumanoidModel<T> pModel, EquipmentSlot pSlot) {
        switch (pSlot) {
            case HEAD -> {
                pModel.head.visible = true;
                pModel.hat.visible = true;
            }
            case CHEST -> {
                pModel.body.visible = true;
                pModel.rightArm.visible = true;
                pModel.leftArm.visible = true;
            }
            case LEGS -> {
                pModel.body.visible = true;
                pModel.rightLeg.visible = true;
                pModel.leftLeg.visible = true;
            }
            case FEET -> {
                pModel.rightLeg.visible = true;
                pModel.leftLeg.visible = true;
            }
            default -> {
            }
        }
    }
}
