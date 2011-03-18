/*
 * Created at 26.01.2004 21:50:28
 * Copyright (c) 2004 by Norman Fomferra
 */
package z.core.support.accumulators;

import z.core.Accumulator;

public final class RadialOrbitTrap2 extends Accumulator {
    private double trapRadius;

    public void reset() {
        super.reset();
        setTrapRadius(1.0);
    }

    public void prepare() {
    }

    public double getTrapRadius() {
        return trapRadius;
    }

    public void setTrapRadius(double trapRadius) {
        this.trapRadius = trapRadius;
    }

    public void compute(final double[] orbitX,
                        final double[] orbitY,
                        final int iter,
                        final int maxIter,
                        final boolean trapMode,
                        final double[] result) {
        final double r = trapRadius;
        final double rr = r * r;
        final double r2 = 2 * r;
        final boolean turbulent = turbulenceUsed;
        double dx, dy, dd;
        double sum = 0;
        for (int i = 0; i < iter; i++) {
            double x = orbitX[i];
            double y = orbitY[i];
            dx = Math.abs(x);
            dy = Math.abs(y);
            if (turbulent) {
                double t = computeTurbulence(orbitX[i], orbitY[i]);
                dx += t;
                dy += t;
            }
            if (dx < r2 && dy < r2) {
                dx -= r;
                dy -= r;
                dd = dx * dx + dy * dy;
                if (dd < rr) {
                    sum += (rr - dd) / rr;
                    if (trapMode)
                        break;
                }
            }
        }
        result[0] = sum;
    }

    public boolean computesIndex() {
        return true;
    }

    public boolean canTrap() {
        return true;
    }
}
