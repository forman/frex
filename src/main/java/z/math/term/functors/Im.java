package z.math.term.functors;

import z.math.Complex;
import z.math.Namespace;
import z.math.Simplify;
import z.math.Symbol;
import z.math.term.Functor;
import z.math.term.Real;
import z.math.term.Term;

public class Im extends Functor {

    public Im() {
        super("im", 1); // NON-NLS
    }

    @Override
    public final double evaluate(Term[] args) {
        return 0.0;
    }

    @Override
    public Term simplify(Term[] args) {
        return Simplify.im(args[0].simplify());
    }

    @Override
    public Term differentiate(Symbol var, Term[] args) {
        // todo - fix this, it may not be correct
        return num(0);
    }

    @Override
    public Complex createComplex(Namespace namespace, Symbol unitSymbol, Term[] args) {
        return Complex.im(args[0].createComplex(namespace, unitSymbol));
    }
}