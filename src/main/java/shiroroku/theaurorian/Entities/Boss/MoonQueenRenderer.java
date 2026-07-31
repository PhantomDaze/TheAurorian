package shiroroku.theaurorian.Entities.Boss;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;
import shiroroku.theaurorian.TheAurorian;

public class MoonQueenRenderer extends HumanoidMobRenderer<MoonQueenEntity, HumanoidModel<MoonQueenEntity>> {

    public MoonQueenRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new HumanoidModel<>(pContext.bakeLayer(ModelLayers.PLAYER)), 0.6F * MoonQueenEntity.MOB_SCALE);
    }

    @Override
    public ResourceLocation getTextureLocation(MoonQueenEntity pEntity) {
        return new ResourceLocation(TheAurorian.MODID, "textures/entity/moon_queen.png");
    }
}
