package shiroroku.theaurorian.Compat.CraftTweaker;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.CraftTweakerConstants;
import com.blamejared.crafttweaker.api.action.recipe.ActionAddRecipe;
import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker.api.ingredient.IIngredient;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.api.recipe.manager.base.IRecipeManager;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import org.openzen.zencode.java.ZenCodeType;
import shiroroku.theaurorian.Blocks.Scrapper.ScrapperRecipe;
import shiroroku.theaurorian.Registry.RecipeRegistry;

/**
 * CraftTweaker manager for the Scrapper.
 *
 * <pre>{@code
 * import mods.theaurorian.Scrapper;
 * mods.theaurorian.Scrapper.addRecipe("iron_sword_scrap",
 *     <item:minecraft:iron_sword>, <item:minecraft:iron_nugget> * 12);
 * }</pre>
 */
@ZenRegister
@ZenCodeType.Name("mods.theaurorian.Scrapper")
public enum ScrapperManager implements IRecipeManager<ScrapperRecipe> {

    INSTANCE;

    @ZenCodeType.Method
    public static void addRecipe(String name, IItemStack output, IIngredient input) {
        ScrapperRecipe recipe = new ScrapperRecipe(
                input.asVanillaIngredient(),
                output.getInternal());
        RecipeHolder<ScrapperRecipe> holder = new RecipeHolder<>(CraftTweakerConstants.rl(name), recipe);
        CraftTweakerAPI.apply(new ActionAddRecipe<>(INSTANCE, holder));
    }

    @Override
    public RecipeType<ScrapperRecipe> getRecipeType() {
        return RecipeRegistry.scrapper.get();
    }
}