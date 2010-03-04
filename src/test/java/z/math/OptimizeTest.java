/**
 * Created by IntelliJ IDEA.
 * User: fomferra
 * Date: Jan 9, 2003
 * Time: 8:47:26 PM
 * To change this template use Options | File Templates.
 */
package z.math;

import junit.framework.TestCase;
import static z.math.Optimize.expandPowByIntExp;
import static z.math.Optimize.replaceTermOccurences;
import z.math.term.Term;

import java.util.ArrayList;
import java.util.List;

public class OptimizeTest extends TestCase {
    public void testReplaceTermOccurences() {
        List<Symbol> varList;

        //////////////////////////////////////
        // z^3 + c

        varList = new ArrayList<Symbol>(32);
        varList.add(Symbol.createVariable("zzx", term("zx * (zx * zx - zy * zy) - zy * (2 * (zx * zy)) + cx")));
        varList.add(Symbol.createVariable("zzy", term("zx * (2 * (zx * zy)) + (zx * zx - zy * zy) * zy + cy")));
        replaceTermOccurences(varList, "tmp");
        assertEquals(4, varList.size());

        assertEquals("tmp1", varList.get(0).getName());
        assertEquals("zx * zx - zy * zy", varList.get(0).getValue().toString());

        assertEquals("tmp2", varList.get(1).getName());
        assertEquals("2 * (zx * zy)", varList.get(1).getValue().toString());

        assertEquals("zzx", varList.get(2).getName());
        assertEquals("zx * tmp1 - zy * tmp2 + cx", varList.get(2).getValue().toString());

        assertEquals("zzy", varList.get(3).getName());
        assertEquals("zx * tmp2 + tmp1 * zy + cy", varList.get(3).getValue().toString());

        //////////////////////////////////////
        // z^3 + c with u1, u2

        varList = new ArrayList<Symbol>(32);
        varList.add(Symbol.createVariable("u1", term("zx * zx")));
        varList.add(Symbol.createVariable("u2", term("zy * zy")));
        varList.add(Symbol.createVariable("zzx", term("zx * (zx * zx - zy * zy) - zy * (2 * (zx * zy)) + cx")));
        varList.add(Symbol.createVariable("zzy", term("zx * (2 * (zx * zy)) + (zx * zx - zy * zy) * zy + cy")));
        replaceTermOccurences(varList, "u");
        assertEquals(6, varList.size());

        assertEquals("u1", varList.get(0).getName());
        assertEquals("zx * zx", varList.get(0).getValue().toString());

        assertEquals("u2", varList.get(1).getName());
        assertEquals("zy * zy", varList.get(1).getValue().toString());

        assertEquals("u3", varList.get(2).getName());
        assertEquals("u1 - u2", varList.get(2).getValue().toString());

        assertEquals("u4", varList.get(3).getName());
        assertEquals("2 * (zx * zy)", varList.get(3).getValue().toString());

        assertEquals("zzx", varList.get(4).getName());
        assertEquals("zx * u3 - zy * u4 + cx", varList.get(4).getValue().toString());

        assertEquals("zzy", varList.get(5).getName());
        assertEquals("zx * u4 + u3 * zy + cy", varList.get(5).getValue().toString());


        //////////////////////////////////////
        // z^2 + c  with u1, u2

        varList = new ArrayList<Symbol>(32);
        varList.add(Symbol.createVariable("u1", term("zx * zx")));
        varList.add(Symbol.createVariable("u2", term("zy * zy")));
        varList.add(Symbol.createVariable("zzx", term("zx * zx - zy * zy + cx")));
        varList.add(Symbol.createVariable("zzy", term("2 * (zx * zy) + cy")));
        replaceTermOccurences(varList, "u");
        assertEquals(4, varList.size());

        assertEquals("u1", varList.get(0).getName());
        assertEquals("zx * zx", varList.get(0).getValue().toString());

        assertEquals("u2", varList.get(1).getName());
        assertEquals("zy * zy", varList.get(1).getValue().toString());

        assertEquals("zzx", varList.get(2).getName());
        assertEquals("u1 - u2 + cx", varList.get(2).getValue().toString());

        assertEquals("zzy", varList.get(3).getName());
        assertEquals("2 * (zx * zy) + cy", varList.get(3).getValue().toString());

    }

    public void testExpandPowWithIntExp() {
        assertEquals(term("1"), expandPowByIntExp(term("x^0")));
        assertEquals(term("x"), expandPowByIntExp(term("x^1")));
        assertEquals(term("x*x"), expandPowByIntExp(term("x^2")));
        assertEquals(term("(x*x)*x"), expandPowByIntExp(term("x^3")));
        assertEquals(term("((((((((x*x)*x)*x)*x)*x)*x)*x)*x)*x"), expandPowByIntExp(term("x^10")));
        assertEquals(term("x^11"), expandPowByIntExp(term("x^11")));

        assertEquals(term("1"), expandPowByIntExp(term("x^-0")));
        assertEquals(term("1/x"), expandPowByIntExp(term("x^-1")));
        assertEquals(term("1/(x*x)"), expandPowByIntExp(term("x^-2")));
        assertEquals(term("1/((x*x)*x)"), expandPowByIntExp(term("x^-3")));
        assertEquals(term("1/(((((((((x*x)*x)*x)*x)*x)*x)*x)*x)*x)"), expandPowByIntExp(term("x^-10")));
        assertEquals(term("x^-11"), expandPowByIntExp(term("x^-11")));

        assertEquals(term("43.2"), expandPowByIntExp(term("43.2")));
        assertEquals(term("x - 3"), expandPowByIntExp(term("x - 3")));
    }

    private Parser parser;

    @Override
    protected void setUp() throws Exception {
        parser = new ParserImpl(new Namespace());
    }

    private Term term(String code) {
        try {
            return parser.parse(code);
        } catch (ParseException e) {
            throw new IllegalArgumentException("unexpected ParseException: " + e.getMessage());
        }
    }
}
