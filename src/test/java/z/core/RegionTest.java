package z.core;

import junit.framework.TestCase;
import org.jdom.Element;
import org.jdom.JDOMException;
import z.util.JDOMHelper;


@SuppressWarnings({"HardCodedStringLiteral"})
public class RegionTest extends TestCase {
    private static final double EPS = 1e-10;

    public void testDefaultConstructor() {
        Region r = new Region();
        assertEquals(0.0, r.getCenterX(), EPS);
        assertEquals(0.0, r.getCenterY(), EPS);
        assertEquals(1.0, r.getRadius(), EPS);
    }

    public void test3ArgConstructor() {
        Region r = new Region(8.4, 3.2, 5.9);
        assertEquals(8.4, r.getCenterX(), EPS);
        assertEquals(3.2, r.getCenterY(), EPS);
        assertEquals(5.9, r.getRadius(), EPS);
    }

    public void testSetters() {
        Region r = new Region();
        r.set(1.1, 2.3, 3.4);
        assertEquals(1.1, r.getCenterX(), EPS);
        assertEquals(2.3, r.getCenterY(), EPS);
        assertEquals(3.4, r.getRadius(), EPS);

        r.setCenterX(-4.9);
        r.setCenterY(-4.5);
        r.setRadius(50);
        assertEquals(-4.9, r.getCenterX(), EPS);
        assertEquals(-4.5, r.getCenterY(), EPS);
        assertEquals(50, r.getRadius(), EPS);
    }

    public void testPositiveRadius() {
        Region r = new Region();

        try {
            r.setRadius(0);
            fail();
        } catch (IllegalArgumentException expected) {
        }

        try {
            r.setRadius(-4.2);
            fail();
        } catch (IllegalArgumentException expected) {
        }

        try {
            r.set(0, 0, 0);
            fail();
        } catch (IllegalArgumentException expected) {
        }

        try {
            r.set(0, 0, -3);
            fail();
        } catch (IllegalArgumentException expected) {
        }

        try {
            new Region(4, 6, -1);
            fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    public void testReadExternal() throws JDOMException {
        Region r = new Region();
        Element element = new Element("region");
        JDOMHelper.setAttributeDouble(element, "centerX", 0.38);
        JDOMHelper.setAttributeDouble(element, "centerY", -6.4);
        JDOMHelper.setAttributeDouble(element, "radius", 10.5);
        r.readExternal(element);
        assertEquals(0.38, r.getCenterX(), EPS);
        assertEquals(-6.4, r.getCenterY(), EPS);
        assertEquals(10.5, r.getRadius(), EPS);
    }

    public void testWriteExternal() throws JDOMException {
        Region r = new Region(1.5, 2.4, 6.1);
        Element element = new Element("region");
        r.writeExternal(element);
        assertEquals(1.5, JDOMHelper.getAttributeDouble(element, "centerX"), EPS);
        assertEquals(2.4, JDOMHelper.getAttributeDouble(element, "centerY"), EPS);
        assertEquals(6.1, JDOMHelper.getAttributeDouble(element, "radius"), EPS);
    }
}
