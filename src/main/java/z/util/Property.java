/*
 * Created at 26.01.2004 21:26:44
 * Copyright (c) 2004 by Norman Fomferra
 */
package z.util;

import org.jdom.Element;
import org.jdom.JDOMException;
import z.StringLiterals;

import java.awt.geom.Point2D;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;

public class Property {
    public static final String PROPERTY_ELEMENT_NAME = "property";  // NON-NLS

    private String name;

    private String label;

    private Class type;

    private Method getter;

    private Method setter;

    public Property(String name, Class type, Method getter, Method setter) {
        this.name = name;
        this.label = mkLabel(name);
        this.type = type;
        this.getter = getter;
        this.setter = setter;
    }

    private static String mkLabel(String name) {
        StringBuilder sb = new StringBuilder();
        char[] chars = name.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (i == 0) {
                sb.append(Character.toUpperCase(chars[i]));
            } else if (Character.isUpperCase(chars[i])
                    && Character.isLowerCase(chars[i - 1])) {
                sb.append(' ');
                sb.append(Character.toLowerCase(chars[i]));
            } else {
                sb.append(chars[i]);
            }
        }
        return sb.toString();
    }

    public String getName() {
        return name;
    }

    public String getLabel() {
        return label;
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
        return getGetter().invoke(instance);
    }

    public String getValueAsText(Object instance) throws IllegalAccessException,
            InvocationTargetException {
        final Object value = getValue(instance);
        return convertValueToText(value);
    }

    public void setValue(Object instance, Object value) throws IllegalAccessException,
            InvocationTargetException {
        getSetter().invoke(instance, value);
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
        final Method[] methods = cls.getDeclaredMethods();
        for (int i = 0; i < methods.length; i++) {
            final Method method = methods[i];
            if ((method.getName().startsWith("get")   // NON-NLS
                    || method.getName().startsWith("is"))  // NON-NLS
                    && !method.getReturnType().equals(Void.TYPE)
                    && method.getParameterTypes().length == 0) {
                try {
                    final int pos = method.getName().startsWith("get") ? 3 : 2; // NON-NLS
                    final String firstChar = method.getName()
                            .substring(pos, pos + 1)
                            .toLowerCase();
                    final String subseqChars = method.getName()
                            .substring(pos + 1);
                    final String name = firstChar + subseqChars;
                    final Class type = method.getReturnType();
                    final Method getter = method;
                    final String setterName = "set"  // NON-NLS
                            + method.getName().substring(pos);
                    final Class[] setterParams = new Class[]{type};
                    final Method setter = cls.getDeclaredMethod(setterName,
                                                                setterParams);
                    properties.add(new Property(name, type, getter, setter));
                } catch (NoSuchMethodException e) {
                    // ok
                } catch (SecurityException e) {
                    // ok
                }
            }
        }
        final Class superClass = cls.getSuperclass();
        if (superClass != null && !superClass.equals(Object.class)) {
            collectPropertiesOfClass(superClass, properties);
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
            final String name = JDOMHelper.getAttributeString(child, "name");   // NON-NLS
            final String textValue = JDOMHelper.getAttributeString(child, "value"); // NON-NLS
            final Property property = getProperty(properties, name);
            if (property != null) {
                try {
                    property.setValueFromText(instance, textValue);
                } catch (IllegalAccessException e) {
                    throw new JDOMException(MessageFormat.format("Eigenschaft ''{0}'' konnte für Objekt der Klasse ''{1}'' nicht gesetzt werden", name, instance.getClass().getName()), e);
                } catch (InvocationTargetException e) {
                    throw new JDOMException(MessageFormat.format("Eigenschaft ''{0}'' konnte für Objekt der Klasse ''{1}'' nicht gesetzt werden", name, instance.getClass().getName()), e);
                }
            } else {
                throw new JDOMException(MessageFormat.format("Eigenschaft ''{0}'' konnte in der Klasse ''{1}'' nicht gefunden werden", name, instance.getClass().getName()));
            }
        }
    }

    public static Element writePropertiesToElement(Element element,
                                                   Object instance,
                                                   Property[] properties) throws JDOMException {
        element.setAttribute("class", instance.getClass().getName());  // NON-NLS
        for (int i = 0; i < properties.length; i++) {
            final Property property = properties[i];
            try {
                final String name = property.getName();
                final Object value = property.getValue(instance);
                final String textValue = convertValueToText(value);
                final Element paramElement = new Element(PROPERTY_ELEMENT_NAME);
                paramElement.setAttribute("name", name);  // NON-NLS
                paramElement.setAttribute("value", textValue); // NON-NLS
                element.addContent(paramElement);
            } catch (IllegalAccessException e) {
                throw new JDOMException(MessageFormat.format(StringLiterals.getString("ex.cannotGetProperty"),
                                                             property.getName(),
                                                             instance.getClass().getName()), e);
            } catch (InvocationTargetException e) {
                throw new JDOMException(MessageFormat.format(StringLiterals.getString("ex.cannotGetProperty"),
                                                             property.getName(),
                                                             instance.getClass().getName()), e);
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
