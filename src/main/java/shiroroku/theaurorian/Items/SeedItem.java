package shiroroku.theaurorian.Items;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import shiroroku.theaurorian.Registry.BlockRegistry;

import java.util.function.Supplier;

/**
 * Plantable seed. Places the crop on the Aurorian Farm Tile (upstream
 * SeedsItem behaviour).
 */
public class SeedItem extends Item {

    private final Supplier<Block> crop;

    public SeedItem(Supplier<Block> crop, Properties pProperties) {
        super(pProperties);
        this.crop = crop;
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        if (pContext.getClickedFace() != Direction.UP) {
            return InteractionResult.PASS;
        }
        BlockPos below = pContext.getClickedPos();
        BlockPos plant = below.above();
        if (!pContext.getLevel().getBlockState(below).is(BlockRegistry.aurorian_farm_tile.get()) && !pContext.getLevel().getBlockState(below).is(net.minecraft.world.level.block.Blocks.FARMLAND)) {
            return InteractionResult.PASS;
        }
        if (!pContext.getLevel().isEmptyBlock(plant)) {
            return InteractionResult.PASS;
        }
        pContext.getLevel().setBlock(plant, this.crop.get().defaultBlockState(), 11);
        ItemStack stack = pContext.getItemInHand();
        if (!pContext.getPlayer().getAbilities().instabuild) {
            stack.shrink(1);
        }
        return InteractionResult.sidedSuccess(pContext.getLevel().isClientSide);
    }
}
