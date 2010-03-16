package z.math.term.functors;

import z.math.Complex;
import z.math.Namespace;
import z.math.Simplify;
import z.math.Symbol;
import z.math.term.Functor;
import z.math.term.Real;
import z.math.term.Term;

public class Re extends Functor {

    public Re() {
        super("re", 1); // NON-NLS
    }

    @Override
    public final double evaluate(Term[] args) {
        return args[0].evaluate();
    }

    @Override
    public Term simplify(Term[] args) {
        return Simplify.re(args[0].simplify());
    }

    @Override
    public Term differentiate(Symbol var, Term[] args) {
        // todo - fix this, it may not be correct
        return num(0);
    }

    @Override
    public Complex createComplex(Namespace namespace, Symbol unitSymbol, Term[] args) {
        return Complex.re(args[0].createComplex(namespace, unitSymbol));
    }
}