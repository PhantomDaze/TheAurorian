package shiroroku.theaurorian.Registry;

import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import shiroroku.theaurorian.Blocks.AbstractInventoryBlockEntity;
import shiroroku.theaurorian.TheAurorian;

public final class CapabilityRegistry {

    private CapabilityRegistry() {
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                BlockEntityRegistry.moonlight_forge.get(),
                (be, side) -> be instanceof AbstractInventoryBlockEntity inv ? inv.getItemHandler() : null
        );
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                BlockEntityRegistry.scrapper.get(),
                (be, side) -> be instanceof AbstractInventoryBlockEntity inv ? inv.getItemHandler() : null
        );
    }
}
