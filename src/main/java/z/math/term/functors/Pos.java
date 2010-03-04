package z.math.term.functors;

import z.math.Complex;
import z.math.Namespace;
import z.math.Symbol;
import z.math.term.Functor;
import z.math.term.Term;

public class Pos extends Functor {

    public Pos() {
        super("+", 1, OpType.fx, 500);
    }

    @Override
    public final double evaluate(Term[] args) {
        return args[0].evaluate();
    }

    @Override
    public Term simplify(Term[] args) {
        return args[0].simplify();
    }

    @Override
    public Term derivate(Symbol var, Term[] args) {
        return args[0].derivate(var);
    }

    @Override
    public Complex createComplex(Namespace namespace, Symbol unitSymbol, Term[] args) {
        return args[0].createComplex(namespace, unitSymbol);
    }
}