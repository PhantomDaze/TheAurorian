package shiroroku.theaurorian.Entities.MoonAcolyte;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import shiroroku.theaurorian.TheAurorian;

public class MoonAcolyteEntityLayer extends RenderLayer<MoonAcolyteEntity, MoonAcolyteEntityRender.MoonAcolyteModel> {

    private static final ResourceLocation LAYER_TEXTURE = new ResourceLocation(TheAurorian.MODID, "textures/entity/moon_acolyte_layer.png");
    private final HumanoidModel<MoonAcolyteEntity> model;

    public MoonAcolyteEntityLayer(RenderLayerParent<MoonAcolyteEntity, MoonAcolyteEntityRender.MoonAcolyteModel> pRenderer, EntityModelSet set) {
        super(pRenderer);
        this.model = new HumanoidModel<>(set.bakeLayer(ModelLayers.ZOMBIE));
    }

    @Override
    public void render(PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight, MoonAcolyteEntity pEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTicks, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        VertexConsumer vertexconsumer = pBuffer.getBuffer(RenderType.entityTranslucent(LAYER_TEXTURE));
        this.getParentModel().copyPropertiesTo(this.model);
        this.model.prepareMobModel(pEntity, pLimbSwing, pLimbSwingAmount, pPartialTicks);
        this.model.setupAnim(pEntity, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
        this.model.renderToBuffer(pMatrixStack, vertexconsumer, pPackedLight, LivingEntityRenderer.getOverlayCoords(pEntity, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
    }
}
