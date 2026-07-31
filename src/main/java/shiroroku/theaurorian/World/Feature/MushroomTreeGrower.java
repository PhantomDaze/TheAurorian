package shiroroku.theaurorian.World.Feature;

import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import org.jetbrains.annotations.Nullable;

public class MushroomTreeGrower extends AbstractTreeGrower {

    private static final Holder<ConfiguredFeature<NoneFeatureConfiguration, ?>> MUSHROOM = FeatureUtils.register("mushroom_tree", new MushroomTreeFeature());

    @Nullable
    @Override
    protected Holder<? extends ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource pRandom, boolean pLargeHive) {
        return MUSHROOM;
    }
}
