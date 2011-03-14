/*
 * Created at 26.01.2004 21:50:28
 * Copyright (c) 2004 by Norman Fomferra
 */
package z.core.support.accumulators;

import z.core.Accumulator;

public final class RadialOrbitTrap3 extends Accumulator {
    protected double trapCenterX;

    protected double trapCenterY;

    protected double trapRadius;

    protected double trapRadiusSqr; // dependent on trapRadius

    public void reset() {
        setTrapCenterX(-1.0);
        setTrapCenterY(1.0);
        setTrapRadius(0.5);
    }

    public void prepare() {
    }

    public double getTrapRadius() {
        return trapRadius;
    }

    public void setTrapRadius(double trapRadius) {
        this.trapRadius = trapRadius;
        this.trapRadiusSqr = trapRadius * trapRadius;
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

    public void compute(final double[] orbitX,
                        final double[] orbitY,
                        final int iter,
                        final int maxIter,
                        final boolean trapMode,
                        final double[] result) {
        final double rSqr = trapRadiusSqr;
        final double cx = trapCenterX;
        final double cy = trapCenterY;
        double xDiff;
        double yDiff;
        double distSqr;
        double res = Double.MAX_VALUE;
        for (int i = 0; i < iter; i++) {
            xDiff = orbitX[i] - cx;
            yDiff = orbitY[i] - cy;
            distSqr = xDiff * xDiff + yDiff * yDiff;
            if (trapMode && distSqr < rSqr) {
                break;
            }
            res = res < distSqr ? res : distSqr;
        }
        result[0] = res;
    }

    public boolean computesIndex() {
        return true;
    }

    public boolean canTrap() {
        return true;
    }
}
