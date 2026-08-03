package shiroroku.theaurorian;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import shiroroku.theaurorian.Renderers.AurorianiteShovelBlockOutline;
import shiroroku.theaurorian.Renderers.UmbraPickaxeBlockOutline;

@EventBusSubscriber(modid = TheAurorian.MODID, value = Dist.CLIENT)
public class EventsForgeClient {

    @SubscribeEvent
    public static void onRenderOutline(RenderHighlightEvent.Block event) {
        UmbraPickaxeBlockOutline.onRenderOutline(event);
        AurorianiteShovelBlockOutline.onRenderOutline(event);
    }

}
