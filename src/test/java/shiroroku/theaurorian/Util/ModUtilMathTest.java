package shiroroku.theaurorian.Util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pure math/helper tests that do not need a Minecraft bootstrap beyond classes on the classpath.
 */
class ModUtilMathTest {

    @Test
    void waveIsBoundedByAmplitude() {
        for (float t = 0f; t < 20f; t += 0.25f) {
            float v = ModUtil.wave(t, 1.0f, 2.0f);
            assertTrue(v >= -2.0001f && v <= 2.0001f, "wave out of bounds: " + v);
        }
    }

    @Test
    void waveAtZeroIsZero() {
        assertEquals(0f, ModUtil.wave(0f, 1.0f, 5.0f), 0.0001f);
    }

    @Test
    void waveZeroAmplitudeIsAlwaysZero() {
        assertEquals(0f, ModUtil.wave(3.5f, 2.0f, 0f), 0.0001f);
    }
}
