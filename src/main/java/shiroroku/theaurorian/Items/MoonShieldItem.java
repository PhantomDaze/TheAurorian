package shiroroku.theaurorian.Items;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;

/**
 * Moon Queen boss shield. Charging it for a moment launches the player forward,
 * and while on cooldown it knocks nearby entities into the air.
 */
public class MoonShieldItem extends BaseAurorianShield {

    public MoonShieldItem(Tier pTier, Properties pProperties) {
        super(pTier, pProperties.durability(512));
    }

    @Override
    public int getUseDuration(ItemStack pStack) {
        return 50;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int itemSlot, boolean isSelected) {
        if (entity instanceof Player player) {
            if (player.getMainHandItem() == stack || player.getOffhandItem() == stack) {
                if (level.isClientSide) {
                    if (!player.isShiftKeyDown() && player.isOnGround() && player.tickCount % 4 == 0 && stack == player.getUseItem()) {
                        double motionX = player.getRandom().nextGaussian() * 0.02D;
                        double motionY = player.getRandom().nextGaussian() * 0.1D;
                        double motionZ = player.getRandom().nextGaussian() * 0.02D;
                        level.addParticle(ParticleTypes.ANGRY_VILLAGER, player.getX() + player.getRandom().nextFloat() - 0.5, player.getY() + player.getRandom().nextFloat() * player.getBbHeight(), player.getZ() + player.getRandom().nextFloat() - 0.5, motionX, motionY, motionZ);
                    }
                }

                if (player.getCooldowns().isOnCooldown(stack.getItem()) && player.getDeltaMovement().x() <= 1.5 && player.getDeltaMovement().z() <= 1.5) {
                    List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, new AABB(player.getX() - 2.5, player.getY() - 1, player.getZ() - 2.5, player.getX() + 2.5, player.getY() + 1, player.getZ() + 2.5));
                    for (LivingEntity e : entities) {
                        if (!(e instanceof EnderDragon) && !(e instanceof WitherBoss) && e != player) {
                            if (e instanceof Player && !player.canHarmPlayer((Player) e)) {
                                return;
                            }
                            e.setDeltaMovement(e.getDeltaMovement().add(0, 1, 0));
                            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ANVIL_PLACE, SoundSource.PLAYERS, 1F, 1.5F);
                            player.getCooldowns().addCooldown(stack.getItem(), 0);
                            return;
                        }
                    }
                }
            }
        }
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (entity instanceof Player player) {
            if (!player.isShiftKeyDown() && player.isOnGround()) {
                double velx = Mth.sin(-player.getYRot() / 180.0F * (float) Math.PI) * Mth.cos(player.getXRot() / 180.0F * (float) Math.PI);
                double velz = Mth.cos(player.getYRot() / 180.0F * (float) Math.PI) * Mth.cos(player.getXRot() / 180.0F * (float) Math.PI);
                int distance = 4;
                player.push(velx * distance, 0, velz * distance);
                player.getCooldowns().addCooldown(stack.getItem(), 10);
            }
        }
        return stack;
    }
}
