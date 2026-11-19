package shiroroku.theaurorian.Entities.DungeonKeeper;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import shiroroku.theaurorian.Entities.AurorianHumanoidModel;
import shiroroku.theaurorian.Entities.AurorianSkeletonModel;
import shiroroku.theaurorian.TheAurorian;

public class DungeonKeeperRenderer extends HumanoidMobRenderer<DungeonKeeperEntity, DungeonKeeperModel> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(TheAurorian.MODID, "textures/entity/dungeon_keeper/dungeon_keeper.png");

    public DungeonKeeperRenderer(EntityRendererProvider.Context context) {
        super(context, new DungeonKeeperModel(context.bakeLayer(DungeonKeeperModel.MODEL_LAYER_LOCATION)), 0.5F);
        this.addLayer(new HumanoidArmorLayer<>(this,
                new AurorianHumanoidModel<>(context.bakeLayer(DungeonKeeperModel.INNER_ARMOR_LAYER)),
                new AurorianHumanoidModel<>(context.bakeLayer(DungeonKeeperModel.OUTER_ARMOR_LAYER)),
                context.getModelManager()));
        this.addLayer(new DungeonKeeperLayer(this, context.getModelSet()));
    }

    @Override
    protected void scale(DungeonKeeperEntity entity, PoseStack poseStack, float partialTick) {
        poseStack.scale(2.0F, 2.0F, 2.0F);
        super.scale(entity, poseStack, partialTick);
    }

    @Override
    protected boolean isShaking(DungeonKeeperEntity entity) {
        return entity.isShaking();
    }

    @Override
    public ResourceLocation getTextureLocation(DungeonKeeperEntity entity) {
        return TEXTURE;
    }
}
