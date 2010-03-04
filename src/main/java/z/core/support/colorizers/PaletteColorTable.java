package z.core.support.colorizers;

import org.jdom.Element;
import org.jdom.JDOMException;
import z.core.ColorTable;
import z.core.color.ColorInterpolator;
import z.core.color.ColorPoint;
import z.core.color.RGBA;
import z.util.JDOMHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class PaletteColorTable extends ColorTable {

    private List<ColorPoint> tiePointList;

    public PaletteColorTable() {
    }

    @Override
    public Object clone() {
        PaletteColorTable c = (PaletteColorTable) super.clone();
        c.tiePointList = new ArrayList<ColorPoint>(getColorPointCount());
        c.setColorPoints(getColorPoints());
        return c;
    }

    public Collection<ColorPoint> getColorPoints() {
        return Collections.unmodifiableCollection(tiePointList);
    }

    public void setColorPoints(Collection<ColorPoint> points) {
        tiePointList.clear();
        for (ColorPoint tiePoint : points) {
            tiePointList.add(new ColorPoint(tiePoint.getPosition(),
                                            tiePoint.getColor()));
        }
    }

    public int getColorPointCount() {
        return tiePointList.size();
    }

    public ColorPoint getColorPoint(int index) {
        return tiePointList.get(index);
    }

    public void addColorPoint(ColorPoint point) {
        tiePointList.add(point);
    }

    public void removeColorPoint(int index) {
        tiePointList.remove(index);
    }

    @Override
    public void reset() {
        super.reset();
        tiePointList = new ArrayList<ColorPoint>(4);
        addColorPoint(new ColorPoint(0.0f, RGBA.BLACK));
        addColorPoint(new ColorPoint(0.33f, RGBA.BLUE));
        addColorPoint(new ColorPoint(0.66f, RGBA.WHITE));
        addColorPoint(new ColorPoint(1.0f, RGBA.BLACK));
    }

    @Override
    protected void fillRgbaArray(int[] rgbaArray) {
        RGBA[] colors = ColorInterpolator.createColors(tiePointList, getNumColors(), null);
        for (int i = 0; i < rgbaArray.length; i++) {
            rgbaArray[i] = colors[i].getValue();
        }
    }

    @Override
    public void writeExternal(Element element) throws JDOMException {
        super.writeExternal(element);
        for (int i = 0; i < getColorPointCount(); i++) {
            final ColorPoint point = getColorPoint(i);
            Element child = new Element("colorPoint");
            JDOMHelper.setAttributeDouble(child,
                                          "pos",
                                          point.getPosition());
            JDOMHelper.setAttributeColor(child,
                                         "color",
                                         point.getColor());
            element.addContent(child);
        }
    }

    @Override
    public void readExternal(Element element) throws JDOMException {
        super.readExternal(element);
        tiePointList.clear();
        readOldFormat(element);
        final List children = element.getChildren("colorPoint");
        for (Object aChildren : children) {
            Element child = (Element) aChildren;
            final float position = (float) JDOMHelper.getAttributeDouble(child,
                                                                         "pos");
            final RGBA color = JDOMHelper.getAttributeColor(child, "color");
            addColorPoint(new ColorPoint(position, color));
        }
    }

    private void readOldFormat(Element element) throws JDOMException {
        final List children = element.getChildren("tiePoint");
        for (Object aChildren : children) {
            Element child = (Element) aChildren;
            final float position = (float) JDOMHelper.getAttributeDouble(child,
                                                                         "pos");
            final RGBA color = JDOMHelper.getAttributeColor(child, "color");
            addColorPoint(new ColorPoint(position, color));
        }
    }

}
