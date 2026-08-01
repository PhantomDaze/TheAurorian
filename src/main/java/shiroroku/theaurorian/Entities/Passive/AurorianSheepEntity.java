package shiroroku.theaurorian.Entities.Passive;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
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
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import shiroroku.theaurorian.Registry.BlockRegistry;
import shiroroku.theaurorian.Registry.EntityRegistry;
import shiroroku.theaurorian.Registry.ItemRegistry;
import shiroroku.theaurorian.TheAurorian;

public class AurorianSheepEntity extends Sheep {

    private int sheepTimer;
    private AurorianSheepAIEatGrass entityAIEatGrass;

    public AurorianSheepEntity(EntityType<? extends AurorianSheepEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Sheep.createAttributes();
    }

    @Override
    protected void registerGoals() {
        this.entityAIEatGrass = new AurorianSheepAIEatGrass(this);
        super.registerGoals();
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.1D, Ingredient.of(ItemRegistry.silkberry.get()), false));
        this.goalSelector.addGoal(5, this.entityAIEatGrass);
    }

    @Override
    public boolean isFood(ItemStack pStack) {
        return pStack.is(ItemRegistry.silkberry.get());
    }

    @Override
    public AurorianSheepEntity getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
        AurorianSheepEntity sheep = EntityRegistry.aurorian_sheep.get().create(pLevel);
        if (sheep != null) {
            sheep.setColor(this.getOffspringColor(this, (Sheep) pOtherParent));
        }
        return sheep;
    }

    @Override
    protected void customServerAiStep() {
        this.sheepTimer = this.entityAIEatGrass.getEatingGrassTimer();
        super.customServerAiStep();
    }

    @Override
    public void aiStep() {
        if (this.level().isClientSide) {
            this.sheepTimer = Math.max(0, this.sheepTimer - 1);
        }
        super.aiStep();
    }

    @Override
    public void handleEntityEvent(byte pId) {
        if (pId == 10) {
            this.sheepTimer = 40;
        } else {
            super.handleEntityEvent(pId);
        }
    }

    @Override
    public float getHeadEatPositionScale(float pPartialTick) {
        if (this.sheepTimer <= 0) {
            return 0.0F;
        } else if (this.sheepTimer >= 4 && this.sheepTimer <= 36) {
            return 1.0F;
        } else {
            return this.sheepTimer < 4 ? ((float) this.sheepTimer - pPartialTick) / 4.0F : -((float) (this.sheepTimer - 40) - pPartialTick) / 4.0F;
        }
    }

    @Override
    public float getHeadEatAngleScale(float pPartialTick) {
        if (this.sheepTimer > 4 && this.sheepTimer <= 36) {
            float f = ((float) (this.sheepTimer - 4) - pPartialTick) / 32.0F;
            return ((float) Math.PI / 5F) + 0.21991149F * Mth.sin(f * 28.7F);
        } else {
            return this.sheepTimer > 0 ? ((float) Math.PI / 5F) : this.getXRot() * ((float) Math.PI / 180F);
        }
    }

    private DyeColor getOffspringColor(Animal pFather, Animal pMother) {
        DyeColor dyecolor = ((Sheep) pFather).getColor();
        DyeColor dyecolor1 = ((Sheep) pMother).getColor();
        CraftingContainer craftingcontainer = makeContainer(dyecolor, dyecolor1);
        return this.level().getRecipeManager().getRecipeFor(RecipeType.CRAFTING, craftingcontainer, this.level())
                .map(p_29828_ -> p_29828_.assemble(craftingcontainer, this.level().registryAccess()))
                .map(ItemStack::getItem)
                .filter(DyeItem.class::isInstance)
                .map(DyeItem.class::cast)
                .map(DyeItem::getDyeColor)
                .orElseGet(() -> this.level().random.nextBoolean() ? dyecolor : dyecolor1);
    }

    private static CraftingContainer makeContainer(DyeColor pFatherColor, DyeColor pMotherColor) {
        CraftingContainer craftingcontainer = new TransientCraftingContainer(new AbstractContainerMenu(null, -1) {
            @Override
            public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
                return ItemStack.EMPTY;
            }

            @Override
            public boolean stillValid(Player pPlayer) {
                return false;
            }
        }, 2, 1);
        craftingcontainer.setItem(0, new ItemStack(DyeItem.byColor(pFatherColor)));
        craftingcontainer.setItem(1, new ItemStack(DyeItem.byColor(pMotherColor)));
        return craftingcontainer;
    }

    @SuppressWarnings("deprecation")
    public static boolean checkSpawn(EntityType<AurorianSheepEntity> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
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
