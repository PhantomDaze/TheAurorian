package shiroroku.theaurorian;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import shiroroku.theaurorian.Config.ClientConfig;
import shiroroku.theaurorian.Config.CommonConfig;
import shiroroku.theaurorian.Registry.*;

@Mod(TheAurorian.MODID)
public class TheAurorian {
    public static final String MODID = "theaurorian";
    public static final Logger LOGGER = LogManager.getLogger();

    public static final ResourceKey<Level> the_aurorian = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(TheAurorian.MODID, "the_aurorian"));

    public TheAurorian() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CommonConfig.config);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfig.config);
        BlockRegistry.register(bus);
        ItemRegistry.register(bus);
        BlockEntityRegistry.BLOCK_ENTITIES.register(bus);
        MenuRegistry.MENUS.register(bus);
        RecipeRegistry.TYPES.register(bus);
        RecipeRegistry.SERIALIZERS.register(bus);
        EntityRegistry.ENTITIES.register(bus);
        EnchantRegistry.ENCHANTMENTS.register(bus);
        POIRegistry.POIS.register(bus);
        StructureRegistry.register(bus);
        FeatureRegistry.register(bus);
        SoundRegistry.register(bus);
        ParticleRegistry.register(bus);
        CreativeTabRegistry.register(bus);
    }

}
