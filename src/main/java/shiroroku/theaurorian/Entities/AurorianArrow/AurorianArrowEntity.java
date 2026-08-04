package shiroroku.theaurorian.Entities.AurorianArrow;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class AurorianArrowEntity extends AbstractArrow {

    private final Item arrowItem;
    private final float weight;

    public AurorianArrowEntity(EntityType<? extends AbstractArrow> type, Level level, Item arrowItem, float damage, Float weight) {
        super(type, level);
        this.arrowItem = arrowItem;
        this.weight = weight == null ? 0f : weight;
        this.setBaseDamage(damage);
    }

    public AurorianArrowEntity(EntityType<? extends AbstractArrow> type, Level level, double x, double y, double z, Item arrowItem, float damage, Float weight) {
        super(type, x, y, z, level, new ItemStack(arrowItem), null);
        this.arrowItem = arrowItem;
        this.weight = weight == null ? 0f : weight;
        this.setBaseDamage(damage);
    }

    public AurorianArrowEntity(EntityType<? extends AbstractArrow> type, Level level, LivingEntity shooter, Item arrowItem, float damage, Float weight) {
        super(type, shooter, level, new ItemStack(arrowItem), null);
        this.arrowItem = arrowItem;
        this.weight = weight == null ? 0f : weight;
        this.setBaseDamage(damage);
    }

    /** Upstream multiplies shoot velocity (Cerulean 1.5, Crystal 0.45). */
    public float getShootVelocityMultiplier() {
        return 1.0f;
    }

    @Override
    public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
        super.shoot(x, y, z, velocity * getShootVelocityMultiplier(), inaccuracy);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.isNoGravity() && !this.isNoPhysics()) {
            Vec3 vel = this.getDeltaMovement();
            this.setDeltaMovement(vel.x, vel.y - weight, vel.z);
        }
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return new ItemStack(arrowItem);
    }
}
