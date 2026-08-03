package shiroroku.theaurorian.Entities.Passive;

import net.minecraft.client.model.RabbitModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;
import shiroroku.theaurorian.TheAurorian;

public class AurorianRabbitEntityModel extends RabbitModel<AurorianRabbitEntity> {

    public static final ModelLayerLocation MODEL_LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(TheAurorian.MODID, "aurorian_rabbit"), "main");

    public AurorianRabbitEntityModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createBodyLayer() {
        return RabbitModel.createBodyLayer();
    }
}
