package shiroroku.theaurorian.Entities.DisturbedHollow;

import net.minecraft.network.syncher.SynchedEntityData;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import shiroroku.theaurorian.Registry.EntityRegistry;

public class DisturbedHollowEntity extends Monster {

    private static final net.minecraft.network.syncher.EntityDataAccessor<Boolean> ARMS_RAISED = net.minecraft.network.syncher.SynchedEntityData.defineId(DisturbedHollowEntity.class, net.minecraft.network.syncher.EntityDataSerializers.BOOLEAN);
    public static final float MOB_SCALE = 1F;

    public DisturbedHollowEntity(EntityType<? extends DisturbedHollowEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public DisturbedHollowEntity(Level pLevel) {
        super(EntityRegistry.disturbed_hollow.get(), pLevel);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.24)
                .add(Attributes.ATTACK_DAMAGE, 3.0)
                .add(Attributes.ARMOR, 2.0)
                .add(Attributes.FOLLOW_RANGE, 35.0);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ARMS_RAISED, false);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new DisturbedHollowAIAttack(this, 1.0D, false));
        this.goalSelector.addGoal(5, new MoveTowardsRestrictionGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new RandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    public void setArmsRaised(boolean armsRaised) {
        this.entityData.set(ARMS_RAISED, armsRaised);
    }

    public boolean isArmsRaised() {
        return this.entityData.get(ARMS_RAISED);
    }

    @Override
    public boolean doHurtTarget(Entity pEntity) {
        if (super.doHurtTarget(pEntity)) {
            if (pEntity instanceof LivingEntity) {
                ((LivingEntity) pEntity).addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200), this);
            }
            return true;
        }
        return false;
    }

    @SuppressWarnings("deprecation")
    public static boolean checkSpawn(EntityType<DisturbedHollowEntity> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return pos.getY() <= 65 && checkMobSpawnRules(type, level, spawnType, pos, random);
    }

    @Override
    public int getMaxSpawnClusterSize() {
        return 5;
    }
}
