/*
 * Created at 06.01.2004 20:19:50
 * Copyright (c) 2004 by Norman Fomferra
 */
package z.core;

/**
 * Fractals compute a complex orbit for a given complex Z-value.
 */
public interface IFractal extends IAlgorithm {
    String ELEMENT_NAME = "fractal"; //$NON-NLS-1$

    /**
     * Computes the complex orbit from the given complex Z-value.
     * @param zx     The X-coordinate of the current complex Z-value.
     * @param zy     The Y-coordinate of the current complex Z-value.
     * @param orbitX Return value. The complex orbit's X-coordinates (a <code>double[iterMax-1]</code>)
     * @param orbitY Return value. The complex orbit's Y-coordinates (a <code>double[iterMax-1]</code>)
     * @return the number of iterations, ranging from zero to {@link #getIterMax()}
     */
    int compute(double zx,
                double zy,
                double[] orbitX,
                double[] orbitY);

    Region getStartRegion();

    int getIterMax();

    void setIterMax(int iterMax);

    double getBailOut();

    void setBailOut(double bailOut);

    boolean isJuliaMode();

    void setJuliaMode(boolean juliaMode);

    double getJuliaCX();

    void setJuliaCX(double juliaCX);

    double getJuliaCY();

    void setJuliaCY(double juliaCY);
}
