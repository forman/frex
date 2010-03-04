package z.core.support;

import z.core.AlgorithmDescriptor;
import z.core.AlgorithmRegistry;
import z.core.AlgorithmSpi;
import z.core.AlgorithmSubRegistry;
import z.core.IAlgorithm;
import z.core.support.fractals.InvMandelbrot2;
import z.core.support.fractals.InvMandelbrot3;

public class DefaultAlgorithmSpi implements AlgorithmSpi {
    public void registerAlgorithms(AlgorithmRegistry registry) {
        // some shortcuts
        AlgorithmSubRegistry fractals = registry.getFractals();
        AlgorithmSubRegistry accumulators = registry.getAccumulators();
        AlgorithmSubRegistry indexers = registry.getIndexers();

        register(fractals, z.core.support.fractals.Mandelbrot.class, "Mandelbrot: z = z^2 + c");
        register(fractals, z.core.support.fractals.MandelbrotP3.class, "Mandelbrot: z = z^3 + c");
        register(fractals, z.core.support.fractals.MandelbrotP4.class, "Mandelbrot: z = z^4 + c");
        register(fractals, z.core.support.fractals.GoldenerSchnitt.class, "Mandelbrot: z = z^2 - z - 1 + c");
        register(fractals, z.core.support.fractals.ExpZ.class, "Mandelbrot: z = e^z + c");
        register(fractals, z.core.support.fractals.SinZ.class, "Mandelbrot: z = sin(z) + c");
        register(fractals, z.core.support.fractals.InvMandelbrot1.class, "Mandelbrot: 1 / z^2 + c");
        register(fractals, z.core.support.fractals.InvMandelbrot2.class, "Mandelbrot: 1 / (z^2 + c^2)");
        register(fractals, z.core.support.fractals.InvMandelbrot3.class, "Mandelbrot: 1 / z^2 + 1 / c^2");
        register(fractals, z.core.support.fractals.NormansError.class, "Norman's Fehler #1");
        register(fractals, z.core.support.fractals.NormansError2.class, "Norman's Fehler #2");
        register(fractals, z.core.support.fractals.NormansError3.class, "Norman's Fehler #3");
        register(fractals, z.core.support.fractals.Sandbox.class, "Sandkasten");
        register(fractals, z.core.support.fractals.Nova1.class, "Nova 1: f = z(z - 1)(z + 1), z = z - f/f' + c");
        register(fractals, z.core.support.fractals.Nova2.class, "Nova 2: f = (z - 1)(z + 1), z = z - f/f' + c");
        register(fractals, z.core.support.fractals.Nova3.class, "Nova 3: f = (z^3 - 1)(z^2 - 4), z = z - f/f' + c");
        register(fractals, z.core.support.fractals.Ferromagnetic.class, "Ferromagnetic");
        register(fractals, z.core.support.fractals.NonFerromagnetic.class, "Non-Ferromagnetic");

        register(accumulators, z.core.support.accumulators.LinearOrbitTrap.class, "Lineare Orbitfalle #1");
        register(accumulators, z.core.support.accumulators.LinearOrbitTrap2.class, "Lineare Orbitfalle #2");
        register(accumulators, z.core.support.accumulators.RadialOrbitTrap.class, "Radiale Orbitfalle #1");
        register(accumulators, z.core.support.accumulators.RadialOrbitTrap2.class, "Radiale Orbitfalle #2");
        register(accumulators, z.core.support.accumulators.RingOrbitTrap.class, "Ringförmige Orbitfalle #1");
        register(accumulators, z.core.support.accumulators.RingOrbitTrap2.class, "Ringförmige Orbitfalle #2");
        register(accumulators, z.core.support.accumulators.SquareOrbitTrap.class, "Quadratische Orbitfalle");
        register(accumulators, z.core.support.accumulators.SineWaveOrbitTrap.class, "Sinuswellen Orbitfalle");

        register(indexers, z.core.support.indexers.AbsX.class, "abs(x)");
        register(indexers, z.core.support.indexers.AbsY.class, "abs(y)");
        register(indexers, z.core.support.indexers.AbsZ.class, "abs(z)");
        register(indexers, z.core.support.indexers.AngleZ.class, "winkel(z)");
        register(indexers, z.core.support.indexers.SinAbsZ.class, "sin(abs(z))");
    }


    private static void register(AlgorithmSubRegistry algorithmSubRegistry, Class<? extends IAlgorithm> algorithmClass, String name) {
        AlgorithmDescriptor descriptor = new AlgorithmDescriptor(algorithmClass);
        descriptor.setName(name);
        descriptor.setOriginator("Norman Fomferra");
        algorithmSubRegistry.register(descriptor);
    }
}
