package shiroroku.theaurorian.Demo;

import net.minecraft.ChatFormatting;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import shiroroku.theaurorian.TheAurorian;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Server-side demo sequencer. Builds each case a few blocks in front of the
 * player, recenters the camera on the action, and waits between cases so a
 * human watching the client can see the result.
 */
@Mod.EventBusSubscriber(modid = TheAurorian.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class VisibleDemoRunner {

    /** Default pause between cases (~4s). */
    public static final int DEFAULT_PAUSE_TICKS = 80;

    private static final Map<UUID, Session> SESSIONS = new HashMap<>();
    private static final Map<UUID, Integer> PAUSE_PREFS = new HashMap<>();

    private VisibleDemoRunner() {
    }

    public static void start(ServerPlayer player, int fromIndex) {
        stop(player);
        List<VisibleDemoCase> cases = VisibleDemoCases.all();
        int idx = Math.max(0, Math.min(fromIndex, cases.size()));
        int pause = PAUSE_PREFS.getOrDefault(player.getUUID(), DEFAULT_PAUSE_TICKS);
        Session session = new Session(player.getUUID(), cases, idx, pause);
        SESSIONS.put(player.getUUID(), session);
        session.phase = Phase.ANNOUNCE;
        session.waitTicks = 10;
        announce(player, session);
    }

    public static boolean stop(ServerPlayer player) {
        Session session = SESSIONS.remove(player.getUUID());
        if (session == null) {
            return false;
        }
        cleanupStage(player.getLevel(), session);
        title(player, "§cDemo stopped", "");
        return true;
    }

    public static boolean skipWait(ServerPlayer player) {
        Session session = SESSIONS.get(player.getUUID());
        if (session == null) {
            return false;
        }
        session.waitTicks = 0;
        return true;
    }

    public static void setPauseTicks(ServerPlayer player, int ticks) {
        PAUSE_PREFS.put(player.getUUID(), ticks);
        Session session = SESSIONS.get(player.getUUID());
        if (session != null) {
            session.pauseTicks = ticks;
        }
    }

    public static int getPauseTicks(ServerPlayer player) {
        Session session = SESSIONS.get(player.getUUID());
        if (session != null) {
            return session.pauseTicks;
        }
        return PAUSE_PREFS.getOrDefault(player.getUUID(), DEFAULT_PAUSE_TICKS);
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || SESSIONS.isEmpty()) {
            return;
        }
        Iterator<Map.Entry<UUID, Session>> it = SESSIONS.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<UUID, Session> entry = it.next();
            Session session = entry.getValue();
            ServerPlayer player = event.getServer().getPlayerList().getPlayer(entry.getKey());
            if (player == null) {
                it.remove();
                continue;
            }
            tickSession(player, session, it);
        }
    }

    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            Session session = SESSIONS.remove(player.getUUID());
            if (session != null) {
                cleanupStage(player.getLevel(), session);
            }
        }
    }

    private static void tickSession(ServerPlayer player, Session session, Iterator<?> it) {
        if (session.waitTicks > 0) {
            session.waitTicks--;
            // keep camera locked on focus while waiting so the player can walk a bit without losing the shot
            if (session.focus != null && session.phase == Phase.HOLD) {
                lookAt(player, session.focus);
            }
            return;
        }

        switch (session.phase) {
            case ANNOUNCE -> {
                if (session.index >= session.cases.size()) {
                    finish(player, session, it);
                    return;
                }
                VisibleDemoCase demoCase = session.cases.get(session.index);
                title(player, "§b[" + (session.index + 1) + "/" + session.cases.size() + "]§r " + demoCase.title(),
                        "§7" + demoCase.subtitle());
                player.sendSystemMessage(Component.literal("§b[TA Demo]§r §f" + demoCase.title() + "§r — §7" + demoCase.subtitle()));
                session.phase = Phase.SETUP;
                session.waitTicks = 25; // ~1.25s to read the title
            }
            case SETUP -> {
                VisibleDemoCase demoCase = session.cases.get(session.index);
                cleanupStage(player.getLevel(), session);
                Stage stage = buildStageInView(player, demoCase.stageDepth(), demoCase.stageWidth(), demoCase.stageHeight());
                session.stage = stage;
                session.trackedEntities.clear();
                try {
                    DemoContext ctx = new DemoContext(player, player.getLevel(), stage, session.trackedEntities);
                    demoCase.setup(ctx);
                    session.focus = ctx.focusOrCenter();
                    lookAt(player, session.focus);
                    session.phase = Phase.RUN;
                    session.waitTicks = 5;
                } catch (Exception ex) {
                    failCase(player, session, ex);
                }
            }
            case RUN -> {
                VisibleDemoCase demoCase = session.cases.get(session.index);
                try {
                    DemoContext ctx = new DemoContext(player, player.getLevel(), session.stage, session.trackedEntities);
                    ctx.focus = session.focus;
                    demoCase.run(ctx);
                    session.focus = ctx.focusOrCenter();
                    lookAt(player, session.focus);
                    boolean ok = demoCase.verify(ctx);
                    if (ok) {
                        player.sendSystemMessage(Component.literal("§a✔ PASS§r §7" + demoCase.title()).withStyle(ChatFormatting.GREEN));
                        title(player, "§a✔ PASS", demoCase.title());
                        session.passed++;
                    } else {
                        player.sendSystemMessage(Component.literal("§c✘ FAIL§r §7" + demoCase.title() + " — " + ctx.failReason));
                        title(player, "§c✘ FAIL", ctx.failReason == null ? demoCase.title() : ctx.failReason);
                        session.failed++;
                    }
                    session.phase = Phase.HOLD;
                    session.waitTicks = session.pauseTicks;
                } catch (Exception ex) {
                    failCase(player, session, ex);
                }
            }
            case HOLD -> {
                session.index++;
                session.phase = Phase.ANNOUNCE;
                session.waitTicks = 15;
            }
            case DONE -> {
                // no-op
            }
        }
    }

    private static void failCase(ServerPlayer player, Session session, Exception ex) {
        VisibleDemoCase demoCase = session.cases.get(session.index);
        TheAurorian.LOGGER.error("Demo case failed: {}", demoCase.id(), ex);
        player.sendSystemMessage(Component.literal("§c✘ ERROR§r §7" + demoCase.title() + " — " + ex.getClass().getSimpleName() + ": " + ex.getMessage()));
        title(player, "§c✘ ERROR", demoCase.title());
        session.failed++;
        session.phase = Phase.HOLD;
        session.waitTicks = session.pauseTicks;
    }

    private static void finish(ServerPlayer player, Session session, Iterator<?> it) {
        cleanupStage(player.getLevel(), session);
        String summary = "§b[TA Demo]§r Done — §a" + session.passed + " passed§r, §c" + session.failed + " failed§r / " + session.cases.size();
        player.sendSystemMessage(Component.literal(summary));
        title(player, session.failed == 0 ? "§aAll demos passed" : "§eDemo finished",
                session.passed + " ok · " + session.failed + " fail");
        it.remove();
    }

    private static void announce(ServerPlayer player, Session session) {
        player.sendSystemMessage(Component.literal(
                "§b[TA Demo]§r " + session.cases.size() + " cases · pause " + session.pauseTicks + "t · stage spawns in front of you"));
    }

    private static void title(ServerPlayer player, String title, String subtitle) {
        player.connection.send(new net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket(10, 45, 15));
        player.connection.send(new net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket(Component.literal(title)));
        player.connection.send(new net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket(Component.literal(subtitle)));
    }

    private static void lookAt(ServerPlayer player, Vec3 target) {
        player.lookAt(EntityAnchorArgument.Anchor.EYES, target);
    }

    /**
     * Flat platform a few blocks in front of the player's look direction,
     * always in front of the camera.
     */
    private static Stage buildStageInView(ServerPlayer player, int depth, int width, int height) {
        ServerLevel level = player.getLevel();
        Direction facing = player.getDirection(); // cardinal
        BlockPos feet = player.blockPosition();
        // origin = front-left corner of the stage floor
        BlockPos front = feet.relative(facing, 4);
        Direction right = facing.getClockWise();
        BlockPos origin = front.relative(right, -(width / 2));

        // clear volume + lay stone floor + barrier rails for readability
        List<BlockPos> touched = new ArrayList<>();
        for (int z = 0; z < depth; z++) {
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height + 2; y++) {
                    BlockPos p = origin.relative(right, x).relative(facing, z).above(y);
                    level.setBlock(p, Blocks.AIR.defaultBlockState(), 3);
                    touched.add(p.immutable());
                }
                BlockPos floor = origin.relative(right, x).relative(facing, z);
                level.setBlock(floor, Blocks.SMOOTH_STONE.defaultBlockState(), 3);
                touched.add(floor.immutable());
            }
        }
        // glowstone under center so the stage is lit
        BlockPos lamp = origin.relative(right, width / 2).relative(facing, depth / 2).below();
        level.setBlock(lamp, Blocks.GLOWSTONE.defaultBlockState(), 3);
        touched.add(lamp.immutable());

        Vec3 center = Vec3.atCenterOf(origin.relative(right, width / 2).relative(facing, depth / 2).above(1));
        return new Stage(origin, facing, right, width, depth, height, center, touched);
    }

    private static void cleanupStage(ServerLevel level, Session session) {
        if (session.stage != null) {
            for (BlockPos p : session.stage.touched) {
                if (level.isLoaded(p)) {
                    level.setBlock(p, Blocks.AIR.defaultBlockState(), 3);
                }
            }
            // kill leftovers in stage AABB
            AABB box = session.stage.bounds().inflate(1);
            for (Entity e : level.getEntities(null, box)) {
                if (!(e instanceof ServerPlayer)) {
                    e.discard();
                }
            }
        }
        for (Entity e : session.trackedEntities) {
            if (e != null && e.isAlive()) {
                e.discard();
            }
        }
        session.trackedEntities.clear();
        session.stage = null;
        session.focus = null;
    }

    // ------------------------------------------------------------------ types

    enum Phase { ANNOUNCE, SETUP, RUN, HOLD, DONE }

    static final class Session {
        final UUID playerId;
        final List<VisibleDemoCase> cases;
        int index;
        int pauseTicks;
        int waitTicks;
        int passed;
        int failed;
        Phase phase = Phase.DONE;
        Stage stage;
        Vec3 focus;
        final List<Entity> trackedEntities = new ArrayList<>();

        Session(UUID playerId, List<VisibleDemoCase> cases, int index, int pauseTicks) {
            this.playerId = playerId;
            this.cases = cases;
            this.index = index;
            this.pauseTicks = pauseTicks;
        }
    }

    public static final class Stage {
        public final BlockPos origin;
        public final Direction forward;
        public final Direction right;
        public final int width;
        public final int depth;
        public final int height;
        public final Vec3 center;
        public final List<BlockPos> touched;

        Stage(BlockPos origin, Direction forward, Direction right, int width, int depth, int height, Vec3 center, List<BlockPos> touched) {
            this.origin = origin;
            this.forward = forward;
            this.right = right;
            this.width = width;
            this.depth = depth;
            this.height = height;
            this.center = center;
            this.touched = touched;
        }

        /** Local (x right, y up, z forward) → world pos on/above the floor. */
        public BlockPos pos(int x, int y, int z) {
            return origin.relative(right, x).relative(forward, z).above(y);
        }

        public Vec3 centerOf(int x, int y, int z) {
            return Vec3.atCenterOf(pos(x, y, z));
        }

        public AABB bounds() {
            BlockPos a = origin;
            BlockPos b = origin.relative(right, width - 1).relative(forward, depth - 1).above(height);
            return new AABB(a, b.offset(1, 1, 1));
        }

        public void set(ServerLevel level, int x, int y, int z, BlockState state) {
            BlockPos p = pos(x, y, z);
            level.setBlock(p, state, 3);
            touched.add(p.immutable());
        }
    }

    public static final class DemoContext {
        public final ServerPlayer player;
        public final ServerLevel level;
        public final Stage stage;
        public final List<Entity> tracked;
        public Vec3 focus;
        public String failReason;

        DemoContext(ServerPlayer player, ServerLevel level, Stage stage, List<Entity> tracked) {
            this.player = player;
            this.level = level;
            this.stage = stage;
            this.tracked = tracked;
            this.focus = stage == null ? player.position() : stage.center;
        }

        public void focusOn(BlockPos p) {
            this.focus = Vec3.atCenterOf(p);
        }

        public void focusOn(Entity e) {
            this.focus = e.position().add(0, e.getBbHeight() * 0.5, 0);
        }

        public void focusOn(Vec3 v) {
            this.focus = v;
        }

        public Vec3 focusOrCenter() {
            return focus != null ? focus : stage.center;
        }

        public void track(Entity e) {
            tracked.add(e);
        }

        public void fail(String reason) {
            this.failReason = reason;
        }
    }
}
