/*
 * Created at 26.01.2004 21:50:28
 * Copyright (c) 2004 by Norman Fomferra
 */
package z.core.support.accumulators;

import z.core.Accumulator;

public class LinearOrbitTrap3 extends Accumulator {
    protected double trapRadius;

    public void reset() {
        super.reset();
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
        final double r = trapRadius;
        final boolean turbulent = turbulenceUsed;
        double sum = 0;
        double temp;
        for (int i = 0; i < iter; i++) {
            temp = orbitX[i] - orbitY[i];
            if (turbulent) {
                temp += computeTurbulence(orbitX[i], orbitY[i]);
            }
            temp *= temp;
            if (temp < r) {
                sum += (r - temp);
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
