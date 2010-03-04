/*
 * Created at 26.01.2004 23:07:28
 * Copyright (c) 2004 by Norman Fomferra
 */
package z.core.support.indexers;

import z.core.ScaledIndexer;

public class SinAbsZ extends ScaledIndexer {

    public void reset() {
        setBias(0.0);
        setFactor(10.0);
    }

    public final double computeIndex(double x, double y) {
        double v = Math.sqrt(x * x + y * y);
        v = scale(v);
        return 100.0 * Math.sin(v);
    }
}
