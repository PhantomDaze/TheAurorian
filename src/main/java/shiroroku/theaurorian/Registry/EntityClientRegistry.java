package shiroroku.theaurorian.Registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import shiroroku.theaurorian.Blocks.SilentwoodChest.SilentwoodChestBlockRenderer;
import shiroroku.theaurorian.Entities.AurorianArrow.AurorianArrowRenderer;
import shiroroku.theaurorian.Entities.AurorianHumanoidModel;
import shiroroku.theaurorian.Entities.AurorianSkeletonModel;
import shiroroku.theaurorian.Entities.Boss.DungeonSpiderModel;
import shiroroku.theaurorian.Entities.Boss.DungeonSpiderRenderer;
import shiroroku.theaurorian.Entities.Boss.MoonQueenModel;
import shiroroku.theaurorian.Entities.Boss.MoonQueenRenderer;
import shiroroku.theaurorian.Entities.CrystallineBeam.CrystallineBeamModel;
import shiroroku.theaurorian.Entities.CrystallineBeam.CrystallineBeamRenderer;
import shiroroku.theaurorian.Entities.CrystallineSprite.CrystallineSpriteEntityRender;
import shiroroku.theaurorian.Entities.DisturbedHollow.DisturbedHollowModel;
import shiroroku.theaurorian.Entities.DisturbedHollow.DisturbedHollowEntityRender;
import shiroroku.theaurorian.Entities.DungeonKeeper.DungeonKeeperModel;
import shiroroku.theaurorian.Entities.DungeonKeeper.DungeonKeeperRenderer;
import shiroroku.theaurorian.Entities.DungeonSlime.DungeonSlimeModel;
import shiroroku.theaurorian.Entities.DungeonSlime.DungeonSlimeOuterModel;
import shiroroku.theaurorian.Entities.DungeonSlime.DungeonSlimeRenderer;
import shiroroku.theaurorian.Entities.Hollow.HollowModel;
import shiroroku.theaurorian.Entities.Hollow.HollowRenderer;
import shiroroku.theaurorian.Entities.MoonAcolyte.MoonAcolyteModel;
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
import shiroroku.theaurorian.Entities.Spirit.SpiritModel;
import shiroroku.theaurorian.Entities.Spirit.SpiritEntityRender;
import shiroroku.theaurorian.Entities.StickySpiker.StickySpikerEntityRender;
import shiroroku.theaurorian.Entities.UndeadKnight.UndeadKnightModel;
import shiroroku.theaurorian.Entities.UndeadKnight.UndeadKnightRenderer;
import shiroroku.theaurorian.Entities.Webbing.WebbingEntityRender;
import shiroroku.theaurorian.TheAurorian;

/**
 * Client-only entity renderer / layer registration. Kept out of
 * {@link EntityRegistry} so dedicated and GameTest servers do not load client classes.
 */
@Mod.EventBusSubscriber(modid = TheAurorian.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class EntityClientRegistry {

    private EntityClientRegistry() {
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityRegistry.cerulean_arrow.get(), (ctx) -> new AurorianArrowRenderer(ctx, new ResourceLocation(TheAurorian.MODID, "textures/entity/cerulean_arrow.png")));
        event.registerEntityRenderer(EntityRegistry.crystal_arrow.get(), (ctx) -> new AurorianArrowRenderer(ctx, new ResourceLocation(TheAurorian.MODID, "textures/entity/crystal_arrow.png")));
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
        event.registerLayerDefinition(DungeonSlimeModel.MODEL_LAYER_LOCATION_OUTER, DungeonSlimeOuterModel::createBodyLayer);
        event.registerLayerDefinition(DungeonSpiderModel.MODEL_LAYER_LOCATION, DungeonSpiderModel::createBodyLayer);

        event.registerLayerDefinition(HollowModel.MODEL_LAYER_LOCATION, HollowModel::createBodyLayer);
        event.registerLayerDefinition(HollowModel.INNER_ARMOR_LAYER, AurorianHumanoidModel::createInnerArmorLayer);
        event.registerLayerDefinition(HollowModel.OUTER_ARMOR_LAYER, AurorianHumanoidModel::createOuterArmorLayer);
        event.registerLayerDefinition(UndeadKnightModel.MODEL_LAYER_LOCATION, UndeadKnightModel::createBodyLayer);
        event.registerLayerDefinition(UndeadKnightModel.INNER_ARMOR_LAYER, AurorianHumanoidModel::createInnerArmorLayer);
        event.registerLayerDefinition(UndeadKnightModel.OUTER_ARMOR_LAYER, AurorianHumanoidModel::createOuterArmorLayer);
        event.registerLayerDefinition(MoonQueenModel.MODEL_LAYER_LOCATION, MoonQueenModel::createBodyLayer);
        event.registerLayerDefinition(MoonQueenModel.INNER_ARMOR_LAYER, AurorianHumanoidModel::createInnerArmorLayer);
        event.registerLayerDefinition(MoonQueenModel.OUTER_ARMOR_LAYER, AurorianHumanoidModel::createOuterArmorLayer);
        event.registerLayerDefinition(MoonAcolyteModel.MODEL_LAYER_LOCATION, MoonAcolyteModel::createBodyLayer);
        event.registerLayerDefinition(MoonAcolyteModel.OVERLAY_LAYER, MoonAcolyteModel::createOverlayLayer);
        event.registerLayerDefinition(SpiritModel.MODEL_LAYER_LOCATION, SpiritModel::createBodyLayer);
        event.registerLayerDefinition(DisturbedHollowModel.MODEL_LAYER_LOCATION, DisturbedHollowModel::createBodyLayer);

        event.registerLayerDefinition(DungeonKeeperModel.MODEL_LAYER_LOCATION, DungeonKeeperModel::createBodyLayer);
        event.registerLayerDefinition(DungeonKeeperModel.INNER_ARMOR_LAYER, AurorianSkeletonModel::createInnerArmorLayer);
        event.registerLayerDefinition(DungeonKeeperModel.OUTER_ARMOR_LAYER, AurorianSkeletonModel::createOuterArmorLayer);
        event.registerLayerDefinition(DungeonKeeperModel.CLOTHING_LAYER, AurorianSkeletonModel::createClothingLayer);

        event.registerLayerDefinition(AurorianPigEntityModel.MODEL_LAYER_LOCATION, AurorianPigEntityModel::createBodyLayer);
        event.registerLayerDefinition(AurorianRabbitEntityModel.MODEL_LAYER_LOCATION, AurorianRabbitEntityModel::createBodyLayer);
        event.registerLayerDefinition(AurorianSheepEntityModel1.MODEL_LAYER_LOCATION, AurorianSheepEntityModel1::createBodyLayer);
        event.registerLayerDefinition(AurorianSheepEntityModel2.MODEL_LAYER_LOCATION, AurorianSheepEntityModel2::createBodyLayer);
    }
}
