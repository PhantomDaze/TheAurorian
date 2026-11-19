package shiroroku.theaurorian.Entities.Boss;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import shiroroku.theaurorian.TheAurorian;

public class DungeonSpiderRenderer extends MobRenderer<DungeonSpiderEntity, DungeonSpiderModel<DungeonSpiderEntity>> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(TheAurorian.MODID, "textures/entity/dungeon_spider.png");

    public DungeonSpiderRenderer(EntityRendererProvider.Context context) {
        super(context, new DungeonSpiderModel<>(context.bakeLayer(DungeonSpiderModel.MODEL_LAYER_LOCATION)), 1.0F);
    }

    @Override
    protected void scale(DungeonSpiderEntity entity, PoseStack poseStack, float partialTick) {
        poseStack.scale(2.0F, 2.0F, 2.0F);
        super.scale(entity, poseStack, partialTick);
    }

    @Override
    public ResourceLocation getTextureLocation(DungeonSpiderEntity entity) {
        return TEXTURE;
    }
}
