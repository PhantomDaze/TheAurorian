package shiroroku.theaurorian.Blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.portal.PortalShape;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import shiroroku.theaurorian.Portal.AurorianPortalTeleporter;
import shiroroku.theaurorian.TheAurorian;

@SuppressWarnings("deprecation")
public class AurorianPortal extends Block {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    protected static final VoxelShape X_AXIS_AABB = Block.box(0.0D, 0.0D, 6.0D, 16.0D, 16.0D, 10.0D);
    protected static final VoxelShape Z_AXIS_AABB = Block.box(6.0D, 0.0D, 0.0D, 10.0D, 16.0D, 16.0D);

    public AurorianPortal(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void entityInside(BlockState pState, Level pLevel, BlockPos pPos, Entity pEntity) {
        if (!pEntity.isPassenger() && !pEntity.isVehicle() && pEntity.canChangeDimensions()) {
            if (pEntity.isOnPortalCooldown()) {
                pEntity.setPortalCooldown();
            } else {
                if (!pEntity.level().isClientSide && !pPos.equals(pEntity.portalEntrancePos)) {
                    pEntity.portalEntrancePos = pPos.immutable();
                }
                Level entityWorld = pEntity.level();
                if (entityWorld != null) {
                    MinecraftServer server = entityWorld.getServer();
                    ResourceKey<Level> destination = pEntity.level().dimension() == TheAurorian.the_aurorian ? Level.OVERWORLD : TheAurorian.the_aurorian;
                    if (server != null) {
                        ServerLevel destinationWorld = server.getLevel(destination);
                        if (destinationWorld != null && !pEntity.isPassenger()) {
                            pEntity.level().getProfiler().push("aurorian_portal");
                            pEntity.setPortalCooldown();
                            pEntity.changeDimension(destinationWorld, new AurorianPortalTeleporter(destinationWorld));
                            pEntity.level().getProfiler().pop();
                        }
                    }
                }
            }
        }
    }

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
        if (pRandom.nextInt(100) == 0) {
            pLevel.playLocalSound((double) pPos.getX() + 0.5D, (double) pPos.getY() + 0.5D, (double) pPos.getZ() + 0.5D, SoundEvents.PORTAL_AMBIENT, SoundSource.BLOCKS, 0.5F, pRandom.nextFloat() * 0.4F + 0.8F, false);
        }
        for (int i = 0; i < 2; ++i) {
            double d0 = (double) pPos.getX() + pRandom.nextDouble();
            double d1 = (double) pPos.getY() + pRandom.nextDouble();
            double d2 = (double) pPos.getZ() + pRandom.nextDouble();
            double d3 = ((double) pRandom.nextFloat() - 0.5D) * 0.5D;
            double d4 = ((double) pRandom.nextFloat() - 0.5D) * 0.5D;
            double d5 = ((double) pRandom.nextFloat() - 0.5D) * 0.5D;
            int j = pRandom.nextInt(2) * 2 - 1;
            if (!pLevel.getBlockState(pPos.west()).is(this) && !pLevel.getBlockState(pPos.east()).is(this)) {
                d0 = (double) pPos.getX() + 0.5D + 0.25D * (double) j;
                d3 = pRandom.nextFloat() * 2.0F * (float) j;
            } else {
                d2 = (double) pPos.getZ() + 0.5D + 0.25D * (double) j;
                d5 = pRandom.nextFloat() * 2.0F * (float) j;
            }
            pLevel.addParticle(ParticleTypes.WAX_OFF, d0, d1, d2, d3, d4, d5);
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
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return switch (pState.getValue(FACING)) {
            case WEST, EAST -> Z_AXIS_AABB;
            default -> X_AXIS_AABB;
        };
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pNeighborPos) {
        Direction.Axis direction$axis = pDirection.getAxis();
        Direction.Axis direction$axis1 = pState.getValue(FACING).getClockWise().getAxis();
        boolean flag = direction$axis1 != direction$axis && direction$axis.isHorizontal();
        return !flag && !pNeighborState.is(this) && !(new PortalShape(pLevel, pCurrentPos, direction$axis1)).isComplete() ? Blocks.AIR.defaultBlockState() : super.updateShape(pState, pDirection, pNeighborState, pLevel, pCurrentPos, pNeighborPos);
    }
}
