package shiroroku.theaurorian.DevTest;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.levelgen.flat.FlatLayerInfo;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import shiroroku.theaurorian.TheAurorian;

import java.util.List;
import java.util.Optional;

/**
 * Dev-only structure layout harness.
 * <p>
 * Enabled with JVM system property {@code theaurorian.structureLayoutTest=true}
 * (see Gradle run {@code clientStructureLayout} / script {@code scripts/run_structure_layout_test.sh}).
 * <p>
 * Flow (no in-game commands required):
 * <ol>
 *   <li>Launch-time script deletes {@value #WORLD_NAME} under {@code run/saves/}.</li>
 *   <li>On title screen, create a fresh superflat world:
 *       bedrock ×1 + dirt ×100 + grass ×1, difficulty NORMAL, creative + cheats.</li>
 *   <li>After the player joins, force-load chunks and place the three boss dungeons.</li>
 *   <li>Teleport the player to the first structure and leave the client running for inspection.</li>
 * </ol>
 */
public final class StructureLayoutTest {

    public static final String PROP = "theaurorian.structureLayoutTest";
    public static final String WORLD_NAME = "ta_structure_layout";
    /** Fixed seed so chunk/structure RNG is repeatable across runs. */
    public static final long SEED = 20260804L;

    /** Layer stack: y0 bedrock, y1–100 dirt, y101 grass → surface height ≈ 102. */
    public static final int BEDROCK_LAYERS = 1;
    public static final int DIRT_LAYERS = 100;
    public static final int GRASS_LAYERS = 1;

    // Chunk-aligned anchors far enough apart that bounding boxes do not collide.
    private static final StructureSpec RUNESTONE = new StructureSpec(
            "runestone_dungeon",
            ResourceLocation.fromNamespaceAndPath(TheAurorian.MODID, "runestone_dungeon"),
            new BlockPos(0, 0, 0),
            6
    );
    private static final StructureSpec DARKSTONE = new StructureSpec(
            "darkstone_dungeon",
            ResourceLocation.fromNamespaceAndPath(TheAurorian.MODID, "darkstone_dungeon"),
            new BlockPos(256, 0, 0),
            8
    );
    private static final StructureSpec MOON_TEMPLE = new StructureSpec(
            "moon_temple",
            ResourceLocation.fromNamespaceAndPath(TheAurorian.MODID, "moon_temple"),
            new BlockPos(0, 0, 256),
            10
    );
    private static final StructureSpec[] SPECS = {RUNESTONE, DARKSTONE, MOON_TEMPLE};

    private static final int TICKS_BEFORE_PLACE = 40;

    private StructureLayoutTest() {
    }

    public static boolean enabled() {
        return Boolean.getBoolean(PROP);
    }

    // ------------------------------------------------------------------ client: auto-create world

    @EventBusSubscriber(modid = TheAurorian.MODID, value = Dist.CLIENT)
    public static final class ClientHooks {
        private static boolean createRequested;

        private ClientHooks() {
        }

        @SubscribeEvent
        public static void onClientTick(ClientTickEvent.Post event) {
            if (!enabled() || createRequested) {
                return;
            }
            Minecraft mc = Minecraft.getInstance();
            if (mc.screen instanceof TitleScreen && mc.getOverlay() == null && mc.level == null) {
                createRequested = true;
                TheAurorian.LOGGER.info("[StructureLayoutTest] Title screen ready — creating superflat world '{}'", WORLD_NAME);
                createSuperflatWorld(mc);
            }
        }

        private static void createSuperflatWorld(Minecraft mc) {
            LevelSettings settings = new LevelSettings(
                    WORLD_NAME,
                    GameType.CREATIVE,
                    false,
                    Difficulty.NORMAL,
                    true,
                    new GameRules(),
                    WorldDataConfiguration.DEFAULT
            );
            WorldOptions options = new WorldOptions(SEED, false, false);
            mc.createWorldOpenFlows().createFreshLevel(
                    WORLD_NAME,
                    settings,
                    options,
                    StructureLayoutTest::buildFlatDimensions,
                    new TitleScreen()
            );
        }
    }

