/**
 * Created by IntelliJ IDEA.
 * User: fomferra
 * Date: Jan 6, 2003
 * Time: 9:19:59 PM
 * To change this template use Options | File Templates.
 */
package z.math;

import z.math.term.Functor;
import z.math.term.Real;
import z.math.term.Term;

public class Complex {

    public static final Complex ZERO = number(0, 0);

    public static final Complex ONE = number(1, 0);

    public static final Complex TWO = number(2, 0);

    protected final Term x;

    protected final Term y;

    public Complex(Term x, Term y) {
        this.x = x;
        this.y = y;
    }

    public Term getX() {
        return x;
    }

    public Term getY() {
        return y;
    }

    public static Complex number(double x, double y) {
        return new Complex(new Real(x), new Real(y));
    }

    public static Complex re(Complex z) {
        return new Complex(z.getX(), new Real(0.0));
    }

    public static Complex im(Complex z) {
        return new Complex(z.getY(), new Real(0.0));
    }

    public static Complex neg(Complex z) {
        final Term zx = Functor.neg(z.getX());
        final Term zy = Functor.neg(z.getY());
        return new Complex(zx, zy);
    }

    public static Complex abs(Complex z) {
        return new Complex(absZ(z), new Real(0.0));
    }

    public static Complex add(Complex z1, Complex z2) {
        final Term zx = Functor.add(z1.getX(), z2.getX());
        final Term zy = Functor.add(z1.getY(), z2.getY());
        return new Complex(zx, zy);
    }

    public static Complex sub(Complex z1, Complex z2) {
        final Term zx = Functor.sub(z1.getX(), z2.getX());
        final Term zy = Functor.sub(z1.getY(), z2.getY());
        return new Complex(zx, zy);
    }

    public static Complex mul(Complex z1, Complex z2) {
        final Term zx = Functor.sub(mulX(z1, z2), mulY(z1, z2));
        final Term zy = Functor.add(mulXY(z1, z2), mulXY(z2, z1));
        return new Complex(zx, zy);
    }

    public static Complex div(Complex z1, Complex z2) {
        final Term nx = Functor.add(mulX(z1, z2), mulY(z1, z2));
        final Term ny = Functor.sub(mulXY(z2, z1), mulXY(z1, z2));
        final Term d = sqrAbs(z2);
        final Term zx = Functor.div(nx, d);
        final Term zy = Functor.div(ny, d);
        return new Complex(zx, zy);
    }

    public static Complex sqrt(Complex z) {
        final Term zx = Functor.sqrt(Functor.mul(Functor.num(0.5),
                                                 Functor.add(z.getX(),
                                                             absZ(z))));
        final Term zy = Functor.sqrt(Functor.mul(Functor.num(-0.5),
                                                 Functor.add(z.getX(),
                                                             absZ(z))));
        return new Complex(zx, zy);
    }

    public static Complex pow(Complex z1, Complex z2) {
        Term z2x = z2.getX();
        Term z2y = z2.getY();
        if (z2x instanceof Real && z2y instanceof Real) {
            Real nz2x = (Real) z2x;
            Real nz2y = (Real) z2y;
            if (nz2y.evaluate() == 0.0) {
                final double n = nz2x.evaluate();
                return pow(z1, n);
            }
        }
        throw new IllegalArgumentException("numeric exponent expected");
    }

    public static Complex pow(Complex z, double n) {
        if (n < 0) {
            return div(ONE, pow(z, -n));
        } else if (n == 0.0) {
            return ONE;
        } else if (n == 1.0) {
            return z;
        } else if (n == 2.0) {
            final Term zx = Functor.sub(sqrX(z), sqrY(z));
            final Term zy = Functor.mul(Functor.num(2), mulXY(z, z));
            return new Complex(zx, zy);
        } else if (n == 3.0) {
            return mul(pow(z, 1), pow(z, 2));
        } else if (n == 4.0) {
            return mul(pow(z, 2), pow(z, 2));
        } else if (n == 5.0) {
            return mul(pow(z, 2), pow(z, 3));
        } else if (n == 6.0) {
            return mul(pow(z, 3), pow(z, 3));
        } else {
            throw new IllegalArgumentException("exponent not supported: " + n);
        }
    }

    public static Complex exp(Complex z) {
        final Term zx = Functor.mul(Functor.exp(z.getX()),
                                    Functor.cos(z.getY()));
        final Term zy = Functor.mul(Functor.exp(z.getX()),
                                    Functor.sin(z.getY()));
        return new Complex(zx, zy);
    }

    public static Complex sin(Complex z) {
        final Term zx = Functor.mul(Functor.sin(z.getX()),
                                    Functor.cosh(z.getY()));
        final Term zy = Functor.mul(Functor.cos(z.getX()),
                                    Functor.sinh(z.getY()));
        return new Complex(zx, zy);
    }

    public static Complex cos(Complex z) {
        final Term zx = Functor.mul(Functor.cos(z.getX()),
                                    Functor.cosh(z.getY()));
        final Term zy = Functor.mul(Functor.sin(z.getX()),
                                    Functor.sinh(z.getY()));
        return new Complex(zx, Functor.neg(zy));
    }

    public static Complex sinh(Complex z) {
        final Term zx = Functor.mul(Functor.sinh(z.getX()),
                                    Functor.cos(z.getY()));
        final Term zy = Functor.mul(Functor.cosh(z.getX()),
                                    Functor.sin(z.getY()));
        return new Complex(zx, zy);
    }

    public static Complex cosh(Complex z) {
        final Term zx = Functor.mul(Functor.cosh(z.getX()),
                                    Functor.cos(z.getY()));
        final Term zy = Functor.mul(Functor.sinh(z.getX()),
                                    Functor.sin(z.getY()));
        return new Complex(zx, zy);
    }

    // ///////////////////////////////////////////////////////////////////////
    // private helpers

    private static Term sqrX(Complex z) {
        return Functor.pow(z.getX(), Functor.num(2));
    }

    private static Term sqrY(Complex z) {
        return Functor.pow(z.getY(), Functor.num(2));
    }

    private static Term mulX(Complex z1, Complex z2) {
        return Functor.mul(z1.getX(), z2.getX());
    }

    private static Term mulY(Complex z1, Complex z2) {
        return Functor.mul(z1.getY(), z2.getY());
    }

    private static Term mulXY(Complex z1, Complex z2) {
        return Functor.mul(z1.getX(), z2.getY());
    }

    private static Term absZ(Complex z) {
        return Functor.sqrt(sqrAbs(z));
    }

    private static Term sqrAbs(Complex z) {
        return Functor.add(sqrX(z), sqrY(z));
    }

}
