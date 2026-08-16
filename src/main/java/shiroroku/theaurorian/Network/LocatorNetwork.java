package shiroroku.theaurorian.Network;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import shiroroku.theaurorian.Items.DungeonLocatorClient;
import shiroroku.theaurorian.TheAurorian;

import java.util.function.Supplier;

/**
 * 最小网络通道：服务端检测到地牢后，把地牢坐标发给使用的客户端，
 * 由客户端生成方向粒子。定位器原本在服务端调用 addParticle（死代码），
 * 粒子永远不显示，经此通道修正。
 */
public class LocatorNetwork {

    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(TheAurorian.MODID, "locator"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private LocatorNetwork() {
    }

    public static void register() {
        CHANNEL.registerMessage(0, DungeonDirectionMessage.class,
                DungeonDirectionMessage::encode,
                DungeonDirectionMessage::decode,
                DungeonDirectionMessage::handle);
    }

    /** 服务端 -> 客户端：地牢坐标。 */
    public record DungeonDirectionMessage(BlockPos dungeon) {

        public static void encode(DungeonDirectionMessage msg, FriendlyByteBuf buf) {
            buf.writeBlockPos(msg.dungeon);
        }

        public static DungeonDirectionMessage decode(FriendlyByteBuf buf) {
            return new DungeonDirectionMessage(buf.readBlockPos());
        }

        public static void handle(DungeonDirectionMessage msg, Supplier<NetworkEvent.Context> ctx) {
            NetworkEvent.Context context = ctx.get();
            context.enqueueWork(() -> DungeonLocatorClient.spawnDirectionParticles(msg.dungeon));
            context.setPacketHandled(true);
        }
    }
}
