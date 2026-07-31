package shiroroku.theaurorian.Util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pure-logic tests for {@link SimpleTimer}. Does not require a running client/server.
 */
class SimpleTimerTest {

    @Test
    void startsInactiveAndAtFullDuration() {
        SimpleTimer timer = new SimpleTimer(10f);
        assertFalse(timer.isActive());
        assertEquals(10f, timer.getTicks(), 0.0001f);
        assertEquals(0f, timer.getPercentageProgress(), 0.0001f);
    }

    @Test
    void startActivatesAndResetsTick() {
        SimpleTimer timer = new SimpleTimer(20f);
        timer.setTick(5f);
        timer.start();
        assertTrue(timer.isActive());
        assertEquals(20f, timer.getTicks(), 0.0001f);
        assertEquals(0f, timer.getPercentageProgress(), 0.0001f);
    }

    @Test
    void tickAdvancesProgressUntilComplete() {
        SimpleTimer timer = new SimpleTimer(10f);
        timer.start();
        timer.tick(4f);
        assertTrue(timer.isActive());
        assertEquals(6f, timer.getTicks(), 0.0001f);
        assertEquals(0.4f, timer.getPercentageProgress(), 0.0001f);

        timer.tick(6f);
        // Tick reaches 0 this frame; still marked inactive on next evaluation path
        assertEquals(0f, timer.getTicks(), 0.0001f);
        // one more tick flips inactive
        timer.tick(0.1f);
        assertFalse(timer.isActive());
        assertEquals(1f, timer.getPercentageProgress(), 0.0001f);
    }

    @Test
    void stopDeactivatesAndResets() {
        SimpleTimer timer = new SimpleTimer(10f);
        timer.start();
        timer.tick(3f);
        timer.stop();
        assertFalse(timer.isActive());
        assertEquals(10f, timer.getTicks(), 0.0001f);
        assertEquals(0f, timer.getPercentageProgress(), 0.0001f);
    }

    @Test
    void loopingTimerRestartsWhenExpired() {
        SimpleTimer timer = new SimpleTimer(5f, true);
        timer.start();
        timer.tick(5f);
        // expired this tick -> inactive then loop start() on same branch after inactive
        // Implementation: when Tick <= 0, IsActive=false then if loop start() which sets active again
        timer.tick(0.01f);
        assertTrue(timer.isActive());
        assertEquals(5f, timer.getTicks(), 0.0001f);
    }

    @Test
    void percentageIsClamped() {
        SimpleTimer timer = new SimpleTimer(10f);
        timer.start();
        timer.setTick(-5f);
        assertEquals(1f, timer.getPercentageProgress(), 0.0001f);
        timer.setTick(50f);
        assertEquals(0f, timer.getPercentageProgress(), 0.0001f);
    }

    @Test
    void tickWhileInactiveIsNoOp() {
        SimpleTimer timer = new SimpleTimer(10f);
        timer.tick(100f);
        assertFalse(timer.isActive());
        assertEquals(10f, timer.getTicks(), 0.0001f);
    }
}
