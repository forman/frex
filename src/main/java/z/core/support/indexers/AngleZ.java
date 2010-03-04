/*
 * Created at 26.01.2004 23:07:28
 * Copyright (c) 2004 by Norman Fomferra
 */
package z.core.support.indexers;

import z.core.Indexer;

public class AngleZ extends Indexer {

    public final double computeIndex(double x, double y) {
        return 180.0 * Math.atan2(y, x) / Math.PI;
    }
}
