package shiroroku.theaurorian.Blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Portal;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import shiroroku.theaurorian.Portal.AurorianPortalTeleporter;
import shiroroku.theaurorian.TheAurorian;

/**
 * Aurorian dimension portal. Uses 1.21 {@link Portal} + {@link DimensionTransition}
 * (replaces Forge ITeleporter / changeDimension overload).
 */
public class AurorianPortal extends Block implements Portal {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    protected static final VoxelShape X_AXIS_AABB = Block.box(0.0D, 0.0D, 6.0D, 16.0D, 16.0D, 10.0D);
    protected static final VoxelShape Z_AXIS_AABB = Block.box(6.0D, 0.0D, 0.0D, 10.0D, 16.0D, 16.0D);

    public AurorianPortal(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (entity.canUsePortal(false)) {
            entity.setAsInsidePortal(this, pos);
        }
    }

    @Override
    public int getPortalTransitionTime(ServerLevel level, Entity entity) {
        return 0; // instant like prior behaviour
    }

    @Nullable
    @Override
    public DimensionTransition getPortalDestination(ServerLevel level, Entity entity, BlockPos pos) {
        ResourceKey<Level> destinationKey = level.dimension() == TheAurorian.the_aurorian
                ? Level.OVERWORLD
                : TheAurorian.the_aurorian;
        ServerLevel destination = level.getServer().getLevel(destinationKey);
        if (destination == null) {
            return null;
        }
        return AurorianPortalTeleporter.createTransition(destination, entity, pos);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextInt(100) == 0) {
            level.playLocalSound(
                    pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D,
                    SoundEvents.PORTAL_AMBIENT, SoundSource.BLOCKS,
                    0.5F, random.nextFloat() * 0.4F + 0.8F, false
            );
        }
        for (int i = 0; i < 2; ++i) {
            double d0 = pos.getX() + random.nextDouble();
            double d1 = pos.getY() + random.nextDouble();
            double d2 = pos.getZ() + random.nextDouble();
            double d3 = (random.nextFloat() - 0.5D) * 0.5D;
            double d4 = (random.nextFloat() - 0.5D) * 0.5D;
            double d5 = (random.nextFloat() - 0.5D) * 0.5D;
            int j = random.nextInt(2) * 2 - 1;
            if (!level.getBlockState(pos.west()).is(this) && !level.getBlockState(pos.east()).is(this)) {
                d0 = pos.getX() + 0.5D + 0.25D * j;
                d3 = random.nextFloat() * 2.0F * j;
            } else {
                d2 = pos.getZ() + 0.5D + 0.25D * j;
                d5 = random.nextFloat() * 2.0F * j;
            }
            level.addParticle(ParticleTypes.WAX_OFF, d0, d1, d2, d3, d4, d5);
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> definition) {
        definition.add(FACING);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case WEST, EAST -> Z_AXIS_AABB;
            default -> X_AXIS_AABB;
        };
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos currentPos, BlockPos neighborPos) {
        // Frame integrity is enforced by shape placement; keep portal if neighbour still portal or frame-ish.
        return super.updateShape(state, direction, neighborState, level, currentPos, neighborPos);
    }
}
