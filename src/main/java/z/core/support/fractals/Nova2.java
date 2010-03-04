/*
 * Created at 06.01.2004 20:53:30
 * Copyright (c) 2004 by Norman Fomferra
 */
package z.core.support.fractals;

import z.core.Fractal;
import z.core.Region;

/**
 * <pre>
 *    f  = (z - 1)^3
 *    z' = z - f/f' + c
 * </pre>
 */
public final class Nova2 extends Fractal {

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
        final double cx = julia ? juliaCX : px;
        final double cy = julia ? juliaCY : py;
        double zx = julia ? px : 0.0;
        double zy = julia ? py : 0.0;

        double dd, zzx = zx, zzy = zy;
        double t1, t2, t3, t4, t5;

        for (int iter = 0; iter < iterMax; iter++) {
            t1 = zx * zx - zy * zy - zx - 1.0;
            t2 = 2.0 * zx - 1.0;
            t3 = 2.0 * (zx * zy) - zy;
            t4 = 2.0 * zy;
            t5 = t2 * t2 + t4 * t4;
            zx = zx - (t1 * t2 + t3 * t4) / t5 + cx;
            zy = zy - (t2 * t3 - t1 * t4) / t5 + cy;

            dd = (zx - zzx) * (zx - zzx) + (zy - zzy) * (zy - zzy);
            //System.out.println("dd="+dd);
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
