package shiroroku.theaurorian.Entities.Passive;

import net.minecraft.client.model.SheepModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;
import shiroroku.theaurorian.TheAurorian;

public class AurorianSheepEntityModel2 extends SheepModel<AurorianSheepEntity> {

    public static final ModelLayerLocation MODEL_LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(TheAurorian.MODID, "aurorian_sheep"), "main");

    public AurorianSheepEntityModel2(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createBodyLayer() {
        return SheepModel.createBodyLayer();
    }
}
