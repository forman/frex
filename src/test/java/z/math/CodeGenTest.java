package z.math;

import junit.framework.TestCase;
import z.math.term.Functor;
import z.math.term.Term;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"HardCodedStringLiteral"})
public class CodeGenTest extends TestCase {
    public void testIt() throws ParseException {

        String code = generateCode("z^2 + c");

        System.out.println("code\n" + code);

    }

    private String generateCode(String line) throws ParseException {
        final Namespace parserNamespace = new Namespace();
        final Symbol complexUnit = parserNamespace.addSymbol("i");
        final Parser parser = new ParserImpl(parserNamespace);
        final Namespace complexNamespace = new Namespace();
        final Term term = parser.parse(line);
        final Complex z = term.createComplex(complexNamespace, complexUnit);
        Term zx = z.getX();
        Term zy = z.getY();
        zx = zx.simplify();
        zy = zy.simplify();
        zx = Optimize.expandPowByIntExp(zx);
        zy = Optimize.expandPowByIntExp(zy);

        List<Symbol> varList = new ArrayList<Symbol>(32);
        Symbol szx = complexNamespace.getSymbol("zx");
        Symbol szy = complexNamespace.getSymbol("zy");
        varList.add(Symbol.createVariable("t1", Functor.mul(Functor.ref(szx), Functor.ref(szx))));
        varList.add(Symbol.createVariable("t2", Functor.mul(Functor.ref(szy), Functor.ref(szy))));
        varList.add(Symbol.createVariable("zzx", zx));
        varList.add(Symbol.createVariable("zzy", zy));
        Optimize.replaceTermOccurences(varList, "t");
        StringBuilder code = new StringBuilder();
        for (Symbol var : varList) {
            code.append("double " + var.getName() + ";\n");
        }
        for (Symbol var : varList) {
            code.append("    " + var.getName() + " = " + var.getValue() + ";\n");
        }
        return code.toString();
    }

}