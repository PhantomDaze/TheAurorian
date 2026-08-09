package shiroroku.theaurorian;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import shiroroku.theaurorian.Config.ClientConfig;
import shiroroku.theaurorian.Config.CommonConfig;
import shiroroku.theaurorian.Registry.*;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@Mod(TheAurorian.MODID)
public class TheAurorian {
    public static final String MODID = "theaurorian";
    public static final Logger LOGGER = LoggerFactory.getLogger(TheAurorian.class);

    public static final ResourceKey<Level> the_aurorian = ResourceKey.create(
            Registries.DIMENSION,
            ResourceLocation.fromNamespaceAndPath(TheAurorian.MODID, "the_aurorian")
    );

    public TheAurorian(IEventBus bus, ModContainer container) {
        container.registerConfig(ModConfig.Type.COMMON, CommonConfig.config);
        container.registerConfig(ModConfig.Type.CLIENT, ClientConfig.config);
        BlockRegistry.register(bus);
        ItemRegistry.register(bus);
        FluidRegistry.register(bus);
        MaterialTiers.ARMOR_MATERIALS.register(bus);
        BlockEntityRegistry.BLOCK_ENTITIES.register(bus);
        MenuRegistry.MENUS.register(bus);
        RecipeRegistry.TYPES.register(bus);
        RecipeRegistry.SERIALIZERS.register(bus);
        EntityRegistry.ENTITIES.register(bus);
        // Enchantments are data-driven (datapack json); no DeferredRegister needed here
        // EnchantRegistry logic uses ResourceKeys + events
        POIRegistry.POIS.register(bus);
        StructureRegistry.register(bus);
        FeatureRegistry.register(bus);
        SoundRegistry.register(bus);
        ParticleRegistry.register(bus);
        CreativeTabRegistry.register(bus);
        bus.addListener(RegisterCapabilitiesEvent.class, CapabilityRegistry::registerCapabilities);

        // Tinkers' Construct integration (parked until a 1.21.1 build of TConstruct exists).
        // The compat package is excluded from the default build, so register it via reflection
        // only when TConstruct is actually installed AND the class made it into the jar.
        if (ModList.get().isLoaded("tconstruct")) {
            try {
                Class.forName("shiroroku.theaurorian.Compat.TinkersConstruct.TinkersCompat")
                        .getMethod("register", IEventBus.class)
                        .invoke(null, bus);
            } catch (ReflectiveOperationException e) {
                LOGGER.warn("TConstruct is present but the Aurorian compat could not be loaded", e);
            }
        }
    }
}
