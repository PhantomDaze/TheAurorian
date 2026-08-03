package shiroroku.theaurorian.Portal;

import net.minecraft.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;
import shiroroku.theaurorian.Blocks.AurorianPortal;
import shiroroku.theaurorian.Registry.BlockRegistry;
import shiroroku.theaurorian.Registry.POIRegistry;

import java.util.Comparator;
import java.util.Optional;

/**
 * Locates or builds an Aurorian portal rectangle and builds a {@link DimensionTransition}.
 * Replaces the removed Forge {@code ITeleporter} path.
 */
public final class AurorianPortalTeleporter {

    private final ServerLevel level;

    public AurorianPortalTeleporter(ServerLevel level) {
        this.level = level;
    }

    public static DimensionTransition createTransition(ServerLevel destination, Entity entity, BlockPos entryPortalPos) {
        AurorianPortalTeleporter teleporter = new AurorianPortalTeleporter(destination);
        Optional<BlockUtil.FoundRectangle> portal = teleporter.getOrMakePortal(entity, entryPortalPos);
        if (portal.isEmpty()) {
            // Fallback: shared spawn of destination
            return new DimensionTransition(
                    destination,
                    entity,
                    DimensionTransition.PLAY_PORTAL_SOUND.then(DimensionTransition.PLACE_PORTAL_TICKET)
            );
        }
        BlockUtil.FoundRectangle rect = portal.get();
        // Center of the portal rectangle
        Vec3 dest = new Vec3(
                rect.minCorner.getX() + rect.axis1Size / 2.0,
                rect.minCorner.getY(),
                rect.minCorner.getZ() + rect.axis2Size / 2.0
        );
        // axis1 is width along horizontal; prefer standing in portal cell
        dest = Vec3.atBottomCenterOf(rect.minCorner.offset(rect.axis1Size / 2, 0, rect.axis2Size / 2));
        return new DimensionTransition(
                destination,
                dest,
                entity.getDeltaMovement(),
                entity.getYRot(),
                entity.getXRot(),
                DimensionTransition.PLAY_PORTAL_SOUND.then(DimensionTransition.PLACE_PORTAL_TICKET)
        );
    }

    private Optional<BlockUtil.FoundRectangle> getOrMakePortal(Entity entity, BlockPos fromPortalPos) {
        WorldBorder border = level.getWorldBorder();
        double minX = Math.max(-2.9999872E7D, border.getMinX() + 16.0D);
        double minZ = Math.max(-2.9999872E7D, border.getMinZ() + 16.0D);
        double maxX = Math.min(2.9999872E7D, border.getMaxX() - 16.0D);
        double maxZ = Math.min(2.9999872E7D, border.getMaxZ() - 16.0D);
        double scale = DimensionType.getTeleportationScale(entity.level().dimensionType(), level.dimensionType());
        BlockPos scaled = BlockPos.containing(
                Mth.clamp(entity.getX() * scale, minX, maxX),
                entity.getY(),
                Mth.clamp(entity.getZ() * scale, minZ, maxZ)
        );
        Optional<BlockUtil.FoundRectangle> existing = findPortalAround(scaled);
        if (existing.isPresent()) {
            return existing;
        }
        Direction facing = Direction.NORTH;
        if (entity.level().getBlockState(fromPortalPos).hasProperty(AurorianPortal.FACING)) {
            facing = entity.level().getBlockState(fromPortalPos).getValue(AurorianPortal.FACING);
        }
        return createPortal(scaled, facing);
    }

    public Optional<BlockUtil.FoundRectangle> findPortalAround(BlockPos pos) {
        PoiManager manager = this.level.getPoiManager();
        manager.ensureLoadedAndValid(this.level, pos, 64);
        Optional<PoiRecord> optional = manager.getInSquare(
                        (poi) -> poi.value() == POIRegistry.aurorian_portal.get(),
                        pos, 64, PoiManager.Occupancy.ANY)
                .sorted(Comparator.<PoiRecord>comparingDouble((poi) -> poi.getPos().distSqr(pos))
                        .thenComparingInt((poi) -> poi.getPos().getY()))
                .filter((poi) -> this.level.getBlockState(poi.getPos()).is(BlockRegistry.aurorian_portal.get()))
                .findFirst();
        return optional.map((poi) -> {
            BlockPos blockPos = poi.getPos();
            this.level.getChunkSource().addRegionTicket(TicketType.PORTAL, new ChunkPos(blockPos), 3, blockPos);
            BlockState blockState = this.level.getBlockState(blockPos);
            Direction.Axis axis = blockState.getValue(AurorianPortal.FACING).getAxis();
            return BlockUtil.getLargestRectangleAround(
                    blockPos, axis, 21, Direction.Axis.Y, 21,
                    (p) -> this.level.getBlockState(p) == blockState
            );
        });
    }

