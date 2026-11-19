package shiroroku.theaurorian.Entities.Passive;

import net.minecraft.client.model.EntityModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
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

public class AurorianRabbitEntityModel extends EntityModel<AurorianRabbitEntity> {

    public static final ModelLayerLocation MODEL_LAYER_LOCATION = new ModelLayerLocation(
            new ResourceLocation(TheAurorian.MODID, "aurorian_rabbit"), "main");

    private final ModelPart leftRearFoot;
    private final ModelPart rightRearFoot;
    private final ModelPart leftHaunch;
    private final ModelPart rightHaunch;
    private final ModelPart body;
    private final ModelPart leftFrontLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart head;
    private final ModelPart rightEar;
    private final ModelPart leftEar;
    private final ModelPart tail;
    private final ModelPart nose;
    private float jumpRotation;

    public AurorianRabbitEntityModel(ModelPart root) {
        this.leftRearFoot = root.getChild("left_hind_foot");
        this.rightRearFoot = root.getChild("right_hind_foot");
        this.leftHaunch = root.getChild("left_haunch");
        this.rightHaunch = root.getChild("right_haunch");
        this.body = root.getChild("body");
        this.leftFrontLeg = root.getChild("left_front_leg");
        this.rightFrontLeg = root.getChild("right_front_leg");
        this.head = root.getChild("head");
        this.rightEar = root.getChild("right_ear");
        this.leftEar = root.getChild("left_ear");
        this.tail = root.getChild("tail");
        this.nose = root.getChild("nose");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("left_hind_foot", CubeListBuilder.create().texOffs(26, 24)
                .addBox(-1.0F, 5.5F, -3.7F, 2.0F, 1.0F, 7.0F), PartPose.offset(3.0F, 17.5F, 3.7F));
        root.addOrReplaceChild("right_hind_foot", CubeListBuilder.create().texOffs(8, 24)
                .addBox(-1.0F, 5.5F, -3.7F, 2.0F, 1.0F, 7.0F), PartPose.offset(-3.0F, 17.5F, 3.7F));
        root.addOrReplaceChild("left_haunch", CubeListBuilder.create().texOffs(30, 15)
                .addBox(-1.0F, 0.0F, 0.0F, 2.0F, 4.0F, 5.0F),
                PartPose.offsetAndRotation(3.0F, 17.5F, 3.7F, -0.34906584F, 0.0F, 0.0F));
        root.addOrReplaceChild("right_haunch", CubeListBuilder.create().texOffs(16, 15)
                .addBox(-1.0F, 0.0F, 0.0F, 2.0F, 4.0F, 5.0F),
                PartPose.offsetAndRotation(-3.0F, 17.5F, 3.7F, -0.34906584F, 0.0F, 0.0F));
        root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0)
                .addBox(-3.0F, -2.0F, -10.0F, 6.0F, 5.0F, 10.0F),
                PartPose.offsetAndRotation(0.0F, 19.0F, 8.0F, -0.34906584F, 0.0F, 0.0F));
        root.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(8, 15)
                .addBox(-1.0F, 0.0F, -1.0F, 2.0F, 7.0F, 2.0F),
                PartPose.offsetAndRotation(3.0F, 17.0F, -1.0F, -0.17453292F, 0.0F, 0.0F));
        root.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(0, 15)
                .addBox(-1.0F, 0.0F, -1.0F, 2.0F, 7.0F, 2.0F),
                PartPose.offsetAndRotation(-3.0F, 17.0F, -1.0F, -0.17453292F, 0.0F, 0.0F));
        root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(32, 0)
                .addBox(-2.5F, -4.0F, -5.0F, 5.0F, 4.0F, 5.0F), PartPose.offset(0.0F, 16.0F, -1.0F));
        root.addOrReplaceChild("right_ear", CubeListBuilder.create().texOffs(52, 0)
                .addBox(-2.5F, -9.0F, -1.0F, 2.0F, 5.0F, 1.0F),
                PartPose.offsetAndRotation(0.0F, 16.0F, -1.0F, 0.0F, -0.2617994F, 0.0F));
        root.addOrReplaceChild("left_ear", CubeListBuilder.create().texOffs(58, 0)
                .addBox(0.5F, -9.0F, -1.0F, 2.0F, 5.0F, 1.0F),
                PartPose.offsetAndRotation(0.0F, 16.0F, -1.0F, 0.0F, 0.2617994F, 0.0F));
        root.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(52, 6)
                .addBox(-1.5F, -1.5F, 0.0F, 3.0F, 3.0F, 2.0F),
                PartPose.offsetAndRotation(0.0F, 20.0F, 7.0F, -0.3490659F, 0.0F, 0.0F));
        root.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(32, 9)
                .addBox(-0.5F, -2.5F, -5.5F, 1.0F, 1.0F, 1.0F), PartPose.offset(0.0F, 16.0F, -1.0F));
        return LayerDefinition.create(mesh, 64, 32);
    }

    @Override
    public void renderToBuffer(com.mojang.blaze3d.vertex.PoseStack poseStack,
                               com.mojang.blaze3d.vertex.VertexConsumer buffer, int packedLight,
                               int packedOverlay, float red, float green, float blue, float alpha) {
        if (this.young) {
            poseStack.pushPose();
            poseStack.scale(0.56666666F, 0.56666666F, 0.56666666F);
            poseStack.translate(0.0F, 1.375F, 0.125F);
            this.head.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
            this.rightEar.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
            this.leftEar.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
            this.nose.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
            poseStack.popPose();
            poseStack.pushPose();
            poseStack.scale(0.4F, 0.4F, 0.4F);
            poseStack.translate(0.0F, 2.25F, 0.0F);
            this.renderBody(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
            poseStack.popPose();
        } else {
            poseStack.pushPose();
            poseStack.scale(0.6F, 0.6F, 0.6F);
            poseStack.translate(0.0F, 1.0F, 0.0F);
            this.head.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
            this.rightEar.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
            this.leftEar.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
            this.nose.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
            this.renderBody(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
            poseStack.popPose();
        }
    }

    private void renderBody(com.mojang.blaze3d.vertex.PoseStack poseStack,
                            com.mojang.blaze3d.vertex.VertexConsumer buffer, int packedLight,
                            int packedOverlay, float red, float green, float blue, float alpha) {
        this.leftRearFoot.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        this.rightRearFoot.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        this.leftHaunch.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        this.rightHaunch.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        this.body.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        this.leftFrontLeg.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        this.rightFrontLeg.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        this.tail.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public void setupAnim(AurorianRabbitEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                          float netHeadYaw, float headPitch) {
        float partialTick = ageInTicks - entity.tickCount;
        this.nose.xRot = headPitch * ((float) Math.PI / 180.0F);
        this.head.xRot = this.nose.xRot;
        this.rightEar.xRot = this.nose.xRot;
        this.leftEar.xRot = this.nose.xRot;
        this.nose.yRot = netHeadYaw * ((float) Math.PI / 180.0F);
        this.head.yRot = this.nose.yRot;
        this.rightEar.yRot = this.nose.yRot - 0.2617994F;
        this.leftEar.yRot = this.nose.yRot + 0.2617994F;
        this.jumpRotation = Mth.sin(entity.getJumpCompletion(partialTick) * (float) Math.PI);
        this.leftHaunch.xRot = (this.jumpRotation * 50.0F - 21.0F) * ((float) Math.PI / 180.0F);
        this.rightHaunch.xRot = this.leftHaunch.xRot;
        this.leftRearFoot.xRot = this.jumpRotation * 50.0F * ((float) Math.PI / 180.0F);
        this.rightRearFoot.xRot = this.leftRearFoot.xRot;
        this.leftFrontLeg.xRot = (this.jumpRotation * -40.0F - 11.0F) * ((float) Math.PI / 180.0F);
        this.rightFrontLeg.xRot = this.leftFrontLeg.xRot;
    }

    @Override
    public void prepareMobModel(AurorianRabbitEntity entity, float limbSwing, float limbSwingAmount, float partialTick) {
        super.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);
        this.jumpRotation = Mth.sin(entity.getJumpCompletion(partialTick) * (float) Math.PI);
    }
}
