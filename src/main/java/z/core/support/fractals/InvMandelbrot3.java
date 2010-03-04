/*
 * Created at 06.01.2004 20:53:30
 * Copyright (c) 2004 by Norman Fomferra
 */
package z.core.support.fractals;

import z.core.Fractal;
import z.core.Region;

/**
 * <pre>
 *       z' = 1 / z&circ;2 + 1 / c&circ;2
 * </pre>
 */
public final class InvMandelbrot3 extends Fractal {

    public InvMandelbrot3() {
        setBailOut(10.0);
        setIterMax(250);
    }

    public Region getStartRegion() {
        if (isJuliaMode()) {
            return new Region(0, 0, 1.6);
        }
        return new Region(0.0, 0.0, 2);
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
        double zx = julia ? px : 1.0;
        double zy = julia ? py : 1.0;

        double t1, t2, t3, t4, t5, t6, t7, t8;

        t3 = cx * cx - cy * cy;
        t6 = 2 * (cx * cy);
        t4 = t6 * t6;
        t8 = t3 * t3 + t4;

        double zxx, zyy, zzx, zzy;
        int iter;
        for (iter = 0; iter < iterMax; iter++) {

            zxx = zx * zx;
            zyy = zy * zy;
            if (zxx + zyy > rr) {
                return iter;
            }
            t1 = zx * zx - zy * zy;
            t5 = 2.0 * (zx * zy);
            t2 = t5 * t5;
            t7 = t1 * t1 + t2;
            zx = t1 / t7 + t3 / t8;
            zy = (-t5) / t7 + (-t6) / t8;

            xValues[iter] = zx;
            yValues[iter] = zy;
        }
        return iterMax;

    }
}