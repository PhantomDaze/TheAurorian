package shiroroku.theaurorian.Entities.Boss;

import com.mojang.blaze3d.vertex.PoseStack;
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
    protected void scale(DungeonSpiderEntity pLivingEntity, PoseStack pMatrixStack, float pPartialTickTime) {
        // The model is scaled independently; the 2.8 x 1.8 hitbox stays unchanged.
        pMatrixStack.scale(2.0F, 2.0F, 2.0F);
        super.scale(pLivingEntity, pMatrixStack, pPartialTickTime);
    }

    @Override
    public ResourceLocation getTextureLocation(DungeonSpiderEntity pEntity) {
        return ResourceLocation.fromNamespaceAndPath(TheAurorian.MODID, "textures/entity/dungeon_spider.png");
    }
}
