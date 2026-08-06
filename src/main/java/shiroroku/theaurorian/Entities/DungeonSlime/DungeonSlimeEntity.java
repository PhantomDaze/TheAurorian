package shiroroku.theaurorian.Entities.DungeonSlime;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import shiroroku.theaurorian.Config.CommonConfig;
import shiroroku.theaurorian.Registry.EntityRegistry;
import shiroroku.theaurorian.Registry.ParticleRegistry;
import shiroroku.theaurorian.TheAurorian;

import javax.annotation.Nullable;

public class DungeonSlimeEntity extends Slime {

    public static final int BASE_MAX_NEARBY = 5;

    public DungeonSlimeEntity(EntityType<? extends Slime> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 6.0).add(Attributes.MOVEMENT_SPEED, 0.35F).add(Attributes.ATTACK_DAMAGE, 2);
    }

    @Override
    protected boolean isDealsDamage() {
        return this.isEffectiveAi();
    }

    @Override
    public boolean causeFallDamage(float pFallDistance, float pMultiplier, DamageSource pSource) {
        return false;
    }

    @Override
    protected void checkFallDamage(double pY, boolean pOnGround, BlockState pState, BlockPos pPos) {
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        SpawnGroupData data = super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
        this.setSize(1, true);
        return data;
    }

    @Override
    public void setSize(int pSize, boolean pResetHealth) {
        super.setSize(1, pResetHealth);
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(6.0);
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.35F);
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(2);
        if (pResetHealth) {
            this.setHealth(this.getMaxHealth());
        }
    }

    @Override
    protected void dealDamage(LivingEntity pLivingEntity) {
        if (this.isAlive()) {
            int i = this.getSize();
            if (this.distanceToSqr(pLivingEntity) < 0.8D * (double) i * 0.8D * (double) i && this.hasLineOfSight(pLivingEntity) && pLivingEntity.hurt(this.damageSources().mobAttack(this), this.getAttackDamage())) {
                this.playSound(SoundEvents.SLIME_ATTACK, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
                this.doEnchantDamageEffects(this, pLivingEntity);
            }
        }
    }

    @Override
    protected void jumpFromGround() {
        Vec3 vec3 = this.getDeltaMovement();
        this.setDeltaMovement(vec3.x * 1.2, this.getJumpPower() * 1.2, vec3.z * 1.2);
        this.hasImpulse = true;
    }

    @Override
    protected int getJumpDelay() {
        return 60 + this.random.nextInt(20);
    }

    @SuppressWarnings("deprecation")
    public static boolean checkSpawn(EntityType<DungeonSlimeEntity> type, ServerLevelAccessor level,
                                      MobSpawnType spawnType, BlockPos pos, net.minecraft.util.RandomSource random) {
        if (!checkMobSpawnRules(type, level, spawnType, pos, random)) {
            return false;
        }
        if (level.getLevel().dimension() != TheAurorian.the_aurorian) {
            return false;
        }
        int cap = maxNearbyCap();
        if (cap <= 0) {
            return false;
        }
        return level.getEntitiesOfClass(DungeonSlimeEntity.class,
                new AABB(pos).inflate(64, 6, 64), e -> e.isAlive()).size() <= cap;
    }

    public static int maxNearbyCap() {
        return BASE_MAX_NEARBY * CommonConfig.runestone_dungeon_mob_density.get();
    }

    @Override
    public int getMaxSpawnClusterSize() {
        return maxNearbyCap();
    }

    @Override
    protected boolean spawnCustomParticles() {
        SimpleParticleType type = ParticleRegistry.AURORIAN_SLIME.get();
        for (int j = 0; j < 8; ++j) {
            this.level().addParticle(type, this.getX(), this.getY(), this.getZ(), 0.0D, 0.0D, 0.0D);
        }
        return true;
    }
}
