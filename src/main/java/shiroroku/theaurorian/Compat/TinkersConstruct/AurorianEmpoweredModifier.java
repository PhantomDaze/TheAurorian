package shiroroku.theaurorian.Compat.TinkersConstruct;

import net.minecraft.core.Direction;
import net.minecraftforge.event.entity.player.PlayerEvent;
import shiroroku.theaurorian.TheAurorian;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.mining.BreakSpeedContext;
import slimeknights.tconstruct.library.modifiers.hook.mining.BreakSpeedModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

/**
 * Port of the 1.12 "Aurorian Empowered" Tinkers trait: mining speed is boosted
 * while inside the Aurorian dimension.
 */
public class AurorianEmpoweredModifier extends Modifier implements BreakSpeedModifierHook {

    private static final float SPEED_MULTIPLIER = 1.5f;

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.BREAK_SPEED);
    }

    @Override
    public float modifyBreakSpeed(IToolStackView tool, ModifierEntry modifier, BreakSpeedContext context, float speed) {
        if (context.player() != null && context.player().level().dimension() == TheAurorian.the_aurorian) {
            return speed * SPEED_MULTIPLIER;
        }
        return speed;
    }

    @Override
    public void onBreakSpeed(IToolStackView tool, ModifierEntry modifier, PlayerEvent.BreakSpeed event, Direction sideHit, boolean isEffective, float miningSpeedModifier) {
        // Modern hook path goes through modifyBreakSpeed; nothing to do here.
    }
}
