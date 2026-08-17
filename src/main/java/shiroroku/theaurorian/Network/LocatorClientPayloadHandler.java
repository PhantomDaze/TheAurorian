package shiroroku.theaurorian.Network;

import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * 客户端处理定位器方向 payload。
 *
 * <p>此类被 {@link NetworkHandler} 以方法引用形式注册为 {@code playToClient} handler。
 * NeoForge 的 mod jar 统一包含客户端类，{@code playToClient} 的 handler 只在客户端
 * 被调用，专用服务端不会执行 {@link #handleDirection} 的方法体，因此引用
 * {@link DungeonLocatorClient}（带 {@code @OnlyIn(Dist.CLIENT)}）是安全的。
 */
public final class LocatorClientPayloadHandler {

    private LocatorClientPayloadHandler() {
    }

    public static void handleDirection(final LocatorDungeonDirectionPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> DungeonLocatorClient.spawnDirectionParticles(payload.dungeon()));
    }
}
