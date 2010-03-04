package z.math.term;

import junit.framework.TestCase;
import z.math.Namespace;
import z.math.ParseException;
import z.math.Parser;
import z.math.ParserImpl;
import static z.math.term.Functor.*;

public class TermTest extends TestCase {
    public void testToString() {
        assertEquals("2", num(2).toString());

        assertEquals("1 + 2 + 3", add(add(num(1), num(2)), num(3)).toString());
        assertEquals("1 + (2 + 3)", add(num(1), add(num(2), num(3))).toString());

        assertEquals("1 - 2 + 3", add(sub(num(1), num(2)), num(3)).toString());
        assertEquals("1 + (2 - 3)", add(num(1), sub(Functor.num(2), num(3))).toString());
        assertEquals("1 + 2 - 3", sub(add(num(1), num(2)), num(3)).toString());
        assertEquals("1 - (2 + 3)", sub(num(1), add(Functor.num(2), num(3))).toString());
    }


    public void testCountOpOccurences() {
        assertEquals(1, countTermOccurences("x+1", "x+1"));
        assertEquals(1, countTermOccurences("1+2*x", "2*x"));
        assertEquals(1, countTermOccurences("2*x+1", "2*x"));
        assertEquals(2, countTermOccurences("2*x+1+2*x", "2*x"));
    }

    public void testReplaceOpOccurences() {
        assertEquals(term("a"), replaceTermOccurences("x+1", "x+1", "a"));
        assertEquals(term("1+a"), replaceTermOccurences("1+2*x", "2*x", "a"));
        assertEquals(term("a+1"), replaceTermOccurences("2*x+1", "2*x", "a"));
        assertEquals(term("a+1+a"), replaceTermOccurences("2*x+1+2*x", "2*x", "a"));
        assertEquals(term("a-10*x/y*a"), replaceTermOccurences("(2+x/(3+2))-10*x/y*(2+x/(3+2))", "2+x/(3+2)", "a"));
        assertEquals(term("(2+x/a)-10*x/y*(2+x/a)"), replaceTermOccurences("(2+x/(3+2))-10*x/y*(2+x/(3+2))", "3+2", "a"));

        assertEquals(term("(a+1)*sin(a+1)"), replaceTermOccurences("x*sin(x)", "x", "a+1"));
    }

    public int countTermOccurences(String code1, String code2) {
        final Term x1 = term(code1);
        final Term x2 = term(code2);
        return x1.countOccurences(x2);
    }

    public Term replaceTermOccurences(String code1, String code2, String code3) {
        final Term x1 = term(code1);
        final Term x2 = term(code2);
        final Term x3 = term(code3);
        return x1.replaceOccurences(x2, x3);
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
