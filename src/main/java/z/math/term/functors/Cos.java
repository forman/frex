package z.math.term.functors;

import z.math.Complex;
import z.math.Namespace;
import z.math.Simplify;
import z.math.Symbol;
import z.math.term.Functor;
import z.math.term.Term;

public class Cos extends Functor {

    public Cos() {
        super("cos", 1); // NON-NLS
    }

    @Override
    public final double evaluate(Term[] args) {
        return Math.cos(args[0].evaluate());
    }

    @Override
    public Term simplify(Term[] args) {
        return Simplify.cos(args[0].simplify());
    }

    @Override
    public Term derivate(Symbol var, Term[] args) {
        Term inner = args[0].derivate(var);
        Term outer = neg(sin(args[0]));
        return mul(inner, outer);
    }

    @Override
    public Complex createComplex(Namespace namespace, Symbol unitSymbol, Term[] args) {
        return Complex.cos(args[0].createComplex(namespace, unitSymbol));
    }
}
