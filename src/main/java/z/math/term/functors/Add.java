package z.math.term.functors;

import z.math.Complex;
import z.math.Namespace;
import z.math.Simplify;
import z.math.Symbol;
import z.math.term.Functor;
import z.math.term.Term;

public class Add extends Functor {

    public Add() {
        super("+", 2, OpType.yfx, 500);
    }

    @Override
    public final double evaluate(Term[] args) {
        return args[0].evaluate() + args[1].evaluate();
    }

    @Override
    public Term simplify(Term[] args) {
        Term e1 = args[0].simplify();
        Term e2 = args[1].simplify();
        return Simplify.add(e1, e2);
    }

    @Override
    public Term derivate(Symbol var, Term[] args) {
        return add(args[0].derivate(var), args[1].derivate(var));
    }

    @Override
    public Complex createComplex(Namespace namespace, Symbol unitSymbol, Term[] args) {
        final Complex z1 = args[0].createComplex(namespace, unitSymbol);
        final Complex z2 = args[1].createComplex(namespace, unitSymbol);
        return Complex.add(z1, z2);
    }
}
