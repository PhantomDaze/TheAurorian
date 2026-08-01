package shiroroku.theaurorian.Registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import shiroroku.theaurorian.TheAurorian;
import shiroroku.theaurorian.World.Feature.MushroomCaveFeature;
import shiroroku.theaurorian.World.Feature.MushroomTreeFeature;
import shiroroku.theaurorian.World.Feature.WeepingWillowTreeFeature;

public class FeatureRegistry {

    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registries.FEATURE, TheAurorian.MODID);

    public static final RegistryObject<Feature<NoneFeatureConfiguration>> WEEPING_WILLOW_TREE = FEATURES.register("weeping_willow_tree", () -> new WeepingWillowTreeFeature());
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> MUSHROOM_TREE = FEATURES.register("mushroom_tree", () -> new MushroomTreeFeature());
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> MUSHROOM_CAVE = FEATURES.register("mushroom_cave", () -> new MushroomCaveFeature());

    public static void register(IEventBus bus) {
        FEATURES.register(bus);
    }
}
