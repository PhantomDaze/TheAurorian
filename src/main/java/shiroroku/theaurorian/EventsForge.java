package shiroroku.theaurorian;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import shiroroku.theaurorian.DataGen.DataGenItemsTags;
import shiroroku.theaurorian.Demo.VisibleDemoCommand;
import shiroroku.theaurorian.Items.MirrorOfGuidance.MirrorDataLoader;
import shiroroku.theaurorian.Items.SlimeBoots.SlimeBootsItem;
import shiroroku.theaurorian.Items.Spectral.Spectral;
import shiroroku.theaurorian.Registry.EnchantRegistry;

import java.util.stream.StreamSupport;

@EventBusSubscriber(modid = TheAurorian.MODID)
public class EventsForge {

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent.Pre event) {
        handleLightningDamage(event);
        Spectral.handleOnDamage(event);
    }

    private static void handleLightningDamage(LivingDamageEvent.Pre event) {
        if (event.getEntity().level().isClientSide()) return;
        LivingEntity target = event.getEntity();
        if (!(event.getSource().getEntity() instanceof LivingEntity attacker)) return;

        // get holders (data driven enchants)
        var enchantReg = ((ServerLevel) target.level()).registryAccess().registryOrThrow(Registries.ENCHANTMENT);
        Holder<Enchantment> lightningHolder = enchantReg.getHolderOrThrow(EnchantRegistry.LIGHTNING);
        Holder<Enchantment> resistHolder = enchantReg.getHolderOrThrow(EnchantRegistry.LIGHTNING_RESISTANCE);

        int metal_armor_pieces = (int) StreamSupport.stream(target.getArmorSlots().spliterator(), false).filter(
                stack -> !stack.isEmpty()
                        && !stack.is(DataGenItemsTags.LIGHTNING_IMMUNE)
                        && stack.getEnchantmentLevel(resistHolder) <= 0
        ).count();

        int lightning_level = attacker.getMainHandItem().getEnchantmentLevel(lightningHolder);
        if (lightning_level > 0) {
            // l1 = x1.5
            // l2 = x2
            // l3 = x2.5
            // l4 = x3
            float multiplier = 1 + 0.5f * Math.min(metal_armor_pieces, lightning_level);
            event.setNewDamage( event.getNewDamage() * multiplier );  // or use setAmount if available
        }
    }

    @SubscribeEvent
    public static void onLivingFall(LivingFallEvent event) {
        SlimeBootsItem.handleFallEvent(event);
    }

    @SubscribeEvent
    public static void onLivingJump(LivingEvent.LivingJumpEvent event) {
        SlimeBootsItem.handleJumpEvent(event);
    }

    @SubscribeEvent
    public static void onAddReloadListener(AddReloadListenerEvent event) {
        event.addListener(new MirrorDataLoader());
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        VisibleDemoCommand.register(event.getDispatcher());
    }
}
