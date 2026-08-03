package shiroroku.theaurorian.Entities.Passive;

import net.minecraft.client.model.PigModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;
import shiroroku.theaurorian.TheAurorian;

public class AurorianPigEntityModel extends PigModel<AurorianPigEntity> {

    public static final ModelLayerLocation MODEL_LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(TheAurorian.MODID, "aurorian_pig"), "main");

    public AurorianPigEntityModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createBodyLayer() {
        return PigModel.createBodyLayer(CubeDeformation.NONE);
    }
}
