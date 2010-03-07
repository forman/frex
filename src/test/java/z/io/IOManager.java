package z.io;

import org.jdom.Element;

import java.text.MessageFormat;
import java.util.HashMap;

public class IOManager {

    final ClassLoader classLoader;
    final HashMap<String, Class<?>> aliasMap = new HashMap<String, Class<?>>(4);

    public IOManager() {
        this(getDefaultClassLoader());
    }

    private static ClassLoader getDefaultClassLoader() {
        return Thread.currentThread().getContextClassLoader() != null ? Thread.currentThread().getContextClassLoader() : ClassLoader.getSystemClassLoader();
    }

    public IOManager(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public void alias(String alias, Class<?> type, ExtensionProvider<IO> extensionProvider) {
        aliasMap.put(alias, type);
        ExtensionManager.instance().addExtensionProvider(Algo.class, extensionProvider);
    }

    public <T> T read(Element element) throws Exception {
        Class<T> type;
        String className = element.getAttributeValue("class"); // NON-NLS
        if (className != null) {
            type = (Class<T>) classLoader.loadClass(className);
        } else {
            type = (Class<T>) aliasMap.get(element.getName());
            if (type == null) {
                type = (Class<T>) classLoader.loadClass(element.getName());
            }
        }
        final T t = type.newInstance();
        IO io = ExtensionManager.instance().getExtension(t, IO.class);
        if (io == null) {
            throw new RuntimeException(MessageFormat.format("No extension of type ''{0}'' found for type ''{1}''", IO.class, type));  // NON-NLS
        }
        io.read(this, element);
        return t;
    }

    public <T> Element write(T t) throws Exception {
        IO io = (IO) ExtensionManager.instance().getExtension(t, IO.class);
        final Element element = new Element(io.getElementName());
        element.setAttribute("class", t.getClass().getName());  // NON-NLS
        io.write(this, element);
        return element;
    }
}
