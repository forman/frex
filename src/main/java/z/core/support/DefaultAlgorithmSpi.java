package z.core.support;

import z.StringLiterals;
import z.core.AlgorithmDescriptor;
import z.core.AlgorithmRegistry;
import z.core.AlgorithmSpi;
import z.core.AlgorithmSubRegistry;
import z.core.IAlgorithm;
import z.core.support.accumulators.LinearOrbitTrap;
import z.core.support.accumulators.LinearOrbitTrap2;
import z.core.support.accumulators.LinearOrbitTrap3;
import z.core.support.accumulators.RadialOrbitTrap;
import z.core.support.accumulators.RadialOrbitTrap2;
import z.core.support.accumulators.RadialOrbitTrap3;
import z.core.support.accumulators.RadialOrbitTrap4;
import z.core.support.accumulators.RingOrbitTrap;
import z.core.support.accumulators.RingOrbitTrap2;
import z.core.support.accumulators.SineWaveOrbitTrap;
import z.core.support.accumulators.SquareOrbitTrap;
import z.core.support.accumulators.TurbulentLinearOrbitTrap3;
import z.core.support.fractals.BurningShip;
import z.core.support.fractals.ExpZ;
import z.core.support.fractals.Ferromagnetic;
import z.core.support.fractals.GoldenRatio;
import z.core.support.fractals.InvMandelbrot1;
import z.core.support.fractals.InvMandelbrot2;
import z.core.support.fractals.InvMandelbrot3;
import z.core.support.fractals.Mandelbrot;
import z.core.support.fractals.MandelbrotP3;
import z.core.support.fractals.MandelbrotP4;
import z.core.support.fractals.NonFerromagnetic;
import z.core.support.fractals.NormansError;
import z.core.support.fractals.NormansError2;
import z.core.support.fractals.NormansError3;
import z.core.support.fractals.NormansError4;
import z.core.support.fractals.Nova1;
import z.core.support.fractals.Nova2;
import z.core.support.fractals.Nova3;
import z.core.support.fractals.Sandbox;
import z.core.support.fractals.SinZ;
import z.core.support.indexers.AbsX;
import z.core.support.indexers.AbsY;
import z.core.support.indexers.AbsZ;
import z.core.support.indexers.AngleZ;
import z.core.support.indexers.ImageLookup;
import z.core.support.indexers.SinAbsZ;
import z.core.support.indexers.TurbulenceIndexer;

public class DefaultAlgorithmSpi implements AlgorithmSpi {

