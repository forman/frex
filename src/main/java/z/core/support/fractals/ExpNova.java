/*
 * Created at 06.01.2004 20:53:30
 * Copyright (c) 2004 by Norman Fomferra
 */
package z.core.support.fractals;

import z.core.Fractal;
import z.core.Region;

/**
 * <pre>
 *    f = z * (z - 1.0) * (z + 1.0)
 *    z' = z - f/f' + c
 * </pre>
 */
public final class ExpNova extends Fractal {

    public Region getStartRegion() {
        return new Region(0, 0, 5);
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
        // final double cx = julia ? juliaCX : px;
        // final double cy = julia ? juliaCY : py;
        double zx = julia ? px : 0.0;
        double zy = julia ? py : 0.0;

        double dd, zzx = zx, zzy = zy;
        double t4, t2, t1, t5, t6, t3, t7;

        for (int iter = 0; iter < iterMax; iter++) {
            t4 = Math.exp(zx);
            t2 = t4 * Math.cos(zy);
            t1 = t2 - 1.0;
            t5 = Math.sin(zy);
            t6 = t4 * t5;
            t3 = t6 * t6;
            t7 = t2 * t2 + t3;
            zx = (t1 * t2 + t3) / t7;
            zy = (t2 * t6 - t1 * t6) / t7;

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
