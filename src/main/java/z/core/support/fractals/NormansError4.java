package z.core.support.fractals;

import z.core.Fractal;
import z.core.Region;

/**
 * <pre>
 *       z' = (z+c)^3
 *       with perturbation
 * </pre>
 */
public final class NormansError4 extends Fractal {

    public Region getStartRegion() {
        if (isJuliaMode()) {
            return new Region(0, 0, 1.6);
        }
        return new Region(-0.6, 0, 1.6);
    }

    public int compute(final double px,
                       final double py,
                       final double[] xValues,
                       final double[] yValues) {
        final int iterMax = this.iterMax;
        final double rr = this.bailOutSqr;

        final double cx, cy;
        double zx, zy;

        if (this.juliaMode) {
            cx = this.juliaCX;
            cy = this.juliaCY;
            zx = px;
            zy = py;
        } else {
            cx = px;
            cy = py;
            zx = 0.0;
            zy = 0.0;
        }

        double zxx;
        double zyy;
        double t0;
        double t1;
        double t3;
        double t2;
        double t4;
        double t5;
        double _zx;
        double _zy;


        for (int iter = 0; iter < iterMax; iter++) {

            zxx = zx * zx;
            zyy = zy * zy;
            t1 = zx + cx;
            t3 = zy + cy;
            t2 = t3 * t3;
            t4 = t1 * t1 - t2;
            t5 = 2 * (t1 * t3);
            _zx = t1 * t4 - t3 * t5;
            _zy = t1 * t5 + t4 * t3;

            if (zxx + zyy > rr) {
                return iter;
            }

            // perturbation
            if (_zy < _zx) {
                t0 = _zx;
                _zx = _zy;
                _zy = t0;
            }

            xValues[iter] = _zx;
            yValues[iter] = _zy;
            zx = _zx;
            zy = _zy;
        }
        return iterMax;
    }
}
