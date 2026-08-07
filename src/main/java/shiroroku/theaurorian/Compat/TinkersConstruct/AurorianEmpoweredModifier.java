package shiroroku.theaurorian.Compat.TinkersConstruct;

import net.minecraft.core.Direction;
import net.minecraftforge.event.entity.player.PlayerEvent;
import shiroroku.theaurorian.TheAurorian;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.mining.BreakSpeedModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

/**
 * Port of the 1.12 "Aurorian Empowered" Tinkers trait: mining speed is boosted
 * while inside the Aurorian dimension. Uses TConstruct 3.8 BreakSpeed hook.
 */
public class AurorianEmpoweredModifier extends Modifier implements BreakSpeedModifierHook {

    private static final float SPEED_MULTIPLIER = 1.5f;

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.BREAK_SPEED);
    }

    @Override
    public void onBreakSpeed(IToolStackView tool, ModifierEntry modifier, PlayerEvent.BreakSpeed event, Direction sideHit, boolean isEffective, float miningSpeedModifier) {
        if (event.getEntity() != null && event.getEntity().level.dimension() == TheAurorian.the_aurorian) {
            event.setNewSpeed(event.getNewSpeed() * SPEED_MULTIPLIER);
        }
    }
}
