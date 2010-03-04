package z.io;

import org.jdom.Element;

public interface IO {
    String getElementName();

    void read(IOManager ioManager, Element element);

    void write(IOManager ioManager, Element element);
}
