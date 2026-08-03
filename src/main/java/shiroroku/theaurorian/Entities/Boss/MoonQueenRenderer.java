package shiroroku.theaurorian.Entities.Boss;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import shiroroku.theaurorian.TheAurorian;

public class MoonQueenRenderer extends HumanoidMobRenderer<MoonQueenEntity, HumanoidModel<MoonQueenEntity>> {

    public MoonQueenRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new HumanoidModel<>(pContext.bakeLayer(ModelLayers.ZOMBIE)), 0.6F * MoonQueenEntity.MOB_SCALE);
        this.addLayer(new HumanoidArmorLayer<>(this,
                new HumanoidModel<>(pContext.bakeLayer(ModelLayers.ZOMBIE_INNER_ARMOR)),
                new HumanoidModel<>(pContext.bakeLayer(ModelLayers.ZOMBIE_OUTER_ARMOR)),
                pContext.getModelManager()));
    }

    @Override
    public ResourceLocation getTextureLocation(MoonQueenEntity pEntity) {
        return ResourceLocation.fromNamespaceAndPath(TheAurorian.MODID, "textures/entity/moon_queen.png");
    }
}
