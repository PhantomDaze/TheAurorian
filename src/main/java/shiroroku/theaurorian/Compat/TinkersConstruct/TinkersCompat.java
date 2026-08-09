package shiroroku.theaurorian.Compat.TinkersConstruct;

import net.neoforged.bus.api.IEventBus;
import shiroroku.theaurorian.Config.CommonConfig;
import shiroroku.theaurorian.TheAurorian;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.ModifierManager.ModifierRegistrationEvent;

/**
 * Entry point for the Tinkers' Construct integration. {@link #register(IEventBus)} is called
 * from {@link TheAurorian} (via reflection, so this package can stay out of the classpath until
 * TConstruct ships a 1.21.1 build) only when TConstruct is loaded.
 *
 * <p>The five materials are data-driven (data/theaurorian/tinkering/...); only the two custom
 * modifiers need Java registration. Armor support from the 1.12 Construct's Armory integration
 * is folded into the materials' plating/trim stats, and Moonlit also applies to armor via
 * {@link MoonlitModifier}.</p>
 *
 * <p>NOTE: Tinkers' Construct has no stable 1.21.1 release yet, so this file is written against
 * the 1.20.1 (TConstruct 3.x) API and excluded from the default build. When a 1.21.1 build of
 * TConstruct lands, set {@code tconstruct_version} in gradle.properties and adjust the imports
 * here to the new API.</p>
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