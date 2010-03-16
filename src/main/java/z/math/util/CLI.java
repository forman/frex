/**
 * Created by IntelliJ IDEA.
 * User: fomferra
 * Date: Jan 6, 2003
 * Time: 11:28:53 PM
 * To change this template use Options | File Templates.
 */
package z.math.util;

import z.StringLiterals;
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
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class CLI {

    public static void main(String[] args) {

        final Namespace parserNamespace = new Namespace();
        final Symbol derivVar = parserNamespace.addSymbol("z");  // NON-NLS
        final Symbol complexUnit = parserNamespace.addSymbol("i"); // NON-NLS
        final Parser parser = createParser(parserNamespace);

        final Namespace complexNamespace = new Namespace();

        final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            System.out.print("\n> ");// NON-NLS
            System.out.flush();

            String line = null;
            try {
                line = in.readLine();
            } catch (IOException e) {
                System.out.println(MessageFormat.format(StringLiterals.getString("cli.error.0"), e.getMessage()));
                System.exit(-1);
            }
            if (line.equalsIgnoreCase("exit")) {  // NON-NLS
                break;
            }

            Term term = null;
            try {
                term = parser.parse(line);
            } catch (ParseException e) {
                System.out.println(MessageFormat.format(StringLiterals.getString("cli.error.0"), e.getMessage()));
            }

            if (term != null) {
                System.out.println(MessageFormat.format(StringLiterals.getString("cli.out.input.0"), term));
                System.out.println(MessageFormat.format(StringLiterals.getString("cli.stdout.simp.input.0"), term.simplify()));
                final Term d = term.differentiate(derivVar);
                System.out.println(MessageFormat.format(StringLiterals.getString("cli.stdout.deriv.0"), d));
                System.out.println(MessageFormat.format(StringLiterals.getString("cli.stdout.simp.deriv.0"), d.simplify()));
                final Complex z = term.createComplex(complexNamespace,
                                                     complexUnit);
                Term zx = z.getX();
                Term zy = z.getY();
                System.out.println(MessageFormat.format(StringLiterals.getString("cli.stdout.compl.x.0"), zx));
                System.out.println(MessageFormat.format(StringLiterals.getString("cli.stdout.compl.y.0"), zy));
                zx = zx.simplify();
                zy = zy.simplify();
                System.out.println(MessageFormat.format(StringLiterals.getString("cli.stdout.simp.compl.x.0"), zx));
                System.out.println(MessageFormat.format(StringLiterals.getString("cli.stdout.simp.compl.y.0"), zy));
                zx = Optimize.expandPowByIntExp(zx);
                zy = Optimize.expandPowByIntExp(zy);
                System.out.println(MessageFormat.format(StringLiterals.getString("cli.stdout.opt.simp.compl.x.0"), zx));
                System.out.println(MessageFormat.format(StringLiterals.getString("cli.stdout.opt.simp.compl.y.0"), zy));

                List<Symbol> varList = new ArrayList<Symbol>(32);
                varList.add(Symbol.createVariable("zzx", zx)); // NON-NLS
                varList.add(Symbol.createVariable("zzy", zy)); // NON-NLS
                int count = Optimize.replaceTermOccurences(varList, "t"); // NON-NLS
                System.out.println(MessageFormat.format(StringLiterals.getString("cli.stdout.0.term.replacements.applied"), count));
                for (Symbol var : varList) {
                    System.out.println(MessageFormat.format("{0} = {1};",  // NON-NLS
                                                            var.getName(), var.getValue()));
                }
            }
        }
    }

    private static ParserImpl createParser(Namespace parserNamespace) {
        return new ParserImpl(parserNamespace);
    }

}
