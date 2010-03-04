package z.core;

import org.jdom.Element;
import org.jdom.JDOMException;
import z.util.Property;

import java.beans.PropertyChangeSupport;

public abstract class Algorithm implements IAlgorithm {
    protected final PropertyChangeSupport propertyChangeSupport;

    protected final Property[] properties;

    protected Algorithm() {
        propertyChangeSupport = new PropertyChangeSupport(this);
        properties = Property.getClassProperties(getClass());
    }

    public Property[] getProperties() {
        return properties;
    }

    public void reset() {
    }

    public void prepare() {
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    public void writeExternal(Element element) throws JDOMException {
        Property.writePropertiesToElement(element, this, properties);
    }

    public void readExternal(Element element) throws JDOMException {
        Property.readPropertiesFromElement(element, this, properties);
    }
}
