package shiroroku.theaurorian.Registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.fluids.FluidType;
import shiroroku.theaurorian.TheAurorian;

import java.util.function.Consumer;

/**
 * Lava-like fluid type for the molten metals used by the Tinkers' Construct smeltery.
 * Values mirror upstream's 1.12 molten fluids (density 2000, viscosity 10000, temperature 800, luminosity 10).
 */
public class MoltenFluidType extends FluidType {

    private final String name;

    public MoltenFluidType(String name) {
        super(FluidType.Properties.create()
                .density(2000)
                .viscosity(10000)
                .temperature(800)
                .lightLevel(10)
                .descriptionId("fluid." + TheAurorian.MODID + ".molten_" + name)
                .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA)
                .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA)
                .motionScale(0.0023333333333333335D)
                .canSwim(false)
                .canDrown(false)
                .pathType(BlockPathTypes.LAVA)
                .adjacentPathType(null));
        this.name = name;
    }

    @Override
    public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
        consumer.accept(new IClientFluidTypeExtensions() {
            @Override
            public ResourceLocation getStillTexture() {
                return new ResourceLocation(TheAurorian.MODID, "block/molten_" + name);
            }

            @Override
            public ResourceLocation getFlowingTexture() {
                return new ResourceLocation(TheAurorian.MODID, "block/molten_" + name + "_flow");
            }
        });
    }
}
