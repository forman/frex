/*
 * $Id: IColorizer.java,v 1.1 2006/11/13 16:22:16 norman Exp $
 *
 * Copyright (c) 2003 Brockmann Consult GmbH. All right reserved.
 * http://www.brockmann-consult.de
 */
package z.core;

/**
 * Colorizers compute an RGBA color value for a given real index value computed either by {@link IIndexer}s
 * or directly by {@link IAccumulator}s.
 */
public interface IColorizer extends IAlgorithm {
    String ELEMENT_NAME = "colorizer"; //$NON-NLS-1$
    String ELEMENT_NAME_INNER = "innerColorizer"; //$NON-NLS-1$
    String ELEMENT_NAME_OUTER = "outerColorizer"; //$NON-NLS-1$

    /**
     * Computes an RGBA value from the given real index value.
     *
     * @param index The real index.
     * @return The RGBA color value.
     */
    int getRgba(float index);
}
