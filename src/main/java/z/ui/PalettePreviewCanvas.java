package z.ui;

import z.core.IColorizer;
import z.core.color.RGBA;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;

public class PalettePreviewCanvas extends Control {

    private IColorizer colorizer;
    private Paint backgroundPattern;
    private BufferedImage paletteImage;

    public PalettePreviewCanvas() {
        backgroundPattern = createBackgroundPattern();
    }

    @Override
    public void dispose() {
        disposePaletteImage();
        super.dispose();
    }

    public IColorizer getColorizer() {
        return colorizer;
    }

    public void setColorizer(IColorizer colorizer) {
        this.colorizer = colorizer;
        disposePaletteImage();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics2D gc) {
        Rectangle clientArea = getClientArea();
        if (paletteImage == null) {
            paletteImage = new BufferedImage(clientArea.width, clientArea.height, BufferedImage.TYPE_INT_RGB);
            Graphics2D gc2 = paletteImage.createGraphics();
            paintPalette(gc2);
            gc2.dispose();
        }
        gc.drawImage(paletteImage, null, clientArea.x, clientArea.y);
    }

    private void paintPalette(Graphics2D gc) {

        Rectangle clientArea = getClientArea();
        gc.setPaint(backgroundPattern);
        gc.fill(clientArea);

        if (colorizer != null) {
            for (int i = 0; i < clientArea.width; i++) {
                int rgba2 = colorizer.getRgba(i / ((float) clientArea.width - 1.0f));
                RGBA rgba = new RGBA(rgba2);
                Color color = new Color(rgba.getR(), rgba.getG(), rgba.getB(), rgba.getA());
                gc.setColor(color);
                gc.drawLine(clientArea.x + i, clientArea.y, clientArea.x + i, clientArea.y + clientArea.height);
            }
        }
    }

    private void disposePaletteImage() {
        if (paletteImage != null) {
            paletteImage = null;
        }
    }

    private static Paint createBackgroundPattern() {
        BufferedImage patternImage = createBackgroundPatternImage();
        return new TexturePaint(patternImage, null);
    }

    private static BufferedImage createBackgroundPatternImage() {
        final int res = 16;
        final int size = 2 * res;
        BufferedImage patternImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        Color white = new Color(255, 255, 255);
        Color grey = new Color(128, 128, 128);
        Graphics2D patternGC = patternImage.createGraphics();
        patternGC.setColor(white);
        patternGC.fill(new Rectangle(0, 0, size, size));
        patternGC.setColor(grey);
        patternGC.fill(new Rectangle(0, 0, res, res));
        patternGC.fill(new Rectangle(res, res, res, res));
        patternGC.dispose();
        return patternImage;
    }
}
