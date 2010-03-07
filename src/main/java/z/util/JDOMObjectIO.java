package z.util;

import org.jdom.Element;
import org.jdom.JDOMException;
import z.core.AlgorithmRegistry;

public class JDOMObjectIO {
    public static final String CLASS_ATTRIBUTE_NAME = "class"; // NON-NLS

    public static Object readObjectFromChild(Element element, String childName) throws JDOMException {
        return readObjectFromChild(element, childName, null);
    }

    public static Object readObjectFromChild(Element element,
                                             String childName,
                                             Class<?> defaultType) throws JDOMException {
        final Element childElement = element.getChild(childName);
        if (childElement == null) {
            return null;
        }
        final Object object = createObject(childElement, defaultType);
        if (object instanceof JDOMExternalizable) {
            ((JDOMExternalizable) object).readExternal(childElement);
        }
        return object;
    }

    public static void writeObjectToChild(final Element element,
                                          String childName,
                                          Object object) throws JDOMException {
        writeObjectToChild(element, childName, object, (Class<?>) null);
    }

    public static void writeObjectToChild(final Element element,
                                          String childName,
                                          Object object,
                                          Class<?> defaultType) throws JDOMException {
        if (object == null) {
            return;
        }
        final Element childElement = new Element(childName);
        if (defaultType == null || object.getClass() != defaultType) {
            JDOMHelper.setAttributeString(childElement,
                                          CLASS_ATTRIBUTE_NAME,
                                          object.getClass().getName());
        }
        if (object instanceof JDOMExternalizable) {
            ((JDOMExternalizable) object).writeExternal(childElement);
        }
        element.addContent(childElement);
    }

    private static Object createObject(final Element element, Class<?> defaultType) throws JDOMException {
        Class<?> type = getType(element, defaultType);
        return createInstance(element, type);
    }

    private static Class<?> getType(final Element element, Class<?> defaultType) throws JDOMException {
        Class<?> type = defaultType;
        final String className = JDOMHelper.getAttributeString(element,
                                                               CLASS_ATTRIBUTE_NAME,
                                                               null);
        if (className != null) {
            type = loadType(element, className);
        } else if (type == null) {
            throw new JDOMException("Element '" + element.getName()
                    + "': Attribut '" + CLASS_ATTRIBUTE_NAME + "' fehlt"); /* I18N */
        }
        return type;
    }

    private static Class<?> loadType(final Element element, final String className) throws JDOMException {
        try {
            return AlgorithmRegistry.instance().getPluginClassLoader().loadClass(className);
        } catch (ClassNotFoundException e) {
            throw new JDOMException("Element '" + element.getName()
                    + "': Klasse '" + className + "' wurde nicht gefunden", e); /* I18N */
        }
    }

    private static Object createInstance(final Element element, Class<?> type) throws JDOMException {
        final Object object;
        try {
            object = type.newInstance();
        } catch (Exception e1) {
            throw new JDOMException("Element '" + element.getName()
                    + "': Objekt vom Typ '" + type.getName()
                    + "' konnte nicht erzeugt werden", e1); /* I18N */
        }
        return object;
    }
}
