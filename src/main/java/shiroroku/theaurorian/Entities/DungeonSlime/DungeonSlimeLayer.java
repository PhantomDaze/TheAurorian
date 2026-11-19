package shiroroku.theaurorian.Entities.DungeonSlime;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;

public class DungeonSlimeLayer extends RenderLayer<DungeonSlimeEntity, DungeonSlimeModel> {

    private final DungeonSlimeOuterModel<DungeonSlimeEntity> model;

    public DungeonSlimeLayer(RenderLayerParent<DungeonSlimeEntity, DungeonSlimeModel> renderer, EntityModelSet modelSet) {
        super(renderer);
        this.model = new DungeonSlimeOuterModel<>(modelSet.bakeLayer(DungeonSlimeModel.MODEL_LAYER_LOCATION_OUTER));
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, DungeonSlimeEntity entity,
                       float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks,
                       float netHeadYaw, float headPitch) {
        boolean glowing = Minecraft.getInstance().shouldEntityAppearGlowing(entity) && entity.isInvisible();
        if (!entity.isInvisible() || glowing) {
            VertexConsumer vertexConsumer = glowing
                    ? buffer.getBuffer(RenderType.outline(this.getTextureLocation(entity)))
                    : buffer.getBuffer(RenderType.entityTranslucent(this.getTextureLocation(entity)));
            this.getParentModel().copyPropertiesTo(this.model);
            this.model.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
            this.model.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            this.model.renderToBuffer(poseStack, vertexConsumer, packedLight,
                    LivingEntityRenderer.getOverlayCoords(entity, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
        }
    }
}
