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

        for (int iter = 0; iter < iterMax; iter++) {

            zx = zzx * zzx - zzy * zzy + cx * cx - cy * cy;
            zy = 2 * zzx * zzy + 2 * cx * cy;

            zzx = zx;
            zzy = zy;


            xValues[iter] = zzx;
            yValues[iter] = zzy;
        }
        return iterMax;
    }
}
