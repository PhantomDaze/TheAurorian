package shiroroku.theaurorian.Blocks.Scrapper;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import shiroroku.theaurorian.Blocks.AbstractCrafterBlockEntity;
import shiroroku.theaurorian.Config.CommonConfig;
import shiroroku.theaurorian.Registry.BlockEntityRegistry;
import shiroroku.theaurorian.Registry.BlockRegistry;
import shiroroku.theaurorian.Registry.RecipeRegistry;
import shiroroku.theaurorian.TheAurorian;
import shiroroku.theaurorian.Util.ModUtil;

public class ScrapperBlockEntity extends AbstractCrafterBlockEntity {

    public ScrapperBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.scrapper.get(), pos, state, RecipeRegistry.scrapper::get);
    }

    @Override
    public ItemStackHandler createItemHandler() {
        return new ItemStackHandler(3) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
                tryStartCraft();
            }

            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return switch (slot) {
                    default -> true;
                    case 0 -> stack.is(BlockRegistry.crystal.get().asItem());
                    case 2 -> false;
                };
            }
        };
    }

    @Override
    public int getCraftingTime(Recipe<RecipeInput> cachedRecipe) {
        return (int) (CommonConfig.scrapper_base_craft_duration.get()
                * (level.getBlockState(getBlockPos().above()).is(BlockRegistry.crystal.get())
                ? CommonConfig.scrapper_crystal_speed_discount.get() : 1));
    }

    @Override
    public boolean isMissingPrerequisites() {
        return !getItemHandler().getStackInSlot(0).is(BlockRegistry.crystal.get().asItem())
                || getItemHandler().getStackInSlot(1).isEmpty();
    }

    @Override
    public boolean isRecipeValid(Recipe<RecipeInput> recipe) {
        if (recipe instanceof ScrapperRecipe scrapperRecipe) {
            ItemStack inputSlot = getItemHandler().getStackInSlot(1);
            ItemStack outputSlot = getItemHandler().getStackInSlot(2);
            if (!scrapperRecipe.input().test(inputSlot)) {
                return false;
            }
            return ModUtil.canItemsStack(scrapperRecipe.output(), outputSlot);
        }
        TheAurorian.LOGGER.error("Scrapper received non-scrapper recipe!");
        return false;
    }

    @Override
    public void finishCraft(Recipe<RecipeInput> recipe) {
        ItemStack inputStack = getItemHandler().getStackInSlot(1);
        float discardChance = (float) inputStack.getDamageValue() / Math.max(1, inputStack.getMaxDamage());
        discardChance = discardChance < 0.25f ? 0 : discardChance;

        getItemHandler().getStackInSlot(0).shrink(1);
        getItemHandler().getStackInSlot(1).shrink(1);
        if (discardChance == 0 || !ModUtil.randomChanceOf(this.level.getRandom(), (double) discardChance)) {
            ModUtil.setAndMergeStack(getItemHandler(), 2, recipe.getResultItem(this.level.registryAccess()));
        }

        if (level.getBlockState(getBlockPos().above()).is(BlockRegistry.crystal.get())
                && ModUtil.randomChanceOf(this.level.getRandom(), CommonConfig.scrapper_crystal_break_chance.get())) {
            level.destroyBlock(getBlockPos().above(), false);
        }
    }
}
