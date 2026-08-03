package shiroroku.theaurorian.World.Feature;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import org.jetbrains.annotations.Nullable;
import shiroroku.theaurorian.TheAurorian;

/**
 * Sapling grower for silentwood; configured feature lives in datapack
 * ({@code theaurorian:silentwood_tree}).
 */
public class SilentwoodTreeFeature extends AbstractTreeGrower {

    private static final ResourceKey<ConfiguredFeature<?, ?>> TREE = ResourceKey.create(
            Registry.CONFIGURED_FEATURE_REGISTRY, new ResourceLocation(TheAurorian.MODID, "silentwood_tree"));

    @Nullable
    @Override
    protected Holder<? extends ConfiguredFeature<?, ?>> getConfiguredFeature(ServerLevel level, ChunkGenerator chunkGenerator, BlockPos pos, BlockState state, RandomSource random, boolean hasFlowers) {
        return level.registryAccess().registryOrThrow(Registry.CONFIGURED_FEATURE_REGISTRY).getHolder(TREE).orElse(null);
    }

    @Nullable
    @Override
    protected Holder<? extends ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource pRandom, boolean pLargeHive) {
        // Deprecated vanilla path — not used when the Forge overload above is present.
        return null;
    }
}
