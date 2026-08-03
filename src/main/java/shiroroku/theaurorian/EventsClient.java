package shiroroku.theaurorian;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterDimensionSpecialEffectsEvent;
import net.neoforged.neoforge.client.event.RegisterItemDecorationsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import shiroroku.theaurorian.Blocks.SilentwoodChest.SilentwoodChestClientExt;
import shiroroku.theaurorian.Renderers.AurorianDimensionSpecialEffects;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import shiroroku.theaurorian.Blocks.BossSpawner.BossSpawnerBlockRenderer;
import shiroroku.theaurorian.Blocks.Crystal.CrystalBlockRenderer;
import shiroroku.theaurorian.Blocks.MoonlightForge.MoonlightForgeBlockRenderer;
import shiroroku.theaurorian.Blocks.MoonlightForge.MoonlightForgeScreen;
import shiroroku.theaurorian.Blocks.Scrapper.ScrapperScreen;
import shiroroku.theaurorian.Blocks.SilentwoodChest.SilentwoodChestBlockRenderer;
import shiroroku.theaurorian.Items.BaseAurorianTea;
import shiroroku.theaurorian.Items.Loot.UmbraPickaxe;
import shiroroku.theaurorian.Items.Spectral.SpectralArmorLayer;
import shiroroku.theaurorian.Particles.WeepingWillowDripParticle;
import shiroroku.theaurorian.Registry.BlockEntityRegistry;
import shiroroku.theaurorian.Registry.BlockRegistry;
import shiroroku.theaurorian.Registry.ItemRegistry;
import shiroroku.theaurorian.Registry.MenuRegistry;
import shiroroku.theaurorian.Registry.ParticleRegistry;

import java.awt.*;
import java.util.function.Supplier;

