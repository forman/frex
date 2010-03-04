/**
 * Created by IntelliJ IDEA.
 * User: fomferra
 * Date: Jan 6, 2003
 * Time: 9:39:40 PM
 * To change this template use Options | File Templates.
 */
package z.math;

import z.math.term.Functor;
import z.math.term.Term;

public class Simplify {

    public static Term neg(Term e) {
        // -0 --> 0
        if (e.equalsZero()) {
            return Functor.num(0);
        }
        // -n --> @(-n)
        else if (e.isConstant()) {
            return Functor.num(-e.evaluate());
        }
        // -(-x) --> x
        else if (e.isNeg()) {
            return e.getArg1();
        }
        // -(x11 - x12) --> x12 - x11
        else if (e.isSub()) {
            return Functor.sub(e.getArg2(), e.getArg1());
        }
        // -x --> -x
        else {
            return Functor.neg(e);
        }
    }

    /**
     * @param x1 the simplified left expression
     * @param x2 the simplified right expression
     * @return the simplified add expression
     */
    public static Term add(Term x1, Term x2) {
        // 0 + n2
        if (x1.equalsZero()) {
            return x2;
        }
        // n1 + 0
        else if (x2.equalsZero()) {
            return x1;
        }
        // n1 + x2
        else if (x1.isConstant() && x2.isConstant()) {
            return Functor.num(x1.evaluate() + x2.evaluate());
        }
        // n1 + x2 --> x2 + n1
        else if (x1.isConstant()) {
            return Functor.add(x2, x1).simplify();
        }
        // -x1 + x2 --> x2 - x1
        else if (x1.isNeg()) {
            return Functor.sub(x2, x1).simplify();
        }
        // x1 + -x2 --> x1 - x2
        else if (x2.isNeg()) {
            return Functor.sub(x1, x2).simplify();
        }
        // x1 + (x21 + x22) --> (x1 + x21) + x22
        else if (x2.isAdd()) {
            return Functor.add(Functor.add(x1, x2.getArg1()), x2.getArg2()).simplify();
        }
        // x1 + (x21 - x22) --> (x1 + x21) - x22
        else if (x2.isSub()) {
            return Functor.sub(Functor.add(x1, x2.getArg1()), x2.getArg2()).simplify();
        }
        // (x11 + x12) + x2
        else if (x1.isAdd()) {
            final Term x11 = x1.getArg1();
            final Term x12 = x1.getArg2();
            // (x11 + n12) + n2 --> x11 + @(n12+n2)
            if (x12.isConstant() && x2.isConstant()) {
                return Functor.add(x11, Functor.num(x12.evaluate()
                        + x2.evaluate())).simplify();
            }
            // (x11 + n12) + x2 --> (x11 + x2) + n12
            else if (x12.isConstant()) {
                return Functor.add(Functor.add(x11, x2), x12).simplify();
            }
            // (x11 + n12) + n2 --> x11 + @(n12+n2)
            else if (x12.isConstant() && x2.isConstant()) {
                return Functor.add(x11, Functor.num(x12.evaluate()
                        + x2.evaluate())).simplify();
            }
            // (x11 + n12) + x2 --> (x11 + x2) + n12
            else if (x12.isConstant()) {
                return Functor.add(Functor.add(x11, x2), x12).simplify();
            }
        }
        // (x11 - x12) + x2
        else if (x1.isSub()) {
            final Term x11 = x1.getArg1();
            final Term x12 = x1.getArg2();
            // (n11 - x12) + n2 --> @(n11+n2) - x12
            if (x11.isConstant() && x2.isConstant()) {
                return Functor.sub(Functor.num(x11.evaluate()
                        + x2.evaluate()), x12).simplify();
            }
            // (n11 - x12) + x2 --> (x2 - x12) + n11
            else if (x11.isConstant()) {
                return Functor.add(Functor.sub(x2, x12), x11).simplify();
            }
            // (x11 - n12) + n2 --> x11 + @(n2-n12)
            else if (x12.isConstant() && x2.isConstant()) {
                return Functor.add(x11, Functor.num(x2.evaluate()
                        - x12.evaluate())).simplify();
            }
            // (x11 - n12) + x2 --> (x11 + x2) - n12
            else if (x12.isConstant()) {
                return Functor.sub(Functor.add(x11, x2), x12).simplify();
            }

            // (x11 - x12) + x2 --> 2 * x11 - x12
            if (x11.equals(x2)) {
                return Functor.sub(Functor.mul(Functor.num(2), x11), x12).simplify();
            }

            // (x11 - x12) + x2 --> x11
            if (x12.equals(x2)) {
                return x11;
            }
        }

        // x1 + x2 --> 2 * x1
        if (x1.equals(x2)) {
            return Functor.mul(Functor.num(2), x1).simplify();
        }

        return Functor.add(x1, x2);
    }

