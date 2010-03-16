package z.math.term.functors;

import z.math.Complex;
import z.math.Namespace;
import z.math.Simplify;
import z.math.Symbol;
import z.math.term.Functor;
import z.math.term.Term;

public class Sin extends Functor {

    public Sin() {
        super("sin", 1); // NON-NLS
    }

    @Override
    public final double evaluate(Term[] args) {
        return Math.sin(args[0].evaluate());
    }

    @Override
    public Term simplify(Term[] args) {
        return Simplify.sin(args[0].simplify());
    }

    @Override
    public Term differentiate(Symbol var, Term[] args) {
        Term inner = args[0].differentiate(var);
        Term outer = cos(args[0]);
        return mul(inner, outer);
    }

    @Override
    public Complex createComplex(Namespace namespace, Symbol unitSymbol, Term[] args) {
        return Complex.sin(args[0].createComplex(namespace, unitSymbol));
    }
}
