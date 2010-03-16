/*
 * Created at 06.01.2004 20:53:30
 * Copyright (c) 2004 by Norman Fomferra
 */
package z.core.support.fractals;

import z.core.Fractal;
import z.core.Region;

/**
 * <pre>
 *       z' = z&circ;2 + c
 * </pre>
 */
public final class Sandbox extends Fractal {

    public Region getStartRegion() {
        return new Region(0, 0, 1);
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
        final double cx = julia ? juliaCX : px;
        final double cy = julia ? juliaCY : py;
        double zx = julia ? px : 0.0;
        double zy = julia ? py : 0.0;

        double dd, zzx = zx, zzy = zy;
        double t, t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15;

        for (int iter = 0; iter < iterMax; iter++) {
            t6 = zx + 1.0;
            t10 = zx - 1.0;
            t8 = zx * t10;
            t9 = zy * zy;
            t11 = zx * zy;
            t12 = t8 - t9;
            t14 = t10 * zy;
            t13 = t11 + t14;
            t1 = t12 * t6 - t13 * zy;
            t5 = 2.0 * zx - 1.0;
            t7 = 2.0 * zy;
            t2 = t5 * t6 - t7 * zy + t8 - t9;
            t3 = t12 * zy + t6 * t13;
            t4 = t5 * zy + t6 * t7 + t11 + t14;
            t15 = t2 * t2 + t4 * t4;
            zx = cx + zx - (t1 * t2 + t3 * t4) / t15;
            zy = cy + zy - (t2 * t3 - t1 * t4) / t15;

            dd = (zx - zzx) * (zx - zzx) + (zy - zzy) * (zy - zzy);
            if (dd < rr) {
                return iter;
            }
            if (zy > zx) {
                t = zx;
                zx = zy;
                zy = t;
            }
            zzx = zx;
            zzy = zy;

            xValues[iter] = zx;
            yValues[iter] = zy;
        }
        return iterMax;
    }
}
