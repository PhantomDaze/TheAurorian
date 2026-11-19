package shiroroku.theaurorian.Entities.DisturbedHollow;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import shiroroku.theaurorian.TheAurorian;

public class DisturbedHollowEntityRender extends MobRenderer<DisturbedHollowEntity, DisturbedHollowModel> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(TheAurorian.MODID, "textures/entity/disturbed_hollow.png");

    public DisturbedHollowEntityRender(EntityRendererProvider.Context context) {
        super(context, new DisturbedHollowModel(context.bakeLayer(DisturbedHollowModel.MODEL_LAYER_LOCATION)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(DisturbedHollowEntity entity) {
        return TEXTURE;
    }
}
