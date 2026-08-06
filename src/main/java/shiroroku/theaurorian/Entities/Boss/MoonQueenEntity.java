package shiroroku.theaurorian.Entities.Boss;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.BossEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.Blocks;
import shiroroku.theaurorian.Entities.Boss.AI.MoonQueenAICharge;
import shiroroku.theaurorian.Entities.Boss.AI.MoonQueenAISideStrafe;
import shiroroku.theaurorian.Registry.BlockRegistry;
import shiroroku.theaurorian.Registry.ItemRegistry;
import shiroroku.theaurorian.TheAurorian;

/**
 * Moon Temple boss. Charge + strafe fight loop, knocks players up and applies
 * mining fatigue. On death removes nearby fog walls and drops a chest with the
 * temple high loot.
 */
public class MoonQueenEntity extends Monster {

    public static final float MOB_SCALE = 0.9F;
    public static final String CHEST_LOOT = "theaurorian:chests/moontemple/high";

    private final ServerBossEvent bossEvent = (ServerBossEvent) (new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.PINK, BossEvent.BossBarOverlay.PROGRESS)).setDarkenScreen(false);

    public MoonQueenEntity(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.xpReward = 500;
        this.fireImmune();
        this.setMaxUpStep(1.0F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 200.0)
                .add(Attributes.FOLLOW_RANGE, 40.0)
                .add(Attributes.MOVEMENT_SPEED, 0.20)
                .add(Attributes.ATTACK_DAMAGE, 4.0)
                .add(Attributes.ARMOR, 8.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.85);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MoonQueenAICharge(this, true));
        this.goalSelector.addGoal(2, new MoonQueenAISideStrafe(this));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.35D, false));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void populateDefaultEquipmentSlots(net.minecraft.util.RandomSource pRandom, net.minecraft.world.DifficultyInstance pDifficulty) {
        this.ensureBossEquipment();
    }

    public void ensureBossEquipment() {
        if (this.getMainHandItem().isEmpty()) {
            this.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(ItemRegistry.moonstone_sword.get()));
        }
        if (this.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.CHEST).isEmpty()) {
            this.setItemSlot(net.minecraft.world.entity.EquipmentSlot.CHEST, new ItemStack(ItemRegistry.knight_chestplate.get()));
        }
        if (this.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.LEGS).isEmpty()) {
            this.setItemSlot(net.minecraft.world.entity.EquipmentSlot.LEGS, new ItemStack(ItemRegistry.knight_leggings.get()));
        }
        if (this.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.FEET).isEmpty()) {
            this.setItemSlot(net.minecraft.world.entity.EquipmentSlot.FEET, new ItemStack(ItemRegistry.knight_boots.get()));
        }
    }
    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide && this.tickCount % 2 == 0 && this.isWindingUpCharge()) {
            double mx = this.random.nextGaussian() * 0.02D;
            double my = this.random.nextGaussian() * 0.1D;
            double mz = this.random.nextGaussian() * 0.02D;
            this.level().addParticle(ParticleTypes.ANGRY_VILLAGER, this.getX() + this.random.nextFloat(), this.getY() + this.random.nextFloat() * this.getBbHeight(), this.getZ() + this.random.nextFloat(), mx, my, mz);
        }

        if (this.didChargeHit()) {
            if (this.level().isClientSide) {
                this.playSound(SoundEvents.ANVIL_PLACE, 1.0F, 1.5F);
            }
            this.setChargeHit(false);
        }
    }

    @Override
    public boolean doHurtTarget(net.minecraft.world.entity.Entity pTarget) {
        if (super.doHurtTarget(pTarget)) {
            if (pTarget instanceof LivingEntity) {
                ((LivingEntity) pTarget).addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 50));
            }
            return true;
        }
        return false;
    }

    @Override
    public void die(DamageSource pCause) {
        super.die(pCause);
        int distance = 50;
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

        this.level().setBlock(this.blockPosition(), Blocks.CHEST.defaultBlockState(), 2);
        BlockEntity te = this.level().getBlockEntity(this.blockPosition());
        if (te instanceof ChestBlockEntity chest) {
            chest.setLootTable(new net.minecraft.resources.ResourceLocation(CHEST_LOOT), this.random.nextLong());
        } else {
            TheAurorian.LOGGER.error("Failed to spawn Moon Queen loot box!");
        }
    }

    // Synched data
    private static final net.minecraft.network.syncher.EntityDataAccessor<Boolean> CHARGING = net.minecraft.network.syncher.SynchedEntityData.defineId(MoonQueenEntity.class, net.minecraft.network.syncher.EntityDataSerializers.BOOLEAN);
    private static final net.minecraft.network.syncher.EntityDataAccessor<Boolean> WINDINGUP_CHARGE = net.minecraft.network.syncher.SynchedEntityData.defineId(MoonQueenEntity.class, net.minecraft.network.syncher.EntityDataSerializers.BOOLEAN);
    private static final net.minecraft.network.syncher.EntityDataAccessor<Boolean> CHARGE_HIT = net.minecraft.network.syncher.SynchedEntityData.defineId(MoonQueenEntity.class, net.minecraft.network.syncher.EntityDataSerializers.BOOLEAN);

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(WINDINGUP_CHARGE, false);
        this.entityData.define(CHARGING, false);
        this.entityData.define(CHARGE_HIT, false);
    }

    public void setCharging(boolean bool) {
        this.entityData.set(CHARGING, bool);
    }

    public boolean isCharging() {
        return this.entityData.get(CHARGING);
    }

    public void setWindingUpCharge(boolean bool) {
        this.entityData.set(WINDINGUP_CHARGE, bool);
    }

    public boolean isWindingUpCharge() {
        return this.entityData.get(WINDINGUP_CHARGE);
    }

    public void setChargeHit(boolean bool) {
        this.entityData.set(CHARGE_HIT, bool);
    }

    public boolean didChargeHit() {
        return this.entityData.get(CHARGE_HIT);
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
        return SoundEvents.GHAST_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        if (this.isUsingItem()) {
            return SoundEvents.ANVIL_PLACE;
        }
        return SoundEvents.IRON_GOLEM_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.GHAST_DEATH;
    }

    @Override
    public boolean removeWhenFarAway(double pDistanceToClosestPlayer) {
        return false;
    }

    @Override
    public boolean isIgnoringBlockTriggers() {
        return false;
    }

    @Override
    public int getMaxSpawnClusterSize() {
        return 1;
    }

    @Override
    public boolean shouldDropExperience() {
        return true;
    }
}
