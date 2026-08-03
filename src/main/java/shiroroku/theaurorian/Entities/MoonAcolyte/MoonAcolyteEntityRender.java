package shiroroku.theaurorian.Entities.MoonAcolyte;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import shiroroku.theaurorian.TheAurorian;

public class MoonAcolyteEntityRender extends MobRenderer<MoonAcolyteEntity, MoonAcolyteEntityRender.MoonAcolyteModel> {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(TheAurorian.MODID, "textures/entity/moon_acolyte.png");

    public MoonAcolyteEntityRender(EntityRendererProvider.Context context) {
        super(context, new MoonAcolyteModel(context.bakeLayer(ModelLayers.ZOMBIE)), 0.5F);
        this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
        this.addLayer(new MoonAcolyteEntityLayer(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(MoonAcolyteEntity pEntity) {
        return TEXTURE;
    }

    static class MoonAcolyteModel extends HumanoidModel<MoonAcolyteEntity> {
        public MoonAcolyteModel(ModelPart root) {
            super(root);
        }

        @Override
        public void setupAnim(MoonAcolyteEntity pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
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
