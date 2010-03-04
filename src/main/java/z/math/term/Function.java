package z.math.term;

import z.math.Complex;
import z.math.Namespace;
import z.math.Symbol;

public class Function extends Functor {

    private final Symbol[] variables;
    private final Term body;

    public Function(String name, Symbol[] variables, Term body) {
        super(name, variables.length);
        this.variables = variables;
        this.body = body;
    }

    public Symbol getVariable(int i) {
        return variables[i];
    }

    public Term getBody() {
        return body;
    }

    public Term getExpandedBody(Term[] args) {
        Term newBody = getBody();
        for (int i = 0; i < getArity(); i++) {
            newBody = newBody.replaceOccurences(new Ref(getVariable(i)), args[i]);
        }
        return newBody;
    }

    @Override
    public double evaluate(Term[] args) {
        Term[] stack = new Term[getArity()];
        for (int i = 0; i < getArity(); i++) {
            Symbol variable = getVariable(i);
            Term value = variable.getValue();
            stack[i] = value;
            variable.setValue(new Real(args[i].evaluate()));
        }
        double result = body.evaluate();
        for (int i = 0; i < getArity(); i++) {
            getVariable(i).setValue(stack[i]);
        }
        return result;
    }

    @Override
    public Term simplify(Term[] args) {
        return getExpandedBody(args).simplify();
    }

    @Override
    public Term derivate(Symbol var, Term[] args) {
        return getExpandedBody(args).derivate(var);
    }

    @Override
    public Complex createComplex(Namespace namespace, Symbol unitSymbol, Term[] args) {
        return getExpandedBody(args).createComplex(namespace, unitSymbol);
    }
}
