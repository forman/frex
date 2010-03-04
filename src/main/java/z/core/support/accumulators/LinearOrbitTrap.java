/*
 * Created at 26.01.2004 21:50:28
 * Copyright (c) 2004 by Norman Fomferra
 */
package z.core.support.accumulators;

import z.core.Accumulator;

public class LinearOrbitTrap extends Accumulator {
    protected double trapRadius;

    public void reset() {
        setTrapRadius(0.1);
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
        double xSum = 0;
        double ySum = 0;
        double temp;
        double r = trapRadius;
        for (int i = 0; i < iter; i++) {
            temp = orbitX[i];
            temp = temp < 0 ? -temp : temp;
            if (temp < r) {
                xSum += r - temp;
                if (trapMode)
                    break;
            }
            temp = orbitY[i];
            temp = temp < 0 ? -temp : temp;
            if (temp < r) {
                ySum += r - temp;
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
