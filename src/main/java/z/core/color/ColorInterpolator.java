/*
 * $Id: ColorInterpolator.java,v 1.3 2008-10-11 11:36:49 norman Exp $
 *
 * Copyright (c) 2003 Brockmann Consult GmbH. All right reserved.
 * http://www.brockmann-consult.de
 */
package z.core.color;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;

public class ColorInterpolator {
    private final float[] rgba1 = new float[4];
    private final float[] rgba2 = new float[4];

    public ColorInterpolator() {
    }

    public RGBA interpolate(RGBA c1, RGBA c2, float weight) {
        c1.getComponents(rgba1);
        c2.getComponents(rgba2);
        return new RGBA(rgba1[0] + weight * (rgba2[0] - rgba1[0]),
                        rgba1[1] + weight * (rgba2[1] - rgba1[1]),
                        rgba1[2] + weight * (rgba2[2] - rgba1[2]),
                        rgba1[3] + weight * (rgba2[3] - rgba1[3]));
    }

    public static RGBA[] createColors(Collection<ColorPoint> positions,
                                      int numColors,
                                      RGBA[] colors) {
        return createColors(positions.toArray(new ColorPoint[positions.size()]),
                            numColors,
                            colors);
    }

    public static RGBA[] createColors(ColorPoint[] positions,
                                      int numColors,
                                      RGBA[] colors) {
        if (colors == null || colors.length != numColors) {
            colors = new RGBA[numColors];
        }
        if (positions.length == 0) {
            Arrays.fill(colors, new RGBA(0, 0, 0));
        } else if (positions.length == 1) {
            Arrays.fill(colors, positions[0].getColor());
        } else {
            Arrays.sort(positions, new Comparator<ColorPoint>() {
                public int compare(ColorPoint point1, ColorPoint point2) {
                    float delta = point1.getPosition() - point2.getPosition();
                    return (delta < 0.0F) ? -1 : (delta > 0.0F) ? +1 : 0;
                }
            });
            ColorInterpolator interpolator = new ColorInterpolator();
            for (int i = 0; i < numColors; i++) {
                float pos = (float) i / (float) (numColors - 1);
                colors[i] = interpolateColor(positions, pos, interpolator);
            }
        }
        return colors;
    }

    public static RGBA interpolateColor(ColorPoint[] positions,
                                        float pos,
                                        ColorInterpolator interpolator) {
        RGBA color = null;
        // pos >= min and pos <= max
        for (int i = 0; i < positions.length - 1; i++) {
            final ColorPoint point1 = positions[i];
            final ColorPoint point2 = positions[i + 1];
            final float pos1 = point1.getPosition();
            final float pos2 = point2.getPosition();
            if (pos1 < pos2 && pos >= pos1 && pos <= pos2) {
                color = interpolator.interpolate(point1.getColor(),
                                                 point2.getColor(),
                                                 (pos - pos1) / (pos2 - pos1));
                return color;
            }
        }
        // pos < min or pos > max
        final ColorPoint point1 = positions[positions.length - 1];
        final ColorPoint point2 = positions[0];
        final float pos1 = point1.getPosition();
        final float pos2 = point2.getPosition() + 1.0f;
        final float posT = pos < pos1 ? pos + 1.0f : pos;
        color = interpolator.interpolate(point1.getColor(),
                                         point2.getColor(),
                                         (posT - pos1) / (pos2 - pos1));
        return color;
    }
}
