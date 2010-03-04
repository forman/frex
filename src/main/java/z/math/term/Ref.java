package z.math.term;

import z.math.Complex;
import z.math.Namespace;
import z.math.Symbol;

public class Ref extends Term {
    private static final Term[] NO_ARGS = new Term[0];

    private final Symbol symbol;

    public Ref(Symbol symbol) {
        if (symbol == null) {
            throw new NullPointerException("symbol is null");
        }
        this.symbol = symbol;
    }

    public final Symbol getSymbol() {
        return symbol;
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
        return getSymbol().getName();
    }

    @Override
    public int compareTo(Term other) {
        if (other.isConstant()) {
            return 1;
        }
        if (other.isSymbolRef()) {
            final Ref ref = (Ref) other;
            return getSymbol().getName().compareTo(ref.getSymbol().getName());
        }
        return -1;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Ref && ((Ref) o).getSymbol() == getSymbol();
    }

    @Override
    public final double evaluate() {
        return getSymbol().evaluate(NO_ARGS);
    }

    @Override
    public Term simplify() {
        return this;
    }

    @Override
    public Term derivate(Symbol var) {
        return getSymbol().derivate(var, NO_ARGS);
    }

    @Override
    public Complex createComplex(Namespace namespace, Symbol unitSymbol) {
        return getSymbol().createComplex(namespace, unitSymbol, NO_ARGS);
    }
}
