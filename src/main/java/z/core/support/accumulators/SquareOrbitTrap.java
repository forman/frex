/*
 * Created at 26.01.2004 21:50:28
 * Copyright (c) 2004 by Norman Fomferra
 */
package z.core.support.accumulators;

import z.core.Accumulator;

public final class SquareOrbitTrap extends Accumulator {
    protected double offsetX;

    protected double offsetY;

    protected double factor;

    protected double radius;

    public void reset() {
        super.reset();
        setOffsetX(0);
        setOffsetY(0);
        setFactor(0.1);
        setRadius(0.2);
    }

    public double getOffsetX() {
        return offsetX;
    }

    public double getOffsetY() {
        return offsetY;
    }

    public double getFactor() {
        return factor;
    }

    public void setOffsetX(double offsetX) {
        this.offsetX = offsetX;
    }

    public void setOffsetY(double offsetY) {
        this.offsetY = offsetY;
    }

    public void setFactor(double size) {
        this.factor = size;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public void compute(final double[] orbitX,
                        final double[] orbitY,
                        final int iter,
                        final int maxIter,
                        final boolean trapMode,
                        final double[] result) {
        final double x0 = offsetX;
        final double y0 = offsetY;
        final double a = factor;
        final double r = radius;
        final boolean turbulent = turbulenceUsed;
        double sum = 0;
        double dx, dy, d;
        for (int i = 0; i < iter; i++) {
            dx = orbitX[i] - x0;
            dy = orbitY[i] - y0;
            if (turbulent) {
                double t = computeTurbulence(orbitX[i], orbitY[i]);
                dx += t;
                dy += t;
            }
            d = a * dx * dx - dy;
            if (d < 0)
                d *= -1;
            if (d < r) {
                sum += r - d;
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
