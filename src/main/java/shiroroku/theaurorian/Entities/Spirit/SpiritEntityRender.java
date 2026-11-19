package shiroroku.theaurorian.Entities.Spirit;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import shiroroku.theaurorian.TheAurorian;

public class SpiritEntityRender extends MobRenderer<SpiritEntity, SpiritModel> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(TheAurorian.MODID, "textures/entity/spirit.png");

    public SpiritEntityRender(EntityRendererProvider.Context context) {
        super(context, new SpiritModel(context.bakeLayer(SpiritModel.MODEL_LAYER_LOCATION)), 0.5F);
    }

    @Override
    protected RenderType getRenderType(SpiritEntity entity, boolean bodyVisible, boolean translucent, boolean glowing) {
        return RenderType.entityTranslucent(this.getTextureLocation(entity));
    }

    @Override
    public ResourceLocation getTextureLocation(SpiritEntity entity) {
        return TEXTURE;
    }
}