    /**
     * FLAT preset (nether/end retained) with overworld generator replaced by
     * bedrock×1 + dirt×100 + grass×1, no decoration / lakes / structure sets.
     */
    private static WorldDimensions buildFlatDimensions(RegistryAccess registry) {
        WorldDimensions base = registry.registryOrThrow(Registries.WORLD_PRESET)
                .getHolderOrThrow(WorldPresets.FLAT)
                .value()
                .createWorldDimensions();

        FlatLevelGeneratorSettings flat = new FlatLevelGeneratorSettings(
                Optional.empty(),
                registry.lookupOrThrow(Registries.BIOME).getOrThrow(Biomes.PLAINS),
                List.of()
        );
        flat.getLayersInfo().add(new FlatLayerInfo(BEDROCK_LAYERS, Blocks.BEDROCK));
        flat.getLayersInfo().add(new FlatLayerInfo(DIRT_LAYERS, Blocks.DIRT));
        flat.getLayersInfo().add(new FlatLayerInfo(GRASS_LAYERS, Blocks.GRASS_BLOCK));
        flat.updateLayers();

        return base.replaceOverworldGenerator(registry, new FlatLevelSource(flat));
    }

    // ------------------------------------------------------------------ server: place structures

    @EventBusSubscriber(modid = TheAurorian.MODID)
    public static final class ServerHooks {
        private static boolean scheduled;
        private static boolean done;
        private static int ticksLeft = -1;
        private static ServerPlayer pendingPlayer;

        private ServerHooks() {
        }

        @SubscribeEvent
        public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
            if (!enabled() || scheduled || done) {
                return;
            }
            Player player = event.getEntity();
            if (!(player instanceof ServerPlayer serverPlayer)) {
                return;
            }
            if (!(serverPlayer.level() instanceof ServerLevel)) {
                return;
            }
            scheduled = true;
            pendingPlayer = serverPlayer;
            ticksLeft = TICKS_BEFORE_PLACE;

            serverPlayer.setGameMode(GameType.CREATIVE);
            serverPlayer.getAbilities().mayfly = true;
            serverPlayer.getAbilities().flying = true;
            serverPlayer.onUpdateAbilities();

            GameRules rules = serverPlayer.serverLevel().getGameRules();
            rules.getRule(GameRules.RULE_DAYLIGHT).set(false, serverPlayer.server);
            rules.getRule(GameRules.RULE_WEATHER_CYCLE).set(false, serverPlayer.server);
            rules.getRule(GameRules.RULE_DOMOBSPAWNING).set(false, serverPlayer.server);
            serverPlayer.serverLevel().setDayTime(6000);

            serverPlayer.sendSystemMessage(Component.literal(
                    "§b[StructureLayoutTest]§r World ready. Placing 3 boss structures in " + TICKS_BEFORE_PLACE + " ticks…"));
            TheAurorian.LOGGER.info("[StructureLayoutTest] Player joined; scheduling structure placement");
        }

        @SubscribeEvent
        public static void onServerTick(ServerTickEvent.Post event) {
            if (!enabled() || done || !scheduled || ticksLeft < 0) {
                return;
            }
            if (--ticksLeft > 0) {
                return;
            }
            ServerPlayer player = pendingPlayer;
            pendingPlayer = null;
            if (player == null || player.hasDisconnected()) {
                TheAurorian.LOGGER.error("[StructureLayoutTest] Player gone before placement");
                done = true;
                return;
            }
            try {
                placeAll(player);
            } catch (Exception ex) {
                TheAurorian.LOGGER.error("[StructureLayoutTest] Placement failed", ex);
                player.sendSystemMessage(Component.literal("§c[StructureLayoutTest] FAILED: " + ex.getMessage()));
            } finally {
                done = true;
            }
        }

