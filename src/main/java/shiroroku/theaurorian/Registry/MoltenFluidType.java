package shiroroku.theaurorian.Registry;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.pathfinder.PathType;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidType;
import shiroroku.theaurorian.TheAurorian;

/**
 * Lava-like fluid type for the molten metals used by the Tinkers' Construct smeltery.
 * Values mirror upstream's 1.12 molten fluids (density 2000, viscosity 10000, temperature 800, luminosity 10).
 *
 * <p>Client textures are registered separately via {@code RegisterClientExtensionsEvent}
 * (see {@link shiroroku.theaurorian.EventsClient#onRegisterClientExtensions}) because
 * {@code FluidType#initializeClient} is deprecated for removal in NeoForge 1.21.1.</p>
 */
public class MoltenFluidType extends FluidType {

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
                .pathType(PathType.LAVA)
                .adjacentPathType(null));
    }
}