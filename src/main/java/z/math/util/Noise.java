package z.math.util;

/**
 * Produces turbulence from noise.
 */
public class Noise {

    private final int n;

    private final double[][] noise;

    private double pixelSize;

    public Noise() {
        this(32);
    }

    public Noise(int n) {
        this.n = n;
        noise = new double[n][n];
        for (int j = 0; j < n; j++) {
            for (int i = 0; i < n; i++) {
                noise[j][i] = Math.random();
            }
        }
        pixelSize = 1.0;
    }

    public double getPixelSize() {
        return pixelSize;
    }

    public void setPixelSize(double pixelSize) {
        this.pixelSize = pixelSize;
    }

    public double computeTurbulence(double u, double v) {
        double t = 0.0;
        double scale = 1.0;
        do {
            t += computeLinearNoise(u / scale, v / scale) * scale;
            scale *= 0.5;
        } while (scale > pixelSize);
        return t;
    }

    public double computeLinearNoise(double u, double v) {
        double fu = Math.floor(u);
        double fv = Math.floor(v);
        double du = u - fu;
        double dv = v - fv;
        int iu = (int) modulo(fu, n);
        int iv = (int) modulo(fv, n);
        int ip = (int) modulo(fu + 1.0, n);
        int iq = (int) modulo(fv + 1.0, n);
        double bot = noise[iu][iv] + du * (noise[ip][iv] - noise[iu][iv]);
        double top = noise[iu][iq] + du * (noise[ip][iq] - noise[iu][iq]);
        return bot + dv * (top - bot);
    }

    private static double modulo(double x, double n) {
        return x - n * Math.floor(x / n);
    }
}
