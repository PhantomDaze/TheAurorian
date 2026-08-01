package shiroroku.theaurorian.Entities.Webbing;

import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import shiroroku.theaurorian.Registry.EntityRegistry;
import shiroroku.theaurorian.Registry.ItemRegistry;

public class WebbingEntity extends ThrowableProjectile {

    public WebbingEntity(EntityType<? extends WebbingEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public WebbingEntity(Level pLevel, LivingEntity thrower) {
        super(EntityRegistry.webbing.get(), thrower, pLevel);
    }

    public WebbingEntity(Level pLevel, double x, double y, double z) {
        super(EntityRegistry.webbing.get(), x, y, z, pLevel);
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    protected float getGravity() {
        return 0.01F;
    }

    @Override
    public void handleEntityEvent(byte pId) {
        if (pId == 3 && this.level().isClientSide) {
            for (int i = 0; i < 8; ++i) {
                this.level().addParticle(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(ItemRegistry.webbing.get())), this.getX(), this.getY(), this.getZ(), 0.0D, 0.0D, 0.0D);
            }
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        if (pResult.getEntity() instanceof LivingEntity e && e != this.getOwner()) {
            e.hurt(this.damageSources().thrown(this, this.getOwner()), 0.5F);
            e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 2));
        }
        this.level().broadcastEntityEvent(this, (byte) 3);
        this.discard();
    }

    @Override
    protected void onHitBlock(BlockHitResult pResult) {
        this.level().broadcastEntityEvent(this, (byte) 3);
        this.discard();
    }
}
