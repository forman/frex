package z.ui;

import z.core.color.ColorPoint;
import z.core.color.RGBA;
import z.util.ChangeListener;
import z.util.ChangeListenerList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class DefaultSliderBarModel implements SliderBarModel {
    private ChangeListenerList changeListenerList = new ChangeListenerList();
    private List<ColorPoint> colorPointList;
    private int selectedIndex;

    public DefaultSliderBarModel() {
        this(new ArrayList<ColorPoint>(7));
    }

    public DefaultSliderBarModel(ColorPoint[] colorPoints) {
        this(Arrays.asList(colorPoints));
    }

    public DefaultSliderBarModel(Collection<ColorPoint> colorPoints) {
        colorPointList = new ArrayList<ColorPoint>(colorPoints);
        selectedIndex = -1;
    }

    public int getColorPointCount() {
        return colorPointList.size();
    }

    public ColorPoint[] getColorPoints() {
        return colorPointList.toArray(new ColorPoint[colorPointList.size()]);
    }

    public void setColorPoints(ColorPoint[] colorPoints) {
        if (!Arrays.equals(colorPointList.toArray(new ColorPoint[colorPointList.size()]), colorPoints)) {
            colorPointList.clear();
            for (ColorPoint colorPoint : colorPoints) {
                colorPointList.add(new ColorPoint(colorPoint.getPosition(), colorPoint.getColor()));
            }
            fireStateChange();
        }
    }

    public void addColorPoint(float position, RGBA color) {
        colorPointList.add(new ColorPoint(position, color));
        fireStateChange();
    }

    public void removeColorPoint(int index) {
        colorPointList.remove(index);
        fireStateChange();
    }

    public float getPosition(int index) {
        return colorPointList.get(index).getPosition();
    }

    public void setPosition(int index, float position) {
        ColorPoint point = colorPointList.get(index);
        if (!point.samePosition(position)) {
            point.setPosition(position);
            colorPointList.set(index, new ColorPoint(position, point.getColor()));
            fireStateChange();
        }
    }

    public RGBA getColor(int index) {
        return colorPointList.get(index).getColor();
    }

    public void setColor(int index, RGBA color) {
        ColorPoint point = colorPointList.get(index);
        if (!point.getColor().equals(color)) {
            colorPointList.set(index, new ColorPoint(point.getPosition(), color));
            fireStateChange();
        }
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int index) {
        if (index != selectedIndex) {
            selectedIndex = index;
            // fireStateChange();
        }
    }

    public void addChangeListener(ChangeListener listener) {
        changeListenerList.addChangeListener(listener);
    }

    public void removeChangeListener(ChangeListener listener) {
        changeListenerList.removeChangeListener(listener);
    }

    protected void fireStateChange() {
        changeListenerList.fireStateChange(this);
    }
}
