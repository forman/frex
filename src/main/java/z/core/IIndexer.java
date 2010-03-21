/*
 * Created at 26.01.2004 22:58:57
 * Copyright (c) 2004 by Norman Fomferra
 */
package z.core;

/**
 * Indexers compute a real index value from a given complex number.
 */
public interface IIndexer extends IAlgorithm {
    String ELEMENT_NAME = "indexer"; //$NON-NLS-1$

    /**
     * Computes the real index value from the given complex number.
     *
     * @param x The X-coordinate of the complex value.
     * @param y The Y-coordinate of the complex value.
     * @return A real index.
     */
    double computeIndex(double x, double y);
}
