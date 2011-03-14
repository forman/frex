package z.core.support;

import z.StringLiterals;
import z.core.AlgorithmDescriptor;
import z.core.AlgorithmRegistry;
import z.core.AlgorithmSpi;
import z.core.AlgorithmSubRegistry;
import z.core.IAlgorithm;
import z.core.support.accumulators.RadialOrbitTrap4;

public class DefaultAlgorithmSpi implements AlgorithmSpi {

    public void registerAlgorithms(AlgorithmRegistry registry) {
        // some shortcuts
        AlgorithmSubRegistry fractals = registry.getFractals();
        AlgorithmSubRegistry accumulators = registry.getAccumulators();
        AlgorithmSubRegistry indexers = registry.getIndexers();

        register(fractals, z.core.support.fractals.Mandelbrot.class, StringLiterals.getString("z.core.support.fractals.Mandelbrot.name"));
        register(fractals, z.core.support.fractals.MandelbrotP3.class, StringLiterals.getString("z.core.support.fractals.MandelbrotP3.name"));
        register(fractals, z.core.support.fractals.MandelbrotP4.class, StringLiterals.getString("z.core.support.fractals.MandelbrotP4.name"));
        register(fractals, z.core.support.fractals.GoldenRatio.class, StringLiterals.getString("z.core.support.fractals.GoldenRatio.name"));
        register(fractals, z.core.support.fractals.ExpZ.class, StringLiterals.getString("z.core.support.fractals.ExpZ.name"));
        register(fractals, z.core.support.fractals.SinZ.class, StringLiterals.getString("z.core.support.fractals.SinZ.name"));
        register(fractals, z.core.support.fractals.InvMandelbrot1.class, StringLiterals.getString("z.core.support.fractals.InvMandelbrot1.name"));
        register(fractals, z.core.support.fractals.InvMandelbrot2.class, StringLiterals.getString("z.core.support.fractals.InvMandelbrot2.name"));
        register(fractals, z.core.support.fractals.InvMandelbrot3.class, StringLiterals.getString("z.core.support.fractals.InvMandelbrot3.name"));
        register(fractals, z.core.support.fractals.BurningShip.class, StringLiterals.getString("z.core.support.fractals.BurningShip.name"));
        register(fractals, z.core.support.fractals.NormansError.class, StringLiterals.getString("z.core.support.fractals.NormansError.name"));
        register(fractals, z.core.support.fractals.NormansError2.class, StringLiterals.getString("z.core.support.fractals.NormansError2.name"));
        register(fractals, z.core.support.fractals.NormansError3.class, StringLiterals.getString("z.core.support.fractals.NormansError3.name"));
        register(fractals, z.core.support.fractals.NormansError4.class, StringLiterals.getString("z.core.support.fractals.NormansError4.name"));
        register(fractals, z.core.support.fractals.Sandbox.class, StringLiterals.getString("z.core.support.fractals.Sandbox.name"));
        register(fractals, z.core.support.fractals.Nova1.class, StringLiterals.getString("z.core.support.fractals.Nova1.name"));
        register(fractals, z.core.support.fractals.Nova2.class, StringLiterals.getString("z.core.support.fractals.Nova2.name"));
        register(fractals, z.core.support.fractals.Nova3.class, StringLiterals.getString("z.core.support.fractals.Nova3.name"));
        register(fractals, z.core.support.fractals.Ferromagnetic.class, StringLiterals.getString("z.core.support.fractals.Ferromagnetic.name"));
        register(fractals, z.core.support.fractals.NonFerromagnetic.class, StringLiterals.getString("z.core.support.fractals.NonFerromagnetic.name"));

        register(accumulators, z.core.support.accumulators.LinearOrbitTrap.class, StringLiterals.getString("z.core.support.accumulators.LinearOrbitTrap.name"));
        register(accumulators, z.core.support.accumulators.LinearOrbitTrap2.class, StringLiterals.getString("z.core.support.accumulators.LinearOrbitTrap2.name"));
        register(accumulators, z.core.support.accumulators.LinearOrbitTrap3.class, "Linear Orbit Trap #3");
        register(accumulators, z.core.support.accumulators.RadialOrbitTrap.class, StringLiterals.getString("z.core.support.accumulators.RadialOrbitTrap.name"));
        register(accumulators, z.core.support.accumulators.RadialOrbitTrap2.class, StringLiterals.getString("z.core.support.accumulators.RadialOrbitTrap2.name"));
        register(accumulators, z.core.support.accumulators.RadialOrbitTrap3.class, StringLiterals.getString("z.core.support.accumulators.RadialOrbitTrap3.name"));
        register(accumulators, z.core.support.accumulators.RadialOrbitTrap4.class, StringLiterals.getString("z.core.support.accumulators.RadialOrbitTrap4.name"));
        register(accumulators, z.core.support.accumulators.RingOrbitTrap.class, StringLiterals.getString("z.core.support.accumulators.RingOrbitTrap.name"));
        register(accumulators, z.core.support.accumulators.RingOrbitTrap2.class, StringLiterals.getString("z.core.support.accumulators.RingOrbitTrap2."));
        register(accumulators, z.core.support.accumulators.SquareOrbitTrap.class, StringLiterals.getString("z.core.support.accumulators.SquareOrbitTrap.name"));
        register(accumulators, z.core.support.accumulators.SineWaveOrbitTrap.class, StringLiterals.getString("z.core.support.accumulators.SineWaveOrbitTrap.name"));

        register(indexers, z.core.support.indexers.AbsX.class, StringLiterals.getString("z.core.support.indexers.AbsX.name"));
        register(indexers, z.core.support.indexers.AbsY.class, StringLiterals.getString("z.core.support.indexers.AbsY.name"));
        register(indexers, z.core.support.indexers.AbsZ.class, StringLiterals.getString("z.core.support.indexers.AbsZ.name"));
        register(indexers, z.core.support.indexers.AngleZ.class, StringLiterals.getString("z.core.support.indexers.AngleZ.name"));
        register(indexers, z.core.support.indexers.SinAbsZ.class, StringLiterals.getString("z.core.support.indexers.SinAbsZ.name"));
        register(indexers, z.core.support.indexers.Turbulence.class, "Turbulence");
        register(indexers, z.core.support.indexers.Image.class, "Image");
    }


    private static void register(AlgorithmSubRegistry algorithmSubRegistry, Class<? extends IAlgorithm> algorithmClass, String name) {
        AlgorithmDescriptor descriptor = new AlgorithmDescriptor(algorithmClass);
        descriptor.setName(name);
        descriptor.setOriginator("Norman Fomferra");  // NON-NLS
        algorithmSubRegistry.register(descriptor);
    }
}
