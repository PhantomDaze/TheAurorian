package shiroroku.theaurorian.Items.SlimeBoots;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import shiroroku.theaurorian.Config.CommonConfig;
import shiroroku.theaurorian.Items.BaseAurorianArmor;
import shiroroku.theaurorian.Registry.ItemRegistry;

/**
 * Aurorian slime boots. Falls over 3 blocks are absorbed into a bounce, and
 * sneaking while jumping does an extra high jump on a configurable cooldown.
 */
public class SlimeBootsItem extends BaseAurorianArmor {

    public SlimeBootsItem(ArmorMaterial pMaterial, Properties pProperties) {
        super(pMaterial, EquipmentSlot.FEET, pProperties);
    }

    public static void handleFallEvent(LivingFallEvent event) {
        if (event.getEntity() instanceof Player player) {
            for (ItemStack s : player.getArmorSlots()) {
                if (s.getItem() instanceof SlimeBootsItem) {
                    if (event.getDistance() > 3f) {
                        player.push(0, 0.75, 0);
                        if (player.level.isClientSide) {
                            player.playSound(SoundEvents.SLIME_SQUISH, 1f, 1f);
                        }
                        event.setCanceled(true);
                    }
                    return;
                }
            }
        }
    }

    public static void handleJumpEvent(LivingJumpEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (player.isShiftKeyDown() && player.isOnGround() && !player.getCooldowns().isOnCooldown(ItemRegistry.slime_boots.get())) {
                for (ItemStack s : player.getArmorSlots()) {
                    if (s.getItem() instanceof SlimeBootsItem) {
                        player.push(0, 1.25D, 0);
                        if (player.level.isClientSide) {
                            player.playSound(SoundEvents.SLIME_JUMP, 1f, 1f);
                        }
                        player.getCooldowns().addCooldown(ItemRegistry.slime_boots.get(), CommonConfig.slime_boots_cooldown.get());
                        return;
                    }
                }
            }
        }
    }
}
