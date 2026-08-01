package shiroroku.theaurorian.World.Feature;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import org.jetbrains.annotations.Nullable;
import shiroroku.theaurorian.TheAurorian;

public class MushroomTreeGrower extends AbstractTreeGrower {

    private static final ResourceKey<ConfiguredFeature<?, ?>> MUSHROOM = ResourceKey.create(
            Registries.CONFIGURED_FEATURE, new ResourceLocation(TheAurorian.MODID, "mushroom_tree"));

    @Nullable
    @Override
    protected ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource pRandom, boolean pLargeHive) {
        return MUSHROOM;
    }
}
