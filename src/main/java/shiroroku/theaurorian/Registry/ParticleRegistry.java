package shiroroku.theaurorian.Registry;

import net.minecraft.core.registries.Registries;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;
import shiroroku.theaurorian.TheAurorian;

public class ParticleRegistry {

    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(Registries.PARTICLE_TYPE, TheAurorian.MODID);

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> WEEPING_WILLOW_DRIP = PARTICLES.register("weeping_willow_drip", () -> new SimpleParticleType(false));

    public static void register(IEventBus bus) {
        PARTICLES.register(bus);
    }
}
