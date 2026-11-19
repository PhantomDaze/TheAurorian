package shiroroku.theaurorian.Entities.DungeonKeeper;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.ResourceLocation;
import shiroroku.theaurorian.Entities.AurorianSkeletonModel;
import shiroroku.theaurorian.TheAurorian;

public class DungeonKeeperModel extends AurorianSkeletonModel<DungeonKeeperEntity> {

    public static final ModelLayerLocation MODEL_LAYER_LOCATION = layer("main");
    public static final ModelLayerLocation INNER_ARMOR_LAYER = layer("inner_armor");
    public static final ModelLayerLocation OUTER_ARMOR_LAYER = layer("outer_armor");
    public static final ModelLayerLocation CLOTHING_LAYER = layer("outer");

    public DungeonKeeperModel(ModelPart root) {
        super(root);
    }

    private static ModelLayerLocation layer(String name) {
        return new ModelLayerLocation(new ResourceLocation(TheAurorian.MODID, "dungeon_keeper"), name);
    }
}
