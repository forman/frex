/*
 * Created at 26.01.2004 21:45:09
 * Copyright (c) 2004 by Norman Fomferra
 */
package z.core;

public abstract class ScaledIndexer extends Indexer {

    protected double bias;
    protected double factor;

    @Override
    public void reset() {
        super.reset();
        bias = 0.0;
        factor = 1.0;
    }

    public final double getBias() {
        return bias;
    }

    public final double getFactor() {
        return factor;
    }

    public final void setBias(double bias) {
        this.bias = bias;
    }

    public final void setFactor(double factor) {
        this.factor = factor;
    }

    public final double scale(double x) {
        return bias + factor * x;
    }
}
