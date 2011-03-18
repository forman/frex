/*
 * Created at 26.01.2004 21:50:28
 * Copyright (c) 2004 by Norman Fomferra
 */
package z.core.support.accumulators;

import z.core.Accumulator;

public class RingOrbitTrap extends Accumulator {
    protected double trapCenterX;

    protected double trapCenterY;

    private double trapRadius1;

    private double trapRadius2;

    public void reset() {
        super.reset();
        setTrapCenterX(1.0);
        setTrapCenterY(1.0);
        setTrapRadius1(0.9);
        setTrapRadius2(0.1);
    }

    public void prepare() {
    }

    public double getTrapCenterX() {
        return trapCenterX;
    }

    public void setTrapCenterX(double trapCenterX) {
        this.trapCenterX = trapCenterX;
    }

    public double getTrapCenterY() {
        return trapCenterY;
    }

    public void setTrapCenterY(double trapCenterY) {
        this.trapCenterY = trapCenterY;
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
        final double x0 = trapCenterX;
        final double y0 = trapCenterY;
        final double r1 = trapRadius1;
        final double r2 = trapRadius2;
        final boolean turbulent = turbulenceUsed;
        double dx, dy, d;
        double sum = 0;
        for (int i = 0; i < iter; i++) {
            dx = orbitX[i] - x0;
            dy = orbitY[i] - y0;
            if (turbulent) {
                double t = computeTurbulence(orbitX[i], orbitY[i]);
                dx += t;
                dy += t;
            }
            d = Math.abs(Math.hypot(dx, dy) - r1);
            if (d < r2) {
                sum += (r2 - d) / r2;
                if (trapMode)
                    break;
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
