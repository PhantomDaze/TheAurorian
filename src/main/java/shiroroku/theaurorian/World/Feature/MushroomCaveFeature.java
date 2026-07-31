package shiroroku.theaurorian.World.Feature;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import shiroroku.theaurorian.DataGen.DataGenBlocksTags;
import shiroroku.theaurorian.Registry.BlockRegistry;

import java.util.ArrayList;
import java.util.List;

/**
 * Underground mushroom grove: a large carved cavern at y 30-40 with aurorian
 * grass floors and scattered indigo mushroom trees. Ported from the 1.12
 * {@code UnderGroundWorldGenerator}.
 */
public class MushroomCaveFeature extends Feature<NoneFeatureConfiguration> {

    private static final int DISTANCE = 320;

    public MushroomCaveFeature() {
        super(NoneFeatureConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        ChunkPos chunk = new ChunkPos(context.origin());
        if (chunk.x % DISTANCE != 0 || chunk.z % DISTANCE != 0) {
            return false;
        }

        BlockPos location = new BlockPos(context.origin().getX(), 30 + random.nextInt(10), context.origin().getZ()).offset(8, 0, 8);
        int size = 250;

        float f = random.nextFloat() * (float) Math.PI;
        double d0 = location.getX() + 8 + Mth.sin(f) * size / 8.0F;
        double d1 = location.getX() + 8 - Mth.sin(f) * size / 8.0F;
        double d2 = location.getZ() + 8 + Mth.cos(f) * size / 8.0F;
        double d3 = location.getZ() + 8 - Mth.cos(f) * size / 8.0F;
        double d4 = location.getY() + random.nextInt(3) - 2;
        double d5 = location.getY() + random.nextInt(3) - 2;

        List<BlockPos> floors = new ArrayList<>();
        for (int i = 0; i < size; ++i) {
            float f1 = (float) i / (float) size;
            double d6 = d0 + (d1 - d0) * f1;
            double d7 = d4 + (d5 - d4) * f1;
            double d8 = d2 + (d3 - d2) * f1;
            double d9 = random.nextDouble() * (size) / 16.0D;
            double d10 = (Mth.sin((float) Math.PI * f1) + 1.0F) * d9 + 1.0D;
            double d11 = (Mth.sin((float) Math.PI * f1) + 1.0F) * (d9 * 0.5D) + 1.0D;
            int j = Mth.floor(d6 - d10 / 2.0D);
            int k = Mth.floor(d7 - d11 / 2.0D);
            int l = Mth.floor(d8 - d10 / 2.0D);
            int i1 = Mth.floor(d6 + d10 / 2.0D);
            int j1 = Mth.floor(d7 + d11 / 2.0D);
            int k1 = Mth.floor(d8 + d10 / 2.0D);

            for (int x = j; x <= i1; ++x) {
                double d12 = (x + 0.5D - d6) / (d10 / 2.0D);
                if (d12 * d12 < 1.0D) {
                    for (int y = k; y <= j1; ++y) {
                        double d13 = (y + 0.5D - d7) / (d11 / 2.0D);
                        if (d12 * d12 + d13 * d13 < 1.0D) {
                            for (int z = l; z <= k1; ++z) {
                                double d14 = (z + 0.5D - d8) / (d10 / 2.0D);
                                if (d12 * d12 + d13 * d13 + d14 * d14 < 1.0D) {
                                    level.setBlock(new BlockPos(x, y, z), Blocks.AIR.defaultBlockState(), 2);
                                }
                            }
                        }
                    }
                }
            }

            for (int x = j; x <= i1; ++x) {
                double d12 = (x + 0.5D - d6) / (d10 / 2.0D);
                if (d12 * d12 < 1.0D) {
                    for (int y = k; y <= j1; ++y) {
                        double d13 = (y + 0.5D - d7) / (d11 / 2.0D);
                        if (d12 * d12 + d13 * d13 < 1.0D) {
                            for (int z = l; z <= k1; ++z) {
                                double d14 = (z + 0.5D - d8) / (d10 / 2.0D);
                                if (d12 * d12 + d13 * d13 + d14 * d14 < 1.0D) {
                                    BlockPos blockpos = new BlockPos(x, y - 1, z);
                                    if (level.isEmptyBlock(blockpos.above()) && level.getBlockState(blockpos.below()).is(DataGenBlocksTags.AURORIAN_STONES)) {
                                        level.setBlock(blockpos, BlockRegistry.aurorian_grass.get().defaultBlockState(), 2);
                                        floors.add(blockpos);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        for (BlockPos p : floors) {
            this.decorateBlock(level, random, p.above());
        }

        return true;
    }

    private void decorateBlock(WorldGenLevel level, RandomSource random, BlockPos position) {
        if (random.nextFloat() < 0.10F) {
            if (!this.isTouchingOrAdjacent(level, position, 6)) {
                if (level.getBlockState(position.below()).is(BlockRegistry.aurorian_grass.get())) {
                    MushroomTreeFeature.placeTree(level, random, position);
                }
            }
        }
    }

    private boolean isTouchingOrAdjacent(WorldGenLevel level, BlockPos position, int distance) {
        for (int x = -distance; x <= distance; x++) {
            for (int y = -distance; y <= distance; y++) {
                for (int z = -distance; z <= distance; z++) {
                    if (level.getBlockState(position.offset(x, y, z)).is(BlockRegistry.mushroom_stem.get())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
