package z.core.support.colorizers;

import org.jdom.Element;
import org.jdom.JDOMException;
import z.core.ColorTable;
import z.core.color.ColorInterpolator;
import z.core.color.ColorPoint;
import z.core.color.RGBA;
import z.util.JDOMHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PaletteColorTable extends ColorTable {

    private List<ColorPoint> colorPointList;

    public PaletteColorTable() {
    }

    @Override
    public Object clone() {
        PaletteColorTable c = (PaletteColorTable) super.clone();
        c.colorPointList = new ArrayList<ColorPoint>(getColorPointCount());
        c.setColorPoints(getColorPoints());
        return c;
    }

    public ColorPoint[] getColorPoints() {
        return colorPointList.toArray(new ColorPoint[colorPointList.size()]);
    }

    public void setColorPoints(ColorPoint[] colorPoints) {
        if (!Arrays.equals(getColorPoints(), colorPoints)) {
            colorPointList.clear();
            for (ColorPoint colorPoint : colorPoints) {
                colorPointList.add(new ColorPoint(colorPoint.getPosition(), colorPoint.getColor()));
            }
            firePropertyChange("colorPointList", // NON-NLS
                               null, colorPointList);
        }
    }

    public int getColorPointCount() {
        return colorPointList.size();
    }

    public ColorPoint getColorPoint(int index) {
        return colorPointList.get(index);
    }

    public void addColorPoint(ColorPoint point) {
        colorPointList.add(point);
        firePropertyChange("colorPointList", // NON-NLS
                           null, colorPointList);
    }

    public void removeColorPoint(int index) {
        colorPointList.remove(index);
        firePropertyChange("colorPointList", // NON-NLS
                           null, colorPointList);
    }

    @Override
    public void reset() {
        super.reset();
        colorPointList = new ArrayList<ColorPoint>(4);
        addColorPoint(new ColorPoint(0.0f, RGBA.BLACK));
        addColorPoint(new ColorPoint(0.33f, RGBA.BLUE));
        addColorPoint(new ColorPoint(0.66f, RGBA.WHITE));
        addColorPoint(new ColorPoint(1.0f, RGBA.BLACK));
    }

    @Override
    protected void fillRgbaArray(int[] rgbaArray) {
        RGBA[] colors = ColorInterpolator.createColors(colorPointList, getNumColors(), null);
        for (int i = 0; i < rgbaArray.length; i++) {
            rgbaArray[i] = colors[i].getValue();
        }
    }

    @Override
    public void writeExternal(Element element) throws JDOMException {
        super.writeExternal(element);
        for (int i = 0; i < getColorPointCount(); i++) {
            final ColorPoint point = getColorPoint(i);
            Element child = new Element("colorPoint"); // NON-NLS
            JDOMHelper.setAttributeDouble(child,
                                          "pos",  // NON-NLS
                                          point.getPosition());
            JDOMHelper.setAttributeColor(child,
                                         "color",  // NON-NLS
                                         point.getColor());
            element.addContent(child);
        }
    }

    @Override
    public void readExternal(Element element) throws JDOMException {
        super.readExternal(element);
        colorPointList.clear();
        readOldFormat(element);
        final List children = element.getChildren("colorPoint");   // NON-NLS
        for (Object aChildren : children) {
            Element child = (Element) aChildren;
            final float pos = (float) JDOMHelper.getAttributeDouble(child,
                                                                    "pos");  // NON-NLS
            final RGBA color = JDOMHelper.getAttributeColor(child, "color");  // NON-NLS
            addColorPoint(new ColorPoint(pos, color));
        }
    }

    private void readOldFormat(Element element) throws JDOMException {
        final List children = element.getChildren("tiePoint");    // NON-NLS
        for (Object aChildren : children) {
            Element child = (Element) aChildren;
            final float position = (float) JDOMHelper.getAttributeDouble(child,
                                                                         "pos");  // NON-NLS
            final RGBA color = JDOMHelper.getAttributeColor(child, "color");  // NON-NLS
            addColorPoint(new ColorPoint(position, color));
        }
    }

}
