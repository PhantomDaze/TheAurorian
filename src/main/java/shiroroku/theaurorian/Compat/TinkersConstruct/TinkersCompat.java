package shiroroku.theaurorian.Compat.TinkersConstruct;

import net.minecraftforge.eventbus.api.IEventBus;
import shiroroku.theaurorian.Config.CommonConfig;
import shiroroku.theaurorian.TheAurorian;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.ModifierManager.ModifierRegistrationEvent;

/**
 * Entry point for the Tinkers' Construct integration. {@link #register(IEventBus)} is called
 * conditionally from {@link TheAurorian} only when TConstruct is loaded, so this class (and
 * everything it references) is never touched otherwise.
 *
 * <p>The five materials are data-driven (data/theaurorian/tinkering/...); only the two custom
 * modifiers need Java registration. Armor support from the 1.12 Construct's Armory integration
 * is folded into the materials' plating/trim stats, and Moonlit also applies to armor via
 * {@link MoonlitModifier}.</p>
 *
 * <p>The {@link CommonConfig#enable_tconstruct_compat} toggle is consulted when the
 * {@link ModifierRegistrationEvent} fires, i.e. after configs are loaded.</p>
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
