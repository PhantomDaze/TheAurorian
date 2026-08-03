package shiroroku.theaurorian.Registry;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import shiroroku.theaurorian.Blocks.SilentwoodChest.SilentwoodChestBlockRenderer;
import shiroroku.theaurorian.Entities.AurorianArrow.AurorianArrowRenderer;
import shiroroku.theaurorian.Entities.Boss.DungeonSpiderRenderer;
import shiroroku.theaurorian.Entities.Boss.MoonQueenRenderer;
import shiroroku.theaurorian.Entities.CrystallineBeam.CrystallineBeamModel;
import shiroroku.theaurorian.Entities.CrystallineBeam.CrystallineBeamRenderer;
import shiroroku.theaurorian.Entities.CrystallineSprite.CrystallineSpriteEntityRender;
import shiroroku.theaurorian.Entities.DisturbedHollow.DisturbedHollowEntityRender;
import shiroroku.theaurorian.Entities.DungeonKeeper.DungeonKeeperRenderer;
import shiroroku.theaurorian.Entities.DungeonSlime.DungeonSlimeModel;
import shiroroku.theaurorian.Entities.DungeonSlime.DungeonSlimeRenderer;
import shiroroku.theaurorian.Entities.Hollow.HollowRenderer;
import shiroroku.theaurorian.Entities.MoonAcolyte.MoonAcolyteEntityRender;
import shiroroku.theaurorian.Entities.Passive.AurorianPigEntityModel;
import shiroroku.theaurorian.Entities.Passive.AurorianPigEntityRender;
import shiroroku.theaurorian.Entities.Passive.AurorianRabbitEntityModel;
import shiroroku.theaurorian.Entities.Passive.AurorianRabbitEntityRender;
import shiroroku.theaurorian.Entities.Passive.AurorianSheepEntityModel1;
import shiroroku.theaurorian.Entities.Passive.AurorianSheepEntityModel2;
import shiroroku.theaurorian.Entities.Passive.AurorianSheepEntityRender;
import shiroroku.theaurorian.Entities.Spiderling.SpiderlingEntityModel;
import shiroroku.theaurorian.Entities.Spiderling.SpiderlingEntityRender;
import shiroroku.theaurorian.Entities.Spirit.SpiritEntityRender;
import shiroroku.theaurorian.Entities.StickySpiker.StickySpikerEntityRender;
import shiroroku.theaurorian.Entities.UndeadKnight.UndeadKnightRenderer;
import shiroroku.theaurorian.Entities.Webbing.WebbingEntityRender;
import shiroroku.theaurorian.TheAurorian;

/**
 * Client-only entity renderer / layer registration. Kept out of
 * {@link EntityRegistry} so dedicated and GameTest servers do not load client classes.
 */
@EventBusSubscriber(modid = TheAurorian.MODID, value = Dist.CLIENT)
public final class EntityClientRegistry {

    private EntityClientRegistry() {
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityRegistry.cerulean_arrow.get(), (ctx) -> new AurorianArrowRenderer(ctx, ResourceLocation.fromNamespaceAndPath(TheAurorian.MODID, "textures/entity/cerulean_arrow.png")));
        event.registerEntityRenderer(EntityRegistry.crystal_arrow.get(), (ctx) -> new AurorianArrowRenderer(ctx, ResourceLocation.fromNamespaceAndPath(TheAurorian.MODID, "textures/entity/crystal_arrow.png")));
        event.registerEntityRenderer(EntityRegistry.crystalline_beam.get(), CrystallineBeamRenderer::new);

        event.registerEntityRenderer(EntityRegistry.dungeon_keeper.get(), DungeonKeeperRenderer::new);
        event.registerEntityRenderer(EntityRegistry.dungeon_slime.get(), DungeonSlimeRenderer::new);
        event.registerEntityRenderer(EntityRegistry.hollow.get(), HollowRenderer::new);
        event.registerEntityRenderer(EntityRegistry.undead_knight.get(), UndeadKnightRenderer::new);
        event.registerEntityRenderer(EntityRegistry.moon_queen.get(), MoonQueenRenderer::new);
        event.registerEntityRenderer(EntityRegistry.dungeon_spider.get(), DungeonSpiderRenderer::new);
        event.registerEntityRenderer(EntityRegistry.spiderling.get(), SpiderlingEntityRender::new);
        event.registerEntityRenderer(EntityRegistry.moon_acolyte.get(), MoonAcolyteEntityRender::new);
        event.registerEntityRenderer(EntityRegistry.crystalline_sprite.get(), CrystallineSpriteEntityRender::new);
        event.registerEntityRenderer(EntityRegistry.spirit.get(), SpiritEntityRender::new);
        event.registerEntityRenderer(EntityRegistry.disturbed_hollow.get(), DisturbedHollowEntityRender::new);
        event.registerEntityRenderer(EntityRegistry.sticky_spiker.get(), StickySpikerEntityRender::new);
        event.registerEntityRenderer(EntityRegistry.webbing.get(), WebbingEntityRender::new);
        event.registerEntityRenderer(EntityRegistry.aurorian_pig.get(), AurorianPigEntityRender::new);
        event.registerEntityRenderer(EntityRegistry.aurorian_rabbit.get(), AurorianRabbitEntityRender::new);
        event.registerEntityRenderer(EntityRegistry.aurorian_sheep.get(), AurorianSheepEntityRender::new);
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(CrystallineBeamModel.MODEL_LAYER_LOCATION, CrystallineBeamModel::createLayer);
        event.registerLayerDefinition(SpiderlingEntityModel.MODEL_LAYER_LOCATION, SpiderlingEntityModel::createLayer);
        event.registerLayerDefinition(SilentwoodChestBlockRenderer.MODEL_LAYER_DOUBLE_LEFT, SilentwoodChestBlockRenderer::createDoubleBodyLeftLayer);
        event.registerLayerDefinition(SilentwoodChestBlockRenderer.MODEL_LAYER_DOUBLE_RIGHT, SilentwoodChestBlockRenderer::createDoubleBodyRightLayer);
        event.registerLayerDefinition(SilentwoodChestBlockRenderer.MODEL_LAYER_NORMAL, SilentwoodChestBlockRenderer::createSingleBodyLayer);

        event.registerLayerDefinition(DungeonSlimeModel.MODEL_LAYER_LOCATION, DungeonSlimeModel::createLayer);
        event.registerLayerDefinition(DungeonSlimeModel.MODEL_LAYER_LOCATION_OUTER, DungeonSlimeModel::createOuterLayer);

        event.registerLayerDefinition(AurorianPigEntityModel.MODEL_LAYER_LOCATION, AurorianPigEntityModel::createBodyLayer);
        event.registerLayerDefinition(AurorianRabbitEntityModel.MODEL_LAYER_LOCATION, AurorianRabbitEntityModel::createBodyLayer);
        event.registerLayerDefinition(AurorianSheepEntityModel1.MODEL_LAYER_LOCATION, AurorianSheepEntityModel1::createBodyLayer);
        event.registerLayerDefinition(AurorianSheepEntityModel2.MODEL_LAYER_LOCATION, AurorianSheepEntityModel2::createBodyLayer);
    }
}
