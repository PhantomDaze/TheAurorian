package shiroroku.theaurorian.World.Structure;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.WorldGenLevel;

/**
 * Post-processing for chests placed from structure templates.
 *
 * <p>Vanilla {@link ChestBlock} only merges neighbours into a double (large) chest during player
 * placement ({@code getStateForPlacement}); structure placement writes the block state verbatim
 * and never calls that logic. Templates for the three Aurorian dungeons therefore spawn adjacent
 * chests as two separate singles. {@link #connectChests} fixes that after the fact by mimicking
 * the vanilla pairing rule: a single chest that has a single chest of the same block and facing on
 * its clockwise neighbour is the LEFT half, and on its counter-clockwise neighbour it is the RIGHT
 * half. Re-scanning is idempotent (non-single chests are skipped), so per-chunk calls are safe.</p>
 */
public final class StructureChests {

    private StructureChests() {
    }

    /**
     * The full box a template occupies at {@code pos} with {@code rotation}, ignoring any
     * chunk clipping in {@code settings}. A double-chest pair always lives in one template,
     * so scanning this box (instead of the chunk-clipped one) can join halves that straddle
     * a chunk border once the neighbouring chunk is generated.
     */
    public static BoundingBox templateBox(StructureTemplate template, Rotation rotation, BlockPos pos) {
        Vec3i size = template.getSize();
        int w = size.getX();
        int d = size.getZ();
        if (rotation == Rotation.CLOCKWISE_90 || rotation == Rotation.COUNTERCLOCKWISE_90) {
            int t = w;
            w = d;
            d = t;
        }
        return new BoundingBox(
                pos.getX(), pos.getY(), pos.getZ(),
                pos.getX() + w - 1, pos.getY() + size.getY() - 1, pos.getZ() + d - 1);
    }

    /**
     * Scan {@code box} and merge any adjacent single chests into large chests.
     */
    public static void connectChests(WorldGenLevel level, BoundingBox box) {
        for (BlockPos pos : BlockPos.betweenClosed(box.minX(), box.minY(), box.minZ(), box.maxX(), box.maxY(), box.maxZ())) {
            BlockState state = level.getBlockState(pos);
            if (!(state.getBlock() instanceof ChestBlock) || state.getValue(ChestBlock.TYPE) != ChestType.SINGLE) {
                continue;
            }
            Direction facing = state.getValue(ChestBlock.FACING);

            // Neighbour on our clockwise side is the partner → we are LEFT.
            BlockPos cw = pos.relative(facing.getClockWise());
            if (isSinglePartner(level, cw, facing)) {
                level.setBlock(pos, state.setValue(ChestBlock.TYPE, ChestType.LEFT), 3);
                level.setBlock(cw, level.getBlockState(cw).setValue(ChestBlock.TYPE, ChestType.RIGHT), 3);
                continue;
            }
            // Neighbour on our counter-clockwise side is the partner → we are RIGHT.
            BlockPos ccw = pos.relative(facing.getCounterClockWise());
            if (isSinglePartner(level, ccw, facing)) {
                level.setBlock(pos, state.setValue(ChestBlock.TYPE, ChestType.RIGHT), 3);
                level.setBlock(ccw, level.getBlockState(ccw).setValue(ChestBlock.TYPE, ChestType.LEFT), 3);
            }
        }
    }

    private static boolean isSinglePartner(WorldGenLevel level, BlockPos pos, Direction facing) {
        BlockState state = level.getBlockState(pos);
        return state.getBlock() instanceof ChestBlock
                && state.getValue(ChestBlock.TYPE) == ChestType.SINGLE
                && state.getValue(ChestBlock.FACING) == facing;
    }
}