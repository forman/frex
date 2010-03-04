/*
 * Created at 06.01.2004 19:49:20
 * Copyright (c) 2004 by Norman Fomferra
 */
package z.core;

import org.jdom.Element;
import org.jdom.JDOMException;
import z.core.support.colorizers.PaletteColorTable;
import z.core.support.fractals.Mandelbrot;
import z.util.JDOMHelper;
import z.util.JDOMObjectIO;

import java.io.File;
import java.io.IOException;

public class Plane extends RenderableNode {

    public static final String ELEMENT_NAME = "plane"; //$NON-NLS-1$

    public static final String FILENAME_EXTENSION = ".plane"; //$NON-NLS-1$

    public static final String FILEFORMAT_VERSION = "1.0"; //$NON-NLS-1$

    private Project project;

    private boolean visible;

    private Region region; // not null

    private IFractal fractal; // not null

    private IAccumulator accumulator;

    private IIndexer indexer;

    private boolean trapMode;

    private boolean decompositionMode;

    private IColorizer colorizer; // not null

    private PlaneRaster raster;

    public Plane(File file) {
        super(file);
        visible = true;
        fractal = new Mandelbrot();
        colorizer = new PaletteColorTable();
        region = new Region(fractal.getStartRegion());
        accumulator = null;
        indexer = null;
    }

    @Override
    public String getElementName() {
        return ELEMENT_NAME;
    }

    @Override
    public String getFilenameExtension() {
        return FILENAME_EXTENSION;
    }

    @Override
    public String getFileFormatVersion() {
        return FILEFORMAT_VERSION;
    }

    public Project getProject() {
        return project;
    }

    void setProject(Project project) {
        this.project = project;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        if (this.visible != visible) {
            this.visible = visible;
            fireStateChange();
        }
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        if (!this.region.equals(region)) {
            this.region = region;
            fireStateChange();
        }
    }

    public boolean getTrapMode() {
        return trapMode;
    }

    public void setTrapMode(boolean trapMode) {
        this.trapMode = trapMode;
    }

    public boolean getDecompositionMode() {
        return decompositionMode;
    }

    public void setDecompositionMode(boolean decompositionMode) {
        this.decompositionMode = decompositionMode;
    }

    public IFractal getFractal() {
        return fractal;
    }

    public void setFractal(IFractal fractal) {
        this.fractal = fractal;
    }

    public IIndexer getIndexer() {
        return indexer;
    }

    public void setIndexer(IIndexer indexer) {
        this.indexer = indexer;
    }

    public IAccumulator getAccumulator() {
        return accumulator;
    }

    public void setAccumulator(IAccumulator accumulator) {
        this.accumulator = accumulator;
    }

    public IColorizer getColorizer() {
        return colorizer;
    }

    public void setColorizer(IColorizer colorizer) {
        this.colorizer = colorizer;
    }

    public PlaneRaster getRaster() {
        return raster;
    }

    public void setRaster(PlaneRaster raster) {
        this.raster = raster;
    }

    @Override
    public void zoomRegion(int imageWidth,
                           int imageHeight,
                           double visibleImageRectX,
                           double visibleImageRectY,
                           double visibleImageRectWidth,
                           double visibleImageRectHeight) {
        setRegion(getRegion().zoom(imageWidth,
                                   imageHeight,
                                   visibleImageRectX,
                                   visibleImageRectY,
                                   visibleImageRectWidth,
                                   visibleImageRectHeight));
    }

    public static Plane readPlane(File file) throws JDOMException, IOException {
        Plane plane = new Plane(file);
        readNode(plane);
        return plane;
    }

    @Override
    public void readExternal(Element element) throws JDOMException {
        super.readExternal(element);

        final String name = JDOMHelper.getAttributeString(element,
                                                          "name",
                                                          getName());
        final boolean visible = JDOMHelper.getAttributeBoolean(element,
                                                               "visible",
                                                               true);
        final boolean decompositionMode = JDOMHelper.getAttributeBoolean(element,
                                                                         "decompositionMode",
                                                                         false);
        final boolean trapMode = JDOMHelper.getAttributeBoolean(element,
                                                                "trapMode",
                                                                false);

        IFractal fractal = (IFractal) JDOMObjectIO.readObjectFromChild(element,
                                                                       IFractal.ELEMENT_NAME);
        Region region = (Region) JDOMObjectIO.readObjectFromChild(element,
                                                                  Region.ELEMENT_NAME,
                                                                  Region.class);
        IAccumulator accumulator = (IAccumulator) JDOMObjectIO.readObjectFromChild(element,
                                                                                   IAccumulator.ELEMENT_NAME);
        IIndexer indexer = (IIndexer) JDOMObjectIO.readObjectFromChild(element,
                                                                       IIndexer.ELEMENT_NAME);
        IColorizer colorTable = (IColorizer) JDOMObjectIO.readObjectFromChild(element,
                                                                              IColorizer.ELEMENT_NAME,
                                                                              PaletteColorTable.class);

        if (fractal == null) {
            fractal = new Mandelbrot();
        }
        if (region == null) {
            region = new Region(fractal.getStartRegion());
        }

        setName(name);
        setVisible(visible);
        setTrapMode(trapMode);
        setDecompositionMode(decompositionMode);
        setFractal(fractal);
        setRegion(region);
        setAccumulator(accumulator);
        setIndexer(indexer);
        setColorizer(colorTable);
    }

    @Override
    public void writeExternal(Element element) throws JDOMException {

        JDOMHelper.setAttributeString(element, "name", getName());
        JDOMHelper.setAttributeBoolean(element, "visible", isVisible(), true);
        JDOMHelper.setAttributeBoolean(element,
                                       "trapMode",
                                       getTrapMode(),
                                       false);
        JDOMHelper.setAttributeBoolean(element,
                                       "decompositionMode",
                                       getDecompositionMode(),
                                       false);

        super.writeExternal(element);

        JDOMObjectIO.writeObjectToChild(element,
                                        IFractal.ELEMENT_NAME,
                                        getFractal(),
                                        Mandelbrot.class);
        JDOMObjectIO.writeObjectToChild(element,
                                        Region.ELEMENT_NAME,
                                        getRegion(),
                                        Region.class);
        JDOMObjectIO.writeObjectToChild(element,
                                        IAccumulator.ELEMENT_NAME,
                                        getAccumulator(),
                                        null);
        JDOMObjectIO.writeObjectToChild(element,
                                        IIndexer.ELEMENT_NAME,
                                        getIndexer(),
                                        null);
        JDOMObjectIO.writeObjectToChild(element,
                                        IColorizer.ELEMENT_NAME,
                                        getColorizer(),
                                        PaletteColorTable.class);
    }
}
