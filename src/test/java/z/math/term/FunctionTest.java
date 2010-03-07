package z.math.term;

import junit.framework.TestCase;
import z.math.Symbol;

import static z.math.term.Functor.*;

@SuppressWarnings({"HardCodedStringLiteral"})
public class FunctionTest extends TestCase {
    public void testFunction() {
        Symbol x = Symbol.createVariable("x", null);
        Function tan = new Function("tan", new Symbol[]{x}, div(sin(ref(x)), cos(ref(x))));

        assertEquals(Math.tan(0.5), tan.evaluate(new Term[]{num(0.5)}), 1.0e-10);
        assertEquals(Math.tan(0.5), new Struct(tan, num(0.5)).evaluate(), 1.0e-10);

        Term expandedBody = tan.getExpandedBody(new Term[]{num(2)});
        assertEquals(div(sin(num(2)), cos(num(2))), expandedBody);
    }
}