package shiroroku.theaurorian.Entities.DisturbedHollow;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import shiroroku.theaurorian.TheAurorian;

public class DisturbedHollowEntityRender extends MobRenderer<DisturbedHollowEntity, DisturbedHollowEntityRender.DisturbedHollowModel> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(TheAurorian.MODID, "textures/entity/disturbed_hollow.png");

    public DisturbedHollowEntityRender(EntityRendererProvider.Context context) {
        super(context, new DisturbedHollowModel(context.bakeLayer(ModelLayers.ZOMBIE)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(DisturbedHollowEntity pEntity) {
        return TEXTURE;
    }

    static class DisturbedHollowModel extends HumanoidModel<DisturbedHollowEntity> {
        public DisturbedHollowModel(ModelPart root) {
            super(root);
        }

        @Override
        public void setupAnim(DisturbedHollowEntity pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
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
