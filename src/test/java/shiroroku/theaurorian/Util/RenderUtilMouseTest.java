package shiroroku.theaurorian.Util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RenderUtilMouseTest {

    @Test
    void mouseInsideBounds() {
        assertTrue(RenderUtil.isMouseOver(10, 20, 30, 40, 10, 20));
        assertTrue(RenderUtil.isMouseOver(10, 20, 30, 40, 25, 35));
        assertTrue(RenderUtil.isMouseOver(10, 20, 30, 40, 39, 59));
    }

    @Test
    void mouseOutsideBounds() {
        assertFalse(RenderUtil.isMouseOver(10, 20, 30, 40, 8, 20));
        assertFalse(RenderUtil.isMouseOver(10, 20, 30, 40, 41, 20));
        assertFalse(RenderUtil.isMouseOver(10, 20, 30, 40, 10, 18));
        assertFalse(RenderUtil.isMouseOver(10, 20, 30, 40, 10, 62));
    }

    @Test
    void onePixelPaddingOnEdges() {
        // implementation uses >= (x-1) and < (x+w+1)
        assertTrue(RenderUtil.isMouseOver(10, 20, 30, 40, 9, 20));
        assertTrue(RenderUtil.isMouseOver(10, 20, 30, 40, 40, 20));
        assertFalse(RenderUtil.isMouseOver(10, 20, 30, 40, 8.9, 20));
    }
}
