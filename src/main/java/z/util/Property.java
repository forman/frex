/*
 * Created at 26.01.2004 21:26:44
 * Copyright (c) 2004 by Norman Fomferra
 */
package z.util;

import org.jdom.Element;
import org.jdom.JDOMException;

import java.awt.geom.Point2D;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

public class Property {
    public static final String PROPERTY_ELEMENT_NAME = "property";

    private String name;

    private Class type;

    private Method getter;

    private Method setter;

    public Property(String name, Class type, Method getter, Method setter) {
        this.name = name;
        this.type = type;
        this.getter = getter;
        this.setter = setter;
    }

    public String getName() {
        return name;
    }

    public Class getType() {
        return type;
    }

    public Method getGetter() {
        return getter;
    }

    public Method getSetter() {
        return setter;
    }

    public Object getValue(Object instance) throws IllegalAccessException,
            InvocationTargetException {
        return getGetter().invoke(instance, new Object[0]);
    }

    public String getValueAsText(Object instance) throws IllegalAccessException,
            InvocationTargetException {
        final Object value = getValue(instance);
        return convertValueToText(value);
    }

    public void setValue(Object instance, Object value) throws IllegalAccessException,
            InvocationTargetException {
        getSetter().invoke(instance, new Object[]{value});
    }

    public void setValueFromText(Object instance, final String textValue) throws IllegalAccessException,
            InvocationTargetException {
        Object value = convertTextToValue(textValue);
        setValue(instance, value);
    }

    public static Property[] getClassProperties(final Class cls) {
        final LinkedList<Property> properties = new LinkedList<Property>();
        collectPropertiesOfClass(cls, properties);
        return properties.toArray(new Property[properties.size()]);
    }

    private static void collectPropertiesOfClass(final Class cls,
                                                 List<Property> properties) {
        final Class superClass = cls.getSuperclass();
        if (superClass != null && !superClass.equals(Object.class)) {
            collectPropertiesOfClass(superClass, properties);
        }
        final Method[] methods = cls.getDeclaredMethods();
        for (int i = 0; i < methods.length; i++) {
            final Method method = methods[i];
            if ((method.getName().startsWith("get") || method.getName()
                    .startsWith("is"))
                    && !method.getReturnType().equals(Void.TYPE)
                    && method.getParameterTypes().length == 0) {
                try {
                    final int pos = method.getName().startsWith("get") ? 3 : 2;
                    final String firstChar = method.getName()
                            .substring(pos, pos + 1)
                            .toLowerCase();
                    final String subseqChars = method.getName()
                            .substring(pos + 1);
                    final String name = firstChar + subseqChars;
                    final Class type = method.getReturnType();
                    final Method getter = method;
                    final String setterName = "set"
                            + method.getName().substring(pos);
                    final Class[] setterParams = new Class[]{type};
                    final Method setter = cls.getDeclaredMethod(setterName,
                                                                setterParams);
                    properties.add(new Property(name, type, getter, setter));
                } catch (NoSuchMethodException e) {
                } catch (SecurityException e) {
                }
            }
        }
    }

    public static Property getProperty(Property[] properties, String name) {
        for (int i = 0; i < properties.length; i++) {
            Property property = properties[i];
            if (property.getName().equalsIgnoreCase(name)) {
                return property;
            }
        }
        return null;
    }

    public static void readPropertiesFromElement(Element element,
                                                 Object instance,
                                                 Property[] properties) throws JDOMException {
        final List children = element.getChildren(PROPERTY_ELEMENT_NAME);
        for (int j = 0; j < children.size(); j++) {
            Element child = (Element) children.get(j);
            final String name = JDOMHelper.getAttributeString(child, "name");
            final String textValue = JDOMHelper.getAttributeString(child,
                                                                   "value");
            final Property property = getProperty(properties, name);
            if (property != null) {
                try {
                    property.setValueFromText(instance, textValue);
                } catch (IllegalAccessException e) {
                    throw new JDOMException("Eigenschaft '" + name
                            + "' konnte für Objekt der Klasse '"
                            + instance.getClass().getName()
                            + "' nicht gesetzt werden", e); /* I18N */
                } catch (InvocationTargetException e) {
                    throw new JDOMException("Eigenschaft '" + name
                            + "' konnte für Objekt der Klasse '"
                            + instance.getClass().getName()
                            + "' nicht gesetzt werden", e); /* I18N */
                }
            } else {
                throw new JDOMException("Eigenschaft '" + name
                        + "' konnte in der Klasse '"
                        + instance.getClass().getName()
                        + "' nicht gefunden werden"); /* I18N */
            }
        }
    }

    public static Element writePropertiesToElement(final Element element,
                                                   Object instance,
                                                   Property[] properties) throws JDOMException {
        element.setAttribute("class", instance.getClass().getName());
        for (int i = 0; i < properties.length; i++) {
            final Property property = properties[i];
            try {
                final String name = property.getName();
                final Object value = property.getValue(instance);
                final String textValue = convertValueToText(value);
                final Element paramElement = new Element(PROPERTY_ELEMENT_NAME);
                paramElement.setAttribute("name", name);
                paramElement.setAttribute("value", textValue);
                element.addContent(paramElement);
            } catch (IllegalAccessException e) {
                throw new JDOMException("Eigenschaft '" + property.getName()
                        + "' konnte von Objekt der Klasse '"
                        + instance.getClass().getName()
                        + "' nicht gelesen werden", e); /* I18N */
            } catch (InvocationTargetException e) {
                throw new JDOMException("Eigenschaft '" + property.getName()
                        + "' konnte von Objekt der Klasse '"
                        + instance.getClass().getName()
                        + "' nicht gelesen werden", e); /* I18N */
            }
        }
        return element;
    }

    public Object convertTextToValue(final String textValue) {
        final Object value;
        final Class type = getType();
        if (type.equals(Boolean.TYPE) || type.equals(Boolean.class)) {
            value = Boolean.valueOf(textValue);
        } else if (type.equals(Integer.TYPE) || type.equals(Integer.class)) {
            value = Integer.valueOf(textValue);
        } else if (type.equals(Long.TYPE) || type.equals(Long.class)) {
            value = Long.valueOf(textValue);
        } else if (type.equals(Float.TYPE) || type.equals(Float.class)) {
            value = Float.valueOf(textValue);
        } else if (type.equals(Double.TYPE) || type.equals(Double.class)) {
            value = Double.valueOf(textValue);
        } else if (Point2D.class.isAssignableFrom(type)) {
            int sepPos = textValue.indexOf(',');
            String x = textValue.substring(0, sepPos).trim();
            String y = textValue.substring(sepPos + 1).trim();
            value = new Point2D.Double(Double.parseDouble(x),
                                       Double.parseDouble(y));
        } else if (type.equals(String.class)) {
            value = textValue;
        } else {
            throw new IllegalStateException("illegal type");
        }
        return value;
    }

    public static String convertValueToText(final Object value) {
        if (value instanceof Point2D) {
            Point2D point2D = (Point2D) value;
            return point2D.getX() + "," + point2D.getY();
        }
        return value != null ? value.toString() : "";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof Property) {
            Property other = (Property) obj;
            return other.name.equals(name);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
