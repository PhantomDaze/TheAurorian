package shiroroku.theaurorian.Entities;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.LivingEntity;

/**
 * The Aurorian 的本地人形模型基础实现。
 *
 * <p>模型层几何在本类中直接创建，不依赖 Minecraft 的具体生物模型或
 * vanilla ModelLayerLocation。</p>
 */
public class AurorianHumanoidModel<T extends LivingEntity> extends HumanoidModel<T> {

    public static final CubeDeformation INNER_ARMOR_DEFORMATION = new CubeDeformation(0.5F);
    public static final CubeDeformation OUTER_ARMOR_DEFORMATION = new CubeDeformation(1.0F);

    public AurorianHumanoidModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createBodyLayer() {
        return LayerDefinition.create(createHumanoidMesh(CubeDeformation.NONE), 64, 64);
    }

    public static LayerDefinition createInnerArmorLayer() {
        return LayerDefinition.create(createArmorMesh(INNER_ARMOR_DEFORMATION), 64, 32);
    }

    public static LayerDefinition createOuterArmorLayer() {
        return LayerDefinition.create(createArmorMesh(OUTER_ARMOR_DEFORMATION), 64, 32);
    }

    public static LayerDefinition createOverlayLayer() {
        return LayerDefinition.create(createHumanoidMesh(new CubeDeformation(0.25F)), 64, 32);
    }

    /** 创建标准四肢的人形网格。 */
    public static MeshDefinition createHumanoidMesh(CubeDeformation deformation) {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0)
                .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, deformation), PartPose.ZERO);
        root.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(32, 0)
                .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, deformation.extend(0.5F)), PartPose.ZERO);
        root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 16)
                .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, deformation), PartPose.ZERO);
        root.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(40, 16)
                .addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, deformation),
                PartPose.offset(-5.0F, 2.0F, 0.0F));
        root.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(40, 16).mirror()
                .addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, deformation),
                PartPose.offset(5.0F, 2.0F, 0.0F));
        root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 16)
                .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, deformation),
                PartPose.offset(-1.9F, 12.0F, 0.0F));
        root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 16).mirror()
                .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, deformation),
                PartPose.offset(1.9F, 12.0F, 0.0F));
        return mesh;
    }

    /** 创建与原版护甲层尺寸一致的网格。 */
    public static MeshDefinition createArmorMesh(CubeDeformation deformation) {
        MeshDefinition mesh = createHumanoidMesh(deformation);
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 16)
                .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, deformation.extend(-0.1F)),
                PartPose.offset(-1.9F, 12.0F, 0.0F));
        root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 16).mirror()
                .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, deformation.extend(-0.1F)),
                PartPose.offset(1.9F, 12.0F, 0.0F));
        return mesh;
    }
}
