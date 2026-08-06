package shiroroku.theaurorian.Entities.Passive;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Pig;
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

public class AurorianPigEntity extends Pig {

    public AurorianPigEntity(EntityType<? extends AurorianPigEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Pig.createAttributes();
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.25D));
        this.goalSelector.addGoal(3, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new TemptGoal(this, 1.2D, Ingredient.of(ItemRegistry.silkberry.get()), false));
        this.goalSelector.addGoal(5, new FollowParentGoal(this, 1.1D));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
    }

    @Override
    public boolean isFood(ItemStack pStack) {
        return pStack.is(ItemRegistry.silkberry.get());
    }

    @Override
    public AurorianPigEntity getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
        return EntityRegistry.aurorian_pig.get().create(pLevel);
    }

    @Override
    public boolean isSaddleable() {
        return this.isAlive() && !this.isBaby();
    }

    @SuppressWarnings("deprecation")
    public static boolean checkSpawn(EntityType<AurorianPigEntity> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        if (!level.getLevel().dimension().equals(TheAurorian.the_aurorian)) {
            return false;
        }
        BlockState below = level.getBlockState(pos.below());
        if (isDungeonBlock(below) || !below.is(BlockRegistry.aurorian_grass.get())) {
            return false;
        }
        return Animal.isBrightEnoughToSpawn(level, pos);
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
