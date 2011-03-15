package z.util;

import java.util.Random;

/**
 * Computes turpulence.
 *
 * @author Norman Fomferra
 */
public class Turbulence {

    public static double computeTurbulence(double x, double y, double scale, int numOctaves) {
        double t = 0;
        double s = 0;
        for (int i = 0; i < numOctaves; i++) {
            t += computeNoise(x / scale, y / scale) * scale;
            s += scale;
            scale /= 2;
        }
        return t / s;
    }

    public static double computeNoise(double u, double v) {

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

    private static final int size = 256;
    private static final float[][] noise = new float[size][size];

    static {
        final Random random = new Random(0);
        for (int j = 0; j < size; j++) {
            for (int i = 0; i < size; i++) {
                noise[i][j] = random.nextFloat();
            }
        }
    }

}
