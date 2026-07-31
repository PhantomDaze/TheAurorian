package shiroroku.theaurorian.Registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import shiroroku.theaurorian.Blocks.SilentwoodChest.SilentwoodChestBlockRenderer;
import shiroroku.theaurorian.Entities.AurorianArrow.AurorianArrowEntity;
import shiroroku.theaurorian.Entities.AurorianArrow.AurorianArrowRenderer;
import shiroroku.theaurorian.Entities.AurorianArrow.CeruleanArrowEntity;
import shiroroku.theaurorian.Entities.AurorianArrow.CrystalArrowEntity;
import shiroroku.theaurorian.Entities.CrystallineBeam.CrystallineBeamEntity;
import shiroroku.theaurorian.Entities.CrystallineBeam.CrystallineBeamModel;
import shiroroku.theaurorian.Entities.CrystallineBeam.CrystallineBeamRenderer;
import shiroroku.theaurorian.Entities.Boss.DungeonSpiderEntity;
import shiroroku.theaurorian.Entities.Boss.DungeonSpiderRenderer;
import shiroroku.theaurorian.Entities.Boss.MoonQueenEntity;
import shiroroku.theaurorian.Entities.Boss.MoonQueenRenderer;
import shiroroku.theaurorian.Entities.DungeonKeeper.DungeonKeeperEntity;
import shiroroku.theaurorian.Entities.DungeonKeeper.DungeonKeeperRenderer;
import shiroroku.theaurorian.Entities.DungeonSlime.DungeonSlimeEntity;
import shiroroku.theaurorian.Entities.DungeonSlime.DungeonSlimeModel;
import shiroroku.theaurorian.Entities.DungeonSlime.DungeonSlimeRenderer;
import shiroroku.theaurorian.Entities.Hollow.HollowEntity;
import shiroroku.theaurorian.Entities.Hollow.HollowRenderer;
import shiroroku.theaurorian.Entities.UndeadKnight.UndeadKnightEntity;
import shiroroku.theaurorian.Entities.UndeadKnight.UndeadKnightRenderer;
import shiroroku.theaurorian.Entities.Spiderling.SpiderlingEntity;
import shiroroku.theaurorian.Entities.Spiderling.SpiderlingEntityModel;
import shiroroku.theaurorian.Entities.Spiderling.SpiderlingEntityRender;
import shiroroku.theaurorian.Entities.MoonAcolyte.MoonAcolyteEntity;
import shiroroku.theaurorian.Entities.MoonAcolyte.MoonAcolyteEntityRender;
import shiroroku.theaurorian.Entities.CrystallineSprite.CrystallineSpriteEntity;
import shiroroku.theaurorian.Entities.CrystallineSprite.CrystallineSpriteEntityRender;
import shiroroku.theaurorian.Entities.Spirit.SpiritEntity;
import shiroroku.theaurorian.Entities.Spirit.SpiritEntityRender;
import shiroroku.theaurorian.Entities.DisturbedHollow.DisturbedHollowEntity;
import shiroroku.theaurorian.Entities.DisturbedHollow.DisturbedHollowEntityRender;
import shiroroku.theaurorian.Entities.StickySpiker.StickySpikerEntity;
import shiroroku.theaurorian.Entities.StickySpiker.StickySpikerEntityRender;
import shiroroku.theaurorian.Entities.Webbing.WebbingEntity;
import shiroroku.theaurorian.Entities.Webbing.WebbingEntityRender;
import shiroroku.theaurorian.TheAurorian;

