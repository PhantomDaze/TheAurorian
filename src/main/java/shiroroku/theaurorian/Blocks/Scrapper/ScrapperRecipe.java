package shiroroku.theaurorian.Blocks.Scrapper;

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
import shiroroku.theaurorian.Registry.RecipeRegistry;

/**
 * Scrapper recipe. JSON fields: input, output (same as 1.20.1).
 */
public record ScrapperRecipe(Ingredient input, ItemStack output) implements Recipe<RecipeInput> {

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
        return RecipeRegistry.scrapper_serializer.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeRegistry.scrapper.get();
    }

    public static class Serializer implements RecipeSerializer<ScrapperRecipe> {
        public static final MapCodec<ScrapperRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(ScrapperRecipe::input),
                ItemStack.STRICT_CODEC.fieldOf("output").forGetter(ScrapperRecipe::output)
        ).apply(inst, ScrapperRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, ScrapperRecipe> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, ScrapperRecipe::input,
                ItemStack.STREAM_CODEC, ScrapperRecipe::output,
                ScrapperRecipe::new
        );

        @Override
        public MapCodec<ScrapperRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ScrapperRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
