package z.math.term.functors;

import z.math.Complex;
import z.math.Namespace;
import z.math.Symbol;
import z.math.term.Functor;
import z.math.term.Term;

public class Assign extends Functor {

    public Assign() {
        super("=", 2, OpType.xfx, 1200);
    }

    @Override
    public final double evaluate(Term[] args) {
        return args[1].evaluate();
    }

    @Override
    public Term simplify(Term[] args) {
        return args[1].simplify();
    }

    @Override
    public Term differentiate(Symbol var, Term[] args) {
        return args[1].differentiate(var);
    }

    @Override
    public Complex createComplex(Namespace namespace, Symbol unitSymbol, Term[] args) {
        return args[1].createComplex(namespace, unitSymbol);
    }
}