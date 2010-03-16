package z.math.term.functors;

import z.math.Complex;
import z.math.Namespace;
import z.math.Simplify;
import z.math.Symbol;
import z.math.term.Functor;
import z.math.term.Term;

public class Abs extends Functor {

    public Abs() {
        super("abs", 1); // NON-NLS
    }

    @Override
    public final double evaluate(Term[] args) {
        return Math.abs(args[0].evaluate());
    }

    @Override
    public Term simplify(Term[] args) {
        return Simplify.abs(args[0].simplify());
    }

    @Override
    public Term differentiate(Symbol var, Term[] args) {
        Term inner = args[0].differentiate(var);
        // todo - fix this, it may not be correct
        Term outer = Functor.abs(args[0]);
        return mul(inner, outer);
    }

    @Override
    public Complex createComplex(Namespace namespace, Symbol unitSymbol, Term[] args) {
        return Complex.abs(args[0].createComplex(namespace, unitSymbol));
    }
}