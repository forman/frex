package z.core;

import z.util.Assert;

import java.util.LinkedList;

public final class AlgorithmSubRegistry {
    private final Class<? extends IAlgorithm> algorithmType;
    private final LinkedList<AlgorithmDescriptor> list;

    public AlgorithmSubRegistry(Class<? extends IAlgorithm> algorithmType) {
        Assert.notNull(algorithmType, "algorithmType");
        this.algorithmType = algorithmType;
        list = new LinkedList<AlgorithmDescriptor>();
    }

    public Class<? extends IAlgorithm> getAlgorithmType() {
        return algorithmType;
    }

    public void register(AlgorithmDescriptor descriptor) {
        if (!algorithmType.isAssignableFrom(descriptor.getAlgorithmClass())) {
            throw new IllegalArgumentException("illegal algorithm class: " + descriptor.getAlgorithmClass() + " is not a " + algorithmType);
        }
        list.add(descriptor);
    }

    public boolean unregister(AlgorithmDescriptor descriptor) {
        return list.remove(descriptor);
    }

    public AlgorithmDescriptor lookup(Class<? extends IAlgorithm> algorithmClass) {
        for (AlgorithmDescriptor algorithmDescriptor : list) {
            if (algorithmDescriptor.getAlgorithmClass().equals(algorithmClass)) {
                return algorithmDescriptor;
            }
        }
        return null;
    }

    public AlgorithmDescriptor[] getAll() {
        return list.toArray(new AlgorithmDescriptor[list.size()]);
    }

}
