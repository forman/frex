/*
 * Created at 26.01.2004 22:58:57
 * Copyright (c) 2004 by Norman Fomferra
 */
package z.core;

public interface IIndexer extends IAlgorithm {
    String ELEMENT_NAME = "indexer"; //$NON-NLS-1$

    double computeIndex(double x, double y);
}
