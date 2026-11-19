package shiroroku.theaurorian.Entities.DisturbedHollow;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;
import shiroroku.theaurorian.Entities.AurorianHumanoidModel;
import shiroroku.theaurorian.TheAurorian;

public class DisturbedHollowModel extends AurorianHumanoidModel<DisturbedHollowEntity> {

    public static final ModelLayerLocation MODEL_LAYER_LOCATION = layer("main");

    public DisturbedHollowModel(ModelPart root) {
        super(root);
    }

    private static ModelLayerLocation layer(String name) {
        return new ModelLayerLocation(new ResourceLocation(TheAurorian.MODID, "disturbed_hollow"), name);
    }

    public static net.minecraft.client.model.geom.builders.LayerDefinition createBodyLayer() {
        return AurorianHumanoidModel.createBodyLayer();
    }

    @Override
    public void setupAnim(DisturbedHollowEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                          float netHeadYaw, float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        if (entity.isArmsRaised()) {
            this.rightArm.xRot = -1.4F;
            this.leftArm.xRot = -1.4F;
            this.rightArm.zRot = 0.0F;
            this.leftArm.zRot = 0.0F;
        }
    }
}
