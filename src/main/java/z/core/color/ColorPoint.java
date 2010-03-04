/*
 * $Id: ColorPoint.java,v 1.1 2006/11/17 02:17:04 norman Exp $
 *
 * Copyright (c) 2003 Brockmann Consult GmbH. All right reserved.
 * http://www.brockmann-consult.de
 */
package z.core.color;

import z.util.Assert;

public class ColorPoint {
    private float position;

    private RGBA color;

    public ColorPoint(float position, RGBA color) {
        setPosition(position);
        setColor(color);
    }

    public float getPosition() {
        return position;
    }

    public void setPosition(float position) {
        Assert.argument(position >= 0.0f, "position >= 0.0f");
        Assert.argument(position <= 1.0f, "position <= 1.0f");
        this.position = position;
    }

    public RGBA getColor() {
        return color;
    }

    public void setColor(RGBA color) {
        Assert.notNull(color, "color");
        this.color = color;
    }
}
