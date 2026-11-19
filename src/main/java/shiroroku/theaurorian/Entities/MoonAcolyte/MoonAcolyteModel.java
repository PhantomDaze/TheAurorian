package shiroroku.theaurorian.Entities.MoonAcolyte;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;
import shiroroku.theaurorian.Entities.AurorianHumanoidModel;
import shiroroku.theaurorian.TheAurorian;

public class MoonAcolyteModel extends AurorianHumanoidModel<MoonAcolyteEntity> {

    public static final ModelLayerLocation MODEL_LAYER_LOCATION = layer("main");
    public static final ModelLayerLocation OVERLAY_LAYER = layer("overlay");

    public MoonAcolyteModel(ModelPart root) {
        super(root);
    }

    private static ModelLayerLocation layer(String name) {
        return new ModelLayerLocation(new ResourceLocation(TheAurorian.MODID, "moon_acolyte"), name);
    }

    public static net.minecraft.client.model.geom.builders.LayerDefinition createBodyLayer() {
        return AurorianHumanoidModel.createBodyLayer();
    }

    public static net.minecraft.client.model.geom.builders.LayerDefinition createOverlayLayer() {
        return AurorianHumanoidModel.createBodyLayer();
    }

    @Override
    public void setupAnim(MoonAcolyteEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks,
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
