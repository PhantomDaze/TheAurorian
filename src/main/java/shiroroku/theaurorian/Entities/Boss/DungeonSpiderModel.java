package shiroroku.theaurorian.Entities.Boss;

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
import net.minecraft.world.entity.Entity;
import shiroroku.theaurorian.TheAurorian;

public class DungeonSpiderModel<T extends Entity> extends HierarchicalModel<T> {

    public static final ModelLayerLocation MODEL_LAYER_LOCATION = new ModelLayerLocation(
            new ResourceLocation(TheAurorian.MODID, "dungeon_spider"), "main");

    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart rightHindLeg;
    private final ModelPart leftHindLeg;
    private final ModelPart rightMiddleHindLeg;
    private final ModelPart leftMiddleHindLeg;
    private final ModelPart rightMiddleFrontLeg;
    private final ModelPart leftMiddleFrontLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart leftFrontLeg;

    public DungeonSpiderModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
        this.rightHindLeg = root.getChild("right_hind_leg");
        this.leftHindLeg = root.getChild("left_hind_leg");
        this.rightMiddleHindLeg = root.getChild("right_middle_hind_leg");
        this.leftMiddleHindLeg = root.getChild("left_middle_hind_leg");
        this.rightMiddleFrontLeg = root.getChild("right_middle_front_leg");
        this.leftMiddleFrontLeg = root.getChild("left_middle_front_leg");
        this.rightFrontLeg = root.getChild("right_front_leg");
        this.leftFrontLeg = root.getChild("left_front_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(32, 4)
                .addBox(-4.0F, -4.0F, -8.0F, 8.0F, 8.0F, 8.0F), PartPose.offset(0.0F, 15.0F, -3.0F));
        root.addOrReplaceChild("body0", CubeListBuilder.create().texOffs(0, 0)
                .addBox(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F), PartPose.offset(0.0F, 15.0F, 0.0F));
        root.addOrReplaceChild("body1", CubeListBuilder.create().texOffs(0, 12)
                .addBox(-5.0F, -4.0F, -6.0F, 10.0F, 8.0F, 12.0F), PartPose.offset(0.0F, 15.0F, 9.0F));
        CubeListBuilder rightLeg = CubeListBuilder.create().texOffs(18, 0)
                .addBox(-15.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F);
        CubeListBuilder leftLeg = CubeListBuilder.create().texOffs(18, 0).mirror()
                .addBox(-1.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F);
        root.addOrReplaceChild("right_hind_leg", rightLeg, PartPose.offset(-4.0F, 15.0F, 2.0F));
        root.addOrReplaceChild("left_hind_leg", leftLeg, PartPose.offset(4.0F, 15.0F, 2.0F));
        root.addOrReplaceChild("right_middle_hind_leg", rightLeg, PartPose.offset(-4.0F, 15.0F, 1.0F));
        root.addOrReplaceChild("left_middle_hind_leg", leftLeg, PartPose.offset(4.0F, 15.0F, 1.0F));
        root.addOrReplaceChild("right_middle_front_leg", rightLeg, PartPose.offset(-4.0F, 15.0F, 0.0F));
        root.addOrReplaceChild("left_middle_front_leg", leftLeg, PartPose.offset(4.0F, 15.0F, 0.0F));
        root.addOrReplaceChild("right_front_leg", rightLeg, PartPose.offset(-4.0F, 15.0F, -1.0F));
        root.addOrReplaceChild("left_front_leg", leftLeg, PartPose.offset(4.0F, 15.0F, -1.0F));
        return LayerDefinition.create(mesh, 64, 32);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                          float netHeadYaw, float headPitch) {
        this.head.yRot = netHeadYaw * ((float) Math.PI / 180.0F);
        this.head.xRot = headPitch * ((float) Math.PI / 180.0F);
        this.rightHindLeg.zRot = -(float) Math.PI / 4.0F;
        this.leftHindLeg.zRot = (float) Math.PI / 4.0F;
        this.rightMiddleHindLeg.zRot = -0.58119464F;
        this.leftMiddleHindLeg.zRot = 0.58119464F;
        this.rightMiddleFrontLeg.zRot = -0.58119464F;
        this.leftMiddleFrontLeg.zRot = 0.58119464F;
        this.rightFrontLeg.zRot = -(float) Math.PI / 4.0F;
        this.leftFrontLeg.zRot = (float) Math.PI / 4.0F;
        this.rightHindLeg.yRot = (float) Math.PI / 4.0F;
        this.leftHindLeg.yRot = -(float) Math.PI / 4.0F;
        this.rightMiddleHindLeg.yRot = (float) Math.PI / 8.0F;
        this.leftMiddleHindLeg.yRot = -(float) Math.PI / 8.0F;
        this.rightMiddleFrontLeg.yRot = -(float) Math.PI / 8.0F;
        this.leftMiddleFrontLeg.yRot = (float) Math.PI / 8.0F;
        this.rightFrontLeg.yRot = -(float) Math.PI / 4.0F;
        this.leftFrontLeg.yRot = (float) Math.PI / 4.0F;
        float rightHindY = -(Mth.cos(limbSwing * 0.6662F * 2.0F) * 0.4F) * limbSwingAmount;
        float leftHindY = -(Mth.cos(limbSwing * 0.6662F * 2.0F + (float) Math.PI) * 0.4F) * limbSwingAmount;
        float leftMiddleHindY = -(Mth.cos(limbSwing * 0.6662F * 2.0F + (float) Math.PI / 2.0F) * 0.4F) * limbSwingAmount;
        float rightFrontY = -(Mth.cos(limbSwing * 0.6662F * 2.0F + (float) Math.PI * 1.5F) * 0.4F) * limbSwingAmount;
        float rightHindZ = Math.abs(Mth.sin(limbSwing * 0.6662F) * 0.4F) * limbSwingAmount;
        float leftHindZ = Math.abs(Mth.sin(limbSwing * 0.6662F + (float) Math.PI) * 0.4F) * limbSwingAmount;
        float rightMiddleHindZ = Math.abs(Mth.sin(limbSwing * 0.6662F + (float) Math.PI / 2.0F) * 0.4F) * limbSwingAmount;
        float rightFrontZ = Math.abs(Mth.sin(limbSwing * 0.6662F + (float) Math.PI * 1.5F) * 0.4F) * limbSwingAmount;
        this.rightHindLeg.yRot += rightHindY;
        this.leftHindLeg.yRot -= rightHindY;
        this.rightMiddleHindLeg.yRot += leftHindY;
        this.leftMiddleHindLeg.yRot -= leftHindY;
        this.rightMiddleFrontLeg.yRot += leftMiddleHindY;
        this.leftMiddleFrontLeg.yRot -= leftMiddleHindY;
        this.rightFrontLeg.yRot += rightFrontY;
        this.leftFrontLeg.yRot -= rightFrontY;
        this.rightHindLeg.zRot += rightHindZ;
        this.leftHindLeg.zRot -= rightHindZ;
        this.rightMiddleHindLeg.zRot += leftHindZ;
        this.leftMiddleHindLeg.zRot -= leftHindZ;
        this.rightMiddleFrontLeg.zRot += rightMiddleHindZ;
        this.leftMiddleFrontLeg.zRot -= rightMiddleHindZ;
        this.rightFrontLeg.zRot += rightFrontZ;
        this.leftFrontLeg.zRot -= rightFrontZ;
    }
}
