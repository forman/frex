package z.core;

import junit.framework.TestCase;

import java.io.File;

@SuppressWarnings({"HardCodedStringLiteral"})
public class PlaneTest extends TestCase {
    public void testDefaultConstructor() {
        final String path = "C:\\projects\\mandel.plane";
        Plane plane = new Plane(new File(path));
        assertEquals(new File(path), plane.getFile());
        assertEquals("mandel", plane.getName());
        assertTrue(plane.isVisible());
        assertNotNull(plane.getFractal());
        assertNotNull(plane.getRegion());
        assertNotNull(plane.getOuterColorizer());
        assertNotNull(plane.getImageInfo());
        assertEquals("PNG", plane.getImageInfo().getImageFormat());
        assertEquals(false, plane.getImageInfo().isUsingWindowSize());
        assertEquals("mandel", plane.getName());
        assertEquals(null, plane.getProject());
        assertEquals(null, plane.getIndexer());
        assertEquals(null, plane.getAccumulator());
    }
}
