package shiroroku.theaurorian.Compat.TinkersConstruct;

import net.minecraftforge.eventbus.api.IEventBus;
import shiroroku.theaurorian.Config.CommonConfig;
import shiroroku.theaurorian.TheAurorian;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.ModifierManager.ModifierRegistrationEvent;

/**
 * Entry point for the Tinkers' Construct integration. {@link #register(IEventBus)} is called
 * conditionally from {@link TheAurorian} only when TConstruct is loaded.
 */
public class TinkersCompat {

    private TinkersCompat() {
    }

    public static void register(IEventBus bus) {
        bus.addListener((ModifierRegistrationEvent event) -> {
            if (!CommonConfig.enable_tconstruct_compat.get()) {
                return;
            }
            event.registerStatic(new ModifierId(TheAurorian.MODID, "moonlit"), new MoonlitModifier());
            event.registerStatic(new ModifierId(TheAurorian.MODID, "aurorian_empowered"), new AurorianEmpoweredModifier());
        });
    }
}
