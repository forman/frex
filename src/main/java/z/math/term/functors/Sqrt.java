package z.math.term.functors;

import z.math.Complex;
import z.math.Namespace;
import z.math.Simplify;
import z.math.Symbol;
import z.math.term.Functor;
import z.math.term.Term;

public class Sqrt extends Functor {

    public Sqrt() {
        super("sqrt", 1);
    }

    @Override
    public final double evaluate(Term[] args) {
        return Math.sqrt(args[0].evaluate());
    }

    @Override
    public Term simplify(Term[] args) {
        return Simplify.sqrt(args[0].simplify());
    }

    @Override
    public Term derivate(Symbol var, Term[] args) {
        Term inner = args[0].derivate(var);
        Term outer = div(num(1), mul(num(2), sqrt(args[0])));
        return mul(inner, outer);
    }

    @Override
    public Complex createComplex(Namespace namespace, Symbol unitSymbol, Term[] args) {
        return Complex.sqrt(args[0].createComplex(namespace, unitSymbol));
    }
}