package shiroroku.theaurorian;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
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

    public static final ResourceKey<Level> the_aurorian = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(TheAurorian.MODID, "the_aurorian"));
    public static final CreativeModeTab CREATIVETAB = new CreativeModeTab(MODID) {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(BlockRegistry.silentwood_sapling.get());
        }
    };

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
    }

}
