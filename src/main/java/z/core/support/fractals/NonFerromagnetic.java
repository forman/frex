/*
 * Created at 06.01.2004 20:53:30
 * Copyright (c) 2004 by Norman Fomferra
 */
package z.core.support.fractals;

import z.core.Fractal;
import z.core.Region;

/**
 * <pre>
 *      z' = ((z&circ;3 + 3*(c-1)*z + (c-1)*(c-2)) / (3*z&circ;2 + 3*(c-2)*z + c&circ;2 - 3*c + 3))&circ;2
 * </pre>
 */
public final class NonFerromagnetic extends Fractal {

    public Region getStartRegion() {
        return new Region(0, 0, 3);
    }

    public int compute(final double px,
                       final double py,
                       final double[] xValues,
                       final double[] yValues) {
        final int iterMax = this.iterMax;
        final double rr = bailOutSqr;
        final boolean julia = this.juliaMode;
        final double cx = julia ? juliaCX : px;
        final double cy = julia ? juliaCY : py;
        double zx = julia ? px : 0.0;
        double zy = julia ? py : 0.0;

        double t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17;
        int iter;
        for (iter = 0; iter < iterMax; iter++) {
            t6 = zx * zx;
            t7 = zy * zy;
            if (t6 + t7 > rr) {
                return iter;
            }
            t12 = 3.0 * cy;
            t9 = t12 * zy;
            t10 = cy * cy;
            t11 = cx - 2.0;
            t14 = cx - 1.0;
            t13 = 3.0 * t14;
            t2 = t6 * zx - 3.0 * (zx * t7) + t13 * zx - t9 + t14 * t11 - t10;
            t8 = 3.0 * t11;
            t3 = 3.0 * (t6 - t7) + t8 * zx - t9 + cx * cx - t10 - 3.0 * cx
                    + 3.0;
            t15 = zx * t12;
            t4 = 3.0 * (t6 * zy) - t7 * zy + t13 * zy + t15 + t14 * cy + t11
                    * cy;
            t5 = 3.0 * (2.0 * (zx * zy)) + t8 * zy + t15 + 2.0 * (cx * cy)
                    - t12;
            t16 = t3 * t3 + t5 * t5;
            t1 = (t2 * t3 + t4 * t5) / t16;
            t17 = (t3 * t4 - t2 * t5) / t16;
            zx = t1 * t1 - t17 * t17;
            zy = 2.0 * (t1 * t17);
            xValues[iter] = zx;
            yValues[iter] = zy;
        }
        return iterMax;
    }
}
