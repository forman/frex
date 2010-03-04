/**
 * Created by IntelliJ IDEA.
 * User: fomferra
 * Date: Jan 6, 2003
 * Time: 11:28:53 PM
 * To change this template use Options | File Templates.
 */
package z.math.util;

import z.math.Complex;
import z.math.Namespace;
import z.math.Optimize;
import z.math.ParseException;
import z.math.Parser;
import z.math.ParserImpl;
import z.math.Symbol;
import z.math.term.Term;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CLI {

    public static void main(String[] args) {

        final Namespace parserNamespace = new Namespace();
        final Symbol derivVar = parserNamespace.addSymbol("z");
        final Symbol complexUnit = parserNamespace.addSymbol("i");
        final Parser parser = createParser(parserNamespace);

        final Namespace complexNamespace = new Namespace();

        final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            System.out.print("\n> ");
            System.out.flush();

            String line = null;
            try {
                line = in.readLine();
            } catch (IOException e) {
                System.out.println("Error: " + e.getMessage());
                System.exit(-1);
            }
            if (line.equalsIgnoreCase("exit")) {
                break;
            }

            Term term = null;
            try {
                term = parser.parse(line);
            } catch (ParseException e) {
                System.out.println("Error: " + e.getMessage());
            }

            if (term != null) {
                System.out.println("Input:        " + term);
                System.out.println("Simp. input:  " + term.simplify());
                final Term d = term.derivate(derivVar);
                System.out.println("Deriv.:       " + d);
                System.out.println("Simp.deriv.:  " + d.simplify());
                final Complex z = term.createComplex(complexNamespace,
                                                     complexUnit);
                Term zx = z.getX();
                Term zy = z.getY();
                System.out.println("Compl.X:      " + zx);
                System.out.println("Compl.Y:      " + zy);
                zx = zx.simplify();
                zy = zy.simplify();
                System.out.println("Simp.compl.X: " + zx);
                System.out.println("Simp.compl.Y: " + zy);
                zx = Optimize.expandPowByIntExp(zx);
                zy = Optimize.expandPowByIntExp(zy);
                System.out.println("Opt.simp.compl.X: " + zx);
                System.out.println("Opt.simp.compl.Y: " + zy);

                List<Symbol> varList = new ArrayList<Symbol>(32);
                varList.add(Symbol.createVariable("zzx", zx));
                varList.add(Symbol.createVariable("zzy", zy));
                int count = Optimize.replaceTermOccurences(varList, "t");
                System.out.println(count + " term. replacements applied:");
                for (Symbol var : varList) {
                    System.out.println(var.getName() + " = " + var.getValue()
                            + ";");
                }
            }
        }
    }

    private static ParserImpl createParser(Namespace parserNamespace) {
        return new ParserImpl(parserNamespace);
    }

}
