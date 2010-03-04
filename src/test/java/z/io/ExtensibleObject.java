package z.io;

public interface ExtensibleObject {
    Object getExtension(Class<?> extensionType);
}