        private static void placeAll(ServerPlayer player) {
            ServerLevel level = player.serverLevel();
            TheAurorian.LOGGER.info("[StructureLayoutTest] Placing structures in {}", level.dimension().location());

            StringBuilder summary = new StringBuilder("§b[StructureLayoutTest]§r Placed:\n");
            BlockPos firstTeleport = null;

            for (StructureSpec spec : SPECS) {
                PlaceResult result = placeOne(level, spec);
                summary.append(" §7- §f").append(spec.label)
                        .append("§7 @ §a").append(result.anchor.getX()).append(' ')
                        .append(result.anchor.getY()).append(' ')
                        .append(result.anchor.getZ())
                        .append("§7 bb=").append(result.box.toString().replace("BoundingBox ", ""))
                        .append(result.ok ? " §aOK" : " §cFAIL")
                        .append('\n');
                if (result.ok && firstTeleport == null) {
                    firstTeleport = result.anchor.above(4);
                }
            }

            player.sendSystemMessage(Component.literal(summary.toString().trim()));
            TheAurorian.LOGGER.info("[StructureLayoutTest] {}", summary.toString().replaceAll("§.", ""));

            if (firstTeleport != null) {
                player.teleportTo(level,
                        firstTeleport.getX() + 0.5,
                        firstTeleport.getY(),
                        firstTeleport.getZ() + 0.5,
                        0.0F, 20.0F);
                player.getAbilities().flying = true;
                player.onUpdateAbilities();
                player.sendSystemMessage(Component.literal(
                        "§b[StructureLayoutTest]§r Teleported to runestone. "
                                + "Other anchors: darkstone (256 y 0), moon temple (0 y 256). "
                                + "Game stays open for inspection."));
            }
        }

        private static PlaceResult placeOne(ServerLevel level, StructureSpec spec) {
            ResourceKey<Structure> key = ResourceKey.create(Registries.STRUCTURE, spec.id);
            Optional<Holder.Reference<Structure>> holderOpt = level.registryAccess()
                    .registryOrThrow(Registries.STRUCTURE)
                    .getHolder(key);
            if (holderOpt.isEmpty()) {
                TheAurorian.LOGGER.error("[StructureLayoutTest] Missing structure registry entry {}", spec.id);
                return new PlaceResult(spec.anchor, new BoundingBox(spec.anchor), false);
            }

            Structure structure = holderOpt.get().value();
            ChunkGenerator generator = level.getChunkSource().getGenerator();
            ChunkPos chunkPos = new ChunkPos(spec.anchor);

            StructureStart start = structure.generate(
                    level.registryAccess(),
                    generator,
                    generator.getBiomeSource(),
                    level.getChunkSource().randomState(),
                    level.getStructureManager(),
                    level.getSeed(),
                    chunkPos,
                    0,
                    level,
                    biome -> true
            );
            if (!start.isValid()) {
                TheAurorian.LOGGER.error("[StructureLayoutTest] Structure.generate invalid for {}", spec.id);
                return new PlaceResult(spec.anchor, new BoundingBox(spec.anchor), false);
            }

            BoundingBox box = start.getBoundingBox();
            ChunkPos min = new ChunkPos(
                    SectionPos.blockToSectionCoord(box.minX()),
                    SectionPos.blockToSectionCoord(box.minZ()));
            ChunkPos max = new ChunkPos(
                    SectionPos.blockToSectionCoord(box.maxX()),
                    SectionPos.blockToSectionCoord(box.maxZ()));

            // Pad so edge pieces and neighbour-chunk postProcess stay loaded.
            int pad = Math.max(2, spec.chunkPad);
            for (int cx = min.x - pad; cx <= max.x + pad; cx++) {
                for (int cz = min.z - pad; cz <= max.z + pad; cz++) {
                    level.setChunkForced(cx, cz, true);
                    level.getChunk(cx, cz);
                }
            }

            ChunkPos.rangeClosed(min, max).forEach(cp -> start.placeInChunk(
                    level,
                    level.structureManager(),
                    generator,
                    level.getRandom(),
                    new BoundingBox(
                            cp.getMinBlockX(),
                            level.getMinBuildHeight(),
                            cp.getMinBlockZ(),
                            cp.getMaxBlockX(),
                            level.getMaxBuildHeight(),
                            cp.getMaxBlockZ()
                    ),
                    cp
            ));

            // Prefer a walkable teleport on the structure origin / surface.
            BlockPos anchor = start.getBoundingBox().getCenter();
            int surface = level.getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    start.getChunkPos().getMiddleBlockX(), start.getChunkPos().getMiddleBlockZ());
            BlockPos visit = new BlockPos(start.getChunkPos().getMiddleBlockX(), Math.max(surface, box.minY() + 2),
                    start.getChunkPos().getMiddleBlockZ());

            TheAurorian.LOGGER.info("[StructureLayoutTest] {} placed bb={} visit={}", spec.label, box, visit);
            return new PlaceResult(visit, box, true);
        }
    }

    private record StructureSpec(String label, ResourceLocation id, BlockPos anchor, int chunkPad) {
    }

    private record PlaceResult(BlockPos anchor, BoundingBox box, boolean ok) {
    }
}
