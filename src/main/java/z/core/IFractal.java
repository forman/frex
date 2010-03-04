/*
 * Created at 06.01.2004 20:19:50
 * Copyright (c) 2004 by Norman Fomferra
 */
package z.core;

public interface IFractal extends IAlgorithm {
    String ELEMENT_NAME = "fractal"; //$NON-NLS-1$

    /**
     * @param zx     the X-coordinate of the current complex Z-value
     * @param zy     the Y-coordinate of the current complex Z-value
     * @param orbitX the orbit X-coordinates (a <code>double[maxIter-1]</code>)
     * @param orbitY the orbit Y-coordinates (a <code>double[maxIter-1]</code>)
     * @return the number of iterations, ranging from zero to {@link getIterMax()}
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
