package z.ui;

import z.core.color.ColorPoint;
import z.core.color.RGBA;
import z.util.ChangeListener;

import java.util.Collection;

public interface SliderBarModel {

    int getColorPointCount();

    Collection<ColorPoint> getColorPoints();

    void setColorPoints(Collection<ColorPoint> colorPoints);

    void addColorPoint(float position, RGBA color);

    void removeColorPoint(int index);

    float getPosition(int index);

    void setPosition(int index, float position);

    RGBA getColor(int index);

    void setColor(int index, RGBA color);

    int getSelectedIndex();

    void setSelectedIndex(int index);

    void addChangeListener(ChangeListener listener);

    void removeChangeListener(ChangeListener listener);
}
