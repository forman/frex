/*
 * Created at 26.01.2004 21:18:16
 * Copyright (c) 2004 by Norman Fomferra
 */
package z.core;

import z.util.JDOMExternalizable;

/**
 * Base interface for all algorithms.
 */
public interface IAlgorithm extends JDOMExternalizable, Cloneable {

    /**
     * Called once before the algorithm is used.
     */
    void reset();

    /**
     * Called for each pixel, on which this algorithm is invoked.
     */
    void prepare();

    Object clone();
}
