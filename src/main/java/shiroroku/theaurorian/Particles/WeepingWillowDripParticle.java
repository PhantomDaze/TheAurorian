package shiroroku.theaurorian.Particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.sounds.SoundSource;
import shiroroku.theaurorian.Registry.SoundRegistry;

/**
 * Weeping willow drip: hangs under the leaves for a moment, then falls and on
 * landing spawns a small cloud and plays the weeping willow bell. Ported from
 * the 1.12 {@code WeepingWillowDripParticle}.
 */
public class WeepingWillowDripParticle extends TextureSheetParticle {

    private int bobTimer;
    private final SpriteSet sprites;

    protected WeepingWillowDripParticle(ClientLevel level, double x, double y, double z, SpriteSet sprites) {
        super(level, x, y, z);
        this.sprites = sprites;
        this.gravity = 0.015F;
        this.bobTimer = 80;
        this.lifetime = 300;
        this.quadSize = 0.5F + this.random.nextFloat() * 0.25F;
        this.setColor(0.05F, 0.1F, 0.15F);
        this.setSprite(sprites.get(0, 0));
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        this.yd -= this.gravity;
        if (this.bobTimer-- > 0) {
            this.xd *= 0.02D;
            this.yd *= 0.02D;
            this.zd *= 0.02D;
            this.setSprite(sprites.get(0, 0));
        } else {
            this.setSprite(sprites.get(1, 1));
        }

        this.move(this.xd, this.yd, this.zd);
        this.xd *= 0.98D;
        this.yd *= 0.98D;
        this.zd *= 0.98D;

        if (this.lifetime-- <= 0) {
            this.remove();
        }
        if (this.onGround) {
            this.remove();
            this.level.addParticle(ParticleTypes.CLOUD, this.x, this.y, this.z, 0.0D, 0.0D, 0.0D);
            this.level.playLocalSound(this.x, this.y, this.z, SoundRegistry.WEEPING_WILLOW_BELL.get(), SoundSource.BLOCKS, 0.015F, this.random.nextFloat() * 0.3F + 0.7F, false);
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public record Provider(SpriteSet sprites) implements ParticleProvider<SimpleParticleType> {

        @Override
        public Particle createParticle(SimpleParticleType pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            return new WeepingWillowDripParticle(pLevel, pX, pY, pZ, this.sprites);
        }
    }
}
