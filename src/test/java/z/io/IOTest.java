package z.io;

import junit.framework.TestCase;
import org.jdom.Element;

public class IOTest extends TestCase {
    public void testIO() throws Exception {

        final IOManager iom = new IOManager();
        iom.alias("algo", Algo.class, new AlgoIO.Provider());

        final Element element = new Element("algo");
        element.setAttribute("threshold", String.valueOf(22.4));
        element.setAttribute("mode", String.valueOf(2));

        final Object o = iom.read(element);
        assertTrue(o instanceof Algo);
        Algo algo = (Algo) o;
        assertEquals(22.4, algo.getThreshold(), 1.0e-10);
        assertEquals(2, algo.getMode());

        algo.setThreshold(44.2);
        algo.setMode(4);

        final Element element2 = iom.write(algo);
        assertEquals("44.2", element2.getAttributeValue("threshold"));
        assertEquals("4", element2.getAttributeValue("mode"));
    }
}
