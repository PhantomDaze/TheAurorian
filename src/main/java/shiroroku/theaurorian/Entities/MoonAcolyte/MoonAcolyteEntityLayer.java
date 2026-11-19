package shiroroku.theaurorian.Entities.MoonAcolyte;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import shiroroku.theaurorian.TheAurorian;

public class MoonAcolyteEntityLayer extends RenderLayer<MoonAcolyteEntity, MoonAcolyteModel> {

    private static final ResourceLocation LAYER_TEXTURE = new ResourceLocation(TheAurorian.MODID, "textures/entity/moon_acolyte_layer.png");
    private final MoonAcolyteModel model;

    public MoonAcolyteEntityLayer(RenderLayerParent<MoonAcolyteEntity, MoonAcolyteModel> renderer,
                                  net.minecraft.client.model.geom.EntityModelSet modelSet) {
        super(renderer);
        this.model = new MoonAcolyteModel(modelSet.bakeLayer(MoonAcolyteModel.OVERLAY_LAYER));
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, MoonAcolyteEntity entity,
                       float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks,
                       float netHeadYaw, float headPitch) {
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityTranslucent(LAYER_TEXTURE));
        this.getParentModel().copyPropertiesTo(this.model);
        this.model.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
        this.model.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        this.model.renderToBuffer(poseStack, vertexConsumer, packedLight,
                LivingEntityRenderer.getOverlayCoords(entity, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
    }
}
