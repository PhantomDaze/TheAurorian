package shiroroku.theaurorian.Entities.Boss;

import net.minecraft.client.model.SpiderModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import shiroroku.theaurorian.TheAurorian;

public class DungeonSpiderRenderer extends MobRenderer<DungeonSpiderEntity, SpiderModel<DungeonSpiderEntity>> {

    public DungeonSpiderRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new SpiderModel<>(pContext.bakeLayer(ModelLayers.SPIDER)), 1.0F);
    }

    @Override
    public ResourceLocation getTextureLocation(DungeonSpiderEntity pEntity) {
        return ResourceLocation.fromNamespaceAndPath(TheAurorian.MODID, "textures/entity/dungeon_spider.png");
    }
}
