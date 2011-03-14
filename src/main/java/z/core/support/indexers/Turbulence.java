/*
 * Created at 26.01.2004 23:07:28
 * Copyright (c) 2004 by Norman Fomferra
 */
package z.core.support.indexers;

import z.core.Indexer;

import java.util.Random;

public final class Turbulence extends Indexer {
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
        return turbulence(x, y, scale, numOctaves);
    }

    public static double turbulence(double x, double y, double scale, int numOctaves) {
        double t = 0;
        double s = 0;
        for (int i = 0; i < numOctaves; i++) {
            t += noise(x / scale, y / scale) * scale;
            s += scale;
            scale /= 2;
        }
        return t / s;
    }

    private static double noise(double u, double v) {

        int iu = (int) Math.floor(u);
        int iv = (int) Math.floor(v);
        float du = (float)(u - iu);
        float dv = (float)(v - iv);

        iu = iu % size;
        if (iu < 0) {
            iu += size;
        }
        iv = iv % size;
        if (iv < 0) {
            iv += size;
        }
        int ip = (iu + 1) % size;
        int iq = (iv + 1) % size;

        float bot = noise[iu][iv] + du * (noise[ip][iv] - noise[iu][iv]);
        float top = noise[iu][iq] + du * (noise[ip][iq] - noise[iu][iq]);

        return bot + dv * (top - bot);
    }

    static final int size = 256;
    static final float[][] noise = new float[size][size];

    static {
        Random random = new Random(0);
        for (int j = 0; j < size; j++) {
            for (int i = 0; i < size; i++) {
                noise[i][j] = random.nextFloat();
            }
        }
    }

}
