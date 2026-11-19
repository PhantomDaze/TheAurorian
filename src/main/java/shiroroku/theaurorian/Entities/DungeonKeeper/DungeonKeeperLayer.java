package shiroroku.theaurorian.Entities.DungeonKeeper;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import shiroroku.theaurorian.Entities.AurorianSkeletonModel;
import shiroroku.theaurorian.TheAurorian;

public class DungeonKeeperLayer extends RenderLayer<DungeonKeeperEntity, DungeonKeeperModel> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(TheAurorian.MODID, "textures/entity/dungeon_keeper/dungeon_keeper_layer.png");
    private final AurorianSkeletonModel<DungeonKeeperEntity> layerModel;

    public DungeonKeeperLayer(RenderLayerParent<DungeonKeeperEntity, DungeonKeeperModel> renderer, EntityModelSet modelSet) {
        super(renderer);
        this.layerModel = new AurorianSkeletonModel<>(modelSet.bakeLayer(DungeonKeeperModel.CLOTHING_LAYER));
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, DungeonKeeperEntity entity,
                       float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks,
                       float netHeadYaw, float headPitch) {
        coloredCutoutModelCopyLayerRender(this.getParentModel(), this.layerModel, TEXTURE, poseStack, buffer,
                packedLight, entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTicks,
                1.0F, 1.0F, 1.0F);
    }
}
