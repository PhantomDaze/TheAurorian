package shiroroku.theaurorian.Entities.Spiderling;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import shiroroku.theaurorian.TheAurorian;

public class SpiderlingEntityModel extends HierarchicalModel<SpiderlingEntity> {

    public static final ModelLayerLocation MODEL_LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(TheAurorian.MODID, "spiderling"), "main");

    private final ModelPart root;
    private final ModelPart spiderHead;
    private final ModelPart spiderNeck;
    private final ModelPart spiderBody;
    private final ModelPart spiderLeg1;
    private final ModelPart spiderLeg2;
    private final ModelPart spiderLeg3;
    private final ModelPart spiderLeg4;
    private final ModelPart spiderLeg5;
    private final ModelPart spiderLeg6;
    private final ModelPart spiderLeg7;
    private final ModelPart spiderLeg8;

    public SpiderlingEntityModel(ModelPart root) {
        this.root = root;
        this.spiderHead = root.getChild("spiderHead");
        this.spiderNeck = root.getChild("spiderNeck");
        this.spiderBody = root.getChild("spiderBody");
        this.spiderLeg1 = root.getChild("spiderLeg1");
        this.spiderLeg2 = root.getChild("spiderLeg2");
        this.spiderLeg3 = root.getChild("spiderLeg3");
        this.spiderLeg4 = root.getChild("spiderLeg4");
        this.spiderLeg5 = root.getChild("spiderLeg5");
        this.spiderLeg6 = root.getChild("spiderLeg6");
        this.spiderLeg7 = root.getChild("spiderLeg7");
        this.spiderLeg8 = root.getChild("spiderLeg8");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("spiderHead", CubeListBuilder.create().texOffs(32, 4).addBox(-4.0F, -4.0F, -8.0F, 8, 8, 8), PartPose.offset(0.0F, 15.0F, -3.0F));
        partdefinition.addOrReplaceChild("spiderNeck", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -3.0F, -3.0F, 6, 6, 6), PartPose.offset(0.0F, 15.0F, 0.0F));
        partdefinition.addOrReplaceChild("spiderBody", CubeListBuilder.create().texOffs(0, 12).addBox(-5.0F, -4.0F, -6.0F, 10, 8, 12), PartPose.offset(0.0F, 15.0F, 9.0F));
        partdefinition.addOrReplaceChild("spiderLeg1", CubeListBuilder.create().texOffs(18, 0).addBox(-15.0F, -1.0F, -1.0F, 16, 2, 2), PartPose.offset(-4.0F, 15.0F, 2.0F));
        partdefinition.addOrReplaceChild("spiderLeg2", CubeListBuilder.create().texOffs(18, 0).addBox(-1.0F, -1.0F, -1.0F, 16, 2, 2), PartPose.offset(4.0F, 15.0F, 2.0F));
        partdefinition.addOrReplaceChild("spiderLeg3", CubeListBuilder.create().texOffs(18, 0).addBox(-15.0F, -1.0F, -1.0F, 16, 2, 2), PartPose.offset(-4.0F, 15.0F, 1.0F));
        partdefinition.addOrReplaceChild("spiderLeg4", CubeListBuilder.create().texOffs(18, 0).addBox(-1.0F, -1.0F, -1.0F, 16, 2, 2), PartPose.offset(4.0F, 15.0F, 1.0F));
        partdefinition.addOrReplaceChild("spiderLeg5", CubeListBuilder.create().texOffs(18, 0).addBox(-15.0F, -1.0F, -1.0F, 16, 2, 2), PartPose.offset(-4.0F, 15.0F, 0.0F));
        partdefinition.addOrReplaceChild("spiderLeg6", CubeListBuilder.create().texOffs(18, 0).addBox(-1.0F, -1.0F, -1.0F, 16, 2, 2), PartPose.offset(4.0F, 15.0F, 0.0F));
        partdefinition.addOrReplaceChild("spiderLeg7", CubeListBuilder.create().texOffs(18, 0).addBox(-15.0F, -1.0F, -1.0F, 16, 2, 2), PartPose.offset(-4.0F, 15.0F, -1.0F));
        partdefinition.addOrReplaceChild("spiderLeg8", CubeListBuilder.create().texOffs(18, 0).addBox(-1.0F, -1.0F, -1.0F, 16, 2, 2), PartPose.offset(4.0F, 15.0F, -1.0F));
        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(SpiderlingEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        float legrotanglez = 0.58119464F * 1.3F;
        float legrotanglex = 0.3926991F * 1.1F;
        float val = 4.6F;

        this.spiderHead.yRot = netHeadYaw * 0.017453292F;
        this.spiderHead.xRot = headPitch * 0.017453292F;
        this.spiderLeg1.zRot = -((float) Math.PI / val);
        this.spiderLeg2.zRot = ((float) Math.PI / val);
        this.spiderLeg3.zRot = -legrotanglez;
        this.spiderLeg4.zRot = legrotanglez;
        this.spiderLeg5.zRot = -legrotanglez;
        this.spiderLeg6.zRot = legrotanglez;
        this.spiderLeg7.zRot = -((float) Math.PI / val);
        this.spiderLeg8.zRot = ((float) Math.PI / val);
        this.spiderLeg1.yRot = ((float) Math.PI / val);
        this.spiderLeg2.yRot = -((float) Math.PI / val);
        this.spiderLeg3.yRot = legrotanglex;
        this.spiderLeg4.yRot = -legrotanglex;
        this.spiderLeg5.yRot = -legrotanglex;
        this.spiderLeg6.yRot = legrotanglex;
        this.spiderLeg7.yRot = -((float) Math.PI / val);
        this.spiderLeg8.yRot = ((float) Math.PI / val);
        float f3 = -(Mth.cos(limbSwing * 0.6662F * 2.0F + 0.0F) * 0.4F) * limbSwingAmount;
        float f4 = -(Mth.cos(limbSwing * 0.6662F * 2.0F + (float) Math.PI) * 0.4F) * limbSwingAmount;
        float f5 = -(Mth.cos(limbSwing * 0.6662F * 2.0F + ((float) Math.PI / 2F)) * 0.4F) * limbSwingAmount;
        float f6 = -(Mth.cos(limbSwing * 0.6662F * 2.0F + ((float) Math.PI * 3F / 2F)) * 0.4F) * limbSwingAmount;
        float f7 = Math.abs(Mth.sin(limbSwing * 0.6662F + 0.0F) * 0.4F) * limbSwingAmount;
        float f8 = Math.abs(Mth.sin(limbSwing * 0.6662F + (float) Math.PI) * 0.4F) * limbSwingAmount;
        float f9 = Math.abs(Mth.sin(limbSwing * 0.6662F + ((float) Math.PI / 2F)) * 0.4F) * limbSwingAmount;
        float f10 = Math.abs(Mth.sin(limbSwing * 0.6662F + ((float) Math.PI * 3F / 2F)) * 0.4F) * limbSwingAmount;
        this.spiderLeg1.yRot += f3;
        this.spiderLeg2.yRot += -f3;
        this.spiderLeg3.yRot += f4;
        this.spiderLeg4.yRot += -f4;
        this.spiderLeg5.yRot += f5;
        this.spiderLeg6.yRot += -f5;
        this.spiderLeg7.yRot += f6;
        this.spiderLeg8.yRot += -f6;
        this.spiderLeg1.zRot += f7;
        this.spiderLeg2.zRot += -f7;
        this.spiderLeg3.zRot += f8;
        this.spiderLeg4.zRot += -f8;
        this.spiderLeg5.zRot += f9;
        this.spiderLeg6.zRot += -f9;
        this.spiderLeg7.zRot += f10;
        this.spiderLeg8.zRot += -f10;
    }
}
