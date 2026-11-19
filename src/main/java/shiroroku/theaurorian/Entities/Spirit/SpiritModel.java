package shiroroku.theaurorian.Entities.Spirit;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;
import shiroroku.theaurorian.Entities.AurorianHumanoidModel;
import shiroroku.theaurorian.TheAurorian;

public class SpiritModel extends AurorianHumanoidModel<SpiritEntity> {

    public static final ModelLayerLocation MODEL_LAYER_LOCATION = layer("main");

    public SpiritModel(ModelPart root) {
        super(root);
    }

    private static ModelLayerLocation layer(String name) {
        return new ModelLayerLocation(new ResourceLocation(TheAurorian.MODID, "spirit"), name);
    }

    public static net.minecraft.client.model.geom.builders.LayerDefinition createBodyLayer() {
        return AurorianHumanoidModel.createBodyLayer();
    }

    @Override
    public void setupAnim(SpiritEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks,
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
