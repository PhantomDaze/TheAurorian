package shiroroku.theaurorian.Entities.MoonAcolyte;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import shiroroku.theaurorian.Registry.BlockRegistry;
import shiroroku.theaurorian.Registry.EntityRegistry;
import shiroroku.theaurorian.Registry.ItemRegistry;
import shiroroku.theaurorian.TheAurorian;

import java.util.List;

public class MoonAcolyteEntity extends Monster {

    private static final net.minecraft.network.syncher.EntityDataAccessor<Boolean> ARMS_RAISED = net.minecraft.network.syncher.SynchedEntityData.defineId(MoonAcolyteEntity.class, net.minecraft.network.syncher.EntityDataSerializers.BOOLEAN);
    public static final float MOB_SCALE = 1F;
    public int maxNearby = 4;

    public MoonAcolyteEntity(EntityType<? extends MoonAcolyteEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public MoonAcolyteEntity(Level pLevel) {
        super(EntityRegistry.moon_acolyte.get(), pLevel);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.ATTACK_DAMAGE, 2.0)
                .add(Attributes.ARMOR, 3.0);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ARMS_RAISED, false);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MoonAcolyteAIAttack(this, 1.0D, false));
        this.goalSelector.addGoal(5, new MoveTowardsRestrictionGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new RandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource pRandom, DifficultyInstance pDifficulty) {
        ItemStack sword = new ItemStack(ItemRegistry.aurorian_stone_sword.get());
        sword.enchant(Enchantments.KNOCKBACK, 2);
        this.setItemInHand(InteractionHand.MAIN_HAND, sword);
        this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
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
                ((LivingEntity) pEntity).addEffect(new MobEffectInstance(MobEffects.LEVITATION, 20), this);
            }
            return true;
        }
        return false;
    }

    @SuppressWarnings("deprecation")
    public static boolean checkSpawn(EntityType<MoonAcolyteEntity> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        if (!checkMobSpawnRules(type, level, spawnType, pos, random)) {
            return false;
        }
        if (level.getLevel().dimension() != TheAurorian.the_aurorian) {
            return false;
        }
        if (level.getBlockState(pos.below()).getBlock() != BlockRegistry.moon_temple_bricks.get()) {
            return false;
        }
        List<MoonAcolyteEntity> nearby = level.getEntitiesOfClass(MoonAcolyteEntity.class, new AABB(pos).inflate(64, 30, 64), e -> e.isAlive());
        int max = 4 * shiroroku.theaurorian.Config.CommonConfig.moon_temple_mob_density.get();
        return max > 0 && nearby.size() <= max;
    }

    @Override
    public int getMaxSpawnClusterSize() {
        return 4 * shiroroku.theaurorian.Config.CommonConfig.moon_temple_mob_density.get();
    }
}
