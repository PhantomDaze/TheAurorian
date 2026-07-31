package shiroroku.theaurorian.Entities.Passive;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import shiroroku.theaurorian.TheAurorian;

public class AurorianRabbitEntityRender extends MobRenderer<AurorianRabbitEntity, AurorianRabbitEntityModel> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(TheAurorian.MODID, "textures/entity/aurorian_rabbit.png");

    public AurorianRabbitEntityRender(EntityRendererProvider.Context context) {
        super(context, new AurorianRabbitEntityModel(context.bakeLayer(AurorianRabbitEntityModel.MODEL_LAYER_LOCATION)), 0.3F);
    }

    @Override
    public ResourceLocation getTextureLocation(AurorianRabbitEntity pEntity) {
        return TEXTURE;
    }
}
