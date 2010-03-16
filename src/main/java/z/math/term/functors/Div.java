package z.math.term.functors;

import z.math.Complex;
import z.math.Namespace;
import z.math.Simplify;
import z.math.Symbol;
import z.math.term.Functor;
import z.math.term.Term;

public class Div extends Functor {

    public Div() {
        super("/", 2, OpType.yfx, 400);
    }

    @Override
    public final double evaluate(Term[] args) {
        return args[0].evaluate() / args[1].evaluate();
    }

    @Override
    public Term simplify(Term[] args) {
        Term e1 = args[0].simplify();
        Term e2 = args[1].simplify();
        return Simplify.div(e1, e2);
    }

    @Override
    public Term differentiate(Symbol var, Term[] args) {
        return div(sub(mul(args[0].differentiate(var), args[1]),
                       mul(args[0], args[1].differentiate(var))),
                   pow(args[1], num(2)));
    }

    @Override
    public Complex createComplex(Namespace namespace, Symbol unitSymbol, Term[] args) {
        final Complex z1 = args[0].createComplex(namespace, unitSymbol);
        final Complex z2 = args[1].createComplex(namespace, unitSymbol);
        return Complex.div(z1, z2);
    }
}
