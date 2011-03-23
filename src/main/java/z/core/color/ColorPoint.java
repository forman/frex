/*
 * $Id: ColorPoint.java,v 1.1 2006/11/17 02:17:04 norman Exp $
 *
 * Copyright (c) 2003 Brockmann Consult GmbH. All right reserved.
 * http://www.brockmann-consult.de
 */
package z.core.color;

import z.util.Assert;

public class ColorPoint {
    private final float position;

    private final RGBA color;

    public ColorPoint(float pos, RGBA color) {
        Assert.argument(pos >= 0.0F, "position >= 0.0"); // NON-NLS
        Assert.argument(pos <= 1.0F, "position <= 1.0"); // NON-NLS
        Assert.notNull(color, "color"); // NON-NLS
        this.position = roundPosition(pos);
        this.color = color;
    }

    public float getPosition() {
        return position;
    }

    public RGBA getColor() {
        return color;
    }

    public boolean samePosition(float pos) {
        return position == roundPosition(pos);
    }

    public static float roundPosition(float pos) {
        return ((int) (pos * 1000.0F)) / 1000.0F;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ColorPoint that = (ColorPoint) o;

        if (!samePosition(that.position)) {
            return false;
        }
        if (!color.equals(that.color)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = Float.floatToIntBits(position);
        result = 31 * result + color.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ColorPoint{" +
                "color=" + color +
                ", position=" + position +
                '}';
    }
}
