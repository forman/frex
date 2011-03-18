package z.core.support.indexers;

import z.core.Indexer;
import z.util.GreyscaleImage;

public final class ImageLookup extends Indexer {
    private double scale;
    private GreyscaleImage image;

    public void reset() {
        super.reset();
        setScale(100.0);
        setImage(new GreyscaleImage(null, 2, 2, new float[]{0f, 1f, 1f, 0f}));
    }

    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    public GreyscaleImage getImage() {
        return image;
    }

    public void setImage(GreyscaleImage image) {
        this.image = image;
    }

    @Override
    public final double computeIndex(double x, double y) {
        return image.getGrey(scale * x, scale * y);
    }

}
