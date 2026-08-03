package shiroroku.theaurorian.Compat.JEI;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import shiroroku.theaurorian.Blocks.MoonlightForge.MoonlightForgeRecipe;
import shiroroku.theaurorian.Registry.BlockRegistry;
import shiroroku.theaurorian.TheAurorian;

public class JEIMoonlightForgeCategory implements IRecipeCategory<MoonlightForgeRecipe> {

    private static final int WIDTH = 148;
    private static final int HEIGHT = 32;

    private final IDrawable bg, icon;
    public static final ResourceLocation screen = ResourceLocation.fromNamespaceAndPath(TheAurorian.MODID, "textures/gui/moonlight_forge.png");

    public JEIMoonlightForgeCategory(IGuiHelper guihelper) {
        this.bg = guihelper.createDrawable(screen, 18, 27, WIDTH, HEIGHT);
        this.icon = guihelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(BlockRegistry.moonlight_forge.get()));
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, MoonlightForgeRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 4, 8).addIngredients(recipe.input());
        builder.addSlot(RecipeIngredientRole.INPUT, 66, 8).addIngredients(recipe.catalyst());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 124, 8).addIngredients(Ingredient.of(recipe.getResultItem(net.minecraft.core.RegistryAccess.EMPTY)));
    }

    @Override
    public RecipeType<MoonlightForgeRecipe> getRecipeType() {
        return JEIPlugin.moonlight_forge;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.theaurorian.moonlight_forge");
    }

    @Override
    public int getWidth() {
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }

    @Override
    public void draw(MoonlightForgeRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        bg.draw(guiGraphics);
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }
}
