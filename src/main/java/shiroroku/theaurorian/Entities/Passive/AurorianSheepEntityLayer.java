package shiroroku.theaurorian.Entities.Passive;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.item.DyeColor;

public class AurorianSheepEntityLayer extends RenderLayer<AurorianSheepEntity, AurorianSheepEntityModel2> {

    private static final ResourceLocation LAYER_TEXTURE = ResourceLocation.parse("textures/entity/sheep/sheep_fur.png");
    private final AurorianSheepEntityModel1 layerModel;

    public AurorianSheepEntityLayer(RenderLayerParent<AurorianSheepEntity, AurorianSheepEntityModel2> renderer, EntityModelSet modelSet) {
        super(renderer);
        this.layerModel = new AurorianSheepEntityModel1(modelSet.bakeLayer(AurorianSheepEntityModel1.MODEL_LAYER_LOCATION));
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, AurorianSheepEntity entity,
                       float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks,
                       float netHeadYaw, float headPitch) {
        if (!entity.isSheared() && !entity.isInvisible()) {
            int color;
            if (entity.getColor() == DyeColor.PURPLE) {
                color = FastColor.ARGB32.color(255, 117, 69, 254);
            } else if (entity.getColor() == DyeColor.LIGHT_BLUE) {
                color = FastColor.ARGB32.color(255, 95, 169, 237);
            } else {
                color = Sheep.getColor(entity.getColor());
            }
            coloredCutoutModelCopyLayerRender(
                    this.getParentModel(), this.layerModel, LAYER_TEXTURE,
                    poseStack, buffer, packedLight, entity,
                    limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTicks, color);
        }
    }
}
