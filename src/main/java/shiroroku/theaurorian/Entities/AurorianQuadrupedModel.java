package shiroroku.theaurorian.Entities;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

/** Local quadruped base copied from the vanilla quadruped model. */
public abstract class AurorianQuadrupedModel<T extends Entity> extends AgeableListModel<T> {

    protected final ModelPart head;
    protected final ModelPart body;
    protected final ModelPart rightHindLeg;
    protected final ModelPart leftHindLeg;
    protected final ModelPart rightFrontLeg;
    protected final ModelPart leftFrontLeg;

    protected AurorianQuadrupedModel(ModelPart root, boolean scaleHead, float babyYHeadOffset,
                                     float babyZHeadOffset, float babyHeadScale, float babyBodyScale,
                                     int bodyYOffset) {
        super(scaleHead, babyYHeadOffset, babyZHeadOffset, babyHeadScale, babyBodyScale, bodyYOffset);
        this.head = root.getChild("head");
        this.body = root.getChild("body");
        this.rightHindLeg = root.getChild("right_hind_leg");
        this.leftHindLeg = root.getChild("left_hind_leg");
        this.rightFrontLeg = root.getChild("right_front_leg");
        this.leftFrontLeg = root.getChild("left_front_leg");
    }

    protected static MeshDefinition createBodyMesh(int legHeight, CubeDeformation deformation) {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0)
                .addBox(-4.0F, -4.0F, -8.0F, 8.0F, 8.0F, 8.0F, deformation),
                PartPose.offset(0.0F, 18.0F - legHeight, -6.0F));
        root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(28, 8)
                .addBox(-5.0F, -10.0F, -7.0F, 10.0F, 16.0F, 8.0F, deformation),
                PartPose.offsetAndRotation(0.0F, 17.0F - legHeight, 2.0F, (float) Math.PI / 2.0F, 0.0F, 0.0F));
        CubeListBuilder legs = CubeListBuilder.create().texOffs(0, 16)
                .addBox(-2.0F, 0.0F, -2.0F, 4.0F, legHeight, 4.0F, deformation);
        root.addOrReplaceChild("right_hind_leg", legs, PartPose.offset(-3.0F, 24.0F - legHeight, 7.0F));
        root.addOrReplaceChild("left_hind_leg", legs, PartPose.offset(3.0F, 24.0F - legHeight, 7.0F));
        root.addOrReplaceChild("right_front_leg", legs, PartPose.offset(-3.0F, 24.0F - legHeight, -5.0F));
        root.addOrReplaceChild("left_front_leg", legs, PartPose.offset(3.0F, 24.0F - legHeight, -5.0F));
        return mesh;
    }

    @Override
    protected Iterable<ModelPart> headParts() {
        return ImmutableList.of(this.head);
    }

    @Override
    protected Iterable<ModelPart> bodyParts() {
        return ImmutableList.of(this.body, this.rightHindLeg, this.leftHindLeg, this.rightFrontLeg, this.leftFrontLeg);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                          float netHeadYaw, float headPitch) {
        this.head.xRot = headPitch * ((float) Math.PI / 180.0F);
        this.head.yRot = netHeadYaw * ((float) Math.PI / 180.0F);
        this.rightHindLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        this.leftHindLeg.xRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
        this.rightFrontLeg.xRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
        this.leftFrontLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
    }
}
