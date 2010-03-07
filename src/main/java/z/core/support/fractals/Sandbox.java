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
        if (isJuliaMode()) {
            return new Region(0, 0, 1.6);
        }
        return new Region(0.0, 0.0, 10);
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

        double zxx, zyy, t1, t2, t3, t4, t5, t6, zzx, zzy;
        int iter;
        for (iter = 0; iter < iterMax; iter++) {
            zxx = zx * zx;
            zyy = zy * zy;
            if (zxx + zyy > rr) {
                return iter;
            }
            t4 = 2 * (zx * zy);
            t5 = zx * zx - zy * zy;
            t1 = zx * t5 - zy * t4 + 1;
            t2 = t5 + 1;
            t3 = zx * t4 + t5 * zy;
            t6 = t2 * t2 + t4 * t4;
            zzx = (t1 * t2 + t3 * t4) / t6 + cx;
            zzy = (t2 * t3 - t1 * t4) / t6 + cy;
            xValues[iter] = zzx;
            yValues[iter] = zzy;
            if (zy < zx) {
                zx = zzy;
                zy = zzx;
            }
        }
        return iterMax;

    }

}
