/**
 * Created by IntelliJ IDEA.
 * User: Norman
 * Date: 06.01.2004
 * Time: 10:23:40
 * To change this template use Options | File Templates.
 */
package z.core;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.jdom.JDOMException;
import z.core.color.RGBA;
import z.core.support.accumulators.LinearOrbitTrap;
import z.core.support.colorizers.PaletteColorTable;
import z.core.support.fractals.Mandelbrot;
import z.core.support.indexers.AbsZ;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

@SuppressWarnings({"HardCodedStringLiteral"})
public class ProjectTest extends TestCase {

    public ProjectTest(String s) {
        super(s);
    }

    public static Test suite() {
        return new TestSuite(ProjectTest.class);
    }

    public void testProjectFileReader() throws IOException, JDOMException, URISyntaxException {
        final String projectResourcePath = "/z/core/test.zpr";

        URL url = getClass().getResource(projectResourcePath);
        URI uri = new URL(url.toExternalForm().replace(" ", "%20")).toURI();
        File file = new File(uri);

        Project project = Project.readProject(file);
        PaletteColorTable paletteColorTable;

        assertNotNull(project);
        assertEquals("test", project.getName());

        assertNotNull(project.getImageInfo());
        assertEquals("test.png", project.getImageInfo().getImagePath());
        assertEquals("PNG", project.getImageInfo().getImageFormat());
        assertEquals(1280, project.getImageInfo().getImageWidth());
        assertEquals(1024, project.getImageInfo().getImageHeight());

//        final Colorizer palette1 = project.getColorTableManager().getColorTable(0);
//        assertEquals("palette-1", palette1.getName());
//        assertEquals(256, palette1.getNumColors());
//        assertEquals(3, palette1.getColorPointCount());
//
//        final Colorizer palette2 = project.getColorTableManager().getColorTable(1);
//        assertEquals("palette-2", palette2.getName());
//        assertEquals(512, palette2.getNumColors());
//        assertEquals(5, palette2.getColorPointCount());

        assertEquals(3, project.getNumPlanes());

        final Plane plane1 = project.getPlane(0);
        assertEquals("plane-1", plane1.getName());
        assertEquals(true, plane1.isVisible());
        final Region region1 = plane1.getRegion();
        assertNotNull(region1);
        assertEquals(-0.75, region1.getCenterX(), 1e-10);
        assertEquals(0.25, region1.getCenterY(), 1e-10);
        assertEquals(2.0, region1.getRadius(), 1e-10);
        assertNotNull(plane1.getColorizer());
        assertTrue(plane1.getColorizer() instanceof PaletteColorTable);
        paletteColorTable = (PaletteColorTable) plane1.getColorizer();
        assertEquals(512, paletteColorTable.getNumColors());
        assertEquals(5, paletteColorTable.getColorPointCount());
        assertEquals(0.0, paletteColorTable.getColorPoint(0).getPosition(), 1e-10);
        assertEquals(0.25, paletteColorTable.getColorPoint(1).getPosition(), 1e-10);
        assertEquals(0.5, paletteColorTable.getColorPoint(2).getPosition(), 1e-10);
        assertEquals(0.75, paletteColorTable.getColorPoint(3).getPosition(), 1e-10);
        assertEquals(1.0, paletteColorTable.getColorPoint(4).getPosition(), 1e-10);
        assertEquals(RGBA.RED, paletteColorTable.getColorPoint(0).getColor());
        assertEquals(RGBA.ORANGE, paletteColorTable.getColorPoint(1).getColor());
        assertEquals(RGBA.BLUE, paletteColorTable.getColorPoint(2).getColor());
        assertEquals(RGBA.YELLOW, paletteColorTable.getColorPoint(3).getColor());
        assertEquals(RGBA.GREEN, paletteColorTable.getColorPoint(4).getColor());

        assertTrue(plane1.getFractal() instanceof Mandelbrot);
        assertTrue(plane1.getIndexer() instanceof AbsZ);
        assertTrue(plane1.getAccumulator() instanceof LinearOrbitTrap);
        final Mandelbrot mandelbrot1 = (Mandelbrot) plane1.getFractal();
        assertEquals(1001, mandelbrot1.getIterMax());
        assertEquals(true, mandelbrot1.isJuliaMode());
        assertEquals(-0.75, mandelbrot1.getJuliaCX(), 1e-10);
        assertEquals(0.15, mandelbrot1.getJuliaCY(), 1e-10);

        final Plane plane2 = project.getPlane(1);
        assertEquals("plane-2", plane2.getName());
        assertEquals(true, plane2.isVisible());
        final Region region2 = plane1.getRegion();
        assertNotNull(region2);
        assertEquals(-0.75, region2.getCenterX(), 1e-10);
        assertEquals(0.25, region2.getCenterY(), 1e-10);
        assertEquals(2.0, region2.getRadius(), 1e-10);

        assertTrue(plane2.getFractal() instanceof Mandelbrot);
        final Mandelbrot mandelbrot2 = (Mandelbrot) plane2.getFractal();
        assertEquals(1002, mandelbrot2.getIterMax());
        assertEquals(true, mandelbrot2.isJuliaMode());
        assertEquals(-0.5, mandelbrot2.getJuliaCX(), 1e-10);
        assertEquals(0.35, mandelbrot2.getJuliaCY(), 1e-10);

        final Plane plane3 = project.getPlane(2);
        assertEquals("plane-3", plane3.getName());
        assertEquals(false, plane3.isVisible());
    }

}

