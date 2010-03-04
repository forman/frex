package z.core;

import org.jdom.Element;
import org.jdom.JDOMException;
import z.util.JDOMExternalizable;
import z.util.JDOMHelper;


/**
 * The region in the z-plane covered by a fractal. An image with given <code>imageWidth</code> and <code>imageHeight</code>
 * covers the following area:
 * <pre>
 *       double pixelSize = 2.0 * region.getRadius() / Math.min(imageWidth, imageHeight);
 *       double zx1 = region.getCenterX() - 0.5 * pixelSize * imageWidth;
 *       double zy1 = region.getCenterY() - 0.5 * pixelSize * imageHeight;
 *       double zx2 = region.getCenterX() + 0.5 * pixelSize * imageWidth;
 *       double zy2 = region.getCenterY() + 0.5 * pixelSize * imageHeight;
 * </pre>
 *
 * @author Norman
 */
public class Region implements JDOMExternalizable, Cloneable {
    public static final String ELEMENT_NAME = "region"; //$NON-NLS-1$

    private double centerX;

    private double centerY;

    private double radius;

    public Region() {
        this(0.0, 0.0, 1.0);
    }

    public Region(double centerX, double centerY, double radius) {
        set(centerX, centerY, radius);
    }

    public Region(Region other) {
        this(other.getCenterX(), other.getCenterY(), other.getRadius());
    }

    public double getCenterX() {
        return centerX;
    }

    public void setCenterX(double centerX) {
        this.centerX = centerX;
    }

    public double getCenterY() {
        return centerY;
    }

    public void setCenterY(double centerY) {
        this.centerY = centerY;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        checkRadius(radius);
        this.radius = radius;
    }

    public void set(double centerX, double centerY, double radius) {
        checkRadius(radius);
        this.centerX = centerX;
        this.centerY = centerY;
        this.radius = radius;
    }

    public void set(Region region) {
        set(region.getCenterX(), region.getCenterY(), region.getRadius());
    }

    public Region zoom(int imageWidth,
                       int imageHeight,
                       double visibleImageRectX,
                       double visibleImageRectY,
                       double visibleImageRectWidth,
                       double visibleImageRectHeight) {
        // double p = 2.0 * getRadius() / imageHeight;
        double p = 2.0 * getRadius() / (double) Math.min(imageWidth, imageHeight);
        // center of given visible image rect
        double icx = visibleImageRectX + 0.5 * visibleImageRectWidth;
        double icy = visibleImageRectY + 0.5 * visibleImageRectHeight;
        // center of given visible image rect in Z-coord.
        double cx = getCenterX() + p * (icx - 0.5 * (double) imageWidth);
        double cy = getCenterY() - p * (icy - 0.5 * (double) imageHeight);
        // new radius of region
        double r = 0.5
                * Math.min(visibleImageRectWidth, visibleImageRectHeight) * p;
        return new Region(cx, cy, r);
    }

    @Override
    public String toString() {
        return getClass().getName() + "[centerX=" + centerX + ", centerY="
                + centerY + ", radius=" + radius + "]";
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Region) {
            Region region = (Region) obj;
            return region.getCenterX() == centerX
                    && region.getCenterY() == centerY
                    && region.getRadius() == radius;
        }
        return false;
    }

    public void readExternal(Element element) throws JDOMException {
        final double centerX = JDOMHelper.getAttributeDouble(element, "centerX");
        final double centerY = JDOMHelper.getAttributeDouble(element, "centerY");
        final double radius = JDOMHelper.getAttributeDouble(element, "radius");
        set(centerX, centerY, radius);
    }

    public void writeExternal(Element element) throws JDOMException {
        JDOMHelper.setAttributeDouble(element, "centerX", getCenterX());
        JDOMHelper.setAttributeDouble(element, "centerY", getCenterY());
        JDOMHelper.setAttributeDouble(element, "radius", getRadius());
    }

    private void checkRadius(double radius) {
        if (radius <= 0.0) {
            throw new IllegalArgumentException("radius <= 0.0");
        }
    }
}
