package shiroroku.theaurorian.Registry;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import shiroroku.theaurorian.TheAurorian;

@Mod.EventBusSubscriber(modid = TheAurorian.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CreativeTabRegistry {

    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TheAurorian.MODID);

    public static final RegistryObject<CreativeModeTab> MAIN = TABS.register("main", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup." + TheAurorian.MODID))
            .icon(() -> new ItemStack(BlockRegistry.silentwood_sapling.get()))
            .displayItems((params, output) -> {
                ItemRegistry.ITEMS.getEntries().forEach(ro -> output.accept(ro.get()));
                ItemRegistry.ITEMS_GEN.getEntries().forEach(ro -> output.accept(ro.get()));
                ItemRegistry.ITEMS_GEN_HANDHELD.getEntries().forEach(ro -> output.accept(ro.get()));
                ItemRegistry.ITEMS_GEN_SHIELD.getEntries().forEach(ro -> output.accept(ro.get()));
                ItemRegistry.ITEMS_GEN_KEY.getEntries().forEach(ro -> output.accept(ro.get()));
                ItemRegistry.ITEMS_GEN_TEA.getEntries().forEach(ro -> output.accept(ro.get()));
                ItemRegistry.ITEMS_SPAWN_EGGS.getEntries().forEach(ro -> output.accept(ro.get()));
            })
            .build());

    public static void register(IEventBus bus) {
        TABS.register(bus);
    }

    // Kept for potential future filtering; main tab already fills via displayItems.
    @SubscribeEvent
    public static void onBuildContents(BuildCreativeModeTabContentsEvent event) {
        // no-op: items are added in tab builder displayItems
    }
}
