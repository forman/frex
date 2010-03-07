/*
 * Created at 06.01.2004 19:53:48
 * Copyright (c) 2004 by Norman Fomferra
 */
package z.util;

import org.jdom.Attribute;
import org.jdom.DataConversionException;
import org.jdom.Element;
import org.jdom.JDOMException;
import z.StringLiterals;
import z.core.color.RGBA;

import java.text.MessageFormat;
import java.text.ParseException;
import java.util.List;

public class JDOMHelper {
    private JDOMHelper() {
    }

    public static boolean getAttributeBoolean(Element element, String name) throws JDOMException {
        return getAttributeBoolean(element, name, true, false);
    }

    public static boolean getAttributeBoolean(Element element,
                                              String name,
                                              boolean defaultValue) throws JDOMException {
        return getAttributeBoolean(element, name, false, defaultValue);
    }

    private static boolean getAttributeBoolean(Element element,
                                               String name,
                                               boolean require,
                                               boolean defaultValue) throws JDOMException {
        final Attribute attribute = element.getAttribute(name);
        if (attribute != null) {
            return attribute.getBooleanValue();
        } else if (require) {
            throw new JDOMException(createMissingAttributeExceptionMessage(element,
                                                                           name));
        } else {
            return defaultValue;
        }
    }

    public static void setAttributeBoolean(Element element,
                                           String name,
                                           boolean value) {
        element.setAttribute(name, String.valueOf(value));
    }

    public static void setAttributeBoolean(Element element,
                                           String name,
                                           boolean value,
                                           boolean defaultValue) {
        if (value != defaultValue) {
            element.setAttribute(name, String.valueOf(value));
        }
    }

    public static int getAttributeInt(Element element, String name) throws JDOMException {
        return getAttributeInt(element, name, true, 0);
    }

    public static int getAttributeInt(Element element,
                                      String name,
                                      int defaultValue) throws JDOMException {
        return getAttributeInt(element, name, false, defaultValue);
    }

    public static void setAttributeInt(Element element, String name, int value) {
        element.setAttribute(name, String.valueOf(value));
    }

    public static void setAttributeInt(Element element,
                                       String name,
                                       int value,
                                       int defaultValue) {
        if (value != defaultValue) {
            element.setAttribute(name, String.valueOf(value));
        }
    }

    private static int getAttributeInt(Element element,
                                       String name,
                                       boolean require,
                                       int defaultValue) throws JDOMException {
        final Attribute attribute = element.getAttribute(name);
        if (attribute != null) {
            return attribute.getIntValue();
        } else if (require) {
            throw new JDOMException(createMissingAttributeExceptionMessage(element,
                                                                           name));
        } else {
            return defaultValue;
        }
    }

    public static float getAttributeFloat(Element element, String name) throws JDOMException {
        return getAttributeFloat(element, name, true, 0.0f);
    }

    public static float getAttributeFloat(Element element,
                                          String name,
                                          float defaultValue) throws JDOMException {
        return getAttributeFloat(element, name, false, defaultValue);
    }

    private static float getAttributeFloat(Element element,
                                           String name,
                                           boolean require,
                                           float defaultValue) throws JDOMException {
        final Attribute attribute = element.getAttribute(name);
        if (attribute != null) {
            return attribute.getFloatValue();
        } else if (require) {
            throw new JDOMException(createMissingAttributeExceptionMessage(element,
                                                                           name));
        } else {
            return defaultValue;
        }
    }

    public static void setAttributeFloat(Element element,
                                         String name,
                                         float value) {
        element.setAttribute(name, String.valueOf(value));
    }

    public static void setAttributeFloat(Element element,
                                         String name,
                                         float value,
                                         float defaultValue) {
        if (value != defaultValue) {
            element.setAttribute(name, String.valueOf(value));
        }
    }

    public static double getAttributeDouble(Element element, String name) throws JDOMException {
        return getAttributeDouble(element, name, true, 0.0);
    }

