package shiroroku.theaurorian.World.Feature;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import shiroroku.theaurorian.TheAurorian;

import java.util.Optional;

/**
 * Sapling growers backed by datapack configured features (1.21 TreeGrower).
 */
public final class AurorianTreeGrowers {

    private AurorianTreeGrowers() {
    }

    private static ResourceKey<ConfiguredFeature<?, ?>> key(String path) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.fromNamespaceAndPath(TheAurorian.MODID, path));
    }

    public static final TreeGrower SILENTWOOD = new TreeGrower(
            "silentwood",
            Optional.empty(),
            Optional.of(key("silentwood_tree")),
            Optional.empty()
    );

    public static final TreeGrower WEEPING_WILLOW = new TreeGrower(
            "weeping_willow",
            Optional.empty(),
            Optional.of(key("weeping_willow_tree")),
            Optional.empty()
    );

    public static final TreeGrower MUSHROOM = new TreeGrower(
            "aurorian_mushroom",
            Optional.empty(),
            Optional.of(key("mushroom_tree")),
            Optional.empty()
    );
}
