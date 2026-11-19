package shiroroku.theaurorian.Entities.Hollow;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import shiroroku.theaurorian.TheAurorian;
import shiroroku.theaurorian.Entities.AurorianHumanoidModel;

public class HollowRenderer extends HumanoidMobRenderer<HollowEntity, HollowModel> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(TheAurorian.MODID, "textures/entity/hollow.png");

    public HollowRenderer(EntityRendererProvider.Context context) {
        super(context, new HollowModel(context.bakeLayer(HollowModel.MODEL_LAYER_LOCATION)), 0.5F);
        this.addLayer(new HumanoidArmorLayer<>(this,
                new AurorianHumanoidModel<>(context.bakeLayer(HollowModel.INNER_ARMOR_LAYER)),
                new AurorianHumanoidModel<>(context.bakeLayer(HollowModel.OUTER_ARMOR_LAYER)),
                context.getModelManager()));
    }

    @Override
    protected void scale(HollowEntity entity, PoseStack poseStack, float partialTick) {
        poseStack.scale(1.0625F, 1.0625F, 1.0625F);
        super.scale(entity, poseStack, partialTick);
    }

    @Override
    protected boolean isShaking(HollowEntity entity) {
        return super.isShaking(entity) || entity.isUnderWaterConverting();
    }

    @Override
    public ResourceLocation getTextureLocation(HollowEntity entity) {
        return TEXTURE;
    }
}
