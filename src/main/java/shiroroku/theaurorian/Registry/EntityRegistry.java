package shiroroku.theaurorian.Registry;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import shiroroku.theaurorian.Entities.AurorianArrow.AurorianArrowEntity;
import shiroroku.theaurorian.Entities.AurorianArrow.CeruleanArrowEntity;
import shiroroku.theaurorian.Entities.AurorianArrow.CrystalArrowEntity;
import shiroroku.theaurorian.Entities.Boss.DungeonSpiderEntity;
import shiroroku.theaurorian.Entities.Boss.MoonQueenEntity;
import shiroroku.theaurorian.Entities.CrystallineBeam.CrystallineBeamEntity;
import shiroroku.theaurorian.Entities.CrystallineSprite.CrystallineSpriteEntity;
import shiroroku.theaurorian.Entities.DisturbedHollow.DisturbedHollowEntity;
import shiroroku.theaurorian.Entities.DungeonKeeper.DungeonKeeperEntity;
import shiroroku.theaurorian.Entities.DungeonSlime.DungeonSlimeEntity;
import shiroroku.theaurorian.Entities.Hollow.HollowEntity;
import shiroroku.theaurorian.Entities.MoonAcolyte.MoonAcolyteEntity;
import shiroroku.theaurorian.Entities.Passive.AurorianPigEntity;
import shiroroku.theaurorian.Entities.Passive.AurorianRabbitEntity;
import shiroroku.theaurorian.Entities.Passive.AurorianSheepEntity;
import shiroroku.theaurorian.Entities.Spiderling.SpiderlingEntity;
import shiroroku.theaurorian.Entities.Spirit.SpiritEntity;
import shiroroku.theaurorian.Entities.StickySpiker.StickySpikerEntity;
import shiroroku.theaurorian.Entities.UndeadKnight.UndeadKnightEntity;
import shiroroku.theaurorian.Entities.Webbing.WebbingEntity;
import shiroroku.theaurorian.TheAurorian;

/**
 * Server-safe entity type registry. Renderer registration lives in
 * {@link shiroroku.theaurorian.EventsClient} so dedicated/GameTest servers do not
 * classload client-only renderer types.
 */