    public static double getAttributeDouble(Element element,
                                            String name,
                                            double defaultValue) throws JDOMException {
        return getAttributeDouble(element, name, false, defaultValue);
    }

    public static void setAttributeDouble(Element element,
                                          String name,
                                          double value) {
        element.setAttribute(name, String.valueOf(value));
    }

    public static void setAttributeDouble(Element element,
                                          String name,
                                          double value,
                                          double defaultValue) {
        if (value != defaultValue) {
            element.setAttribute(name, String.valueOf(value));
        }
    }

    private static double getAttributeDouble(Element element,
                                             String name,
                                             boolean require,
                                             double defaultValue) throws JDOMException {
        final Attribute attribute = element.getAttribute(name);
        if (attribute != null) {
            return attribute.getDoubleValue();
        } else if (require) {
            throw new JDOMException(createMissingAttributeExceptionMessage(element,
                                                                           name));
        } else {
            return defaultValue;
        }
    }

    public static String getAttributeString(Element element, String name) throws JDOMException {
        return getAttributeString(element, name, true, null);
    }

    public static String getAttributeString(Element element,
                                            String name,
                                            String defaultValue) throws JDOMException {
        return getAttributeString(element, name, false, defaultValue);
    }

    public static void setAttributeString(Element element,
                                          String name,
                                          String value) {
        element.setAttribute(name, value != null ? value : "");
    }

    private static String getAttributeString(Element element,
                                             String name,
                                             boolean require,
                                             String defaultValue) throws JDOMException {
        final Attribute attribute = element.getAttribute(name);
        if (attribute != null) {
            return attribute.getValue();
        } else if (require) {
            throw new JDOMException(createMissingAttributeExceptionMessage(element,
                                                                           name));
        } else {
            return defaultValue;
        }
    }

    public static RGBA getAttributeColor(Element element, String name) throws JDOMException {
        return getAttributeColor(element, name, true, null);
    }

    public static RGBA getAttributeColor(Element element,
                                         String name,
                                         RGBA defaultValue) throws JDOMException {
        return getAttributeColor(element, name, false, defaultValue);
    }

    @SuppressWarnings("unused")
    public static void setAttributeColor(Element element,
                                         String name,
                                         RGBA value) throws JDOMException {
        element.setAttribute(name, value.toString());
    }

    private static RGBA getAttributeColor(Element element,
                                          String name,
                                          boolean require,
                                          RGBA defaultValue) throws JDOMException {
        final Attribute attribute = element.getAttribute(name);
        if (attribute != null) {
            try {
                return RGBA.parseRGBA(attribute.getValue());
            } catch (ParseException e) {
                throw new DataConversionException(e.getMessage(), attribute.getValue());
            }
        } else if (require) {
            throw new JDOMException(createMissingAttributeExceptionMessage(element,
                                                                           name));
        } else {
            return defaultValue;
        }
    }


    public static Element getChild(Element element, String name) throws JDOMException {
        final Element child = element.getChild(name);
        if (child == null) {
            throw new JDOMException(MessageFormat.format(StringLiterals.getString("ex.missing.child.element.0.in.element.1"),
                                                         name, element.getName()));
        }
        return child;
    }

    public static List getChildren(Element element,
                                   String name,
                                   int min,
                                   int max) throws JDOMException {
        final List children = element.getChildren(name);
        if (min > 0 && children.size() < min) {
            throw new JDOMException(MessageFormat.format(StringLiterals.getString("ex.a.minimum.of.0.child.element.s.1.are.expected.in.element.2"),
                                                         min, name, element.getName()));
        }
        if (max > min && children.size() > max) {
            throw new JDOMException(MessageFormat.format(StringLiterals.getString("ex.a.maximum.of.0.child.element.s.1.are.expected.in.element.2"),
                                                         max, name, element.getName()));
        }
        return children;
    }

    private static String createMissingAttributeExceptionMessage(Element element,
                                                                 String attributeName) {
        return MessageFormat.format(StringLiterals.getString("ex.missing.attribute.0.in.element.1"),
                                    attributeName, element.getName());
    }

}
