package shiroroku.theaurorian.Items;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * 地牢定位器的客户端粒子。服务端通过 {@link shiroroku.theaurorian.Network.LocatorNetwork}
 * 把地牢坐标发到客户端后，在此生成指向方向云粒子。
 * 独立成类，避免在专用服务端/GameTest 类加载 {@link Minecraft}。
 */
@OnlyIn(Dist.CLIENT)
public final class DungeonLocatorClient {

    private DungeonLocatorClient() {
    }

    public static void spawnDirectionParticles(BlockPos dungeon) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        LocalPlayer player = minecraft.player;
        if (level == null || player == null) {
            return;
        }

        double lookx = 0.25D + -Mth.sin((float) Math.toRadians(player.getYHeadRot())) * Mth.cos((float) Math.toRadians(player.getXRot()));
        double looky = 0.25D + -Mth.sin((float) Math.toRadians(player.getXRot()));
        double lookz = 0.25D + Mth.cos((float) Math.toRadians(player.getYHeadRot())) * Mth.cos((float) Math.toRadians(player.getXRot()));

        double y = player.getY() + 1 + level.getRandom().nextDouble() * 6.0D / 16.0D;
        double speed = 0.01D;
        double targetx = player.getX() - dungeon.getX();
        double targetz = player.getZ() - dungeon.getZ();
        double partx = Mth.clamp(targetx * -speed, -0.5D, 0.5D);
        double partz = Mth.clamp(targetz * -speed, -0.5D, 0.5D);
        double randx = level.getRandom().nextDouble() / 8;
        double randz = level.getRandom().nextDouble() / 8;

        for (int i = 0; i < 2; i++) {
            level.addParticle(ParticleTypes.CLOUD, player.getX() + lookx, y + looky, player.getZ() + lookz, partx + randx, 0.25D, partz + randz);
        }
    }
}
