package shiroroku.theaurorian.World.Structure;

import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;

/**
 * Shared placement checks for the custom surface structures. The Aurorian lakes
 * biome is excluded from the structure biome lists, and these checks additionally
 * reject any location whose anchor column is water-covered (e.g. a pond inside an
 * otherwise land-based biome) so nothing except the floating moon temple spawns
 * on water.
 */
public final class StructurePlacementChecks {

    private StructurePlacementChecks() {
    }

    /**
     * True when the column at (x, z) has liquid between the ocean floor and the
     * world surface, i.e. the location is water-covered. {@code WORLD_SURFACE_WG}
     * counts liquids while {@code OCEAN_FLOOR_WG} ignores them, so on dry land the
     * two heights coincide.
     */
    public static boolean isWaterCovered(Structure.GenerationContext context, int x, int z) {
        int surface = context.chunkGenerator().getBaseHeight(x, z, Heightmap.Types.WORLD_SURFACE_WG,
                context.heightAccessor(), context.randomState());
        int floor = context.chunkGenerator().getBaseHeight(x, z, Heightmap.Types.OCEAN_FLOOR_WG,
                context.heightAccessor(), context.randomState());
        return surface != floor;
    }
}
