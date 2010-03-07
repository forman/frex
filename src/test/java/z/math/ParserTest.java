/**
 * Created by IntelliJ IDEA.
 * User: fomferra
 * Date: Jan 9, 2003
 * Time: 8:47:26 PM
 * To change this template use Options | File Templates.
 */
package z.math;

import junit.framework.TestCase;
import z.math.term.Functor;
import z.math.term.Term;

import static z.math.term.Functor.*;

@SuppressWarnings({"HardCodedStringLiteral"})
public class ParserTest extends TestCase {

    public void testNum() {
        testEquals("3.65", num(3.65));
        final double value1 = +3.65;
        testEquals("+3.65", num(value1));
        final double value = -3.65;
        testEquals("-3.65", num(value));
    }


    public void testNeg() {
        testEquals("-x", neg(ref("x")));
        testEquals("--x", neg(neg(ref("x"))));
        testEquals("-(-x)", "--x");
    }

    public void testAdd() {
        testEquals("a + 2", add(ref("a"), num((double) 2)));
        testEquals("a + -b", add(ref("a"), neg(ref("b"))));
        testEquals("-a + b", add(neg(ref("a")), ref("b")));
        testEquals("a + (b + 2)", add(ref("a"), add(ref("b"), num((double) 2))));
        testEquals("(a + b) + 2", add(add(ref("a"), ref("b")), num((double) 2)));
        testEquals("a + b + c + d", "((a + b) + c) + d");
    }

    public void testSub() {
        testEquals("a - 2", sub(ref("a"), num((double) 2)));
        testEquals("a - -b", sub(ref("a"), neg(ref("b"))));
        testEquals("-a - b", sub(neg(ref("a")), ref("b")));
        testEquals("a - (b - 2)", sub(ref("a"), sub(ref("b"), num((double) 2))));
        testEquals("(a - b) - 2", sub(sub(ref("a"), ref("b")), num((double) 2)));
        testEquals("a - b - c - d", "((a - b) - c) - d");
    }

    public void testMul() {
        testEquals("a * 2", mul(ref("a"), num((double) 2)));
        testEquals("a * -b", mul(ref("a"), neg(ref("b"))));
        testEquals("-a * b", mul(neg(ref("a")), ref("b")));
        testEquals("a * (b * 2)", mul(ref("a"), mul(ref("b"), num((double) 2))));
        testEquals("(a * b) * 2", mul(mul(ref("a"), ref("b")), num((double) 2)));
        testEquals("a * b * c * d", "((a * b) * c) * d");
    }

    public void testDiv() {
        testEquals("a / 2", div(ref("a"), num((double) 2)));
        testEquals("a / -b", div(ref("a"), neg(ref("b"))));
        testEquals("-a / b", div(neg(ref("a")), ref("b")));
        testEquals("a / (b / 2)", div(ref("a"), div(ref("b"), num((double) 2))));
        testEquals("(a / b) / 2", div(div(ref("a"), ref("b")), num((double) 2)));
        testEquals("a / b / c / d", "((a / b) / c) / d");
    }

    public void testPow() {
        testEquals("a ^ 2", pow(ref("a"), num((double) 2)));
        testEquals("a ^ -b", pow(ref("a"), neg(ref("b"))));
        testEquals("-a ^ b", pow(neg(ref("a")), ref("b")));
        testEquals("a ^ (b ^ 2)", pow(ref("a"), pow(ref("b"), num((double) 2))));
        testEquals("(a ^ b) ^ 2", pow(pow(ref("a"), ref("b")), num((double) 2)));
        testEquals("a ^ b ^ c ^ d", "((a ^ b) ^ c) ^ d");
    }

    public void testOpMix() {
        testEquals("-a + b - c * d / e ^ f", "((-a) + b) - ((c * d) / (e ^ f))");
        testEquals("a ^ b / c * d - e + -f", "((((a ^ b) / c) * d) - e) + (-f)");
    }

    private void testEquals(final String code, final Term expectedTerm) {
        try {
            assertEquals(expectedTerm, parser.parse(code));
        } catch (ParseException e) {
            e.printStackTrace();
            fail("unexpected ParseException: " + e.getMessage());
        }
    }

    private void testEquals(final String code, final String expectedExprCode) {
        Term expectedTerm = null;
        try {
            expectedTerm = parser.parse(expectedExprCode);
        } catch (ParseException e) {
            throw new IllegalArgumentException("illegal 'expectedExprCode': unexpected ParseException: " + e.getMessage());
        }
        testEquals(code, expectedTerm);
    }

    private Term ref(final String name) {
        return Functor.ref(defSym(name));
    }

    private Symbol defSym(final String name) {
        return parser.getNamespace().addSymbol(name);
    }

    private Parser parser;

    @Override
    protected void setUp() throws Exception {
        parser = new ParserImpl(new Namespace());
    }

}
