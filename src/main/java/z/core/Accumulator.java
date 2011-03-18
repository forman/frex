/*
 * Created at 26.01.2004 21:45:09
 * Copyright (c) 2004 by Norman Fomferra
 */
package z.core;

import z.util.Turbulence;

public abstract class Accumulator extends Algorithm implements IAccumulator {
    protected boolean turbulenceUsed = false;
    protected double turbulenceIntensity = 1.0;
    protected double turbulenceScale = 1.0;
    protected int turbulenceOctaveCount = 4;

    protected Accumulator() {
        reset();
    }

    @Override
    public void reset() {
        super.reset();
        setTurbulenceUsed(false);
        setTurbulenceIntensity(0.5);
        setTurbulenceScale(0.2);
        setTurbulenceOctaveCount(5);
    }

    public final boolean isTurbulenceUsed() {
        return turbulenceUsed;
    }

    public final void setTurbulenceUsed(boolean turbulenceUsed) {
        this.turbulenceUsed = turbulenceUsed;
    }

    public final double getTurbulenceIntensity() {
        return turbulenceIntensity;
    }

    public final void setTurbulenceIntensity(double turbulenceIntensity) {
        this.turbulenceIntensity = turbulenceIntensity;
    }

    public final double getTurbulenceScale() {
        return turbulenceScale;
    }

    public final void setTurbulenceScale(double turbulenceScale) {
        this.turbulenceScale = turbulenceScale;
    }

    public final int getTurbulenceOctaveCount() {
        return turbulenceOctaveCount;
    }

    public final void setTurbulenceOctaveCount(int turbulenceOctaveCount) {
        this.turbulenceOctaveCount = turbulenceOctaveCount;
    }

    protected final double computeTurbulence(double x, double y) {
        return turbulenceIntensity * Turbulence.computeTurbulence(x, y, turbulenceScale, turbulenceOctaveCount);
    }
}
