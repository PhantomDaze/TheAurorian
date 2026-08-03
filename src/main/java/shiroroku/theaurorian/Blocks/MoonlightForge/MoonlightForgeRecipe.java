package shiroroku.theaurorian.Blocks.MoonlightForge;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import shiroroku.theaurorian.Blocks.EmptyRecipeInput;
import shiroroku.theaurorian.Registry.RecipeRegistry;

/**
 * Moonlight Forge recipe. JSON fields: input, catalyst, output (same as 1.20.1).
 * Identity is external via RecipeHolder in 1.21.
 */
public record MoonlightForgeRecipe(Ingredient input, Ingredient catalyst, ItemStack output) implements Recipe<RecipeInput> {

    @Override
    public boolean matches(RecipeInput input, Level level) {
        return true;
    }

    @Override
    public ItemStack assemble(RecipeInput input, HolderLookup.Provider registries) {
        return this.output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return this.output.copy();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.moonlight_forge_serializer.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeRegistry.moonlight_forge.get();
    }

    public static class Serializer implements RecipeSerializer<MoonlightForgeRecipe> {
        public static final MapCodec<MoonlightForgeRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(MoonlightForgeRecipe::input),
                Ingredient.CODEC_NONEMPTY.fieldOf("catalyst").forGetter(MoonlightForgeRecipe::catalyst),
                ItemStack.STRICT_CODEC.fieldOf("output").forGetter(MoonlightForgeRecipe::output)
        ).apply(inst, MoonlightForgeRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, MoonlightForgeRecipe> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, MoonlightForgeRecipe::input,
                Ingredient.CONTENTS_STREAM_CODEC, MoonlightForgeRecipe::catalyst,
                ItemStack.STREAM_CODEC, MoonlightForgeRecipe::output,
                MoonlightForgeRecipe::new
        );

        @Override
        public MapCodec<MoonlightForgeRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, MoonlightForgeRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