    public void registerAlgorithms(AlgorithmRegistry registry) {
        // some shortcuts
        AlgorithmSubRegistry fractals = registry.getFractals();
        AlgorithmSubRegistry accumulators = registry.getAccumulators();
        AlgorithmSubRegistry indexers = registry.getIndexers();

        register(fractals, Mandelbrot.class, StringLiterals.getString("z.core.support.fractals.Mandelbrot.name"));
        register(fractals, MandelbrotP3.class, StringLiterals.getString("z.core.support.fractals.MandelbrotP3.name"));
        register(fractals, MandelbrotP4.class, StringLiterals.getString("z.core.support.fractals.MandelbrotP4.name"));
        register(fractals, GoldenRatio.class, StringLiterals.getString("z.core.support.fractals.GoldenRatio.name"));
        register(fractals, ExpZ.class, StringLiterals.getString("z.core.support.fractals.ExpZ.name"));
        register(fractals, SinZ.class, StringLiterals.getString("z.core.support.fractals.SinZ.name"));
        register(fractals, InvMandelbrot1.class, StringLiterals.getString("z.core.support.fractals.InvMandelbrot1.name"));
        register(fractals, InvMandelbrot2.class, StringLiterals.getString("z.core.support.fractals.InvMandelbrot2.name"));
        register(fractals, InvMandelbrot3.class, StringLiterals.getString("z.core.support.fractals.InvMandelbrot3.name"));
        register(fractals, BurningShip.class, StringLiterals.getString("z.core.support.fractals.BurningShip.name"));
        register(fractals, NormansError.class, StringLiterals.getString("z.core.support.fractals.NormansError.name"));
        register(fractals, NormansError2.class, StringLiterals.getString("z.core.support.fractals.NormansError2.name"));
        register(fractals, NormansError3.class, StringLiterals.getString("z.core.support.fractals.NormansError3.name"));
        register(fractals, NormansError4.class, StringLiterals.getString("z.core.support.fractals.NormansError4.name"));
        register(fractals, Sandbox.class, StringLiterals.getString("z.core.support.fractals.Sandbox.name"));
        register(fractals, Nova1.class, StringLiterals.getString("z.core.support.fractals.Nova1.name"));
        register(fractals, Nova2.class, StringLiterals.getString("z.core.support.fractals.Nova2.name"));
        register(fractals, Nova3.class, StringLiterals.getString("z.core.support.fractals.Nova3.name"));
        register(fractals, Ferromagnetic.class, StringLiterals.getString("z.core.support.fractals.Ferromagnetic.name"));
        register(fractals, NonFerromagnetic.class, StringLiterals.getString("z.core.support.fractals.NonFerromagnetic.name"));

        register(accumulators, LinearOrbitTrap.class, StringLiterals.getString("z.core.support.accumulators.LinearOrbitTrap.name"));
        register(accumulators, LinearOrbitTrap2.class, StringLiterals.getString("z.core.support.accumulators.LinearOrbitTrap2.name"));
        register(accumulators, LinearOrbitTrap3.class, "Linear Orbit Trap #3");
        register(accumulators, TurbulentLinearOrbitTrap3.class, "Linear Orbit Trap #3 (turbulent)");
        register(accumulators, RadialOrbitTrap.class, StringLiterals.getString("z.core.support.accumulators.RadialOrbitTrap.name"));
        register(accumulators, RadialOrbitTrap2.class, StringLiterals.getString("z.core.support.accumulators.RadialOrbitTrap2.name"));
        register(accumulators, RadialOrbitTrap3.class, StringLiterals.getString("z.core.support.accumulators.RadialOrbitTrap3.name"));
        register(accumulators, RadialOrbitTrap4.class, StringLiterals.getString("z.core.support.accumulators.RadialOrbitTrap4.name"));
        register(accumulators, RingOrbitTrap.class, StringLiterals.getString("z.core.support.accumulators.RingOrbitTrap.name"));
        register(accumulators, RingOrbitTrap2.class, StringLiterals.getString("z.core.support.accumulators.RingOrbitTrap2."));
        register(accumulators, SquareOrbitTrap.class, StringLiterals.getString("z.core.support.accumulators.SquareOrbitTrap.name"));
        register(accumulators, SineWaveOrbitTrap.class, StringLiterals.getString("z.core.support.accumulators.SineWaveOrbitTrap.name"));

        register(indexers, AbsX.class, StringLiterals.getString("z.core.support.indexers.AbsX.name"));
        register(indexers, AbsY.class, StringLiterals.getString("z.core.support.indexers.AbsY.name"));
        register(indexers, AbsZ.class, StringLiterals.getString("z.core.support.indexers.AbsZ.name"));
        register(indexers, AngleZ.class, StringLiterals.getString("z.core.support.indexers.AngleZ.name"));
        register(indexers, SinAbsZ.class, StringLiterals.getString("z.core.support.indexers.SinAbsZ.name"));
        register(indexers, TurbulenceIndexer.class, "Turbulence");
        register(indexers, ImageLookup.class, "Image");
    }


    private static void register(AlgorithmSubRegistry algorithmSubRegistry, Class<? extends IAlgorithm> algorithmClass, String name) {
        AlgorithmDescriptor descriptor = new AlgorithmDescriptor(algorithmClass);
        descriptor.setName(name);
        descriptor.setOriginator("Norman Fomferra");  // NON-NLS
        algorithmSubRegistry.register(descriptor);
    }
}
