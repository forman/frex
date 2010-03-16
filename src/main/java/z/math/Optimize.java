/**
 * Created by IntelliJ IDEA.
 * User: fomferra
 * Date: Jan 12, 2003
 * Time: 10:37:35 AM
 * To change this template use Options | File Templates.
 */
package z.math;

import z.math.term.Functor;
import z.math.term.Ref;
import z.math.term.Struct;
import z.math.term.Term;

import java.util.List;

public class Optimize {

    static class Replacer {
        final List<Symbol> varList;
        final String varPrefix;
        int lastVarId;

        Replacer(List<Symbol> varList, String varPrefix) {
            this.varList = varList;
            this.varPrefix = varPrefix;
            this.lastVarId = 0;
            for (Symbol symbol : varList) {
                String name = symbol.getName();
                if (name.startsWith(varPrefix)) {
                    try {
                        int index = Integer.parseInt(name.substring(varPrefix.length()));
                        lastVarId = Math.max(lastVarId, index);
                    } catch (NumberFormatException e) {
                        // ok
                    }
                }
            }
        }

        public int replaceTermOccurences() {

            int numOccurences = 0;
            int n;
            do {
                n = varList.size();
                for (int i = 0; i < n; i++) {
                    final Symbol var = varList.get(i);
                    final Term testTerm = var.getValue();
                    numOccurences += replaceTermOccurences(testTerm, var);
                }
            } while (n < varList.size());
            return numOccurences;
        }

        private int replaceTermOccurences(final Term testTerm, Symbol var) {
            if (!testTerm.isStruct()) {
                return 0;
            }

            int numOccurencesTotal = 0;
            int insertPos = -1;
            int n = varList.size();
            for (int i = 0; i < n; i++) {
                final Symbol symbol = varList.get(i);
                final Term term = symbol.getValue();
                if (var != symbol && !term.equals(testTerm)) {
                    int numOccurences = term.countOccurences(testTerm);
                    if (numOccurences > 0 && insertPos == -1) {
                        insertPos = i;
                    }
                    numOccurencesTotal += numOccurences;
                }
            }

            if (var != null && numOccurencesTotal > 0 || numOccurencesTotal > 1) {
                Symbol refVar;
                if (var == null) {
                    refVar = Symbol.createVariable(varPrefix + (++lastVarId), testTerm);
                } else {
                    refVar = var;
                }
                final Ref varRef = new Ref(refVar);

                int m = varList.size();
                for (int i = 0; i < m; i++) {
                    final Symbol symbol = varList.get(i);
                    final Term oldTerm = symbol.getValue();
                    if (var != symbol && !oldTerm.equals(testTerm)) {
                        final Term newTerm = oldTerm.replaceOccurences(testTerm, varRef);
                        symbol.setValue(newTerm);
                    }
                }
                if (var == null) {
                    varList.add(insertPos, refVar);
                }
            } else {
                numOccurencesTotal = 0;
                if (testTerm.getArg1() != null) {
                    numOccurencesTotal += replaceTermOccurences(testTerm.getArg1(), null);
                }
                if (testTerm.getArg2() != null) {
                    numOccurencesTotal += replaceTermOccurences(testTerm.getArg2(), null);
                }
            }
            return numOccurencesTotal;
        }

    }

    public static int replaceTermOccurences(final List<Symbol> varList,
                                            String varPrefix) {
        Replacer replacer = new Replacer(varList, varPrefix);
        return replacer.replaceTermOccurences();
    }


    public static Term expandPowByIntExp(Term x) {
        if (!x.isStruct()) {
            return x;
        }
        Term[] terms = new Term[x.getArity()];
        for (int i = 0; i < terms.length; i++) {
            terms[i] = expandPowByIntExp(x.getArg(i));
        }
        if (x.isPow() && terms[1].isReal()) {
            final double realExp = terms[1].evaluate();
            int intExp = (int) Math.floor(realExp);
            final double delta = realExp - intExp;
            if (delta == 0.0 && intExp >= -10 && intExp <= 10) {
                if (intExp == 0) {
                    return Functor.num(1);
                }
                final int sign = intExp < 0 ? -1 : 1;
                intExp *= sign;
                Term x1 = terms[0];
                for (int i = 1; i < intExp; i++) {
                    x1 = Functor.mul(x1, terms[0]);
                }
                if (sign < 0) {
                    x1 = Functor.div(Functor.num(1), x1);
                }
                return x1;
            }
        }
        return ((Struct) x).createStruct(terms);
    }
}
