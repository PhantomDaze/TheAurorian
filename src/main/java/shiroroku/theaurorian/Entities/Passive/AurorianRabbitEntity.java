package shiroroku.theaurorian.Entities.Passive;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.ClimbOnTopOfPowderSnowGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import shiroroku.theaurorian.Registry.BlockRegistry;
import shiroroku.theaurorian.Registry.EntityRegistry;
import shiroroku.theaurorian.Registry.ItemRegistry;
import shiroroku.theaurorian.TheAurorian;

public class AurorianRabbitEntity extends Rabbit {

    public AurorianRabbitEntity(EntityType<? extends AurorianRabbitEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Rabbit.createAttributes();
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(1, new ClimbOnTopOfPowderSnowGoal(this, this.level()));
        this.goalSelector.addGoal(1, new AurorianRabbitPanicGoal(this, 2.2D));
        this.goalSelector.addGoal(2, new BreedGoal(this, 0.8D));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.0D, Ingredient.of(ItemRegistry.silkberry.get()), false));
        this.goalSelector.addGoal(4, new AvoidEntityGoal<>(this, Player.class, 8.0F, 2.2D, 2.2D));
        this.goalSelector.addGoal(4, new AvoidEntityGoal<>(this, Wolf.class, 10.0F, 2.2D, 2.2D));
        this.goalSelector.addGoal(4, new AvoidEntityGoal<>(this, Monster.class, 4.0F, 2.2D, 2.2D));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.6D));
        this.goalSelector.addGoal(11, new LookAtPlayerGoal(this, Player.class, 10.0F));
    }

    @Override
    public boolean isFood(ItemStack pStack) {
        return pStack.is(ItemRegistry.silkberry.get());
    }

    @Override
    public AurorianRabbitEntity getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
        AurorianRabbitEntity rabbit = EntityRegistry.aurorian_rabbit.get().create(pLevel);
        if (rabbit != null) {
            Rabbit.Variant variant = this.getVariant();
            if (this.random.nextInt(20) != 0) {
                if (pOtherParent instanceof Rabbit other && this.random.nextBoolean()) {
                    variant = other.getVariant();
                } else {
                    variant = this.getVariant();
                }
            }
            rabbit.setVariant(variant);
        }
        return rabbit;
    }

    @SuppressWarnings("deprecation")
    public static boolean checkSpawn(EntityType<AurorianRabbitEntity> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        if (!level.getLevel().dimension().equals(TheAurorian.the_aurorian)) {
            return false;
        }
        BlockState below = level.getBlockState(pos.below());
        if (isDungeonBlock(below) || !below.is(BlockRegistry.aurorian_grass.get())) {
            return false;
        }
        return Animal.isBrightEnoughToSpawn(level, pos);
    }

    static class AurorianRabbitPanicGoal extends PanicGoal {

        private final AurorianRabbitEntity rabbit;

        public AurorianRabbitPanicGoal(AurorianRabbitEntity rabbit, double speedModifier) {
            super(rabbit, speedModifier);
            this.rabbit = rabbit;
        }

        @Override
        public void tick() {
            super.tick();
            this.rabbit.setSpeedModifier(this.speedModifier);
        }
    }

    private static boolean isDungeonBlock(BlockState state) {
        return state.is(BlockRegistry.runestone.get())
                || state.is(BlockRegistry.runestone_smooth.get())
                || state.is(BlockRegistry.runestone_lamp.get())
                || state.is(BlockRegistry.darkstone.get())
                || state.is(BlockRegistry.darkstone_chipped.get())
                || state.is(BlockRegistry.darkstone_lamp.get())
                || state.is(BlockRegistry.darkstone_pillar.get())
                || state.is(BlockRegistry.moon_temple_bricks.get())
                || state.is(BlockRegistry.moon_temple_bricks_smooth.get())
                || state.is(BlockRegistry.moon_temple_lamp.get());
    }
}
