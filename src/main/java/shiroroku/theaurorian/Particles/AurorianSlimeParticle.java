package shiroroku.theaurorian.Particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.BreakingItemParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.item.ItemStack;
import shiroroku.theaurorian.Registry.ItemRegistry;

public class AurorianSlimeParticle extends BreakingItemParticle {

    protected AurorianSlimeParticle(ClientLevel level, double x, double y, double z, ItemStack stack) {
        super(level, x, y, z, stack);
    }

    public record Provider(SpriteSet sprites) implements ParticleProvider<SimpleParticleType> {
        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed) {
            return new AurorianSlimeParticle(level, x, y, z,
                    new ItemStack(ItemRegistry.aurorian_slime_ball.get()));
        }
    }
}
