package z.io;

public interface ExtensionProvider<T> {
    T getExtension(Object extensibleObject, Class<T> extensionType);
}
