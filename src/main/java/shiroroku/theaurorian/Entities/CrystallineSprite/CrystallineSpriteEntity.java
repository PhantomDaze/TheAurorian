package shiroroku.theaurorian.Entities.CrystallineSprite;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import shiroroku.theaurorian.Entities.CrystallineBeam.CrystallineBeamEntity;
import shiroroku.theaurorian.Config.CommonConfig;
import shiroroku.theaurorian.Registry.BlockRegistry;
import shiroroku.theaurorian.Registry.EntityRegistry;
import shiroroku.theaurorian.TheAurorian;

import java.util.List;

public class CrystallineSpriteEntity extends Monster implements RangedAttackMob {

    private float heightOffset = 0.5F;
    private int heightOffsetUpdateTime;
    public int maxNearby = 5;

    public CrystallineSpriteEntity(EntityType<? extends CrystallineSpriteEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.xpReward = 25;
        this.setSilent(true);
    }

    public CrystallineSpriteEntity(Level pLevel) {
        super(EntityRegistry.crystalline_sprite.get(), pLevel);
        this.xpReward = 25;
        this.setSilent(true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.ATTACK_DAMAGE, 6.0)
                .add(Attributes.MOVEMENT_SPEED, 0.23000000417232513)
                .add(Attributes.FOLLOW_RANGE, 20.0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(2, new CrystallineSpriteAIRangedAttack(this, 0.85F, 40, 40F));
        this.goalSelector.addGoal(5, new MoveTowardsRestrictionGoal(this, 1.0D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    protected float getStandingEyeHeight(Pose pPose, EntityDimensions pSize) {
        return 0.5F;
    }

    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {
        CrystallineBeamEntity beam = new CrystallineBeamEntity(this.level(), this);
        beam.setDamage(CommonConfig.crystalline_sprite_beam_damage.get().floatValue());
        double d0 = target.getX() - this.getX();
        double d1 = target.getBoundingBox().minY + target.getBbHeight() / 3.0F - beam.getY();
        double d2 = target.getZ() - this.getZ();
        double d3 = Math.sqrt(d0 * d0 + d2 * d2);
        // upstream EntityArrow.shoot velocity 1.6 * CrystallineBeam arrowVelocity 0.5 = 0.8
        Vec3 dir = new Vec3(d0, d1 + d3 * 0.2, d2).normalize();
        beam.setDeltaMovement(dir.x * 0.8, dir.y * 0.8, dir.z * 0.8);
        this.playSound(SoundEvents.ENCHANTMENT_TABLE_USE, 1.0F, 1.0F / (this.random.nextFloat() * 0.4F + 0.8F));
        this.level().addFreshEntity(beam);
    }

    @Override
    public void tick() {
        if (!this.onGround() && this.getDeltaMovement().y < 0.0D) {
            Vec3 vel = this.getDeltaMovement();
            this.setDeltaMovement(vel.x, vel.y * 0.6D, vel.z);
        }
        super.tick();
    }

    @Override
    protected void customServerAiStep() {
        --this.heightOffsetUpdateTime;
        if (this.heightOffsetUpdateTime <= 0) {
            this.heightOffsetUpdateTime = 100;
            this.heightOffset = 0.5F + (float) this.random.nextGaussian() * 3.0F;
        }
        LivingEntity target = this.getTarget();
        if (target != null && target.getY() + target.getEyeHeight() > this.getY() + this.getEyeHeight() + this.heightOffset) {
            Vec3 vel = this.getDeltaMovement();
            this.setDeltaMovement(vel.x, vel.y + (0.3 - vel.y) * 0.3, vel.z);
            this.hasImpulse = true;
        }
        super.customServerAiStep();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return SoundEvents.GENERIC_HURT;
    }

    @Override
    public boolean causeFallDamage(float pFallDistance, float pMultiplier, DamageSource pSource) {
        return false;
    }

    @SuppressWarnings("deprecation")
    public static boolean checkSpawn(EntityType<CrystallineSpriteEntity> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        if (!checkMobSpawnRules(type, level, spawnType, pos, random)) {
            return false;
        }
        if (level.getLevel().dimension() != TheAurorian.the_aurorian) {
            return false;
        }
        if (level.getBlockState(pos.below()).getBlock() != BlockRegistry.moon_temple_bricks.get()) {
            return false;
        }
        List<CrystallineSpriteEntity> nearby = level.getEntitiesOfClass(CrystallineSpriteEntity.class, new AABB(pos).inflate(64, 30, 64), e -> e.isAlive());
        int max = 5 * CommonConfig.moon_temple_mob_density.get();
        return max > 0 && nearby.size() <= max;
    }

    @Override
    public int getMaxSpawnClusterSize() {
        return 5 * CommonConfig.moon_temple_mob_density.get();
    }
}
