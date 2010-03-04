/*
 * Created at 06.01.2004 20:53:30
 * Copyright (c) 2004 by Norman Fomferra
 */
package z.core.support.fractals;

import z.core.Fractal;
import z.core.Region;

/**
 * <pre>
 *       z' = sin(z) + c
 * </pre>
 */
public final class SinZ extends Fractal {

    public Region getStartRegion() {
        return new Region(0, 0, 1.6);
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

        double zxx, zyy, t1, t2;
        int iter;
        for (iter = 0; iter < iterMax; iter++) {
            zxx = zx * zx;
            zyy = zy * zy;
            if (zxx + zyy > rr) {
                return iter;
            }
            t1 = 0.5 * Math.exp(zy);
            t2 = 0.5 * Math.exp(-zy);
            zx = Math.sin(zx) * (t1 + t2) + cx;
            zy = Math.cos(zx) * (t1 - t2) + cy;
            xValues[iter] = zx;
            yValues[iter] = zy;
        }
        return iterMax;

    }
}
