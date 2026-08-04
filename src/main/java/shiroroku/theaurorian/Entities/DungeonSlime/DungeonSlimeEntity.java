package shiroroku.theaurorian.Entities.DungeonSlime;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
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
import shiroroku.theaurorian.Registry.ParticleRegistry;
import shiroroku.theaurorian.TheAurorian;

import javax.annotation.Nullable;
import java.util.List;

public class DungeonSlimeEntity extends Slime {

    /** Base nearby cap × runestone density config (upstream 5 * density). */
    public static final int BASE_MAX_NEARBY = 5;

    public DungeonSlimeEntity(EntityType<? extends Slime> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public static AttributeSupplier.Builder createAttributes() {
        // SLM1: HP 6 base (size set also enforces)
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 6.0)
                .add(Attributes.MOVEMENT_SPEED, 0.35F)
                .add(Attributes.ATTACK_DAMAGE, 2);
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
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData) {
        SpawnGroupData data = super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData);
        // SLM1: always size 1
        this.setSize(1, true);
        return data;
    }

    @Override
    public void setSize(int pSize, boolean pResetHealth) {
        // Force size 1 parity with upstream AurorianSlime
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
            }
        }
    }

    @Override
    public void jumpFromGround() {
        Vec3 vec3 = this.getDeltaMovement();
        this.setDeltaMovement(vec3.x * 1.2, this.getJumpPower() * 1.2, vec3.z * 1.2);
        this.hasImpulse = true;
    }

    @Override
    protected int getJumpDelay() {
        return this.random.nextInt(20) + 60;
    }

    @Override
    protected boolean spawnCustomParticles() {
        // SLM2: custom particles instead of vanilla slime
        SimpleParticleType type = ParticleRegistry.AURORIAN_SLIME.get();
        for (int j = 0; j < 8; ++j) {
            this.level().addParticle(type, this.getX(), this.getY(), this.getZ(), 0.0D, 0.0D, 0.0D);
        }
        return true;
    }

    public static int maxNearbyCap() {
        return BASE_MAX_NEARBY * CommonConfig.runestone_dungeon_mob_density.get();
    }

    @SuppressWarnings("deprecation")
    public static boolean checkSpawn(EntityType<DungeonSlimeEntity> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, net.minecraft.util.RandomSource random) {
        if (!Mob.checkMobSpawnRules(type, level, spawnType, pos, random)) {
            return false;
        }
        if (!level.getLevel().dimension().equals(TheAurorian.the_aurorian)) {
            return false;
        }
        // SLM3 / SYS4 density
        int max = maxNearbyCap();
        if (max <= 0) {
            return false;
        }
        List<DungeonSlimeEntity> nearby = level.getEntitiesOfClass(DungeonSlimeEntity.class, new AABB(pos).inflate(64, 6, 64), e -> e.isAlive());
        return nearby.size() <= max;
    }

    @Override
    public int getMaxSpawnClusterSize() {
        return maxNearbyCap();
    }
}
