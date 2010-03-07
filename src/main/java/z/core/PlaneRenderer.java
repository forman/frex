/*
 * Created at 06.01.2004 14:39:17
 * Copyright (c) 2004 by Norman Fomferra
 */
package z.core;

import z.StringLiterals;
import z.core.color.RGBA;
import z.core.progress.ProgressMonitor;
import z.core.progress.SubProgressMonitor;
import z.util.Assert;

import java.text.MessageFormat;

public class PlaneRenderer {

    private final ImageInfo imageInfo;

    private final boolean colorizeOnly;


    public PlaneRenderer(ImageInfo imageInfo, boolean colorizeOnly) {
        Assert.notNull(imageInfo, "imageInfo"); // NON-NLS
        this.imageInfo = imageInfo;
        this.colorizeOnly = colorizeOnly;
    }

    /**
     * Renders multiple planes.
     *
     * @param planes     the planes
     * @param pixelDatas the pixel data buffers in ABGR order
     * @param pm         a progress monitor
     */
    public void renderPlanes(Plane[] planes, int[][] pixelDatas, ProgressMonitor pm) {
        Assert.notNull(planes, "planes"); // NON-NLS
        Assert.notNull(pixelDatas, "pixelDatas"); // NON-NLS
        Assert.notNull(pm, "pm"); // NON-NLS
        int background = imageInfo.getBackground().getValue();

        pm.beginTask(StringLiterals.getString("gui.msg.computingLayer"), planes.length);
        try {
            for (int i = 0; i < planes.length && !pm.isCanceled(); i++) {
                renderPlane(planes[i], pixelDatas[i], background, new SubProgressMonitor(pm, 1));
                background = RGBA.TRANSPARENT.getValue();
            }
        } finally {
            pm.done();
        }
    }

    /**
     * Renders a plane.
     *
     * @param plane     the plane
     * @param pixelData the pixel data buffer in ABGR order
     * @param pm        a progress monitor
     */
    public void renderPlane(Plane plane, int[] pixelData, ProgressMonitor pm) {
        Assert.notNull(plane, "plane");  // NON-NLS
        Assert.notNull(pixelData, "pixelData"); // NON-NLS
        Assert.notNull(pm, "pm"); // NON-NLS
        renderPlane(plane,
                    pixelData,
                    imageInfo.getBackground().getValue(),
                    pm);
    }


    private void renderPlane(Plane plane,
                             int[] pixelData,
                             int background,
                             ProgressMonitor pm) {

        int imageWidth = imageInfo.getImageWidth();
        int imageHeight = imageInfo.getImageHeight();
        pm.beginTask(MessageFormat.format(StringLiterals.getString("gui.msg.computingLayer0"), plane.getName()), imageHeight);
        try {
            renderPlane(plane, imageWidth, imageHeight, pixelData, pm, background);
        } finally {
            pm.done();
        }
    }

    private void renderPlane(Plane plane, int imageWidth, int imageHeight, int[] pixelData, ProgressMonitor pm, int background) {
        boolean computeFractal = !this.colorizeOnly;
        PlaneRaster raster = plane.getRaster();
        if (raster == null || raster.getWidth() != imageWidth
                || raster.getHeight() != imageHeight) {
            raster = new PlaneRaster(imageWidth, imageHeight, pixelData);
            plane.setRaster(raster);
            computeFractal = true;
        }
        raster.clearStatistics();

        final IFractal fractal = plane.getFractal();
        final IAccumulator accumulator = plane.getAccumulator();
        IIndexer indexer = plane.getIndexer();
        final IColorizer colorizer = plane.getColorizer();
        final boolean trapMode = plane.getTrapMode();
        final boolean decompositionMode = plane.getDecompositionMode();

        final float[] rawData = raster.getRawData();
        final Region region = plane.getRegion();
        final double pixelSize = 2.0 * region.getRadius() / (double) Math.min(imageWidth, imageHeight);
        final double zx1 = region.getCenterX() - 0.5 * pixelSize * (double) imageWidth;
        final double zy2 = region.getCenterY() + 0.5 * pixelSize * (double) imageHeight;

        fractal.prepare();
        if (accumulator != null) {
            accumulator.prepare();
            if (!accumulator.computesIndex()) {
                indexer = null;
            }
            if (indexer != null) {
                indexer.prepare();
            }
        }
        colorizer.prepare();

        double zx, zy;
        int i, k, iy, ix, color, iter;
        float index;
        final int iterMax = fractal.getIterMax();
        double[] orbitX = new double[iterMax];
        double[] orbitY = new double[iterMax];
        double[] accuResult = new double[2];

        for (iy = 0; iy < imageHeight && !pm.isCanceled(); iy++) {
            zy = zy2 - (double) iy * pixelSize;
            i = iy * imageWidth;
            k = iy * imageWidth;
            for (ix = 0; ix < imageWidth; ix++) {
                zx = zx1 + (double) ix * pixelSize;

                if (computeFractal) {
                    iter = fractal.compute(zx, zy, orbitX, orbitY);

                    // todo: use different accumulators/indexers/colorizers for inside (iter == iterMax) and outside (iter < iterMax)

                    if (accumulator != null) {
                        accumulator.compute(orbitX,
                                            orbitY,
                                            iter,
                                            iterMax,
                                            trapMode,
                                            accuResult);
                        if (indexer != null) {
                            index = (float) indexer.computeIndex(accuResult[0],
                                                                 accuResult[1]);
                        } else {
                            index = (float) accuResult[0];
                        }
                    } else if (iter < iterMax) {
                        index = (float) iter;
                    } else {
                        index = 0.0f;
                    }
                    if (iter == iterMax) {
                        index = -1.0f - index;
                    }
                    if (decompositionMode && iter > 0 && orbitY[iter - 1] < 0.0) {
                        index *= 2.0f;
                    }

                    rawData[k++] = index;
                } else {
                    index = rawData[k++];
                }

                if (index != -1.0f) {
                    color = colorizer.getRgba(index < 0.0f ? -1.0f - index : index);
                } else {
                    color = background;
                }

                pixelData[i++] = color;
            }
            pm.worked(1);
        }
    }
}
