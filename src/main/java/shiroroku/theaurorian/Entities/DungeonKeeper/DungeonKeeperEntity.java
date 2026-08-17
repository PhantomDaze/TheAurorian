package shiroroku.theaurorian.Entities.DungeonKeeper;

import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import shiroroku.theaurorian.Entities.DungeonKeeper.AI.KeeperBarrageGoal;
import shiroroku.theaurorian.Entities.DungeonKeeper.AI.KeeperMeleeGoal;
import shiroroku.theaurorian.Entities.DungeonKeeper.AI.KeeperRangedGoal;
import shiroroku.theaurorian.Registry.BlockRegistry;
import shiroroku.theaurorian.Registry.EnchantRegistry;
import shiroroku.theaurorian.Registry.ItemRegistry;

public class DungeonKeeperEntity extends AbstractSkeleton {

    private final ServerBossEvent bossEvent = (ServerBossEvent) (new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.BLUE, BossEvent.BossBarOverlay.PROGRESS)).setDarkenScreen(true);

    public DungeonKeeperEntity(EntityType<? extends AbstractSkeleton> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.xpReward = 350;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 175.0)
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.ATTACK_DAMAGE, 2.0)
                .add(Attributes.ARMOR, 4.0)
                .add(Attributes.FOLLOW_RANGE, 50);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(2, new KeeperBarrageGoal<>(this));
        this.goalSelector.addGoal(3, new KeeperMeleeGoal(this));
        // K4: upstream 0.85 move / interval 20 / radius 40
        this.goalSelector.addGoal(4, new KeeperRangedGoal<>(this, 0.85D, 20, 40.0F));
        this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, false));
    }

    protected void populateDefaultEquipmentSlots(RandomSource pRandom, DifficultyInstance pDifficulty) {
        ensureBossEquipment();
    }

    /**
     * Restores the upstream Keeper weapon only when the main hand is empty.
     * Combat goals are allowed to replace it with the silentwood bow.
     */
    public void ensureBossEquipment() {
        if (!this.getMainHandItem().isEmpty()) {
            return;
        }

        ItemStack sword = new ItemStack(ItemRegistry.moonstone_sword.get());
        if (this.level() instanceof ServerLevel sl) {
            var enchantments = sl.registryAccess().registryOrThrow(Registries.ENCHANTMENT);
            sword.enchant(enchantments.getHolderOrThrow(Enchantments.KNOCKBACK), 2);
            sword.enchant(enchantments.getHolderOrThrow(EnchantRegistry.LIGHTNING), 3);
        }
        this.setItemInHand(InteractionHand.MAIN_HAND, sword);
    }

    /** K1: melee applies Slowness 200 ticks (upstream). */
    @Override
    public boolean doHurtTarget(Entity pEntity) {
        boolean flag = super.doHurtTarget(pEntity);
        if (flag && pEntity instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200), this);
        }
        return flag;
    }

    /**
     * 守卫身高 4.2 格，vanilla AbstractSkeleton 的箭起点（getEyeY-0.1）会落在
     * 不自然的位置（实测从腿部射出）。这里显式把箭起点设为头部，再按原版
     * 弹道逻辑射向目标。
     */
    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {
        ItemStack bow = this.getItemInHand(net.minecraft.world.entity.projectile.ProjectileUtil.getWeaponHoldingHand(this, item -> item instanceof net.minecraft.world.item.BowItem));
        ItemStack arrowItem = this.getProjectile(bow);
        net.minecraft.world.entity.projectile.AbstractArrow arrow = this.getArrow(arrowItem, distanceFactor, bow);
        // 起点定在头部（实体顶部下方 0.5 格），而非默认的眼高
        arrow.setPos(this.getX(), this.getY() + this.getBbHeight() - 0.5, this.getZ());

        double d0 = target.getX() - this.getX();
        double d1 = target.getY(0.3333333333333333) - arrow.getY();
        double d2 = target.getZ() - this.getZ();
        double d3 = Math.sqrt(d0 * d0 + d2 * d2);
        arrow.shoot(d0, d1 + d3 * 0.20000000298023224, d2, 1.6F, (float) (14 - this.level().getDifficulty().getId() * 4));
        this.playSound(SoundEvents.SKELETON_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.level().addFreshEntity(arrow);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        // K8: low-HP client water splash particles (≤30%)
        if (this.level().isClientSide && this.tickCount % 2 == 0 && this.getHealth() <= this.getMaxHealth() * 0.30F) {
            double mx = this.random.nextGaussian() * 0.02D;
            double my = this.random.nextGaussian() * 0.02D;
            double mz = this.random.nextGaussian() * 0.02D;
            this.level().addParticle(ParticleTypes.SPLASH,
                    this.getX() + this.random.nextFloat(),
                    this.getY() + this.random.nextFloat() * this.getBbHeight(),
                    this.getZ() + this.random.nextFloat(),
                    mx, my, mz);
        }
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
    }

    @Override
    public void reassessWeaponGoal() {
        // we do this ourselves
    }

    // Drops: data/theaurorian/loot_table/entities/dungeon_keeper.json
    // (keepers_amulet, runestone_loot_key, trophy_keeper — no darkstone_key)

    @Override
    public void remove(RemovalReason pReason) {
        if (pReason == RemovalReason.KILLED) {
            int distance = 50;
            for (int x = 0; x <= distance; x++) {
                for (int y = 0; y <= distance; y++) {
                    for (int z = 0; z <= distance; z++) {
                        int offs = distance / 2;
                        BlockPos p = BlockPos.containing(x + this.position().x() - offs, y + this.position().y() - offs, z + this.position().z() - offs);
                        if (this.level().getBlockState(p).getBlock() == BlockRegistry.fog_wall.get()) {
                            this.level().destroyBlock(p, false);
                        }
                    }
                }
            }
        }
        super.remove(pReason);
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
    public void checkDespawn() {
        if (this.level().getDifficulty() == Difficulty.PEACEFUL && this.shouldDespawnInPeaceful()) {
            this.discard();
        } else {
            this.noActionTime = 0;
        }
    }

    // K9: Wither Skeleton sounds
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.WITHER_SKELETON_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return SoundEvents.WITHER_SKELETON_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.WITHER_SKELETON_DEATH;
    }

    @Override
    protected SoundEvent getStepSound() {
        return SoundEvents.WITHER_SKELETON_STEP;
    }
}
