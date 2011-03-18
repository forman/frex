/*
 * Created at 26.01.2004 21:50:28
 * Copyright (c) 2004 by Norman Fomferra
 */
package z.core.support.accumulators;

import z.core.Accumulator;

public class RingOrbitTrap2 extends Accumulator {
    private double trapRadius1;

    private double trapRadius2;

    public void reset() {
        super.reset();
        setTrapRadius1(0.9);
        setTrapRadius2(0.1);
    }

    public void prepare() {
    }

    public double getTrapRadius1() {
        return trapRadius1;
    }

    public void setTrapRadius1(double trapRadius1) {
        this.trapRadius1 = trapRadius1;
    }

    public double getTrapRadius2() {
        return trapRadius2;
    }

    public void setTrapRadius2(double trapRadius2) {
        this.trapRadius2 = trapRadius2;
    }

    public void compute(final double[] orbitX,
                        final double[] orbitY,
                        final int iter,
                        final int maxIter,
                        final boolean trapMode,
                        final double[] result) {
        final double r1 = trapRadius1;
        final double r2 = trapRadius2;
        final double a = r1 + r2;
        final double b = 2 * a;
        final boolean turbulent = turbulenceUsed;
        double dx, dy, d;
        double sum = 0;
        for (int i = 0; i < iter; i++) {
            dx = Math.abs(orbitX[i]);
            dy = Math.abs(orbitY[i]);
            if (turbulent) {
                double t = computeTurbulence(orbitX[i], orbitY[i]);
                dx += t;
                dy += t;
            }
            if (dx < b && dy < b) {
                dx -= a;
                dy -= a;
                d = Math.abs(Math.hypot(dx, dy) - a);
                if (d < r2) {
                    sum += (r2 - d) / r2;
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
