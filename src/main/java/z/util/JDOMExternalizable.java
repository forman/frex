/*
 * Created at 06.01.2004 13:29:13
 * Copyright (c) 2004 by Norman Fomferra
 */
package z.util;

import org.jdom.Element;
import org.jdom.JDOMException;

public interface JDOMExternalizable {
    void writeExternal(Element element) throws JDOMException;

    void readExternal(Element element) throws JDOMException;
}
