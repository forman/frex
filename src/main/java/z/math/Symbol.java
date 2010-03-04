/**
 * Created by IntelliJ IDEA.
 * User: fomferra
 * Date: Jan 6, 2003
 * Time: 7:57:52 PM
 * To change this template use Options | File Templates.
 */
package z.math;

import z.math.term.Functor;
import z.math.term.Ref;
import z.math.term.Term;

// todo - rename to variable
public class Symbol extends Functor {

    // todo - constant symbols are zero-arg-functors
    private final boolean constant;

    private Term term;

    public static Symbol createVariable(String name, Term term) {
        return new Symbol(name, term, false);
    }

    public static Symbol createSymbol(String name) {
        return new Symbol(name, null, true);
    }

    public static Symbol createConstant(String name, Term term) {
        return new Symbol(name, term, true);
    }

    private Symbol(String name, Term term, boolean constant) {
        super(name, 0);
        this.term = term;
        this.constant = constant;
    }

    public boolean isConstant() {
        return constant;
    }

    public Term getValue() {
        return term;
    }

    public void setValue(Term term) {
        if (constant) {
            throw new UnsupportedOperationException("symbol is constant");
        }
        this.term = term;
    }

    @Override
    public double evaluate(Term[] args) {
        return getValue().evaluate();
    }

    @Override
    public Term simplify(Term[] args) {
        return null;
    }

    @Override
    public Term derivate(Symbol var, Term[] args) {
        return this == var ? Functor.num(1) : Functor.num(0);
    }

    @Override
    public Complex createComplex(Namespace namespace, Symbol unitSymbol, Term[] args) {
        if (this == unitSymbol) {
            return new Complex(Functor.num(0), Functor.num(1));
        } else if (isConstant()) {
            return new Complex(new Ref(this), Functor.num(0));
        } else {
            final Symbol x = namespace.addSymbol(getName() + "x");
            final Symbol y = namespace.addSymbol(getName() + "y");
            return new Complex(new Ref(x), new Ref(y));
        }
    }

    @Override
    public String toString() {
        return getName() + "=" + (term!= null?term.toString():"?");
    }
}
