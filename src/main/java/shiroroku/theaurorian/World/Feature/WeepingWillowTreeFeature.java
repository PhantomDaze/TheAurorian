package shiroroku.theaurorian.World.Feature;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import shiroroku.theaurorian.Registry.BlockRegistry;
import shiroroku.theaurorian.TheAurorian;
import shiroroku.theaurorian.World.Structure.ReplaceAirStructureProcessor;

/**
 * Weeping willow tree, generated from the 5 upstream willow NBTs (75% small,
 * 25% large, random rotation, only filling air). Requires 5 empty blocks above
 * and an {@code aurorian_grass_light} block underneath, matching the 1.12
 * generator.
 */
public class WeepingWillowTreeFeature extends Feature<NoneFeatureConfiguration> {

    private static final String[] SMALL = {"willow_s1", "willow_s2", "willow_s3"};
    private static final String[] LARGE = {"willow_l1", "willow_l2"};

    public WeepingWillowTreeFeature() {
        super(NoneFeatureConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos pos = context.origin();
        RandomSource random = context.random();

        if (random.nextFloat() < 0.3F) {
            return false;
        }
        boolean small = random.nextFloat() < 0.75F;
        for (int i = 0; i <= 4; i++) {
            if (!level.isEmptyBlock(pos.above(i))) {
                return false;
            }
        }
        if (!level.getBlockState(pos.below()).is(BlockRegistry.aurorian_grass_light.get())) {
            return false;
        }
        level.setBlock(pos.below(), BlockRegistry.aurorian_dirt.get().defaultBlockState(), 2);

        StructureTemplateManager manager = ((ServerLevelAccessor) level).getLevel().getServer().getStructureManager();
        String[] pool = small ? SMALL : LARGE;
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(TheAurorian.MODID, "weepingwillow/" + pool[random.nextInt(pool.length)]);
        StructureTemplate template = manager.get(id).orElse(null);
        if (template == null) {
            return false;
        }

        Rotation rotation = Rotation.getRandom(random);
        BlockPos place = willowPlacement(pos, rotation, small ? 3 : 4);
        StructurePlaceSettings settings = new StructurePlaceSettings().setRotation(rotation).setRandom(random)
                .addProcessor(ReplaceAirStructureProcessor.INSTANCE);
        template.placeInWorld(level, place, place, settings, random, 2);
        return true;
    }

    private static BlockPos willowPlacement(BlockPos position, Rotation rotation, int offset) {
        return switch (rotation) {
            case CLOCKWISE_90 -> position.offset(offset, 0, -offset);
            case CLOCKWISE_180 -> position.offset(offset, 0, offset);
            case COUNTERCLOCKWISE_90 -> position.offset(-offset, 0, offset);
            default -> position.offset(-offset, 0, -offset);
        };
    }
}