public class EntityRegistry {

    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, TheAurorian.MODID);

    public static final RegistryObject<EntityType<AurorianArrowEntity>> cerulean_arrow = ENTITIES.register("cerulean_arrow", () -> EntityType.Builder.<AurorianArrowEntity>of(CeruleanArrowEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(20).build("cerulean_arrow"));
    public static final RegistryObject<EntityType<AurorianArrowEntity>> crystal_arrow = ENTITIES.register("crystal_arrow", () -> EntityType.Builder.<AurorianArrowEntity>of(CrystalArrowEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(20).build("crystal_arrow"));
    public static final RegistryObject<EntityType<CrystallineBeamEntity>> crystalline_beam = ENTITIES.register("crystalline_beam", () -> EntityType.Builder.<CrystallineBeamEntity>of(CrystallineBeamEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).fireImmune().setUpdateInterval(1).build("crystalline_beam"));

    public static final RegistryObject<EntityType<DungeonKeeperEntity>> dungeon_keeper = ENTITIES.register("dungeon_keeper", () -> EntityType.Builder.of(DungeonKeeperEntity::new, MobCategory.MONSTER).sized(1.4F, 4.2F).clientTrackingRange(8).fireImmune().build("dungeon_keeper"));
    public static final RegistryObject<EntityType<DungeonSlimeEntity>> dungeon_slime = ENTITIES.register("dungeon_slime", () -> EntityType.Builder.of(DungeonSlimeEntity::new, MobCategory.MONSTER).sized(DungeonSlimeEntity.BASE_SIZE, DungeonSlimeEntity.BASE_SIZE).clientTrackingRange(10).build("dungeon_slime"));
    public static final RegistryObject<EntityType<HollowEntity>> hollow = ENTITIES.register("hollow", () -> EntityType.Builder.<HollowEntity>of(HollowEntity::new, MobCategory.MONSTER).sized(0.6F, 1.95F).clientTrackingRange(8).build("hollow"));
    public static final RegistryObject<EntityType<UndeadKnightEntity>> undead_knight = ENTITIES.register("undead_knight", () -> EntityType.Builder.<UndeadKnightEntity>of(UndeadKnightEntity::new, MobCategory.MONSTER).sized(0.8F, 2.3F).clientTrackingRange(8).build("undead_knight"));
    public static final RegistryObject<EntityType<MoonQueenEntity>> moon_queen = ENTITIES.register("moon_queen", () -> EntityType.Builder.<MoonQueenEntity>of(MoonQueenEntity::new, MobCategory.MONSTER).sized(0.6F, 1.95F).clientTrackingRange(8).fireImmune().build("moon_queen"));
    public static final RegistryObject<EntityType<DungeonSpiderEntity>> dungeon_spider = ENTITIES.register("dungeon_spider", () -> EntityType.Builder.<DungeonSpiderEntity>of(DungeonSpiderEntity::new, MobCategory.MONSTER).sized(2.8F, 1.8F).clientTrackingRange(8).fireImmune().build("dungeon_spider"));
    public static final RegistryObject<EntityType<SpiderlingEntity>> spiderling = ENTITIES.register("spiderling", () -> EntityType.Builder.<SpiderlingEntity>of(SpiderlingEntity::new, MobCategory.MONSTER).sized(0.7F, 0.45F).clientTrackingRange(8).build("spiderling"));
    public static final RegistryObject<EntityType<MoonAcolyteEntity>> moon_acolyte = ENTITIES.register("moon_acolyte", () -> EntityType.Builder.<MoonAcolyteEntity>of(MoonAcolyteEntity::new, MobCategory.MONSTER).sized(0.6F, 1.95F).clientTrackingRange(8).build("moon_acolyte"));
    public static final RegistryObject<EntityType<CrystallineSpriteEntity>> crystalline_sprite = ENTITIES.register("crystalline_sprite", () -> EntityType.Builder.<CrystallineSpriteEntity>of(CrystallineSpriteEntity::new, MobCategory.MONSTER).sized(1.0F, 1.5F).clientTrackingRange(8).build("crystalline_sprite"));
    public static final RegistryObject<EntityType<SpiritEntity>> spirit = ENTITIES.register("spirit", () -> EntityType.Builder.<SpiritEntity>of(SpiritEntity::new, MobCategory.MONSTER).sized(0.6F, 1.95F).clientTrackingRange(8).build("spirit"));
    public static final RegistryObject<EntityType<DisturbedHollowEntity>> disturbed_hollow = ENTITIES.register("disturbed_hollow", () -> EntityType.Builder.<DisturbedHollowEntity>of(DisturbedHollowEntity::new, MobCategory.MONSTER).sized(0.6F, 1.95F).clientTrackingRange(8).build("disturbed_hollow"));
    public static final RegistryObject<EntityType<StickySpikerEntity>> sticky_spiker = ENTITIES.register("sticky_spiker", () -> EntityType.Builder.<StickySpikerEntity>of(StickySpikerEntity::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).setUpdateInterval(10).build("sticky_spiker"));
    public static final RegistryObject<EntityType<WebbingEntity>> webbing = ENTITIES.register("webbing", () -> EntityType.Builder.<WebbingEntity>of(WebbingEntity::new, MobCategory.MISC).sized(0.3F, 0.3F).clientTrackingRange(4).setUpdateInterval(10).build("webbing"));

    public static final RegistryObject<EntityType<AurorianPigEntity>> aurorian_pig = ENTITIES.register("aurorian_pig", () -> EntityType.Builder.<AurorianPigEntity>of(AurorianPigEntity::new, MobCategory.CREATURE).sized(0.9F, 0.9F).clientTrackingRange(8).build("aurorian_pig"));
    public static final RegistryObject<EntityType<AurorianRabbitEntity>> aurorian_rabbit = ENTITIES.register("aurorian_rabbit", () -> EntityType.Builder.<AurorianRabbitEntity>of(AurorianRabbitEntity::new, MobCategory.CREATURE).sized(0.4F, 0.5F).clientTrackingRange(8).build("aurorian_rabbit"));
    public static final RegistryObject<EntityType<AurorianSheepEntity>> aurorian_sheep = ENTITIES.register("aurorian_sheep", () -> EntityType.Builder.<AurorianSheepEntity>of(AurorianSheepEntity::new, MobCategory.CREATURE).sized(0.9F, 1.3F).clientTrackingRange(8).build("aurorian_sheep"));

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
        event.put(EntityRegistry.aurorian_pig.get(), AurorianPigEntity.createAttributes().build());
        event.put(EntityRegistry.aurorian_rabbit.get(), AurorianRabbitEntity.createAttributes().build());
        event.put(EntityRegistry.aurorian_sheep.get(), AurorianSheepEntity.createAttributes().build());
    }

    public static void registerSpawnPlacements(SpawnPlacementRegisterEvent event) {
        event.register(EntityRegistry.dungeon_slime.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, DungeonSlimeEntity::checkSpawn, SpawnPlacementRegisterEvent.Operation.AND);
        event.register(EntityRegistry.spiderling.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpiderlingEntity::checkSpawn, SpawnPlacementRegisterEvent.Operation.AND);
        event.register(EntityRegistry.moon_acolyte.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, MoonAcolyteEntity::checkSpawn, SpawnPlacementRegisterEvent.Operation.AND);
        event.register(EntityRegistry.crystalline_sprite.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, CrystallineSpriteEntity::checkSpawn, SpawnPlacementRegisterEvent.Operation.AND);
        event.register(EntityRegistry.spirit.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpiritEntity::checkSpawn, SpawnPlacementRegisterEvent.Operation.AND);
        event.register(EntityRegistry.disturbed_hollow.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, DisturbedHollowEntity::checkSpawn, SpawnPlacementRegisterEvent.Operation.AND);
        event.register(EntityRegistry.aurorian_pig.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, AurorianPigEntity::checkSpawn, SpawnPlacementRegisterEvent.Operation.AND);
        event.register(EntityRegistry.aurorian_rabbit.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, AurorianRabbitEntity::checkSpawn, SpawnPlacementRegisterEvent.Operation.AND);
        event.register(EntityRegistry.aurorian_sheep.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, AurorianSheepEntity::checkSpawn, SpawnPlacementRegisterEvent.Operation.AND);
    }
}
