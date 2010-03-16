package z.math.term;

import z.math.Complex;
import z.math.Namespace;
import z.math.Symbol;

public class Struct extends Term {

    private static final Term[] NO_ARGS = new Term[0];

    private final Functor functor;
    private final Term[] args;

    protected Struct(Functor functor) {
        this(functor, NO_ARGS);
    }

    protected Struct(Functor functor, Term arg) {
        this(functor, new Term[]{arg});
    }

    protected Struct(Functor functor, Term arg1, Term arg2) {
        this(functor, new Term[]{arg1, arg2});
    }

    protected Struct(Functor functor, Term[] args) {
        if (functor == null) {
            throw new NullPointerException("functor is null");
        }
        if (args == null) {
            throw new NullPointerException("args is null");
        }
        int arity = args.length;
        if (functor.getArity() != arity) {
            throw new IllegalArgumentException("Illegal number of arguments");
        }
        for (int i = 0; i < arity; i++) {
            Term arg = args[i];
            if (arg == null) {
                throw new NullPointerException("args[" + i + "] is null");
            }
        }

        this.functor = functor;
        this.args = args;
    }

    public Struct createStruct(Term[] args) {
        return new Struct(getFunctor(), args);
    }

    public Functor getFunctor() {
        return functor;
    }

    @Override
    public final int getOpPrecendence() {
        return functor.getOpPrecedence();
    }

    @Override
    public final int getArity() {
        return functor.getArity();
    }

    @Override
    public final Term getArg(int i) {
        return i < getArity() ? args[i] : null;
    }

    @Override
    public double evaluate() {
        return getFunctor().evaluate(args);
    }

    @Override
    public Term simplify() {
        return getFunctor().simplify(args);
    }

    @Override
    public Complex createComplex(Namespace namespace, Symbol unitSymbol) {
        return getFunctor().createComplex(namespace, unitSymbol, args);
    }

    @Override
    public Term differentiate(Symbol var) {
        return getFunctor().differentiate(var, args);
    }

    @Override
    public int compareTo(Term other) {
        if (!other.isStruct()) {
            return 1;
        }
        return getOpPrecendence() - other.getOpPrecendence();
    }

    @Override
    public String toString() {
        if (getFunctor().getArity() == 1) {
            Term arg1 = getArg1();
            if (getFunctor().getOpType() == Functor.OpType.fx) {
                return getFunctor().getName() + slt(arg1);
            } else if (getFunctor().getOpType() == Functor.OpType.fy) {
                return getFunctor().getName() + sle(arg1);
            } else if (getFunctor().getOpType() == Functor.OpType.xf) {
                return sle(arg1) + getFunctor().getName();
            } else if (getFunctor().getOpType() == Functor.OpType.yf) {
                return slt(arg1) + getFunctor().getName();
            }
        } else if (getFunctor().getArity() == 2) {
            Term arg1 = getArg1();
            Term arg2 = getArg2();
            if (getFunctor().getOpType() == Functor.OpType.xfx) {
                return slt(arg1) + " " + getFunctor().getName() + " " + slt(arg2);
            } else if (getFunctor().getOpType() == Functor.OpType.xfy) {
                return slt(arg1) + " " + getFunctor().getName() + " " + sle(arg2);
            } else if (getFunctor().getOpType() == Functor.OpType.yfx) {
                return sle(arg1) + " " + getFunctor().getName() + " " + slt(arg2);
            }
        }

        StringBuilder sb;
        sb = new StringBuilder(getFunctor().getName());
        sb.append('(');
        for (int i = 0; i < args.length; i++) {
            if (i > 0) {
                sb.append(',');
                sb.append(' ');
            }
            sb.append(args[i].toString());
        }
        sb.append(')');
        return sb.toString();
    }

    private String slt(Term arg1) {
        String s1;
        if (lt(arg1)) {
            s1 = arg1.toString();
        } else {
            s1 = p(arg1);
        }
        return s1;
    }

    private String sle(Term arg2) {
        String s2;
        if (le(arg2)) {
            s2 = arg2.toString();
        } else {
            s2 = p(arg2);
        }
        return s2;
    }

    private static String p(Term arg2) {
        return "(" + arg2 + ")";
    }

    private boolean le(Term arg2) {
        return arg2.getOpPrecendence() <= getOpPrecendence();
    }

    private boolean lt(Term arg1) {
        return arg1.getOpPrecendence() < getOpPrecendence();
    }


    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Struct)) {
            return false;
        }
        Struct struct = (Struct) o;
        if (!struct.getFunctor().equals(getFunctor())) {
            return false;
        }
        for (int i = 0; i < getArity(); i++) {
            if (!struct.getArg(i).equals(getArg(i))) {
                return false;
            }
        }
        return true;
    }
}
