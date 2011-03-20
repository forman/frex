package z.core;

import org.jdom.Element;
import org.jdom.JDOMException;
import z.util.Property;

import java.beans.PropertyChangeListener;
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

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }

    protected void fireIndexedPropertyChange(String propertyName, int index, Object oldValue, Object newValue) {
        propertyChangeSupport.fireIndexedPropertyChange(propertyName, index, oldValue, newValue);
    }
}
