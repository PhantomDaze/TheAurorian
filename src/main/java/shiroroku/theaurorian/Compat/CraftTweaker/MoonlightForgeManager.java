package shiroroku.theaurorian.Compat.CraftTweaker;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.CraftTweakerConstants;
import com.blamejared.crafttweaker.api.action.recipe.ActionAddRecipe;
import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker.api.ingredient.IIngredient;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.api.recipe.manager.base.IRecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import org.openzen.zencode.java.ZenCodeType;
import shiroroku.theaurorian.Blocks.MoonlightForge.MoonlightForgeRecipe;
import shiroroku.theaurorian.Registry.RecipeRegistry;

/**
 * CraftTweaker manager for the Moonlight Forge.
 *
 * <pre>{@code
 * import mods.theaurorian.MoonlightForge;
 * mods.theaurorian.MoonlightForge.addRecipe("moonstone_diamond_sword",
 *     <item:theaurorian:moonstone_sword>, <item:minecraft:diamond>, <item:minecraft:gold_ingot>);
 * }</pre>
 */
@ZenRegister
@ZenCodeType.Name("mods.theaurorian.MoonlightForge")
public enum MoonlightForgeManager implements IRecipeManager<MoonlightForgeRecipe> {

    INSTANCE;

    @ZenCodeType.Method
    public static void addRecipe(String name, IItemStack output, IIngredient input, IIngredient catalyst) {
        MoonlightForgeRecipe recipe = new MoonlightForgeRecipe(
                CraftTweakerConstants.rl(name),
                input.asVanillaIngredient(),
                catalyst.asVanillaIngredient(),
                output.getInternal());
        CraftTweakerAPI.apply(new ActionAddRecipe<>(INSTANCE, recipe));
    }

    @Override
    public RecipeType<MoonlightForgeRecipe> getRecipeType() {
        return RecipeRegistry.moonlight_forge.get();
    }
}
