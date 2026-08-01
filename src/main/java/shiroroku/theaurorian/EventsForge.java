package shiroroku.theaurorian;

import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import shiroroku.theaurorian.Demo.VisibleDemoCommand;
import shiroroku.theaurorian.Enchantments.LightningEnchant;
import shiroroku.theaurorian.Items.MirrorOfGuidance.MirrorDataLoader;
import shiroroku.theaurorian.Items.SlimeBoots.SlimeBootsItem;
import shiroroku.theaurorian.Items.Spectral.Spectral;

@Mod.EventBusSubscriber(modid = TheAurorian.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EventsForge {

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event) {
        LightningEnchant.handleOnDamage(event);
        Spectral.handleOnDamage(event);
    }

    @SubscribeEvent
    public static void onLivingFall(LivingFallEvent event) {
        SlimeBootsItem.handleFallEvent(event);
    }

    @SubscribeEvent
    public static void onLivingJump(LivingJumpEvent event) {
        SlimeBootsItem.handleJumpEvent(event);
    }

    @SubscribeEvent
    public static void onAddReloadListener(AddReloadListenerEvent event) {
        event.addListener(new MirrorDataLoader());
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        VisibleDemoCommand.register(event.getDispatcher());
    }
}
