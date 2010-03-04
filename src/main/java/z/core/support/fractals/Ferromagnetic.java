/*
 * Created at 06.01.2004 20:53:30
 * Copyright (c) 2004 by Norman Fomferra
 */
package z.core.support.fractals;

import z.core.Fractal;
import z.core.Region;

/**
 * <pre>
 *      z' = ((z&circ;2 + c - 1) / (2*z + c - 2))&circ;2
 * </pre>
 */
public final class Ferromagnetic extends Fractal {

    public Region getStartRegion() {
        return new Region(0, 0, 5.5);
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

        double t1, t2, t3, t4, t5, t6, t7;
        double zxx, zyy;
        int iter;
        for (iter = 0; iter < iterMax; iter++) {
            zxx = zx * zx;
            zyy = zy * zy;
            if (zxx + zyy > rr) {
                return iter;
            }
            t2 = zxx - zyy + cx + 1.0;
            t3 = 2.0 * zx + cx - 2.0;
            t4 = 2.0 * (zx * zy) + cy;
            t5 = 2.0 * zy + cy;
            t6 = t3 * t3 + t5 * t5;
            t1 = (t2 * t3 + t4 * t5) / t6;
            t7 = (t3 * t4 - t2 * t5) / t6;
            zx = t1 * t1 - t7 * t7;
            zy = 2.0 * (t1 * t7);
            xValues[iter] = zx;
            yValues[iter] = zy;
        }
        return iterMax;
    }
}
