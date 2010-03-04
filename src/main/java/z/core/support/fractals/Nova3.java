/*
 * Created at 06.01.2004 20:53:30
 * Copyright (c) 2004 by Norman Fomferra
 */
package z.core.support.fractals;

import z.core.Fractal;
import z.core.Region;

/**
 * <pre>
 *    f  = (z &circ; 3 - 1) * (z &circ; 2 - 4)
 *    z' = z - f/f' + c
 * </pre>
 */
public final class Nova3 extends Fractal {

    public Region getStartRegion() {
        return new Region(0, 0, 2);
    }

    public void reset() {
        super.reset();
        setBailOut(0.001);
    }

    public int compute(final double px,
                       final double py,
                       final double[] xValues,
                       final double[] yValues) {
        final int iterMax = this.iterMax;
        final double rr = bailOutSqr;
        final boolean julia = this.juliaMode;
        final double cx = px;
        final double cy = py;
        double zx = julia ? juliaCX : px;
        double zy = julia ? juliaCY : py;

        double dd, zzx = zx, zzy = zy;
        double t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16;

        for (int iter = 0; iter < iterMax; iter++) {
            t14 = zx * zx;
            t15 = zy * zy;
            t13 = t14 - t15;
            t6 = t13 - 4.0;
            t8 = 2.0 * (zx * zy);
            t9 = t14 * zx - 3.0 * (zx * t15) - 1.0;
            t11 = 3.0 * (t14 * zy) - t15 * zy;
            t1 = t9 * t6 - t11 * t8;
            t5 = 3.0 * t13;
            t7 = 3.0 * t8;
            t10 = 2.0 * zx;
            t12 = 2.0 * zy;
            t2 = t5 * t6 - t7 * t8 + t9 * t10 - t11 * t12;
            t3 = t9 * t8 + t6 * t11;
            t4 = t5 * t8 + t6 * t7 + t9 * t12 + t10 * t11;
            t16 = t2 * t2 + t4 * t4;
            zx = zx + cx - (t1 * t2 + t3 * t4) / t16;
            zy = zy + cy - (t2 * t3 - t1 * t4) / t16;

            dd = (zx - zzx) * (zx - zzx) + (zy - zzy) * (zy - zzy);
            if (dd < rr) {
                return iter;
            }
            zzx = zx;
            zzy = zy;

            xValues[iter] = zx;
            yValues[iter] = zy;
        }
        return iterMax;
    }
}
