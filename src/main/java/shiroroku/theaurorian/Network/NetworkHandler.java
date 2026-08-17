package shiroroku.theaurorian.Network;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/**
 * 网络通道注册。1.21.1 NeoForge 用 {@link net.minecraft.network.protocol.common.custom.CustomPacketPayload}
 * + {@link PayloadRegistrar}。
 */
@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class NetworkHandler {

    private NetworkHandler() {
    }

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        // 服务端 → 客户端：地牢坐标，用于客户端生成方向粒子
        registrar.playToClient(
                LocatorDungeonDirectionPayload.TYPE,
                LocatorDungeonDirectionPayload.STREAM_CODEC,
                LocatorClientPayloadHandler::handleDirection
        );
    }
}
