package shiroroku.theaurorian.Blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import shiroroku.theaurorian.Registry.BlockRegistry;

import java.util.function.Supplier;

/**
 * Aurorian crops. Grow on the Aurorian Farm Tile or vanilla farmland, need sky
 * access (upstream canBlockStay requires canSeeSky) and drop their seed.
 */
public class AurorianCropBlock extends CropBlock {

    private final Supplier<Item> seed;

    public AurorianCropBlock(Supplier<Item> seed, Properties pProperties) {
        super(pProperties);
        this.seed = seed;
    }

    @Override
    protected boolean mayPlaceOn(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return pState.is(BlockRegistry.aurorian_farm_tile.get()) || pState.is(Blocks.FARMLAND);
    }

    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        return pLevel.canSeeSky(pPos) && (pLevel.getBlockState(pPos.below()).is(BlockRegistry.aurorian_farm_tile.get()) || pLevel.getBlockState(pPos.below()).is(Blocks.FARMLAND));
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return seed.get();
    }
}
