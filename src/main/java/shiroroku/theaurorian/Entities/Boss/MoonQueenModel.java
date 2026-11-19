package shiroroku.theaurorian.Entities.Boss;

import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;
import shiroroku.theaurorian.Entities.AurorianHumanoidModel;
import shiroroku.theaurorian.TheAurorian;

public class MoonQueenModel extends AurorianHumanoidModel<MoonQueenEntity> {

    public static final ModelLayerLocation MODEL_LAYER_LOCATION = layer("main");
    public static final ModelLayerLocation INNER_ARMOR_LAYER = layer("inner_armor");
    public static final ModelLayerLocation OUTER_ARMOR_LAYER = layer("outer_armor");

    public MoonQueenModel(ModelPart root) {
        super(root);
    }

    private static ModelLayerLocation layer(String name) {
        return new ModelLayerLocation(new ResourceLocation(TheAurorian.MODID, "moon_queen"), name);
    }

    public static LayerDefinition createBodyLayer() {
        return AurorianHumanoidModel.createBodyLayer();
    }

    public static LayerDefinition createInnerArmorLayer() {
        return AurorianHumanoidModel.createInnerArmorLayer();
    }

    public static LayerDefinition createOuterArmorLayer() {
        return AurorianHumanoidModel.createOuterArmorLayer();
    }

    @Override
    public void setupAnim(MoonQueenEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                          float netHeadYaw, float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        AnimationUtils.animateZombieArms(this.leftArm, this.rightArm, entity.isAggressive(), this.attackTime, ageInTicks);
    }
}
