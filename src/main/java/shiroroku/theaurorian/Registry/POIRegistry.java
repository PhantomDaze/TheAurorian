package shiroroku.theaurorian.Registry;

import net.minecraft.core.registries.Registries;

import com.google.common.collect.ImmutableSet;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;
import shiroroku.theaurorian.TheAurorian;

public class POIRegistry {

    public static final DeferredRegister<PoiType> POIS = DeferredRegister.create(Registries.POINT_OF_INTEREST_TYPE, TheAurorian.MODID);

    public static final DeferredHolder<PoiType, PoiType> aurorian_portal = POIS.register("aurorian_portal", () -> new PoiType(ImmutableSet.copyOf(BlockRegistry.aurorian_portal.get().getStateDefinition().getPossibleStates()), 0, 1));

}
