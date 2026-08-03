package shiroroku.theaurorian.Blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Simple inventory with a single timer and single recipe type.
 */
public abstract class AbstractCrafterBlockEntity extends AbstractInventoryBlockEntity {

    public int craftingProgress = -1;
    public Recipe<RecipeInput> cachedRecipe = null;
    public final Supplier<RecipeType<? extends Recipe<RecipeInput>>> recipeType;

    public AbstractCrafterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state,
                                      Supplier<RecipeType<? extends Recipe<RecipeInput>>> recipeType) {
        super(type, pos, state);
        this.recipeType = recipeType;
    }

    public abstract boolean isMissingPrerequisites();

    public abstract int getCraftingTime(Recipe<RecipeInput> cachedRecipe);

    public abstract void finishCraft(Recipe<RecipeInput> recipe);

    public abstract boolean isRecipeValid(Recipe<RecipeInput> recipe);

    public boolean isCrafting() {
        return craftingProgress != -1;
    }

    public void resetCrafting() {
        craftingProgress = -1;
        cachedRecipe = null;
        updateClient();
    }

    public void tryStartCraft() {
        if (isCrafting()) {
            return;
        }
        Optional<Recipe<RecipeInput>> recipe = tryGetRecipe();
        if (recipe.isEmpty()) {
            return;
        }
        updateClient();
        cachedRecipe = recipe.get();
        craftingProgress = 0;
    }

    public static <T extends BlockEntity> void updateCraft(Level level, BlockPos pos, BlockState blockState, T t) {
        if (t instanceof AbstractCrafterBlockEntity crafter) {
            crafter.validateCachedRecipe();
            if (crafter.isCrafting()) {
                crafter.craftingProgress++;
                int craftingTime = crafter.getCraftingTime(crafter.cachedRecipe);
                if (craftingTime == 0) {
                    crafter.resetCrafting();
                }
                if (crafter.craftingProgress >= craftingTime) {
                    if (crafter.validateCachedRecipe() || level.isClientSide) {
                        return;
                    }
                    crafter.finishCraft(crafter.cachedRecipe);
                    crafter.resetCrafting();
                }
            } else {
                if (level.getGameTime() % 20 == 0) {
                    crafter.tryStartCraft();
                }
            }
        }
    }

    public boolean validateCachedRecipe() {
        if (isCrafting() && cachedRecipe != null) {
            if (isMissingPrerequisites() || !isRecipeValid(cachedRecipe)) {
                resetCrafting();
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private Optional<Recipe<RecipeInput>> tryGetRecipe() {
        if (isMissingPrerequisites() || level == null) {
            return Optional.empty();
        }
        RecipeType<? extends Recipe<RecipeInput>> type = recipeType.get();
        List<RecipeHolder<?>> holders = (List) level.getRecipeManager().getAllRecipesFor((RecipeType) type);
        for (RecipeHolder<?> holder : holders) {
            Recipe<?> recipe = holder.value();
            if (isRecipeValid((Recipe<RecipeInput>) recipe)) {
                return Optional.of((Recipe<RecipeInput>) recipe);
            }
        }
        return Optional.empty();
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        if (tag.contains("crafting_progress")) {
            craftingProgress = tag.getInt("crafting_progress");
        }
        super.loadAdditional(tag, registries);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putInt("crafting_progress", craftingProgress);
        super.saveAdditional(tag, registries);
    }
}
