package shiroroku.theaurorian.Entities.UndeadKnight;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;
import shiroroku.theaurorian.Entities.AurorianHumanoidModel;
import shiroroku.theaurorian.TheAurorian;

public class UndeadKnightModel extends AurorianHumanoidModel<UndeadKnightEntity> {

    public static final ModelLayerLocation MODEL_LAYER_LOCATION = layer("main");
    public static final ModelLayerLocation INNER_ARMOR_LAYER = layer("inner_armor");
    public static final ModelLayerLocation OUTER_ARMOR_LAYER = layer("outer_armor");

    public UndeadKnightModel(ModelPart root) {
        super(root);
    }

    private static ModelLayerLocation layer(String name) {
        return new ModelLayerLocation(new ResourceLocation(TheAurorian.MODID, "undead_knight"), name);
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

}
