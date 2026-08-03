package shiroroku.theaurorian.Registry;

import net.minecraft.core.registries.Registries;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;
import shiroroku.theaurorian.TheAurorian;

public class SoundRegistry {

    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, TheAurorian.MODID);

    public static final DeferredHolder<SoundEvent, SoundEvent> MUSIC = SOUNDS.register("music", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(TheAurorian.MODID, "music")));
    public static final DeferredHolder<SoundEvent, SoundEvent> WEEPING_WILLOW_BELL = SOUNDS.register("weepingwillowbell", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(TheAurorian.MODID, "weepingwillowbell")));

    /**
     * Aurorian background music (min 1200t / max 3600t delay, matching upstream).
     */
    public static Music getMusic() {
        return new Music(MUSIC, 1200, 3600, false);
    }

    public static void register(IEventBus bus) {
        SOUNDS.register(bus);
    }
}
