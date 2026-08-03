package shiroroku.theaurorian.Blocks;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

/**
 * Dummy recipe input — machine BEs validate ingredients against their own handlers.
 */
public final class EmptyRecipeInput implements RecipeInput {
    public static final EmptyRecipeInput INSTANCE = new EmptyRecipeInput();

    private EmptyRecipeInput() {
    }

    @Override
    public ItemStack getItem(int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }
}
