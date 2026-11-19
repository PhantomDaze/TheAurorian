package shiroroku.theaurorian.Entities.UndeadKnight;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import shiroroku.theaurorian.Entities.AurorianHumanoidModel;
import shiroroku.theaurorian.TheAurorian;

public class UndeadKnightRenderer extends HumanoidMobRenderer<UndeadKnightEntity, UndeadKnightModel> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(TheAurorian.MODID, "textures/entity/undead_knight.png");

    public UndeadKnightRenderer(EntityRendererProvider.Context context) {
        super(context, new UndeadKnightModel(context.bakeLayer(UndeadKnightModel.MODEL_LAYER_LOCATION)), 0.5F);
        this.addLayer(new HumanoidArmorLayer<>(this,
                new AurorianHumanoidModel<>(context.bakeLayer(UndeadKnightModel.INNER_ARMOR_LAYER)),
                new AurorianHumanoidModel<>(context.bakeLayer(UndeadKnightModel.OUTER_ARMOR_LAYER)),
                context.getModelManager()));
    }

    @Override
    protected void scale(UndeadKnightEntity entity, PoseStack poseStack, float partialTick) {
        poseStack.scale(1.3F, 1.3F, 1.3F);
        super.scale(entity, poseStack, partialTick);
    }

    @Override
    public ResourceLocation getTextureLocation(UndeadKnightEntity entity) {
        return TEXTURE;
    }
}
