package shiroroku.theaurorian.Blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import shiroroku.theaurorian.TheAurorian;

/**
 * Urn from the Aurorian ruins/dungeons. Breaks into its block loot table
 * ({@code loot_tables/blocks/urn.json}) rather than dropping itself.
 */
public class UrnBlock extends Block {

    public static final String ID = "urn";
    public static final VoxelShape AABB = Block.box(3.0D, 0.0D, 3.0D, 13.0D, 15.0D, 13.0D);

    public UrnBlock(Properties pProperties) {
        super(pProperties.noOcclusion().strength(0.5F).sound(SoundType.GLASS));
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return AABB;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        if (pState.getBlock() != pNewState.getBlock()) {
            // The block loot table handles drops on break; super just forwards to the
            // normal drop system which uses our data-driven block loot table.
            pLevel.playSound(null, pPos, SoundEvents.GLASS_BREAK, SoundSource.BLOCKS, 1.0F, 0.6F);
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
    }

    @Override
    public String getDescriptionId() {
        return "block." + TheAurorian.MODID + "." + ID;
    }
}
