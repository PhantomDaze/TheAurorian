package shiroroku.theaurorian.Entities.MoonAcolyte;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import shiroroku.theaurorian.TheAurorian;

public class MoonAcolyteEntityRender extends MobRenderer<MoonAcolyteEntity, MoonAcolyteModel> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(TheAurorian.MODID, "textures/entity/moon_acolyte.png");

    public MoonAcolyteEntityRender(EntityRendererProvider.Context context) {
        super(context, new MoonAcolyteModel(context.bakeLayer(MoonAcolyteModel.MODEL_LAYER_LOCATION)), 0.5F);
        this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
        this.addLayer(new MoonAcolyteEntityLayer(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(MoonAcolyteEntity entity) {
        return TEXTURE;
    }
}
