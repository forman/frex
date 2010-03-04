/*
 * $Id: IColorizer.java,v 1.1 2006/11/13 16:22:16 norman Exp $
 *
 * Copyright (c) 2003 Brockmann Consult GmbH. All right reserved.
 * http://www.brockmann-consult.de
 */
package z.core;

public interface IColorizer extends IAlgorithm {
    String ELEMENT_NAME = "colorizer"; //$NON-NLS-1$

    int getRgba(float index);
}
