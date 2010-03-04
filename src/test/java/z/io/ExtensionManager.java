package z.io;

import java.util.HashMap;

public class ExtensionManager {
    private static ExtensionManager extensionManager = new ExtensionManager();

    private HashMap<Class<?>, ExtensionProvider<?>> extensions = new HashMap<Class<?>, ExtensionProvider<?>>(32);

    public static ExtensionManager instance() {
        return extensionManager;
    }

    public static void load(ExtensionManager extensionManager) {
        ExtensionManager.extensionManager = extensionManager;
    }

    public ExtensionProvider<?> addExtensionProvider(Class<?> extensibleType, ExtensionProvider<?> extensionProvider) {
        return extensions.put(extensibleType, extensionProvider);
    }

    public ExtensionProvider<?> getExtensionProvider(Class<?> extensibleType) {
        return extensions.get(extensibleType);
    }

    public ExtensionProvider<?> removeExtensionProvider(Class<?> extensibleType) {
        return extensions.remove(extensibleType);
    }

    public <T> T getExtension(Object extensibleObject, Class<T> extensionType) {
        if (extensibleObject instanceof ExtensionProvider) {
            final T extension = ((ExtensionProvider<T>) extensibleObject).getExtension(extensibleObject, extensionType);
            if (extension != null) {
                return extension;
            }
        }
        return findExtension(extensibleObject, extensionType);
    }

    protected <T> T findExtension(Object extensibleObject, Class<T> extensionType) {
        Class<?> extensibleType = extensibleObject.getClass();
        do {
            ExtensionProvider<T> extensionProvider = (ExtensionProvider<T>) getExtensionProvider(extensibleType);
            if (extensionProvider != null) {
                T extension = extensionProvider.getExtension(extensibleObject, extensionType);
                if (extension != null) {
                    return extension;
                }
            }
            extensibleType = extensibleType.getSuperclass();
        } while (extensibleType != null);
        return null;
    }
}
