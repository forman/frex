package z.core;

import z.util.Assert;

import java.awt.Image;

public class AlgorithmDescriptor {
    private final Class<? extends IAlgorithm> algorithmClass;

    private String name;

    private String description;

    private String version;

    private String originator;

    private Image icon;

    public AlgorithmDescriptor(Class<? extends IAlgorithm> algorithmClass) {
        Assert.notNull(algorithmClass, "algorithmClass");
        this.algorithmClass = algorithmClass;
        this.name = algorithmClass.getName();
    }

    public Class<? extends IAlgorithm> getAlgorithmClass() {
        return algorithmClass;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getOriginator() {
        return originator;
    }

    public void setOriginator(String originator) {
        this.originator = originator;
    }

    public Image getIcon() {
        return icon;
    }

    public void setIcon(Image icon) {
        this.icon = icon;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            return true;
        }
        if (obj instanceof AlgorithmDescriptor) {
            AlgorithmDescriptor other = (AlgorithmDescriptor) obj;
            return algorithmClass.equals(other.algorithmClass);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return algorithmClass.hashCode();
    }
}
