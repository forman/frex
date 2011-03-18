/*
 * Created at 26.01.2004 21:50:28
 * Copyright (c) 2004 by Norman Fomferra
 */
package z.core.support.accumulators;

import z.core.Accumulator;

public class SineWaveOrbitTrap extends Accumulator {
    private double trapSize;
    private double frequency;
    private double amplitude;

    public void reset() {
        super.reset();
        setTrapSize(0.1);
        setFrequency(1.0);
        setAmplitude(1.0);
    }

    public void prepare() {
    }

    public double getTrapSize() {
        return trapSize;
    }

    public void setTrapSize(double trapSize) {
        this.trapSize = trapSize;
    }


    public double getFrequency() {
        return frequency;
    }

    public void setFrequency(double factor) {
        this.frequency = factor;
    }


    public double getAmplitude() {
        return amplitude;
    }

    public void setAmplitude(double amplitude) {
        this.amplitude = amplitude;
    }

    public void compute(final double[] orbitX,
                        final double[] orbitY,
                        final int iter,
                        final int maxIter,
                        final boolean trapMode,
                        final double[] result) {
        final double r = trapSize;
        final double f = frequency;
        final double a = amplitude;
        final boolean turbulent = turbulenceUsed;
        double dy;
        double sum = 0;
        for (int i = 0; i < iter; i++) {
            dy = a * Math.sin(f * orbitX[i]) - orbitY[i];
            if (dy < 0) {
                dy = -dy;
            }
            if (turbulent) {
                double t = computeTurbulence(orbitX[i], orbitY[i]);
                dy += t;
            }
            if (dy < r) {
                sum += r - dy;
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
