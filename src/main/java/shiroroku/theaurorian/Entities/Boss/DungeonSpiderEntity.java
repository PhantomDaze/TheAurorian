package shiroroku.theaurorian.Entities.Boss;

import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WallClimberNavigation;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import shiroroku.theaurorian.Entities.Boss.AI.SpiderAIHang;
import shiroroku.theaurorian.Entities.Boss.AI.SpiderAILeap;
import shiroroku.theaurorian.Entities.Boss.AI.SpiderAISpit;
import shiroroku.theaurorian.Registry.BlockRegistry;

/**
 * Darkstone dungeon boss (Spider Mother). Hangs from ceilings and spawns
 * spiderlings, spits webbing, and leaps at distant players. Climbs like a
 * vanilla spider.
 */
public class DungeonSpiderEntity extends Monster {

    private final ServerBossEvent bossEvent = (ServerBossEvent) (new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.BLUE, BossEvent.BossBarOverlay.PROGRESS)).setDarkenScreen(true);

    public DungeonSpiderEntity(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.xpReward = 500;
        this.fireImmune();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 160.0)
                .add(Attributes.FOLLOW_RANGE, 50.0)
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.ATTACK_DAMAGE, 3.0)
                .add(Attributes.ARMOR, 2.0);
    }

    @Override
    public MobType getMobType() {
        return MobType.ARTHROPOD;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SpiderAIHang(this));
        this.goalSelector.addGoal(2, new SpiderAISpit(this));
        this.goalSelector.addGoal(4, new SpiderAILeap(this));
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.2D, true) {
            @Override
            protected double getAttackReachSqr(net.minecraft.world.entity.LivingEntity pAttackTarget) {
                return this.mob.getBbWidth() * this.mob.getBbWidth() + pAttackTarget.getBbWidth();
            }
        });
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Cow.class, true));
    }

    @Override
    protected PathNavigation createNavigation(Level pLevel) {
        return new WallClimberNavigation(this, pLevel);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide && this.tickCount % 2 == 0 && this.isWindingUpSpit()) {
            float distance = 1.6F;
            double sinx = Math.sin(-this.yHeadRot / 180.0F * (float) Math.PI) * Math.cos(this.getXRot() / 180.0F * (float) Math.PI) * distance;
            double cosz = Math.cos(this.yHeadRot / 180.0F * (float) Math.PI) * Math.cos(this.getXRot() / 180.0F * (float) Math.PI) * distance;
            this.level().addParticle(net.minecraft.core.particles.ParticleTypes.CLOUD,
                    this.getX() + sinx, this.getY() + this.getEyeHeight() - 0.2F, this.getZ() + cosz,
                    this.random.nextGaussian() * 0.02D, this.random.nextGaussian() * 0.02D, this.random.nextGaussian() * 0.02D);
        }
        if (!this.level().isClientSide) {
            this.setBesideClimbableBlock(this.horizontalCollision);
        }
    }

    @Override
    public void die(DamageSource pCause) {
        super.die(pCause);
        int distance = 70;
        for (int x = 0; x <= distance; x++) {
            for (int y = 0; y <= distance; y++) {
                for (int z = 0; z <= distance; z++) {
                    int offs = distance / 2;
                    var p = new net.minecraft.core.BlockPos(x + this.blockPosition().getX() - offs, y + this.blockPosition().getY() - offs, z + this.blockPosition().getZ() - offs);
                    if (this.level().getBlockState(p).is(BlockRegistry.fog_wall.get())) {
                        this.level().destroyBlock(p, false);
                    }
                }
            }
        }
    }

    @Override
    public boolean causeFallDamage(float pFallDistance, float pMultiplier, DamageSource pSource) {
        return false;
    }

    @Override
    public boolean onClimbable() {
        return this.isBesideClimbableBlock();
    }

    @Override
    public void makeStuckInBlock(BlockState pState, net.minecraft.world.phys.Vec3 pMotionMultiplier) {
        // immune to webs
    }

    // Synched data
    private static final net.minecraft.network.syncher.EntityDataAccessor<Boolean> WINDINGUP_SPIT = net.minecraft.network.syncher.SynchedEntityData.defineId(DungeonSpiderEntity.class, net.minecraft.network.syncher.EntityDataSerializers.BOOLEAN);
    private static final net.minecraft.network.syncher.EntityDataAccessor<Boolean> SPITTING = net.minecraft.network.syncher.SynchedEntityData.defineId(DungeonSpiderEntity.class, net.minecraft.network.syncher.EntityDataSerializers.BOOLEAN);
    private static final net.minecraft.network.syncher.EntityDataAccessor<Boolean> CLIMBING = net.minecraft.network.syncher.SynchedEntityData.defineId(DungeonSpiderEntity.class, net.minecraft.network.syncher.EntityDataSerializers.BOOLEAN);
    private static final net.minecraft.network.syncher.EntityDataAccessor<Boolean> HANGING = net.minecraft.network.syncher.SynchedEntityData.defineId(DungeonSpiderEntity.class, net.minecraft.network.syncher.EntityDataSerializers.BOOLEAN);

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(WINDINGUP_SPIT, false);
        this.entityData.define(SPITTING, false);
        this.entityData.define(CLIMBING, false);
        this.entityData.define(HANGING, false);
    }

    public void setWindingUpSpit(boolean bool) {
        this.entityData.set(WINDINGUP_SPIT, bool);
    }

    public boolean isWindingUpSpit() {
        return this.entityData.get(WINDINGUP_SPIT);
    }

    public void setSpitting(boolean bool) {
        this.entityData.set(SPITTING, bool);
    }

    public boolean isSpitting() {
        return this.entityData.get(SPITTING);
    }

    public void setBesideClimbableBlock(boolean bool) {
        this.entityData.set(CLIMBING, bool);
    }

    public boolean isBesideClimbableBlock() {
        return this.entityData.get(CLIMBING);
    }

    public void setHanging(boolean bool) {
        this.entityData.set(HANGING, bool);
    }

    public boolean isHanging() {
        return this.entityData.get(HANGING);
    }

    @Override
    public void startSeenByPlayer(ServerPlayer pPlayer) {
        super.startSeenByPlayer(pPlayer);
        this.bossEvent.addPlayer(pPlayer);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer pPlayer) {
        super.stopSeenByPlayer(pPlayer);
        this.bossEvent.removePlayer(pPlayer);
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.SPIDER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return SoundEvents.SPIDER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SPIDER_DEATH;
    }

    @Override
    public boolean removeWhenFarAway(double pDistanceToClosestPlayer) {
        return false;
    }

    @Override
    public int getMaxSpawnClusterSize() {
        return 1;
    }
}
