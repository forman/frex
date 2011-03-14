package z.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * todo - add api doc
 *
 * @author Norman Fomferra
 */
public class GreyscaleImage {
    final File sourceFile;
    final int width;
    final int height;
    final float[] data;

    public GreyscaleImage(File sourceFile, int width, int height, float[] data) {
        this.sourceFile = sourceFile;
        this.width = width;
        this.height = height;
        this.data = data;
    }

    public static GreyscaleImage create(File sourceFile) throws IOException {
        BufferedImage image = ImageIO.read(sourceFile);
        int width = image.getWidth();
        int height = image.getHeight();
        float[] data = new float[width * height];
        int[] argbs = image.getRGB(0, 0, width, height, null, 0, width);
        for (int i = 0; i < width * height; i++) {
            int argb = argbs[i];
            int r = (argb & 0x00FF0000) >> 16;
            int g = (argb & 0x0000FF00) >> 8;
            int b = argb & 0x000000FF;
            data[i] = (0.3F * r + 0.59F * g + 0.11F * b) / 255.0F;
        }
        return new GreyscaleImage(sourceFile, width, height, data);
    }

    public float[] getData() {
        return data;
    }

    public int getHeight() {
        return height;
    }

    public File getSourceFile() {
        return sourceFile;
    }

    public int getWidth() {
        return width;
    }

    public final float getGrey(double u, double v) {

        int iu = (int) Math.floor(u);
        int iv = (int) Math.floor(v);

        float du = (float)(u - iu);
        float dv = (float)(v - iv);

        iu = iu % width;
        if (iu < 0) {
            iu += width;
        }
        int ip = (iu + 1) % width;

        iv = iv % height;
        if (iv < 0) {
            iv += height;
        }
        int iq = (iv + 1) % height;

        float bot = data[iu + iv * width] + du * (data[ip + iv * width] - data[iu + iv * width]);
        float top = data[iu + iq * width] + du * (data[ip + iq * width] - data[iu + iq * width]);

        return bot + dv * (top - bot);
    }
}
