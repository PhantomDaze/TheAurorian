package shiroroku.theaurorian.World.Feature;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import shiroroku.theaurorian.Registry.BlockRegistry;

/**
 * Procedural indigo mushroom tree (stem + cap + occasional crystal), ported
 * from the 1.12 {@code MushroomWorldGenerator}. Also used as the mushroom
 * "sapling" grower.
 */
public class MushroomTreeFeature extends Feature<NoneFeatureConfiguration> {

    public MushroomTreeFeature() {
        super(NoneFeatureConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        return placeTree(context.level(), context.random(), context.origin());
    }

    public static boolean placeTree(WorldGenLevel level, RandomSource random, BlockPos pos) {
        int height = 5 - random.nextInt(3);
        int capwidth = 5;
        for (int i = 0; i <= height; i++) {
            if (!level.isEmptyBlock(pos.above(i))) {
                return false;
            }
        }

        for (int i = 0; i < height; i++) {
            BlockPos p = pos.above(i);
            if (level.isEmptyBlock(p)) {
                level.setBlock(p, BlockRegistry.mushroom_stem.get().defaultBlockState(), 2);
            }
        }

        int w2 = capwidth - 2;
        for (int x = 0; x < w2; x++) {
            for (int z = 0; z < w2; z++) {
                BlockPos p = pos.offset(x - w2 / 2, height, z - w2 / 2);
                if (level.isEmptyBlock(p)) {
                    level.setBlock(p, BlockRegistry.mushroom.get().defaultBlockState(), 2);
                }
            }
        }

        for (int x = 0; x < capwidth; x++) {
            for (int z = 0; z < capwidth; z++) {
                BlockPos p = pos.offset(x - capwidth / 2, height - 1, z - capwidth / 2);
                if (level.isEmptyBlock(p) && (x == 0 || z == 0 || x == capwidth - 1 || z == capwidth - 1)) {
                    boolean corner = (x == 0 && z == 0) || (x == capwidth - 1 && z == capwidth - 1) || (x == 0 && z == capwidth - 1) || (x == capwidth - 1 && z == 0);
                    if (!corner) {
                        level.setBlock(p, BlockRegistry.mushroom.get().defaultBlockState(), 2);
                    }
                }
            }
        }

        for (int x = 0; x < w2; x++) {
            for (int z = 0; z < w2; z++) {
                BlockPos p = pos.offset(x - w2 / 2, height - 1, z - w2 / 2);
                if (level.isEmptyBlock(p) && random.nextFloat() < 0.25F) {
                    level.setBlock(p, BlockRegistry.mushroom_crystal.get().defaultBlockState(), 2);
                    return true;
                }
            }
        }

        return true;
    }
}
