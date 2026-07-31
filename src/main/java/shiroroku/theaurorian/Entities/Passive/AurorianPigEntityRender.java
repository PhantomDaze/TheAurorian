package shiroroku.theaurorian.Entities.Passive;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import shiroroku.theaurorian.TheAurorian;

public class AurorianPigEntityRender extends MobRenderer<AurorianPigEntity, AurorianPigEntityModel> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(TheAurorian.MODID, "textures/entity/aurorian_pig.png");

    public AurorianPigEntityRender(EntityRendererProvider.Context context) {
        super(context, new AurorianPigEntityModel(context.bakeLayer(AurorianPigEntityModel.MODEL_LAYER_LOCATION)), 0.7F);
    }

    @Override
    public ResourceLocation getTextureLocation(AurorianPigEntity pEntity) {
        return TEXTURE;
    }
}
