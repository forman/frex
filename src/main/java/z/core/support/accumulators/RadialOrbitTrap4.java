/*
 * Created at 26.01.2004 21:50:28
 * Copyright (c) 2004 by Norman Fomferra
 */
package z.core.support.accumulators;

import z.core.Accumulator;

public final class RadialOrbitTrap4 extends Accumulator {

    private double trapRadius;
    private double trapCenterX;
    private double trapCenterY;

    public double getTrapRadius() {
        return trapRadius;
    }

    public void setTrapRadius(double trapRadius) {
        this.trapRadius = trapRadius;
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

    public void reset() {
        setTrapRadius(1);
        setTrapCenterX(1);
        setTrapCenterY(1);
    }

    public void prepare() {
    }

    public void compute(final double[] orbitX,
                        final double[] orbitY,
                        final int iter,
                        final int maxIter,
                        final boolean trapMode,
                        final double[] result) {
        double xSum = 0;
        double ySum = 0;
        double r = trapRadius;
        double rr = r*r;
        double dxx, dyy, dd;
        for (int i = 0; i < iter; i++) {
            dxx = (orbitX[i] - trapCenterX) * (orbitX[i] - trapCenterX);
            dyy = (orbitY[i] - trapCenterY) * (orbitY[i] - trapCenterY);
            dd = dxx + dyy;
            if (dd < rr) {
                double f = (rr - dd)*(rr-dd)/rr;
                xSum += f*dxx;
                ySum += f*dyy;
                if (trapMode)
                    break;
            }
        }
        result[0] = xSum;
        result[1] = ySum;
    }

    public boolean computesIndex() {
        return false;
    }

    public boolean canTrap() {
        return true;
    }
}
