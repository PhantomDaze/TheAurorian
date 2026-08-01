package shiroroku.theaurorian.Entities.Spirit;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import shiroroku.theaurorian.Registry.BlockRegistry;
import shiroroku.theaurorian.Registry.EntityRegistry;
import shiroroku.theaurorian.TheAurorian;

public class SpiritEntity extends Monster {

    private static final net.minecraft.network.syncher.EntityDataAccessor<Boolean> ARMS_RAISED = net.minecraft.network.syncher.SynchedEntityData.defineId(SpiritEntity.class, net.minecraft.network.syncher.EntityDataSerializers.BOOLEAN);
    public static final float MOB_SCALE = 1F;

    public SpiritEntity(EntityType<? extends SpiritEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public SpiritEntity(Level pLevel) {
        super(EntityRegistry.spirit.get(), pLevel);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.FOLLOW_RANGE, 35.0)
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.2)
                .add(Attributes.ATTACK_DAMAGE, 3.0)
                .add(Attributes.ARMOR, 0.0);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ARMS_RAISED, false);
    }

    @Override
    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new SpiritAIHaunt(this));
        this.goalSelector.addGoal(3, new SpiritAIRunAway(this));
        this.goalSelector.addGoal(4, new SpiritAIAttack(this, 1.25D, false));
        this.goalSelector.addGoal(5, new MoveTowardsRestrictionGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new RandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public boolean causeFallDamage(float pFallDistance, float pMultiplier, DamageSource pSource) {
        return false;
    }

    public void setArmsRaised(boolean armsRaised) {
        this.entityData.set(ARMS_RAISED, armsRaised);
    }

    public boolean isArmsRaised() {
        return this.entityData.get(ARMS_RAISED);
    }

    @SuppressWarnings("deprecation")
    public static boolean checkSpawn(EntityType<SpiritEntity> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        if (!checkMobSpawnRules(type, level, spawnType, pos, random)) {
            return false;
        }
        if (level.getLevel().dimension() != TheAurorian.the_aurorian) {
            return false;
        }
        return level.getBlockState(pos.below()).getBlock() == BlockRegistry.aurorian_grass.get();
    }

    @Override
    public int getMaxSpawnClusterSize() {
        return 1;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        if (this.random.nextBoolean()) {
            return SoundEvents.AMBIENT_CAVE.value();
        }
        return SoundEvents.VEX_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return SoundEvents.WITHER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.BLAZE_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pPos, BlockState pBlock) {
        this.playSound(SoundEvents.ZOMBIE_STEP, 0.15F, 1.0F);
    }

    /**
     * Returns true when the given looker is looking at the given target.
     * Port of upstream EntityHelper.isLookingAt.
     */
    public static boolean isLookingAt(LivingEntity looker, LivingEntity target, double accuracy) {
        Vec3 lookvec = target.getViewVector(1.0F).normalize();
        Vec3 vec = new Vec3(looker.getX() - target.getX(), looker.getBoundingBox().minY + looker.getEyeHeight() - (target.getY() + target.getEyeHeight()), looker.getZ() - target.getZ());
        double leng = vec.length();
        vec = vec.normalize();
        double mult = lookvec.dot(vec);
        return mult > 1.0D - accuracy / leng && target.hasLineOfSight(looker);
    }
}
