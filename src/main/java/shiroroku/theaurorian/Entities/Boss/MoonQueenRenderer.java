package shiroroku.theaurorian.Entities.Boss;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import shiroroku.theaurorian.Entities.AurorianHumanoidModel;
import shiroroku.theaurorian.TheAurorian;

public class MoonQueenRenderer extends HumanoidMobRenderer<MoonQueenEntity, MoonQueenModel> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(TheAurorian.MODID, "textures/entity/moon_queen.png");

    public MoonQueenRenderer(EntityRendererProvider.Context context) {
        super(context, new MoonQueenModel(context.bakeLayer(MoonQueenModel.MODEL_LAYER_LOCATION)),
                0.6F * MoonQueenEntity.MOB_SCALE);
        this.addLayer(new HumanoidArmorLayer<>(this,
                new AurorianHumanoidModel<>(context.bakeLayer(MoonQueenModel.INNER_ARMOR_LAYER)),
                new AurorianHumanoidModel<>(context.bakeLayer(MoonQueenModel.OUTER_ARMOR_LAYER)),
                context.getModelManager()));
    }

    @Override
    public ResourceLocation getTextureLocation(MoonQueenEntity entity) {
        return TEXTURE;
    }
}