@EventBusSubscriber(modid = TheAurorian.MODID, value = Dist.CLIENT)
public class EventsClient {

    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            // RENDER LAYERS (translucent glass / cutout torches & ladder)
            ItemBlockRenderTypes.setRenderLayer(BlockRegistry.aurorian_glass.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(BlockRegistry.moon_glass.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(BlockRegistry.aurorian_glass_pane.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(BlockRegistry.moon_glass_pane.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(BlockRegistry.silentwood_torch.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(BlockRegistry.moon_torch.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(BlockRegistry.silentwood_ladder.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(BlockRegistry.aurorian_tallgrass_light.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(BlockRegistry.urn.get(), RenderType.cutout());

            // ITEM PROPERTIES
            ItemRegistry.ITEMS_GEN_SHIELD.getEntries().stream().map(Supplier::get).forEach((shield) -> ItemProperties.register(shield, ResourceLocation.withDefaultNamespace("blocking"), (stack, level, entity, i) -> entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F));
            ItemProperties.register(ItemRegistry.crystalline_sword.get(), ResourceLocation.withDefaultNamespace("charge"), (stack, level, entity, i) -> entity == null || entity.getUseItem() != stack ? 0.0F : (float) (stack.getUseDuration(entity) - entity.getUseItemRemainingTicks()) / 20.0F);
            ItemProperties.register(ItemRegistry.crystalline_sword.get(), ResourceLocation.withDefaultNamespace("charging"), (stack, level, entity, i) -> entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F);
            ItemProperties.register(ItemRegistry.silentwood_bow.get(), ResourceLocation.withDefaultNamespace("pull"), (stack, level, entity, i) -> entity == null || entity.getUseItem() != stack ? 0.0F : (float) (stack.getUseDuration(entity) - entity.getUseItemRemainingTicks()) / 20.0F);
            ItemProperties.register(ItemRegistry.silentwood_bow.get(), ResourceLocation.withDefaultNamespace("pulling"), (stack, level, entity, i) -> entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F);
            ItemProperties.register(ItemRegistry.keepers_bow.get(), ResourceLocation.withDefaultNamespace("pull"), (stack, level, entity, i) -> entity == null || entity.getUseItem() != stack ? 0.0F : (float) (stack.getUseDuration(entity) - entity.getUseItemRemainingTicks()) / 20.0F);
            ItemProperties.register(ItemRegistry.keepers_bow.get(), ResourceLocation.withDefaultNamespace("pulling"), (stack, level, entity, i) -> entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F);

            // BLOCK ENTITY RENDERERS
            BlockEntityRenderers.register(BlockEntityRegistry.boss_spawner.get(), BossSpawnerBlockRenderer::new);
            BlockEntityRenderers.register(BlockEntityRegistry.crystal.get(), CrystalBlockRenderer::new);
            BlockEntityRenderers.register(BlockEntityRegistry.moonlight_forge.get(), MoonlightForgeBlockRenderer::new);
            BlockEntityRenderers.register(BlockEntityRegistry.silentwood_chest.get(), SilentwoodChestBlockRenderer::new);
        });
    }

    @SubscribeEvent
    public static void onRegisterMenuScreens(RegisterMenuScreensEvent event) {
        event.register(MenuRegistry.moonlight_forge.get(), MoonlightForgeScreen::new);
        event.register(MenuRegistry.scrapper.get(), ScrapperScreen::new);
    }

    // Entity renderers / layer definitions: EntityClientRegistry (Dist.CLIENT)

    /**
     * Adds the translucent spectral armor layer to every player renderer skin.
     */
    @SubscribeEvent
    public static void onAddLayers(EntityRenderersEvent.AddLayers event) {
        for (PlayerSkin.Model skin : event.getSkins()) {
            PlayerRenderer renderer = event.getSkin(skin);
            if (renderer != null) {
                EntityModelSet models = event.getEntityModels();
                renderer.addLayer(new SpectralArmorLayer<>(renderer,
                        new HumanoidModel<>(models.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)),
                        new HumanoidModel<>(models.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR))));
            }
        }
    }

    /**
     * Adds flashing to the crystalline sword when fully charged
     */
    @SubscribeEvent
    public static void onItemColorHandler(RegisterColorHandlersEvent.Item event) {
        // 1.21 item colors are ARGB; 0x00FFFFFF (old "white") is fully transparent.
        final int NO_TINT = 0xFFFFFFFF;
        // Crystalline sword glow when charged
        event.register((stack, tintIndex) -> {
            if (tintIndex == 0) {
                LivingEntity user = Minecraft.getInstance().player;
                float max = (user == null || user.getUseItem() != stack)
                        ? 0.0F
                        : (float) (stack.getUseDuration(user) - user.getUseItemRemainingTicks()) / 20.0F;
                if (max <= 0.8) {
                    return NO_TINT;
                }
                float wave = (float) (Math.sin(((double) System.currentTimeMillis()) / 200));
                float charge = Math.min(Math.max(0.15f * wave + 0.85f, 0), 1);
                int c = Math.round(charge * 255.0F);
                return 0xFF000000 | (c << 16) | (c << 8) | c;
            }
            return NO_TINT;
        }, ItemRegistry.crystalline_sword.get());
        // Tea color modifier (layer 1 tinted; layer 0 must stay opaque white)
        event.register((stack, tintIndex) -> {
            if (tintIndex == 1) {
                int rgb = ((BaseAurorianTea) stack.getItem()).color & 0x00FFFFFF;
                return 0xFF000000 | rgb;
            }
            return NO_TINT;
        }, ItemRegistry.ITEMS_GEN_TEA.getEntries().stream().map(Supplier::get).toArray(ItemLike[]::new));
    }

    /**
     * Adds block icon to the umbra pickaxe item
     */
    @SubscribeEvent
    public static void onRegisterItemDecorations(RegisterItemDecorationsEvent event) {
        event.register(ItemRegistry.umbra_pickaxe.get(), (graphics, font, stack, xOffset, yOffset) -> {
            Block selectedBlock = UmbraPickaxe.getSelectedBlock(stack);
            if (selectedBlock == null) {
                return false;
            }
            graphics.pose().pushPose();
            graphics.pose().scale(0.5f, 0.5f, 1);
            graphics.pose().translate(xOffset, yOffset + 8, 0);
            graphics.renderItem(new ItemStack(selectedBlock), xOffset, yOffset);
            graphics.pose().popPose();
            return true;
        });
    }

    @SubscribeEvent
    public static void onRegisterParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ParticleRegistry.WEEPING_WILLOW_DRIP.get(), WeepingWillowDripParticle.Provider::new);
    }

    /**
     * 1.12 WorldProvider lighting/sky → 1.21 DimensionSpecialEffects.
     * Must match {@code effects} in dimension_type JSON.
     */
    @SubscribeEvent
    public static void onRegisterDimensionEffects(RegisterDimensionSpecialEffectsEvent event) {
        event.register(ResourceLocation.fromNamespaceAndPath(TheAurorian.MODID, "the_aurorian"),
                new AurorianDimensionSpecialEffects());
    }

    /**
     * Replaces removed Item#initializeClient. Silentwood chest item uses BEWLR.
     */
    @SubscribeEvent
    public static void onRegisterClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerItem(SilentwoodChestClientExt.INSTANCE, BlockRegistry.silentwood_chest.get().asItem());
    }
}
