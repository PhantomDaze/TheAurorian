package shiroroku.theaurorian.Entities.Spiderling;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import shiroroku.theaurorian.TheAurorian;

public class SpiderlingEntityRender extends MobRenderer<SpiderlingEntity, SpiderlingEntityModel> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(TheAurorian.MODID, "textures/entity/spiderling.png");

    private static final float MOB_SCALE = 0.5F;

    public SpiderlingEntityRender(EntityRendererProvider.Context context) {
        super(context, new SpiderlingEntityModel(context.bakeLayer(SpiderlingEntityModel.MODEL_LAYER_LOCATION)), 0.2F);
    }

    @Override
    protected void scale(SpiderlingEntity pLivingEntity, PoseStack pMatrixStack, float pPartialTickTime) {
        pMatrixStack.scale(MOB_SCALE, MOB_SCALE, MOB_SCALE);
        super.scale(pLivingEntity, pMatrixStack, pPartialTickTime);
    }

    @Override
    public ResourceLocation getTextureLocation(SpiderlingEntity pEntity) {
        return TEXTURE;
    }
}
