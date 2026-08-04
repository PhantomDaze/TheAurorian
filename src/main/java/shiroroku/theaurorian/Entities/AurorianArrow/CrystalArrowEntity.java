package shiroroku.theaurorian.Entities.AurorianArrow;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import shiroroku.theaurorian.Registry.EntityRegistry;
import shiroroku.theaurorian.Registry.ItemRegistry;

public class CrystalArrowEntity extends AurorianArrowEntity {

    private static final Item item = ItemRegistry.crystal_arrow.get();
    // PR2 damage 2.0; PR3 KB 2; PR4 velocity 0.45
    private static final float damage = 2.0f;
    private static final float weight = 0f;
    /** Upstream setKnockbackStrength(2) — 1.21 arrows no longer expose setKnockback. */
    private static final double KNOCKBACK = 2.0D;

    public CrystalArrowEntity(EntityType<? extends AbstractArrow> pEntityType, Level pLevel) {
        super(pEntityType, pLevel, item, damage, weight);
    }

    public CrystalArrowEntity(Level pLevel, double pX, double pY, double pZ) {
        super(EntityRegistry.crystal_arrow.get(), pLevel, pX, pY, pZ, item, damage, weight);
    }

    public CrystalArrowEntity(Level pLevel, LivingEntity pShooter) {
        super(EntityRegistry.crystal_arrow.get(), pLevel, pShooter, item, damage, weight);
    }

    @Override
    public float getShootVelocityMultiplier() {
        return 0.45f;
    }

    @Override
    protected void doKnockback(LivingEntity entity, net.minecraft.world.damagesource.DamageSource damageSource) {
        double resist = Math.max(0.0, 1.0 - entity.getAttributeValue(net.minecraft.world.entity.ai.attributes.Attributes.KNOCKBACK_RESISTANCE));
        Vec3 push = this.getDeltaMovement().multiply(1.0, 0.0, 1.0).normalize().scale(KNOCKBACK * 0.6 * resist);
        if (push.lengthSqr() > 0.0) {
            entity.push(push.x, 0.1, push.z);
        }
    }
}
