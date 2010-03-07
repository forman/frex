/*
 * Created at 06.01.2004 20:53:30
 * Copyright (c) 2004 by Norman Fomferra
 */
package z.core.support.fractals;

import z.core.Fractal;
import z.core.Region;

/**
 * <pre>
 *       z' = z&circ;2 - z - 1 + c
 * </pre>
 */
public final class GoldenRatio extends Fractal {

    public Region getStartRegion() {
        return new Region(0, 0, 2.5);
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

        double tx, ty;
        int iter;
        for (iter = 0; iter < iterMax; iter++) {
            tx = zx * zx;
            ty = zy * zy;
            if (tx + ty > rr) {
                return iter;
            }
            tx = tx - ty - zx + cx - 1.0;
            ty = 2.0 * zx * zy - zy + cy;

            zx = tx;
            zy = ty;
            xValues[iter] = zx;
            yValues[iter] = zy;
        }
        return iterMax;
    }
}
