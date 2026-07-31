package shiroroku.theaurorian.Entities.Passive;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class AurorianSheepEntityRender extends MobRenderer<AurorianSheepEntity, AurorianSheepEntityModel2> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/sheep/sheep.png");

    public AurorianSheepEntityRender(EntityRendererProvider.Context context) {
        super(context, new AurorianSheepEntityModel2(context.bakeLayer(AurorianSheepEntityModel2.MODEL_LAYER_LOCATION)), 0.7F);
        this.addLayer(new AurorianSheepEntityLayer(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(AurorianSheepEntity pEntity) {
        return TEXTURE;
    }
}
