package shiroroku.theaurorian.Items;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeTier;
import shiroroku.theaurorian.Registry.BlockRegistry;

/**
 * Moon Queen boss pickaxe. Right-clicking a dungeon block (runestone, darkstone
 * or moon temple families) breaks it instantly and costs durability.
 */
public class QueensChipperItem extends BaseAurorianPickaxe {

    public QueensChipperItem(ForgeTier pTier, int pAttackDamageModifier, float pAttackSpeedModifier, Properties pProperties) {
        super(pTier, pAttackDamageModifier, pAttackSpeedModifier, pProperties);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        Level level = pContext.getLevel();
        BlockPos pos = pContext.getClickedPos();
        if (isDungeonBlock(level.getBlockState(pos))) {
            level.playSound(pContext.getPlayer(), pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 0.8F, 0.7F);
            if (!level.isClientSide) {
                level.destroyBlock(pos, true);
                pContext.getItemInHand().hurtAndBreak(1, pContext.getPlayer(), (p) -> p.broadcastBreakEvent(pContext.getHand()));
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    private static boolean isDungeonBlock(BlockState state) {
        return state.is(BlockRegistry.runestone.get())
                || state.is(BlockRegistry.runestone_bars.get())
                || state.is(BlockRegistry.runestone_gate.get())
                || state.is(BlockRegistry.runestone_gate_keyhole.get())
                || state.is(BlockRegistry.runestone_gate_loot_keyhole.get())
                || state.is(BlockRegistry.runestone_lamp.get())
                || state.is(BlockRegistry.runestone_smooth.get())
                || state.is(BlockRegistry.runestone_stairs.get())
                || state.is(BlockRegistry.darkstone.get())
                || state.is(BlockRegistry.darkstone_chipped.get())
                || state.is(BlockRegistry.darkstone_gate.get())
                || state.is(BlockRegistry.darkstone_gate_keyhole.get())
                || state.is(BlockRegistry.darkstone_lamp.get())
                || state.is(BlockRegistry.darkstone_pillar.get())
                || state.is(BlockRegistry.darkstone_stairs.get())
                || state.is(BlockRegistry.moon_temple_bricks.get())
                || state.is(BlockRegistry.moon_temple_bars.get())
                || state.is(BlockRegistry.moon_temple_bricks_smooth.get())
                || state.is(BlockRegistry.moon_temple_gate.get())
                || state.is(BlockRegistry.moon_temple_gate_keyhole.get())
                || state.is(BlockRegistry.moon_temple_interior_gate.get())
                || state.is(BlockRegistry.moon_temple_interior_gate_keyhole.get())
                || state.is(BlockRegistry.moon_temple_lamp.get())
                || state.is(BlockRegistry.moon_temple_stairs.get());
    }
}
