package z.math.term;

import z.math.Complex;
import z.math.Namespace;
import z.math.Symbol;

public abstract class Term {

    public int getOpPrecendence() {
        return 0;
    }

    public final Term getArg1() {
        return getArg(0);
    }

    public final Term getArg2() {
        return getArg(1);
    }

    public abstract int getArity();

    public abstract Term getArg(int i);

    public abstract double evaluate();

    @Override
    public abstract boolean equals(Object o);

    @Override
    public abstract String toString();

    public abstract int compareTo(Term other);

    public abstract Term simplify();

    public abstract Term differentiate(Symbol var);

    public abstract Complex createComplex(Namespace namespace, Symbol unitSymbol);

    public final boolean equalsZero() {
        return equals(Functor.num(0));
    }

    public final boolean equalsOne() {
        return equals(Functor.num(1));
    }

    public final boolean equalsTwo() {
        return equals(Functor.num(2));
    }

    public final boolean isReal() {
        return this instanceof Real;
    }

    public final boolean isSymbolRef() {
        return this instanceof Ref;
    }

    public final boolean isStruct() {
        return this instanceof Struct;
    }

    public final boolean isNeg() {
        return isStruct() && ((Struct) this).getFunctor() == Functor.NEG;
    }

    public final boolean isAdd() {
        return isStruct() && ((Struct) this).getFunctor() == Functor.ADD;
    }

    public final boolean isSub() {
        return isStruct() && ((Struct) this).getFunctor() == Functor.SUB;
    }

    public final boolean isMul() {
        return isStruct() && ((Struct) this).getFunctor() == Functor.MUL;
    }

    public final boolean isPow() {
        return isStruct() && ((Struct) this).getFunctor() == Functor.POW;
    }

    public final boolean isSqrt() {
        return isStruct() && ((Struct) this).getFunctor() == Functor.SQRT;
    }


    /**
     * Counts the number of occurences of {@code t2} in this term.
     *
     * @param pattern the expression to be found
     * @return the number of occurences
     */
    public int countOccurences(final Term pattern) {
        if (equals(pattern)) {
            return 1;
        }
        if (isStruct()) {
            int count = 0;
            for (int i = 0; i < getArity(); i++) {
                count += getArg(i).countOccurences(pattern);
            }
            return count;
        }
        return 0;
    }

    /**
     * Creates a new term in which all occurences of
     * {@code t2} in this term are replaced by {@code expr3}.
     *
     * @param pattern     the expression to be found
     * @param replacement the replacement for <code>expr2</code>
     * @return the modified expression
     */
    public Term replaceOccurences(final Term pattern, final Term replacement) {
        if (equals(pattern)) {
            return replacement;
        }
        if (isStruct()) {
            Term[] terms = new Term[getArity()];
            for (int i = 0; i < terms.length; i++) {
                terms[i] = getArg(i).replaceOccurences(pattern, replacement);
            }
            return ((Struct) this).createStruct(terms);
        }
        return this;
    }

}
