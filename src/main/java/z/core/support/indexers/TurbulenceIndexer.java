/*
 * Created at 26.01.2004 23:07:28
 * Copyright (c) 2004 by Norman Fomferra
 */
package z.core.support.indexers;

import z.core.Indexer;
import z.util.Turbulence;

import java.util.Random;

public final class TurbulenceIndexer extends Indexer {
    private double scale;
    private int numOctaves;

    public void reset() {
        setScale(0.2);
        setNumOctaves(6);
    }

    public int getNumOctaves() {
        return numOctaves;
    }

    public void setNumOctaves(int numOctaves) {
        this.numOctaves = numOctaves;
    }

    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    public final double computeIndex(double x, double y) {
        return Turbulence.computeTurbulence(x, y, scale, numOctaves);
    }
}
