package shiroroku.theaurorian.Entities.Spirit;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import shiroroku.theaurorian.TheAurorian;

public class SpiritEntityRender extends MobRenderer<SpiritEntity, SpiritEntityRender.SpiritModel> {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(TheAurorian.MODID, "textures/entity/spirit.png");

    public SpiritEntityRender(EntityRendererProvider.Context context) {
        super(context, new SpiritModel(context.bakeLayer(ModelLayers.ZOMBIE)), 0.5F);
    }

    @Override
    protected RenderType getRenderType(SpiritEntity pLivingEntity, boolean pBodyVisible, boolean pTranslucent, boolean pGlowing) {
        return RenderType.entityTranslucent(this.getTextureLocation(pLivingEntity));
    }

    @Override
    public ResourceLocation getTextureLocation(SpiritEntity pEntity) {
        return TEXTURE;
    }

    static class SpiritModel extends HumanoidModel<SpiritEntity> {
        public SpiritModel(ModelPart root) {
            super(root);
        }

        @Override
        public void setupAnim(SpiritEntity pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
            super.setupAnim(pEntity, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
            if (pEntity.isArmsRaised()) {
                this.rightArm.xRot = -1.4F;
                this.leftArm.xRot = -1.4F;
                this.rightArm.zRot = 0.0F;
                this.leftArm.zRot = 0.0F;
            }
        }
    }
}
