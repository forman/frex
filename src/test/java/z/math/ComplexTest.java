package z.math;

import junit.framework.TestCase;
import z.math.term.Real;

public class ComplexTest extends TestCase {

    public void testComplex() {
        Real x = new Real(2.4);
        Real y = new Real(-0.3);
        Complex complex = new Complex(x, y);
        assertSame(x, complex.getX());
        assertSame(y, complex.getY());
    }
}
