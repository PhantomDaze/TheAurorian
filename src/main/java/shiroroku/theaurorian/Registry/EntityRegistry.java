package shiroroku.theaurorian.Registry;

import net.minecraft.core.registries.Registries;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;
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

    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, TheAurorian.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<AurorianArrowEntity>> cerulean_arrow = ENTITIES.register("cerulean_arrow", () -> EntityType.Builder.<AurorianArrowEntity>of(CeruleanArrowEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(20).build("cerulean_arrow"));
    public static final DeferredHolder<EntityType<?>, EntityType<AurorianArrowEntity>> crystal_arrow = ENTITIES.register("crystal_arrow", () -> EntityType.Builder.<AurorianArrowEntity>of(CrystalArrowEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(20).build("crystal_arrow"));
    public static final DeferredHolder<EntityType<?>, EntityType<CrystallineBeamEntity>> crystalline_beam = ENTITIES.register("crystalline_beam", () -> EntityType.Builder.<CrystallineBeamEntity>of(CrystallineBeamEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).fireImmune().updateInterval(1).build("crystalline_beam"));

    // K3: upstream setSize(0.7*2, 4.2) ≈ 1.4×4.2; renderer scale 1.3 keeps visual match
    public static final DeferredHolder<EntityType<?>, EntityType<DungeonKeeperEntity>> dungeon_keeper = ENTITIES.register("dungeon_keeper", () -> EntityType.Builder.of(DungeonKeeperEntity::new, MobCategory.MONSTER).sized(1.4F, 4.2F).clientTrackingRange(8).fireImmune().build("dungeon_keeper"));
    // SLM1: size-1 slime footprint (~0.51)
    public static final DeferredHolder<EntityType<?>, EntityType<DungeonSlimeEntity>> dungeon_slime = ENTITIES.register("dungeon_slime", () -> EntityType.Builder.of(DungeonSlimeEntity::new, MobCategory.MONSTER).sized(0.52F, 0.52F).clientTrackingRange(10).build("dungeon_slime"));
    public static final DeferredHolder<EntityType<?>, EntityType<HollowEntity>> hollow = ENTITIES.register("hollow", () -> EntityType.Builder.<HollowEntity>of(HollowEntity::new, MobCategory.MONSTER).sized(0.6F, 1.95F).clientTrackingRange(8).build("hollow"));
    // UK1/UK4: fireImmune + scale 1.3 on 0.6×1.95
    public static final DeferredHolder<EntityType<?>, EntityType<UndeadKnightEntity>> undead_knight = ENTITIES.register("undead_knight", () -> EntityType.Builder.<UndeadKnightEntity>of(UndeadKnightEntity::new, MobCategory.MONSTER).sized(0.6F * 1.3F, 1.95F * 1.3F).clientTrackingRange(8).fireImmune().build("undead_knight"));
    public static final DeferredHolder<EntityType<?>, EntityType<MoonQueenEntity>> moon_queen = ENTITIES.register("moon_queen", () -> EntityType.Builder.<MoonQueenEntity>of(MoonQueenEntity::new, MobCategory.MONSTER).sized(0.6F * MoonQueenEntity.MOB_SCALE, 1.95F * MoonQueenEntity.MOB_SCALE).clientTrackingRange(8).fireImmune().build("moon_queen"));
    public static final DeferredHolder<EntityType<?>, EntityType<DungeonSpiderEntity>> dungeon_spider = ENTITIES.register("dungeon_spider", () -> EntityType.Builder.<DungeonSpiderEntity>of(DungeonSpiderEntity::new, MobCategory.MONSTER).sized(2.8F, 1.8F).clientTrackingRange(8).fireImmune().build("dungeon_spider"));
    public static final DeferredHolder<EntityType<?>, EntityType<SpiderlingEntity>> spiderling = ENTITIES.register("spiderling", () -> EntityType.Builder.<SpiderlingEntity>of(SpiderlingEntity::new, MobCategory.MONSTER).sized(0.7F, 0.45F).clientTrackingRange(8).build("spiderling"));
    public static final DeferredHolder<EntityType<?>, EntityType<MoonAcolyteEntity>> moon_acolyte = ENTITIES.register("moon_acolyte", () -> EntityType.Builder.<MoonAcolyteEntity>of(MoonAcolyteEntity::new, MobCategory.MONSTER).sized(0.6F, 1.95F).clientTrackingRange(8).build("moon_acolyte"));
    public static final DeferredHolder<EntityType<?>, EntityType<CrystallineSpriteEntity>> crystalline_sprite = ENTITIES.register("crystalline_sprite", () -> EntityType.Builder.<CrystallineSpriteEntity>of(CrystallineSpriteEntity::new, MobCategory.MONSTER).sized(1.0F, 1.5F).clientTrackingRange(8).build("crystalline_sprite"));
    public static final DeferredHolder<EntityType<?>, EntityType<SpiritEntity>> spirit = ENTITIES.register("spirit", () -> EntityType.Builder.<SpiritEntity>of(SpiritEntity::new, MobCategory.MONSTER).sized(0.6F, 1.95F).clientTrackingRange(8).build("spirit"));
    public static final DeferredHolder<EntityType<?>, EntityType<DisturbedHollowEntity>> disturbed_hollow = ENTITIES.register("disturbed_hollow", () -> EntityType.Builder.<DisturbedHollowEntity>of(DisturbedHollowEntity::new, MobCategory.MONSTER).sized(0.6F, 1.95F).clientTrackingRange(8).build("disturbed_hollow"));
    public static final DeferredHolder<EntityType<?>, EntityType<StickySpikerEntity>> sticky_spiker = ENTITIES.register("sticky_spiker", () -> EntityType.Builder.<StickySpikerEntity>of(StickySpikerEntity::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10).build("sticky_spiker"));
    public static final DeferredHolder<EntityType<?>, EntityType<WebbingEntity>> webbing = ENTITIES.register("webbing", () -> EntityType.Builder.<WebbingEntity>of(WebbingEntity::new, MobCategory.MISC).sized(0.3F, 0.3F).clientTrackingRange(4).updateInterval(10).build("webbing"));

    public static final DeferredHolder<EntityType<?>, EntityType<AurorianPigEntity>> aurorian_pig = ENTITIES.register("aurorian_pig", () -> EntityType.Builder.<AurorianPigEntity>of(AurorianPigEntity::new, MobCategory.CREATURE).sized(0.9F, 0.9F).clientTrackingRange(8).build("aurorian_pig"));
    public static final DeferredHolder<EntityType<?>, EntityType<AurorianRabbitEntity>> aurorian_rabbit = ENTITIES.register("aurorian_rabbit", () -> EntityType.Builder.<AurorianRabbitEntity>of(AurorianRabbitEntity::new, MobCategory.CREATURE).sized(0.4F, 0.5F).clientTrackingRange(8).build("aurorian_rabbit"));
    public static final DeferredHolder<EntityType<?>, EntityType<AurorianSheepEntity>> aurorian_sheep = ENTITIES.register("aurorian_sheep", () -> EntityType.Builder.<AurorianSheepEntity>of(AurorianSheepEntity::new, MobCategory.CREATURE).sized(0.9F, 1.3F).clientTrackingRange(8).build("aurorian_sheep"));

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

    public static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        event.register(EntityRegistry.hollow.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, HollowEntity::checkSpawn, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(EntityRegistry.dungeon_slime.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, DungeonSlimeEntity::checkSpawn, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(EntityRegistry.spiderling.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpiderlingEntity::checkSpawn, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(EntityRegistry.moon_acolyte.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, MoonAcolyteEntity::checkSpawn, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(EntityRegistry.crystalline_sprite.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, CrystallineSpriteEntity::checkSpawn, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(EntityRegistry.spirit.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpiritEntity::checkSpawn, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(EntityRegistry.disturbed_hollow.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, DisturbedHollowEntity::checkSpawn, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(EntityRegistry.aurorian_pig.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, AurorianPigEntity::checkSpawn, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(EntityRegistry.aurorian_rabbit.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, AurorianRabbitEntity::checkSpawn, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(EntityRegistry.aurorian_sheep.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, AurorianSheepEntity::checkSpawn, RegisterSpawnPlacementsEvent.Operation.AND);
    }
}
