package shiroroku.theaurorian.Items;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.jetbrains.annotations.Nullable;
import shiroroku.theaurorian.TheAurorian;

import java.util.List;

/**
 * Dungeon locator. Sneak-right-click cycles Runestone / Darkstone / Moon Temple;
 * right-click points toward the nearest selected structure and costs durability.
 */
public class DungeonLocatorItem extends Item {

    public static final String ID = "locator";

    private static final String[] DUNGEONS = {"Runestone", "Darkstone", "Moontemple"};

    public DungeonLocatorItem(Properties pProperties) {
        super(pProperties.stacksTo(1).durability(30).rarity(Rarity.EPIC));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
        ItemStack stack = pPlayer.getItemInHand(pHand);
        if (pPlayer.isShiftKeyDown()) {
            String current = getSelectedDungeon(stack);
            int idx = java.util.Arrays.asList(DUNGEONS).indexOf(current);
            String next = DUNGEONS[(idx + 1) % DUNGEONS.length];
            stack.getOrCreateTag().putString("dungeon", next);
            pPlayer.displayClientMessage(Component.literal("[" + next + "]"), true);
            return InteractionResultHolder.sidedSuccess(stack, pLevel.isClientSide);
        }

        if (!pLevel.isClientSide) {
            String selected = getSelectedDungeon(stack);
            ResourceKey<Structure> key = structureKey(selected);
            if (key != null) {
                ServerLevel server = (ServerLevel) pLevel;
                Registry<Structure> registry = server.registryAccess().registryOrThrow(Registry.STRUCTURE_REGISTRY);
                Holder<Structure> holder = registry.getHolderOrThrow(key);
                var found = server.getChunkSource().getGenerator().findNearestMapStructure(server, HolderSet.direct(holder), pPlayer.blockPosition(), searchRadius(selected), false);
                if (found != null) {
                    stack.hurtAndBreak(1, pPlayer, (p) -> p.broadcastBreakEvent(pHand));
                    pLevel.playSound(null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), SoundEvents.END_PORTAL_FRAME_FILL, SoundSource.NEUTRAL, 0.5F, 0.4F / (pLevel.getRandom().nextFloat() * 0.4F + 0.8F));
                    spawnDirectionParticles(pLevel, pPlayer, found.getFirst());
                } else {
                    pPlayer.displayClientMessage(Component.translatable("string.theaurorian.locator_none"), true);
                }
            }
        }
        return InteractionResultHolder.sidedSuccess(stack, pLevel.isClientSide);
    }

    private static int searchRadius(String dungeon) {
        return switch (dungeon) {
            case "Darkstone" -> 6 * 4;   // density*6 upstream
            case "Moontemple" -> 4 * 4;  // density*4 upstream
            default -> 2 * 4;            // density*2 upstream
        } * 16;
    }

    private static ResourceKey<Structure> structureKey(String dungeon) {
        String id = switch (dungeon) {
            case "Darkstone" -> "darkstone_dungeon";
            case "Moontemple" -> "moon_temple";
            default -> "runestone_dungeon";
        };
        return ResourceKey.create(Registry.STRUCTURE_REGISTRY, new ResourceLocation(TheAurorian.MODID, id));
    }

    private static void spawnDirectionParticles(Level level, Player player, BlockPos dungeon) {
        if (!level.isClientSide) {
            return;
        }
        double lookx = 0.25D + -Mth.sin((float) Math.toRadians(player.getYHeadRot())) * Mth.cos((float) Math.toRadians(player.getXRot()));
        double looky = 0.25D + -Mth.sin((float) Math.toRadians(player.getXRot()));
        double lookz = 0.25D + Mth.cos((float) Math.toRadians(player.getYHeadRot())) * Mth.cos((float) Math.toRadians(player.getXRot()));

        double y = player.getY() + 1 + level.getRandom().nextDouble() * 6.0D / 16.0D;
        double speed = 0.01D;
        double targetx = player.getX() - dungeon.getX();
        double targetz = player.getZ() - dungeon.getZ();
        double partx = targetx * -speed;
        double partz = targetz * -speed;
        partx = Mth.clamp(partx, -0.5D, 0.5D);
        partz = Mth.clamp(partz, -0.5D, 0.5D);
        double randx = level.getRandom().nextDouble() / 8;
        double randz = level.getRandom().nextDouble() / 8;

        for (int i = 0; i < 2; i++) {
            level.addParticle(ParticleTypes.CLOUD, player.getX() + lookx, y + looky, player.getZ() + lookz, partx + randx, 0.25D, partz + randz);
        }
    }

    private static String getSelectedDungeon(ItemStack stack) {
        String dungeon = stack.getOrCreateTag().getString("dungeon");
        return dungeon.isEmpty() ? "Runestone" : dungeon;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(Component.literal("§b[" + getSelectedDungeon(pStack) + "§r]"));
        pTooltipComponents.add(Component.translatable("string.theaurorian.tooltip.locator"));
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }
}
