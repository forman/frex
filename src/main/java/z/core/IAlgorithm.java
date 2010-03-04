/*
 * Created at 26.01.2004 21:18:16
 * Copyright (c) 2004 by Norman Fomferra
 */
package z.core;

import z.util.JDOMExternalizable;

public interface IAlgorithm extends JDOMExternalizable, Cloneable {
    void reset();

    void prepare();

    Object clone();
}