    public static Term sub(Term e1, Term e2) {
        if (e1.equalsZero()) {
            return Functor.neg(e2).simplify();
        } else if (e2.equalsZero()) {
            return e1;
        } else if (e1.isConstant() && e2.isConstant()) {
            return Functor.num(e1.evaluate() - e2.evaluate());
        } else if (e1.equals(e2)) {
            return Functor.num(0);
        } else {
            return Functor.sub(e1, e2);
        }
    }

    public static Term mul(Term e1, Term e2) {
        if (e1.equalsZero() || e2.equalsZero()) {
            return Functor.num(0);
        } else if (e1.equals(Functor.num(1))) {
            return e2;
        } else if (e2.equals(Functor.num(1))) {
            return e1;
        } else if (e1.isConstant() && e2.isConstant()) {
            return Functor.num(e1.evaluate() * e2.evaluate());
        } else if (e1.isNeg() && e2.isNeg()) {
            return Functor.mul(e1.getArg1(), e2.getArg1());
        } else if (e1.equals(e2)) {
            return Functor.pow(e1, Functor.num(2)).simplify();
        } else {
            return Functor.mul(e1, e2);
        }
    }

    public static Term div(Term e1, Term e2) {
        if (e1.equalsZero()) {
            return Functor.num(0);
        } else if (e2.equalsOne()) {
            return e1;
        } else if (e1.equals(e2)) {
            return Functor.num(1);
        } else if (e1.isConstant() && e2.isConstant()) {
            return Functor.num(e1.evaluate() / e2.evaluate());
        } else if (e1.isNeg() && e2.isNeg()) {
            return Functor.div(e1.getArg1(), e2.getArg1());
        } else {
            return Functor.div(e1, e2);
        }
    }

    public static Term sqrt(Term e) {
        if (e.equalsZero()) {
            return Functor.num(0);
        } else if (e.equalsOne()) {
            return Functor.num(1);
        } else if (e.isPow() && e.getArg2().equalsTwo()) {
            return e.getArg1();
        } else {
            return Functor.sqrt(e);
        }
    }

    public static Term pow(Term e1, Term e2) {
        if (e1.equalsZero()) {
            return Functor.num(0);
        } else if (e1.equalsOne() || e2.equalsZero()) {
            return Functor.num(1);
        } else if (e2.equalsOne()) {
            return e1;
        } else if (e2.equalsTwo() && e1.isSqrt()) {
            return e1.getArg1();
        } else if (e1.isConstant() && e2.isConstant()) {
            return Functor.num(Math.pow(e1.evaluate(), e2.evaluate()));
        } else {
            return Functor.pow(e1, e2);
        }
    }

    public static Term exp(Term e) {
        if (e.equalsZero()) {
            return Functor.num(1);
        }
        return Functor.exp(e);
    }

    public static Term cos(Term e) {
        if (e.equalsZero()) {
            return Functor.num(1);
        }
        return Functor.cos(e);
    }

    public static Term sin(Term e) {
        if (e.equalsZero()) {
            return Functor.num(0);
        }
        return Functor.sin(e);
    }

    public static Term cosh(Term e) {
        // todo:  implement
        return Functor.cosh(e);
    }

    public static Term sinh(Term e) {
        // todo:  implement
        return Functor.sinh(e);
    }
}
