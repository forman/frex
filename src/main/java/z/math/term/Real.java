package z.math.term;

import z.math.Complex;
import z.math.Namespace;
import z.math.Symbol;

public class Real extends Term {

    private double value;

    public Real(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public int getArity() {
        return 0;
    }

    @Override
    public Term getArg(int i) {
        return null;
    }

    @Override
    public String toString() {
        long l = (long) value;
        if (l == value) {
            return String.valueOf(l);
        }
        return String.valueOf(value);
    }

    @Override
    public int compareTo(Term other) {
        if (other.isConstant()) {
            final double d = getValue() - ((Real) other).getValue();
            return d == 0 ? 0 : d < 0 ? -1 : 1;
        }
        return -1;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Real && ((Real) o).getValue() == getValue();
    }

    @Override
    public final double evaluate() {
        return value;
    }

    @Override
    public Term simplify() {
        return this;
    }

    @Override
    public Term derivate(Symbol var) {
        return Functor.num(0);
    }

    @Override
    public Complex createComplex(Namespace namespace, Symbol unitSymbol) {
        return new Complex(this, Functor.num(0));
    }
}
