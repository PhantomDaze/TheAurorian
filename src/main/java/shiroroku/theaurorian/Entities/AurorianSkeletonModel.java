package shiroroku.theaurorian.Entities;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/** The Aurorian 的本地骨骼模型，包含原版弓姿态和攻击动画。 */
public class AurorianSkeletonModel<T extends Mob & RangedAttackMob> extends HumanoidModel<T> {

    public AurorianSkeletonModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createBodyLayer() {
        return LayerDefinition.create(createSkeletonMesh(), 64, 32);
    }

    public static LayerDefinition createInnerArmorLayer() {
        return LayerDefinition.create(AurorianHumanoidModel.createArmorMesh(
                AurorianHumanoidModel.INNER_ARMOR_DEFORMATION), 64, 32);
    }

    public static LayerDefinition createOuterArmorLayer() {
        return LayerDefinition.create(AurorianHumanoidModel.createArmorMesh(
                AurorianHumanoidModel.OUTER_ARMOR_DEFORMATION), 64, 32);
    }

    public static LayerDefinition createClothingLayer() {
        return LayerDefinition.create(AurorianHumanoidModel.createHumanoidMesh(
                new CubeDeformation(0.25F)), 64, 32);
    }

    private static MeshDefinition createSkeletonMesh() {
        MeshDefinition mesh = AurorianHumanoidModel.createHumanoidMesh(CubeDeformation.NONE);
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(40, 16)
                .addBox(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F),
                PartPose.offset(-5.0F, 2.0F, 0.0F));
        root.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(40, 16).mirror()
                .addBox(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F),
                PartPose.offset(5.0F, 2.0F, 0.0F));
        root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 16)
                .addBox(-1.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F),
                PartPose.offset(-2.0F, 12.0F, 0.0F));
        root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 16).mirror()
                .addBox(-1.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F),
                PartPose.offset(2.0F, 12.0F, 0.0F));
        return mesh;
    }

    @Override
    public void prepareMobModel(T entity, float limbSwing, float limbSwingAmount, float partialTick) {
        this.rightArmPose = ArmPose.EMPTY;
        this.leftArmPose = ArmPose.EMPTY;
        ItemStack stack = entity.getItemInHand(InteractionHand.MAIN_HAND);
        if (stack.is(Items.BOW) && entity.isAggressive()) {
            if (entity.getMainArm() == HumanoidArm.RIGHT) {
                this.rightArmPose = ArmPose.BOW_AND_ARROW;
            } else {
                this.leftArmPose = ArmPose.BOW_AND_ARROW;
            }
        }
        super.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                          float netHeadYaw, float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        ItemStack stack = entity.getMainHandItem();
        if (entity.isAggressive() && (stack.isEmpty() || !stack.is(Items.BOW))) {
            float attack = Mth.sin(this.attackTime * (float) Math.PI);
            float attackEase = Mth.sin((1.0F - (1.0F - this.attackTime) * (1.0F - this.attackTime)) * (float) Math.PI);
            this.rightArm.zRot = 0.0F;
            this.leftArm.zRot = 0.0F;
            this.rightArm.yRot = -(0.1F - attack * 0.6F);
            this.leftArm.yRot = 0.1F - attack * 0.6F;
            this.rightArm.xRot = -(float) Math.PI / 2.0F;
            this.leftArm.xRot = -(float) Math.PI / 2.0F;
            this.rightArm.xRot -= attack * 1.2F - attackEase * 0.4F;
            this.leftArm.xRot -= attack * 1.2F - attackEase * 0.4F;
            AnimationUtils.bobArms(this.rightArm, this.leftArm, ageInTicks);
        }
    }

    @Override
    public void translateToHand(HumanoidArm side, PoseStack poseStack) {
        float offset = side == HumanoidArm.RIGHT ? 1.0F : -1.0F;
        ModelPart arm = this.getArm(side);
        arm.x += offset;
        arm.translateAndRotate(poseStack);
        arm.x -= offset;
    }
}
