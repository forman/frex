package z.math.term;

import z.math.Complex;
import z.math.Namespace;
import z.math.Symbol;
import z.math.term.functors.Add;
import z.math.term.functors.Cos;
import z.math.term.functors.Cosh;
import z.math.term.functors.Div;
import z.math.term.functors.Exp;
import z.math.term.functors.Mul;
import z.math.term.functors.Neg;
import z.math.term.functors.Pow;
import z.math.term.functors.Sin;
import z.math.term.functors.Sinh;
import z.math.term.functors.Sqrt;
import z.math.term.functors.Sub;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public abstract class Functor {

    public static final Functor NEG = new Neg();

    public static final Functor ADD = new Add();
    public static final Functor SUB = new Sub();
    public static final Functor MUL = new Mul();
    public static final Functor DIV = new Div();
    public static final Functor POW = new Pow();

    public static final Functor COS = new Cos();
    public static final Functor COSH = new Cosh();
    public static final Functor EXP = new Exp();
    public static final Functor SIN = new Sin();
    public static final Functor SINH = new Sinh();
    public static final Functor SQRT = new Sqrt();

    private static final Map<ID, Functor> MAP = new HashMap<ID, Functor>(256);
    private final ID id;
    private final OpType opType;
    private final int opPrecedence;


    /**
     * Prefix (fx), infix (xfx) or postfix (xf).
     * 'x' means, all 'f's in x have a lower precedence as 'f'
     * 'y' means, all 'f's in x have a lower or equal precedence as 'f'
     */
    public enum OpType {
        none,
        fx,
        fy,
        xf,
        yf,
        xfx,
        xfy,
        yfx,
    }

    static {
        Field[] declaredFields = Functor.class.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            if ((declaredField.getType().equals(Functor.class)
                    && (declaredField.getModifiers() & Modifier.PUBLIC) != 0
                    && (declaredField.getModifiers() & Modifier.FINAL) != 0
                    && (declaredField.getModifiers() & Modifier.STATIC) != 0)) {
                Functor functor = null;
                try {
                    functor = (Functor) declaredField.get(null);
                    put(functor);
                } catch (Exception e) {
                    throw new IllegalStateException("Failed to register functor " + declaredField, e);
                }
            }
        }
    }

    public static Functor get(String name, int arity) {
        return MAP.get(new ID(name, arity));
    }

    public static Functor put(Functor f) {
        return MAP.put(f.id, f);
    }

    public static Functor isOp(String op) {
        return null;  //To change body of created methods use File | Settings | File Templates.
    }

    public static Term neg(Term arg) {
        return new Struct(NEG, arg);
    }

    public static Term add(Term arg1, Term arg2) {
        return new Struct(ADD, arg1, arg2);
    }

    public static Term sub(Term arg1, Term arg2) {
        return new Struct(SUB, arg1, arg2);
    }

    public static Term mul(Term arg1, Term arg2) {
        return new Struct(MUL, arg1, arg2);
    }

    public static Term div(Term arg1, Term arg2) {
        return new Struct(DIV, arg1, arg2);
    }

    public static Term pow(Term arg1, Term arg2) {
        return new Struct(POW, arg1, arg2);
    }

    public static Term exp(Term arg) {
        return new Struct(EXP, arg);
    }

    public static Term sqrt(Term arg) {
        return new Struct(SQRT, arg);
    }

    public static Term sin(Term arg) {
        return new Struct(SIN, arg);
    }

    public static Term cos(Term arg) {
        return new Struct(COS, arg);
    }

    public static Term sinh(Term arg) {
        return new Struct(SINH, arg);
    }

    public static Term cosh(Term arg) {
        return new Struct(COSH, arg);
    }

    public static Term ref(Symbol s) {
        return new Ref(s);
    }

    public static Term num(int n) {
        return new Real(n);
    }

    public static Term num(double n) {
        return new Real(n);
    }

    protected Functor(String name, int arity) {
        this(name, arity, OpType.none, 0);
    }

    protected Functor(String name, int arity, OpType opType, int opPrecedence) {
        this.id = new ID(name, arity);
        this.opType = opType;
        this.opPrecedence = opPrecedence;
    }

    public String getName() {
        return id.name;
    }

    public int getArity() {
        return id.arity;
    }

    public OpType getOpType() {
        return opType;
    }

    public int getOpPrecedence() {
        return opPrecedence;
    }

    public abstract double evaluate(Term[] args);

    public abstract Term simplify(Term[] args);

    public abstract Term derivate(Symbol var, Term[] args);

    public abstract Complex createComplex(Namespace namespace, Symbol unitSymbol, Term[] args);

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Functor)) {
            return false;
        }
        Functor functor = (Functor) o;
        if (!id.equals(functor.id)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return id.toString();
    }

    private static class ID {
        final String name;
        final int arity;

        private ID(String name, int arity) {
            this.name = name;
            this.arity = arity;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            ID id = (ID) o;
            if (arity != id.arity) {
                return false;
            }
            if (!name.equals(id.name)) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int result;
            result = name.hashCode();
            result = 31 * result + arity;
            return result;
        }

        @Override
        public String toString() {
            return name + ":" + arity;
        }
    }
}