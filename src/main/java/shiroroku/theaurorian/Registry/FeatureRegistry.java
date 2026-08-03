package shiroroku.theaurorian.Registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import shiroroku.theaurorian.TheAurorian;
import shiroroku.theaurorian.World.Feature.MushroomCaveFeature;
import shiroroku.theaurorian.World.Feature.MushroomTreeFeature;
import shiroroku.theaurorian.World.Feature.WeepingWillowTreeFeature;

public class FeatureRegistry {

    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registries.FEATURE, TheAurorian.MODID);

    public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> WEEPING_WILLOW_TREE = FEATURES.register("weeping_willow_tree", () -> new WeepingWillowTreeFeature());
    public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> MUSHROOM_TREE = FEATURES.register("mushroom_tree", () -> new MushroomTreeFeature());
    public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> MUSHROOM_CAVE = FEATURES.register("mushroom_cave", () -> new MushroomCaveFeature());

    public static void register(IEventBus bus) {
        FEATURES.register(bus);
    }
}