public class EntityRegistry {

    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, TheAurorian.MODID);

    public static final RegistryObject<EntityType<AurorianArrowEntity>> cerulean_arrow = ENTITIES.register("cerulean_arrow", () -> EntityType.Builder.<AurorianArrowEntity>of(CeruleanArrowEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(20).build("cerulean_arrow"));
    public static final RegistryObject<EntityType<AurorianArrowEntity>> crystal_arrow = ENTITIES.register("crystal_arrow", () -> EntityType.Builder.<AurorianArrowEntity>of(CrystalArrowEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(20).build("crystal_arrow"));
    public static final RegistryObject<EntityType<CrystallineBeamEntity>> crystalline_beam = ENTITIES.register("crystalline_beam", () -> EntityType.Builder.<CrystallineBeamEntity>of(CrystallineBeamEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).fireImmune().setUpdateInterval(1).build("crystalline_beam"));

    public static final RegistryObject<EntityType<DungeonKeeperEntity>> dungeon_keeper = ENTITIES.register("dungeon_keeper", () -> EntityType.Builder.of(DungeonKeeperEntity::new, MobCategory.MONSTER).sized(0.8F, 2.3F).clientTrackingRange(8).sized(0.7F, 2.4F).fireImmune().build("dungeon_keeper"));
    public static final RegistryObject<EntityType<DungeonSlimeEntity>> dungeon_slime = ENTITIES.register("dungeon_slime", () -> EntityType.Builder.of(DungeonSlimeEntity::new, MobCategory.MONSTER).sized(2.04F, 2.04F).clientTrackingRange(10).build("dungeon_slime"));
    public static final RegistryObject<EntityType<HollowEntity>> hollow = ENTITIES.register("hollow", () -> EntityType.Builder.<HollowEntity>of(HollowEntity::new, MobCategory.MONSTER).sized(0.6F, 1.95F).clientTrackingRange(8).build("hollow"));
    public static final RegistryObject<EntityType<UndeadKnightEntity>> undead_knight = ENTITIES.register("undead_knight", () -> EntityType.Builder.<UndeadKnightEntity>of(UndeadKnightEntity::new, MobCategory.MONSTER).sized(0.8F, 2.3F).clientTrackingRange(8).build("undead_knight"));
    public static final RegistryObject<EntityType<MoonQueenEntity>> moon_queen = ENTITIES.register("moon_queen", () -> EntityType.Builder.<MoonQueenEntity>of(MoonQueenEntity::new, MobCategory.MONSTER).sized(0.6F * MoonQueenEntity.MOB_SCALE, 1.95F * MoonQueenEntity.MOB_SCALE).clientTrackingRange(8).fireImmune().build("moon_queen"));
    public static final RegistryObject<EntityType<DungeonSpiderEntity>> dungeon_spider = ENTITIES.register("dungeon_spider", () -> EntityType.Builder.<DungeonSpiderEntity>of(DungeonSpiderEntity::new, MobCategory.MONSTER).sized(2.8F, 1.8F).clientTrackingRange(8).fireImmune().build("dungeon_spider"));
    public static final RegistryObject<EntityType<SpiderlingEntity>> spiderling = ENTITIES.register("spiderling", () -> EntityType.Builder.<SpiderlingEntity>of(SpiderlingEntity::new, MobCategory.MONSTER).sized(0.7F, 0.45F).clientTrackingRange(8).build("spiderling"));
    public static final RegistryObject<EntityType<MoonAcolyteEntity>> moon_acolyte = ENTITIES.register("moon_acolyte", () -> EntityType.Builder.<MoonAcolyteEntity>of(MoonAcolyteEntity::new, MobCategory.MONSTER).sized(0.6F, 1.95F).clientTrackingRange(8).build("moon_acolyte"));
    public static final RegistryObject<EntityType<CrystallineSpriteEntity>> crystalline_sprite = ENTITIES.register("crystalline_sprite", () -> EntityType.Builder.<CrystallineSpriteEntity>of(CrystallineSpriteEntity::new, MobCategory.MONSTER).sized(1.0F, 1.5F).clientTrackingRange(8).build("crystalline_sprite"));
    public static final RegistryObject<EntityType<SpiritEntity>> spirit = ENTITIES.register("spirit", () -> EntityType.Builder.<SpiritEntity>of(SpiritEntity::new, MobCategory.MONSTER).sized(0.6F, 1.95F).clientTrackingRange(8).build("spirit"));
    public static final RegistryObject<EntityType<DisturbedHollowEntity>> disturbed_hollow = ENTITIES.register("disturbed_hollow", () -> EntityType.Builder.<DisturbedHollowEntity>of(DisturbedHollowEntity::new, MobCategory.MONSTER).sized(0.6F, 1.95F).clientTrackingRange(8).build("disturbed_hollow"));
    public static final RegistryObject<EntityType<StickySpikerEntity>> sticky_spiker = ENTITIES.register("sticky_spiker", () -> EntityType.Builder.<StickySpikerEntity>of(StickySpikerEntity::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).setUpdateInterval(10).build("sticky_spiker"));
    public static final RegistryObject<EntityType<WebbingEntity>> webbing = ENTITIES.register("webbing", () -> EntityType.Builder.<WebbingEntity>of(WebbingEntity::new, MobCategory.MISC).sized(0.3F, 0.3F).clientTrackingRange(4).setUpdateInterval(10).build("webbing"));

    public static void entityAttributeCreation(EntityAttributeCreationEvent event) {
        event.put(EntityRegistry.dungeon_keeper.get(), DungeonKeeperEntity.createAttributes().build());
        event.put(EntityRegistry.dungeon_slime.get(), DungeonSlimeEntity.createAttributes().build());
        event.put(EntityRegistry.hollow.get(), HollowEntity.createAttributes().build());
        event.put(EntityRegistry.undead_knight.get(), UndeadKnightEntity.createAttributes().build());
        event.put(EntityRegistry.moon_queen.get(), MoonQueenEntity.createAttributes().build());
        event.put(EntityRegistry.dungeon_spider.get(), DungeonSpiderEntity.createAttributes().build());
        event.put(EntityRegistry.spiderling.get(), SpiderlingEntity.createAttributes().build());
        event.put(EntityRegistry.moon_acolyte.get(), MoonAcolyteEntity.createAttributes().build());
        event.put(EntityRegistry.crystalline_sprite.get(), CrystallineSpriteEntity.createAttributes().build());
        event.put(EntityRegistry.spirit.get(), SpiritEntity.createAttributes().build());
        event.put(EntityRegistry.disturbed_hollow.get(), DisturbedHollowEntity.createAttributes().build());
    }

    public static void registerSpawnPlacements(SpawnPlacementRegisterEvent event) {
        event.register(EntityRegistry.hollow.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, HollowEntity::checkSpawn, SpawnPlacementRegisterEvent.Operation.AND);
        event.register(EntityRegistry.spiderling.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpiderlingEntity::checkSpawn, SpawnPlacementRegisterEvent.Operation.AND);
        event.register(EntityRegistry.moon_acolyte.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, MoonAcolyteEntity::checkSpawn, SpawnPlacementRegisterEvent.Operation.AND);
        event.register(EntityRegistry.crystalline_sprite.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, CrystallineSpriteEntity::checkSpawn, SpawnPlacementRegisterEvent.Operation.AND);
        event.register(EntityRegistry.spirit.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpiritEntity::checkSpawn, SpawnPlacementRegisterEvent.Operation.AND);
        event.register(EntityRegistry.disturbed_hollow.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, DisturbedHollowEntity::checkSpawn, SpawnPlacementRegisterEvent.Operation.AND);
    }

    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // Misc
        event.registerEntityRenderer(EntityRegistry.cerulean_arrow.get(), (ctx) -> new AurorianArrowRenderer(ctx, new ResourceLocation(TheAurorian.MODID, "textures/entity/cerulean_arrow.png")));
        event.registerEntityRenderer(EntityRegistry.crystal_arrow.get(), (ctx) -> new AurorianArrowRenderer(ctx, new ResourceLocation(TheAurorian.MODID, "textures/entity/crystal_arrow.png")));
        event.registerEntityRenderer(EntityRegistry.crystalline_beam.get(), CrystallineBeamRenderer::new);

        // Living
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
    }

    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        // Misc
        event.registerLayerDefinition(CrystallineBeamModel.MODEL_LAYER_LOCATION, CrystallineBeamModel::createLayer);
        event.registerLayerDefinition(SpiderlingEntityModel.MODEL_LAYER_LOCATION, SpiderlingEntityModel::createLayer);
        event.registerLayerDefinition(SilentwoodChestBlockRenderer.MODEL_LAYER_DOUBLE_LEFT, SilentwoodChestBlockRenderer::createDoubleBodyLeftLayer);
        event.registerLayerDefinition(SilentwoodChestBlockRenderer.MODEL_LAYER_DOUBLE_RIGHT, SilentwoodChestBlockRenderer::createDoubleBodyRightLayer);
        event.registerLayerDefinition(SilentwoodChestBlockRenderer.MODEL_LAYER_NORMAL, SilentwoodChestBlockRenderer::createSingleBodyLayer);

        // Living
        event.registerLayerDefinition(DungeonSlimeModel.MODEL_LAYER_LOCATION, DungeonSlimeModel::createLayer);
        event.registerLayerDefinition(DungeonSlimeModel.MODEL_LAYER_LOCATION_OUTER, DungeonSlimeModel::createOuterLayer);
    }

}
