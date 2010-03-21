/*
 * Created at 26.01.2004 21:18:16
 * Copyright (c) 2004 by Norman Fomferra
 */
package z.core;

/**
 * Accumulators are used to aggregate the complex orbit computed by {@link IFractal}s.
 */
public interface IAccumulator extends IAlgorithm {
    String ELEMENT_NAME = "accumulator"; //$NON-NLS-1$

    /**
     * Accumulates a given complex orbit.
     * Depending on the value of {@link #computesIndex()},
     * the result is either a real index value or a complex number.
     *
     * @param orbitX   the complex orbit's X-coordinates (a <code>double[maxIter-1]</code>)
     * @param orbitY   the complex orbit's Y-coordinates (a <code>double[maxIter-1]</code>)
     * @param iter     number of iterations, always &gt;= 1 and &lt;=
     *                 <code>maxIter</code>
     * @param maxIter  maximum number of iterations
     * @param trapMode if true, method returns immediately if orbit is trapped
     * @param result   <code>result[0]</code> is the X-coordinate of the accumulated complex return value,
     *                 <code>result[1]</code> is the y-coordinate of the accumulated complex return value.
     */
    void compute(double[] orbitX,
                 double[] orbitY,
                 int iter,
                 int maxIter,
                 boolean trapMode,
                 double[] result);

    /**
     * Returns, whether or not the {@link #compute} method returns a real index and not a new complex value.
     *
     * @return true, if so.
     */
    boolean computesIndex();

    /**
     * Returns, whether or not the {@link #compute} method can "trap" an orbit and immediately return in this case.
     *
     * @return true, if so.
     */
    boolean canTrap();
}
