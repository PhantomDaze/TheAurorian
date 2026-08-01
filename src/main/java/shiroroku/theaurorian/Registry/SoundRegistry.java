package shiroroku.theaurorian.Registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import shiroroku.theaurorian.TheAurorian;

public class SoundRegistry {

    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, TheAurorian.MODID);

    public static final RegistryObject<SoundEvent> MUSIC = SOUNDS.register("music", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(TheAurorian.MODID, "music")));
    public static final RegistryObject<SoundEvent> WEEPING_WILLOW_BELL = SOUNDS.register("weepingwillowbell", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(TheAurorian.MODID, "weepingwillowbell")));

    /**
     * Aurorian background music (min 1200t / max 3600t delay, matching upstream).
     */
    public static Music getMusic() {
        return new Music(MUSIC.getHolder().orElseThrow(), 1200, 3600, false);
    }

    public static void register(IEventBus bus) {
        SOUNDS.register(bus);
    }
}
