package shiroroku.theaurorian.Demo;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

/**
 * Visible, watchable client-side demo of ported features.
 * <p>
 * In-game (creative/op recommended):
 * <pre>
 *   /ta demo              — start full suite (look at the stage in front of you)
 *   /ta demo stop         — abort
 *   /ta demo next         — skip wait / jump to next case
 *   /ta demo pause 100    — set pause between cases in ticks (default 70 ≈ 3.5s)
 * </pre>
 * Each case builds in your line of sight, recenters the camera on the action,
 * and leaves enough time to see the result.
 */
public final class VisibleDemoCommand {

    private VisibleDemoCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("ta")
                        .requires(s -> s.hasPermission(2))
                        .then(Commands.literal("demo")
                                .executes(ctx -> start(ctx.getSource(), -1))
                                .then(Commands.literal("stop")
                                        .executes(ctx -> stop(ctx.getSource())))
                                .then(Commands.literal("next")
                                        .executes(ctx -> next(ctx.getSource())))
                                .then(Commands.literal("pause")
                                        .then(Commands.argument("ticks", IntegerArgumentType.integer(20, 400))
                                                .executes(ctx -> pause(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "ticks")))))
                                .then(Commands.literal("from")
                                        .then(Commands.argument("index", IntegerArgumentType.integer(0, 99))
                                                .executes(ctx -> start(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "index"))))))
        );
    }

    private static int start(CommandSourceStack source, int fromIndex) {
        ServerPlayer player = source.getPlayer();
        if (player == null) {
            source.sendFailure(Component.literal("Must be run by a player (so the stage stays in your view)."));
            return 0;
        }
        VisibleDemoRunner.start(player, fromIndex < 0 ? 0 : fromIndex);
        source.sendSuccess(() -> Component.literal(
                "§b[TA Demo]§r Starting visible feature demo — keep looking forward. " +
                        "Pause between cases: §e" + VisibleDemoRunner.getPauseTicks(player) + "t§r. " +
                        "§7(/ta demo stop | next | pause <ticks>)"), false);
        return 1;
    }

    private static int stop(CommandSourceStack source) {
        ServerPlayer player = source.getPlayer();
        if (player == null) {
            return 0;
        }
        if (VisibleDemoRunner.stop(player)) {
            source.sendSuccess(() -> Component.literal("§b[TA Demo]§r Stopped."), false);
            return 1;
        }
        source.sendFailure(Component.literal("No demo running."));
        return 0;
    }

    private static int next(CommandSourceStack source) {
        ServerPlayer player = source.getPlayer();
        if (player == null) {
            return 0;
        }
        if (VisibleDemoRunner.skipWait(player)) {
            source.sendSuccess(() -> Component.literal("§b[TA Demo]§r Skipping to next case…"), false);
            return 1;
        }
        source.sendFailure(Component.literal("No demo running."));
        return 0;
    }

    private static int pause(CommandSourceStack source, int ticks) {
        ServerPlayer player = source.getPlayer();
        if (player == null) {
            return 0;
        }
        VisibleDemoRunner.setPauseTicks(player, ticks);
        source.sendSuccess(() -> Component.literal("§b[TA Demo]§r Pause between cases set to §e" + ticks + "t§r (~" + String.format("%.1f", ticks / 20.0) + "s)."), false);
        return 1;
    }
}
