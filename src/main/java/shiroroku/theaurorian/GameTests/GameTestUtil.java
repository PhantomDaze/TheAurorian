package shiroroku.theaurorian.GameTests;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;

import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

/**
 * Shared helpers for in-world GameTests.
 */
public final class GameTestUtil {

    private GameTestUtil() {
    }

    public static Player survivalPlayer(GameTestHelper helper, BlockPos relativePos) {
        ServerLevel level = helper.getLevel();
        // Unique profile each call so equipment/state does not leak across tests.
        FakePlayer player = FakePlayerFactory.get(level, new GameProfile(UUID.randomUUID(), "ta-test-" + UUID.randomUUID()));
        player.setPos(Vec3.atBottomCenterOf(helper.absolutePos(relativePos)));
        player.setGameMode(GameType.SURVIVAL);
        player.getInventory().clearContent();
        return player;
    }

    public static void hold(Player player, ItemStack stack) {
        player.setItemInHand(InteractionHand.MAIN_HAND, stack);
    }

    public static void useBlockAs(GameTestHelper helper, BlockPos relativePos, Player player) {
        BlockPos abs = helper.absolutePos(relativePos);
        var state = helper.getLevel().getBlockState(abs);
        state.use(helper.getLevel(), player, InteractionHand.MAIN_HAND,
                new BlockHitResult(Vec3.atCenterOf(abs), Direction.NORTH, abs, true));
    }

    public static void useItemOn(GameTestHelper helper, BlockPos relativePos, Player player) {
        BlockPos abs = helper.absolutePos(relativePos);
        var ctx = new net.minecraft.world.item.context.UseOnContext(
                helper.getLevel(),
                player,
                InteractionHand.MAIN_HAND,
                player.getMainHandItem(),
                new BlockHitResult(Vec3.atCenterOf(abs), Direction.NORTH, abs, true));
        player.getMainHandItem().useOn(ctx);
    }

    @SuppressWarnings("unchecked")
    public static <T extends BlockEntity> T be(GameTestHelper helper, BlockPos relativePos, Class<T> type) {
        BlockEntity be = helper.getBlockEntity(relativePos);
        if (be == null || !type.isInstance(be)) {
            helper.fail("Expected block entity " + type.getSimpleName() + " at " + relativePos + " got " + be);
        }
        return (T) be;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Entity> List<T> entitiesOf(GameTestHelper helper, EntityType<T> type) {
        ServerLevel level = helper.getLevel();
        return (List<T>) level.getEntities(type, e -> true);
    }

    public static <T extends Entity> void assertAny(GameTestHelper helper, EntityType<T> type, Predicate<T> pred, String msg) {
        List<T> list = entitiesOf(helper, type);
        if (list.stream().noneMatch(pred)) {
            helper.fail(msg + " (found " + list.size() + " of type)");
        }
    }

    public static void assertTrue(GameTestHelper helper, boolean cond, String msg) {
        if (!cond) {
            helper.fail(msg);
        }
    }

    public static void assertEquals(GameTestHelper helper, Object expected, Object actual, String msg) {
        if (expected == null ? actual != null : !expected.equals(actual)) {
            helper.fail(msg + " expected=" + expected + " actual=" + actual);
        }
    }

    public static void assertApprox(GameTestHelper helper, double expected, double actual, double eps, String msg) {
        if (Math.abs(expected - actual) > eps) {
            helper.fail(msg + " expected~=" + expected + " actual=" + actual);
        }
    }
}
