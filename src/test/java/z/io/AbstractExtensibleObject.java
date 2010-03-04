package z.io;

public abstract class AbstractExtensibleObject implements ExtensibleObject {
    public Object getExtension(Class<?> extensionType) {
        return ExtensionManager.instance().getExtension(this, extensionType);
    }
}