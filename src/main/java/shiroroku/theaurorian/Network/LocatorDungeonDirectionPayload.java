package shiroroku.theaurorian.Network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import shiroroku.theaurorian.TheAurorian;

/**
 * 服务端 → 客户端：定位器找到的地牢坐标。
 * 定位器原本在服务端调用 addParticle（死代码），粒子永远不显示，
 * 经此 payload 在客户端生成方向粒子。
 */
public record LocatorDungeonDirectionPayload(BlockPos dungeon) implements CustomPacketPayload {

    public static final Type<LocatorDungeonDirectionPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(TheAurorian.MODID, "locator_direction"));

    public static final StreamCodec<RegistryFriendlyByteBuf, LocatorDungeonDirectionPayload> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, LocatorDungeonDirectionPayload::dungeon,
                    LocatorDungeonDirectionPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
