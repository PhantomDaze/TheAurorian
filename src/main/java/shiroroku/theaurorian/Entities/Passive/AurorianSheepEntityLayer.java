package shiroroku.theaurorian.Entities.Passive;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.item.DyeColor;

public class AurorianSheepEntityLayer extends RenderLayer<AurorianSheepEntity, AurorianSheepEntityModel2> {

    private static final ResourceLocation LAYER_TEXTURE = new ResourceLocation("textures/entity/sheep/sheep_fur.png");

    private final AurorianSheepEntityModel1 layerModel;

    public AurorianSheepEntityLayer(RenderLayerParent<AurorianSheepEntity, AurorianSheepEntityModel2> renderer, EntityModelSet modelSet) {
        super(renderer);
        this.layerModel = new AurorianSheepEntityModel1(modelSet.bakeLayer(AurorianSheepEntityModel1.MODEL_LAYER_LOCATION));
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, AurorianSheepEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!entity.isSheared() && !entity.isInvisible()) {
            float red;
            float green;
            float blue;
            if (entity.getColor() == DyeColor.PURPLE) {
                red = 117.0F / 255.0F;
                green = 69.0F / 255.0F;
                blue = 254.0F / 255.0F;
            } else if (entity.getColor() == DyeColor.LIGHT_BLUE) {
                red = 95.0F / 255.0F;
                green = 169.0F / 255.0F;
                blue = 237.0F / 255.0F;
            } else {
                float[] rgb = Sheep.getColorArray(entity.getColor());
                red = rgb[0];
                green = rgb[1];
                blue = rgb[2];
            }
            coloredCutoutModelCopyLayerRender(this.getParentModel(), this.layerModel, LAYER_TEXTURE, poseStack, buffer, packedLight, entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTicks, red, green, blue);
        }
    }
}
