package shiroroku.theaurorian.Registry;

import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import shiroroku.theaurorian.TheAurorian;

/**
 * Molten metals used by the Tinkers' Construct smeltery. Registered unconditionally;
 * melting/casting recipes only apply when TConstruct is loaded.
 */
public class FluidRegistry {

    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, TheAurorian.MODID);
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, TheAurorian.MODID);

    // Molten Cerulean
    public static final RegistryObject<FluidType> molten_cerulean_type = FLUID_TYPES.register("molten_cerulean", () -> new MoltenFluidType("cerulean"));
    private static ForgeFlowingFluid.Properties molten_cerulean_properties;
    public static final RegistryObject<FlowingFluid> molten_cerulean = FLUIDS.register("molten_cerulean", () -> new ForgeFlowingFluid.Source(molten_cerulean_properties));
    public static final RegistryObject<FlowingFluid> molten_cerulean_flowing = FLUIDS.register("molten_cerulean_flowing", () -> new ForgeFlowingFluid.Flowing(molten_cerulean_properties));
    public static final RegistryObject<LiquidBlock> molten_cerulean_block = BlockRegistry.BLOCKS.register("molten_cerulean", () -> new LiquidBlock(molten_cerulean, BlockBehaviour.Properties.copy(Blocks.LAVA).noLootTable().lightLevel(state -> 10)));
    public static final RegistryObject<Item> molten_cerulean_bucket = ItemRegistry.ITEMS.register("molten_cerulean_bucket", () -> new BucketItem(molten_cerulean, new Item.Properties().stacksTo(1).tab(TheAurorian.CREATIVETAB)));

    // Molten Moonstone
    public static final RegistryObject<FluidType> molten_moonstone_type = FLUID_TYPES.register("molten_moonstone", () -> new MoltenFluidType("moonstone"));
    private static ForgeFlowingFluid.Properties molten_moonstone_properties;
    public static final RegistryObject<FlowingFluid> molten_moonstone = FLUIDS.register("molten_moonstone", () -> new ForgeFlowingFluid.Source(molten_moonstone_properties));
    public static final RegistryObject<FlowingFluid> molten_moonstone_flowing = FLUIDS.register("molten_moonstone_flowing", () -> new ForgeFlowingFluid.Flowing(molten_moonstone_properties));
    public static final RegistryObject<LiquidBlock> molten_moonstone_block = BlockRegistry.BLOCKS.register("molten_moonstone", () -> new LiquidBlock(molten_moonstone, BlockBehaviour.Properties.copy(Blocks.LAVA).noLootTable().lightLevel(state -> 10)));
    public static final RegistryObject<Item> molten_moonstone_bucket = ItemRegistry.ITEMS.register("molten_moonstone_bucket", () -> new BucketItem(molten_moonstone, new Item.Properties().stacksTo(1).tab(TheAurorian.CREATIVETAB)));

    // Molten Aurorian Steel
    public static final RegistryObject<FluidType> molten_aurorian_steel_type = FLUID_TYPES.register("molten_aurorian_steel", () -> new MoltenFluidType("aurorian_steel"));
    private static ForgeFlowingFluid.Properties molten_aurorian_steel_properties;
    public static final RegistryObject<FlowingFluid> molten_aurorian_steel = FLUIDS.register("molten_aurorian_steel", () -> new ForgeFlowingFluid.Source(molten_aurorian_steel_properties));
    public static final RegistryObject<FlowingFluid> molten_aurorian_steel_flowing = FLUIDS.register("molten_aurorian_steel_flowing", () -> new ForgeFlowingFluid.Flowing(molten_aurorian_steel_properties));
    public static final RegistryObject<LiquidBlock> molten_aurorian_steel_block = BlockRegistry.BLOCKS.register("molten_aurorian_steel", () -> new LiquidBlock(molten_aurorian_steel, BlockBehaviour.Properties.copy(Blocks.LAVA).noLootTable().lightLevel(state -> 10)));
    public static final RegistryObject<Item> molten_aurorian_steel_bucket = ItemRegistry.ITEMS.register("molten_aurorian_steel_bucket", () -> new BucketItem(molten_aurorian_steel, new Item.Properties().stacksTo(1).tab(TheAurorian.CREATIVETAB)));

    static {
        molten_cerulean_properties = new ForgeFlowingFluid.Properties(molten_cerulean_type, molten_cerulean, molten_cerulean_flowing)
                .block(molten_cerulean_block)
                .bucket(molten_cerulean_bucket)
                .explosionResistance(100f)
                .slopeFindDistance(2)
                .levelDecreasePerBlock(2);
        molten_moonstone_properties = new ForgeFlowingFluid.Properties(molten_moonstone_type, molten_moonstone, molten_moonstone_flowing)
                .block(molten_moonstone_block)
                .bucket(molten_moonstone_bucket)
                .explosionResistance(100f)
                .slopeFindDistance(2)
                .levelDecreasePerBlock(2);
        molten_aurorian_steel_properties = new ForgeFlowingFluid.Properties(molten_aurorian_steel_type, molten_aurorian_steel, molten_aurorian_steel_flowing)
                .block(molten_aurorian_steel_block)
                .bucket(molten_aurorian_steel_bucket)
                .explosionResistance(100f)
                .slopeFindDistance(2)
                .levelDecreasePerBlock(2);
    }

    public static void register(IEventBus bus) {
        FLUID_TYPES.register(bus);
        FLUIDS.register(bus);
    }
}
