package shiroroku.theaurorian.Registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import shiroroku.theaurorian.TheAurorian;

/**
 * Molten metals (molten_cerulean, molten_moonstone, molten_aurorian_steel) used by the
 * Tinkers' Construct smeltery to cast the mod's alloys. Registered unconditionally;
 * the smeltery melting/casting recipes are data-driven and only apply when TConstruct is loaded.
 *
 * <p>The {@link BaseFlowingFluid.Properties} cannot be initialized inline (Java forbids
 * forward references from the lambda field initializers), so each properties field is
 * declared before the fluids and assigned in a static block after all registry objects.</p>
 */
public class FluidRegistry {

    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, TheAurorian.MODID);
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(Registries.FLUID, TheAurorian.MODID);

    // Molten Cerulean
    public static final DeferredHolder<FluidType, FluidType> molten_cerulean_type = FLUID_TYPES.register("molten_cerulean", () -> new MoltenFluidType("cerulean"));
    private static BaseFlowingFluid.Properties molten_cerulean_properties;
    public static final DeferredHolder<Fluid, FlowingFluid> molten_cerulean = FLUIDS.register("molten_cerulean", () -> new BaseFlowingFluid.Source(molten_cerulean_properties));
    public static final DeferredHolder<Fluid, FlowingFluid> molten_cerulean_flowing = FLUIDS.register("molten_cerulean_flowing", () -> new BaseFlowingFluid.Flowing(molten_cerulean_properties));
    public static final DeferredHolder<Block, LiquidBlock> molten_cerulean_block = BlockRegistry.BLOCKS.register("molten_cerulean", () -> new LiquidBlock(molten_cerulean.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.LAVA).noLootTable().lightLevel(state -> 10)));
    public static final DeferredHolder<Item, Item> molten_cerulean_bucket = ItemRegistry.ITEMS.register("molten_cerulean_bucket", () -> new BucketItem(molten_cerulean.get(), new Item.Properties().stacksTo(1)));

    // Molten Moonstone
    public static final DeferredHolder<FluidType, FluidType> molten_moonstone_type = FLUID_TYPES.register("molten_moonstone", () -> new MoltenFluidType("moonstone"));
    private static BaseFlowingFluid.Properties molten_moonstone_properties;
    public static final DeferredHolder<Fluid, FlowingFluid> molten_moonstone = FLUIDS.register("molten_moonstone", () -> new BaseFlowingFluid.Source(molten_moonstone_properties));
    public static final DeferredHolder<Fluid, FlowingFluid> molten_moonstone_flowing = FLUIDS.register("molten_moonstone_flowing", () -> new BaseFlowingFluid.Flowing(molten_moonstone_properties));
    public static final DeferredHolder<Block, LiquidBlock> molten_moonstone_block = BlockRegistry.BLOCKS.register("molten_moonstone", () -> new LiquidBlock(molten_moonstone.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.LAVA).noLootTable().lightLevel(state -> 10)));
    public static final DeferredHolder<Item, Item> molten_moonstone_bucket = ItemRegistry.ITEMS.register("molten_moonstone_bucket", () -> new BucketItem(molten_moonstone.get(), new Item.Properties().stacksTo(1)));

    // Molten Aurorian Steel
    public static final DeferredHolder<FluidType, FluidType> molten_aurorian_steel_type = FLUID_TYPES.register("molten_aurorian_steel", () -> new MoltenFluidType("aurorian_steel"));
    private static BaseFlowingFluid.Properties molten_aurorian_steel_properties;
    public static final DeferredHolder<Fluid, FlowingFluid> molten_aurorian_steel = FLUIDS.register("molten_aurorian_steel", () -> new BaseFlowingFluid.Source(molten_aurorian_steel_properties));
    public static final DeferredHolder<Fluid, FlowingFluid> molten_aurorian_steel_flowing = FLUIDS.register("molten_aurorian_steel_flowing", () -> new BaseFlowingFluid.Flowing(molten_aurorian_steel_properties));
    public static final DeferredHolder<Block, LiquidBlock> molten_aurorian_steel_block = BlockRegistry.BLOCKS.register("molten_aurorian_steel", () -> new LiquidBlock(molten_aurorian_steel.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.LAVA).noLootTable().lightLevel(state -> 10)));
    public static final DeferredHolder<Item, Item> molten_aurorian_steel_bucket = ItemRegistry.ITEMS.register("molten_aurorian_steel_bucket", () -> new BucketItem(molten_aurorian_steel.get(), new Item.Properties().stacksTo(1)));

    static {
        molten_cerulean_properties = new BaseFlowingFluid.Properties(molten_cerulean_type, molten_cerulean, molten_cerulean_flowing)
                .block(molten_cerulean_block)
                .bucket(molten_cerulean_bucket)
                .explosionResistance(100f)
                .slopeFindDistance(2)
                .levelDecreasePerBlock(2);
        molten_moonstone_properties = new BaseFlowingFluid.Properties(molten_moonstone_type, molten_moonstone, molten_moonstone_flowing)
                .block(molten_moonstone_block)
                .bucket(molten_moonstone_bucket)
                .explosionResistance(100f)
                .slopeFindDistance(2)
                .levelDecreasePerBlock(2);
        molten_aurorian_steel_properties = new BaseFlowingFluid.Properties(molten_aurorian_steel_type, molten_aurorian_steel, molten_aurorian_steel_flowing)
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