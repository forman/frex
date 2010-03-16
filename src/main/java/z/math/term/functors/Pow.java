package z.math.term.functors;

import z.math.Complex;
import z.math.Namespace;
import z.math.Simplify;
import z.math.Symbol;
import z.math.term.Functor;
import z.math.term.Term;

public class Pow extends Functor {

    public Pow() {
        super("^", 2, OpType.yfx, 300);
    }

    @Override
    public final double evaluate(Term[] args) {
        return Math.pow(args[0].evaluate(), args[1].evaluate());
    }

    @Override
    public Term simplify(Term[] args) {
        Term e1 = args[0].simplify();
        Term e2 = args[1].simplify();
        return Simplify.pow(e1, e2);
    }

    @Override
    public Term differentiate(Symbol var, Term[] args) {
        if (args[1].isReal()) {
            double n = args[1].evaluate();
            Term inner = args[0].differentiate(var);
            Term outer = mul(args[1], pow(args[0], num(n - 1)));
            return mul(inner, outer);
        }
        throw new IllegalArgumentException("numeric exponent expected");
    }

    @Override
    public Complex createComplex(Namespace namespace, Symbol unitSymbol, Term[] args) {
        final Complex z1 = args[0].createComplex(namespace, unitSymbol);
        final Complex z2 = args[1].createComplex(namespace, unitSymbol);
        return Complex.pow(z1, z2);
    }

}
