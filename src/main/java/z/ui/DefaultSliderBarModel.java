package z.ui;

import z.core.color.ColorPoint;
import z.core.color.RGBA;
import z.util.ChangeListener;
import z.util.ChangeListenerList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class DefaultSliderBarModel implements SliderBarModel {
    private ChangeListenerList changeListenerList = new ChangeListenerList();
    private List<ColorPoint> pointList;
    private int selectedIndex;

    public DefaultSliderBarModel() {
        this(new ArrayList<ColorPoint>(7));
    }

    public DefaultSliderBarModel(ColorPoint[] colorPoints) {
        this(Arrays.asList(colorPoints));
    }

    public DefaultSliderBarModel(Collection<ColorPoint> colorPoints) {
        pointList = new ArrayList<ColorPoint>(colorPoints);
        selectedIndex = -1;
    }

    public int getColorPointCount() {
        return pointList.size();
    }

    public Collection<ColorPoint> getColorPoints() {
        return Collections.unmodifiableList(pointList);
    }

    public void setColorPoints(Collection<ColorPoint> tiePoints) {
        pointList.clear();
        for (ColorPoint tiePoint : tiePoints) {
            pointList.add(new ColorPoint(tiePoint.getPosition(), tiePoint.getColor()));
        }
        fireStateChange();
    }

    public void addColorPoint(float position, RGBA color) {
        pointList.add(new ColorPoint(position, color));
        fireStateChange();
    }

    public void removeColorPoint(int index) {
        pointList.remove(index);
        fireStateChange();
    }

    public float getPosition(int index) {
        return pointList.get(index).getPosition();
    }

    public void setPosition(int index, float position) {
        ColorPoint point = pointList.get(index);
        if (point.getPosition() != position) {
            point.setPosition(position);
            fireStateChange();
        }
    }

    public RGBA getColor(int index) {
        return pointList.get(index).getColor();
    }

    public void setColor(int index, RGBA color) {
        ColorPoint point = pointList.get(index);
        if (point.getColor() != color) {
            point.setColor(color);
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
