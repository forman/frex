/*
 * Created at 26.01.2004 21:50:28
 * Copyright (c) 2004 by Norman Fomferra
 */
package z.core.support.accumulators;

import z.core.Accumulator;
import z.util.Turbulence;

public class TurbulentLinearOrbitTrap3 extends Accumulator {
    private double turbulenceScale;
    private double turbulenceIntensity;
    private int turbulenceNumOctaves;
    protected double trapRadius;

    public void reset() {
        setTrapRadius(0.1);
        setTurbulenceScale(0.1);
        setTurbulenceNumOctaves(4);
        setTurbulenceIntensity(1.0);
    }

    public void prepare() {
    }

    public double getTurbulenceIntensity() {
        return turbulenceIntensity;
    }

    public void setTurbulenceIntensity(double turbulenceIntensity) {
        this.turbulenceIntensity = turbulenceIntensity;
    }

    public double getTrapRadius() {
        return trapRadius;
    }

    public void setTrapRadius(double trapRadius) {
        this.trapRadius = trapRadius;
    }

    public int getTurbulenceNumOctaves() {
        return turbulenceNumOctaves;
    }

    public void setTurbulenceNumOctaves(int turbulenceNumOctaves) {
        this.turbulenceNumOctaves = turbulenceNumOctaves;
    }

    public double getTurbulenceScale() {
        return turbulenceScale;
    }

    public void setTurbulenceScale(double turbulenceScale) {
        this.turbulenceScale = turbulenceScale;
    }

    public void compute(final double[] orbitX,
                        final double[] orbitY,
                        final int iter,
                        final int maxIter,
                        final boolean trapMode,
                        final double[] result) {

        final double r = trapRadius * trapRadius;
        final double intens = turbulenceIntensity;
        final double scale = turbulenceScale;
        final int numOctaves = turbulenceNumOctaves;

        double sum = 0.0;
        double temp;

        for (int i = 0; i < iter; i++) {
            temp = (orbitX[i] - orbitY[i]) + intens * Turbulence.computeTurbulence(orbitX[i], orbitY[i], scale, numOctaves);
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