    public Optional<BlockUtil.FoundRectangle> createPortal(BlockPos pos, Direction direction) {
        double d0 = -1.0D;
        BlockPos blockpos = null;
        double d1 = -1.0D;
        BlockPos blockpos1 = null;
        WorldBorder worldBorder = this.level.getWorldBorder();
        int maxY = Math.min(this.level.getMaxBuildHeight(), this.level.getMinBuildHeight() + this.level.getLogicalHeight()) - 1;
        BlockPos.MutableBlockPos scratch = pos.mutable();

        for (BlockPos.MutableBlockPos mut : BlockPos.spiralAround(pos, 48, Direction.EAST, Direction.SOUTH)) {
            int height = Math.min(maxY, this.level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, mut.getX(), mut.getZ()));
            if (worldBorder.isWithinBounds(mut) && worldBorder.isWithinBounds(mut.move(direction, 1))) {
                mut.move(direction.getOpposite(), 1);
                for (int y = height; y >= this.level.getMinBuildHeight(); --y) {
                    mut.setY(y);
                    if (this.level.isEmptyBlock(mut)) {
                        int emptyY = y;
                        while (y > this.level.getMinBuildHeight() && this.level.isEmptyBlock(mut.move(Direction.DOWN))) {
                            --y;
                        }
                        if (y + 4 <= maxY) {
                            int j1 = emptyY - y;
                            if (j1 <= 0 || j1 >= 3) {
                                mut.setY(y);
                                if (this.validFrame(mut, scratch, direction, 0)) {
                                    double d2 = pos.distSqr(mut);
                                    if (this.validFrame(mut, scratch, direction, -1)
                                            && this.validFrame(mut, scratch, direction, 1)
                                            && (d0 == -1.0D || d0 > d2)) {
                                        d0 = d2;
                                        blockpos = mut.immutable();
                                    }
                                    if (d0 == -1.0D && (d1 == -1.0D || d1 > d2)) {
                                        d1 = d2;
                                        blockpos1 = mut.immutable();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (d0 == -1.0D && d1 != -1.0D) {
            blockpos = blockpos1;
            d0 = d1;
        }

        if (d0 == -1.0D) {
            int k1 = Math.max(this.level.getMinBuildHeight() + 1, 70);
            int i2 = maxY - 9;
            if (i2 < k1) {
                return Optional.empty();
            }
            blockpos = new BlockPos(pos.getX(), Mth.clamp(pos.getY(), k1, i2), pos.getZ()).immutable();
            Direction direction1 = direction.getClockWise();
            if (!worldBorder.isWithinBounds(blockpos)) {
                return Optional.empty();
            }
            for (int i3 = -1; i3 < 2; ++i3) {
                for (int j3 = 0; j3 < 2; ++j3) {
                    for (int k3 = -1; k3 < 3; ++k3) {
                        BlockState blockstate1 = k3 < 0
                                ? BlockRegistry.aurorian_portal_frame.get().defaultBlockState()
                                : Blocks.AIR.defaultBlockState();
                        scratch.setWithOffset(blockpos,
                                j3 * direction.getStepX() + i3 * direction1.getStepX(),
                                k3,
                                j3 * direction.getStepZ() + i3 * direction1.getStepZ());
                        this.level.setBlockAndUpdate(scratch, blockstate1);
                    }
                }
            }
        }

        for (int l1 = -1; l1 < 3; ++l1) {
            for (int j2 = -1; j2 < 4; ++j2) {
                if (l1 == -1 || l1 == 2 || j2 == -1 || j2 == 3) {
                    scratch.setWithOffset(blockpos, l1 * direction.getStepX(), j2, l1 * direction.getStepZ());
                    this.level.setBlock(scratch, BlockRegistry.aurorian_portal_frame.get().defaultBlockState(), 3);
                }
            }
        }

        BlockState portal = BlockRegistry.aurorian_portal.get().defaultBlockState()
                .setValue(AurorianPortal.FACING, direction.getClockWise());
        for (int k2 = 0; k2 < 2; ++k2) {
            for (int l2 = 0; l2 < 3; ++l2) {
                scratch.setWithOffset(blockpos, k2 * direction.getStepX(), l2, k2 * direction.getStepZ());
                this.level.setBlock(scratch, portal, 18);
            }
        }

        return Optional.of(new BlockUtil.FoundRectangle(blockpos.immutable(), 2, 3));
    }

    private boolean validFrame(BlockPos originalPos, BlockPos.MutableBlockPos offsetPos, Direction direction, int offsetScale) {
        Direction clockwise = direction.getClockWise();
        for (int xz = -1; xz < 3; ++xz) {
            for (int y = -1; y < 4; ++y) {
                offsetPos.setWithOffset(originalPos,
                        direction.getStepX() * xz + clockwise.getStepX() * offsetScale,
                        y,
                        direction.getStepZ() * xz + clockwise.getStepZ() * offsetScale);
                if (y < 0 && !this.level.getBlockState(offsetPos).isSolid()) {
                    return false;
                }
                if (y >= 0 && !this.level.isEmptyBlock(offsetPos)) {
                    return false;
                }
            }
        }
        return true;
    }
}
