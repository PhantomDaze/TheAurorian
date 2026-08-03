package shiroroku.theaurorian.Blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.block.CropBlock;
import net.neoforged.neoforge.common.util.TriState;
import shiroroku.theaurorian.Registry.BlockRegistry;

/**
 * Aurorian farmland. Mirrors vanilla farmland behaviour but reverts to
 * {@link BlockRegistry#aurorian_dirt aurorian_dirt} instead of vanilla dirt.
 */
public class AurorianFarmTile extends Block {

    public static final IntegerProperty MOISTURE = BlockStateProperties.MOISTURE;
    protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 15.0D, 16.0D);

    public AurorianFarmTile(Properties pProperties) {
        super(pProperties.randomTicks());
        this.registerDefaultState(this.stateDefinition.any().setValue(MOISTURE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(MOISTURE);
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean useShapeForLightOcclusion(BlockState pState) {
        return true;
    }

    @Override
    public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        int moisture = pState.getValue(MOISTURE);
        if (!isNearWater(pLevel, pPos) && !pLevel.isRainingAt(pPos.above())) {
            if (moisture > 0) {
                pLevel.setBlock(pPos, pState.setValue(MOISTURE, moisture - 1), 2);
            } else if (!hasCrops(pLevel, pPos)) {
                turnToDirt(pState, pLevel, pPos);
            }
        } else if (moisture < 7) {
            pLevel.setBlock(pPos, pState.setValue(MOISTURE, 7), 2);
        }
    }

    @Override
    public void fallOn(Level pLevel, BlockState pState, BlockPos pPos, Entity pEntity, float pFallDistance) {
        if (pEntity.isSuppressingBounce()) {
            super.fallOn(pLevel, pState, pPos, pEntity, pFallDistance);
        } else {
            pEntity.causeFallDamage(pFallDistance, 1.0F, pEntity.damageSources().fall());
        }
    }

    private static boolean hasCrops(LevelReader pLevel, BlockPos pPos) {
        BlockState state = pLevel.getBlockState(pPos.above());
        return state.getBlock() instanceof net.minecraft.world.level.block.CropBlock;
    }

    private static void turnToDirt(BlockState pState, Level pLevel, BlockPos pPos) {
        pLevel.setBlock(pPos, pushEntitiesUp(pState, BlockRegistry.aurorian_dirt.get().defaultBlockState(), pLevel, pPos), 3);
    }

    private static boolean isNearWater(LevelReader pLevel, BlockPos pPos) {
        for (BlockPos pos : BlockPos.betweenClosed(pPos.offset(-4, 0, -4), pPos.offset(4, 1, 4))) {
            if (pLevel.getFluidState(pos).is(Fluids.WATER)) {
                return true;
            }
        }
        return net.neoforged.neoforge.common.FarmlandWaterManager.hasBlockWaterTicket(pLevel, pPos);
    }

    @Override
    public TriState canSustainPlant(BlockState state, BlockGetter level, BlockPos soilPosition, Direction facing, BlockState plant) {
        if (plant.getBlock() instanceof CropBlock || plant.is(net.minecraft.tags.BlockTags.MAINTAINS_FARMLAND)) {
            return TriState.TRUE;
        }
        return TriState.DEFAULT;
    }

}
