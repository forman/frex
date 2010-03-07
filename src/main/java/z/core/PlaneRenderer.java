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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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

    private void renderPlane(Plane plane,
                             int[] pixelData,
                             int background,
                             ProgressMonitor pm) {

        int imageWidth = imageInfo.getImageWidth();
        int imageHeight = imageInfo.getImageHeight();
        pm.beginTask(MessageFormat.format(StringLiterals.getString("gui.msg.computingLayer0"), plane.getName()), imageHeight);
        try {
            renderPlane(plane, imageWidth, imageHeight, pixelData, background, pm);
        } finally {
            pm.done();
        }
    }

    private void renderPlane(final Plane plane,
                             final int imageWidth,
                             final int imageHeight,
                             final int[] pixelData,
                             final int background,
                             final ProgressMonitor pm) {
        PlaneRaster raster = plane.getRaster();
        final boolean computeFractal;
        if (raster == null
                || raster.getWidth() != imageWidth
                || raster.getHeight() != imageHeight) {
            raster = new PlaneRaster(imageWidth, imageHeight, pixelData);
            plane.setRaster(raster);
            computeFractal = true;
        } else {
            computeFractal = !this.colorizeOnly;
        }
        raster.clearStatistics();

        final IFractal fractal = plane.getFractal();
        final IAccumulator accumulator = plane.getAccumulator();
        final IIndexer indexer = plane.getIndexer();
        final IColorizer colorizer = plane.getColorizer();

        fractal.prepare();
        if (accumulator != null) {
            accumulator.prepare();
            if (indexer != null) {
                indexer.prepare();
            }
        }
        colorizer.prepare();

        int numProcessors = Runtime.getRuntime().availableProcessors();
        if (numProcessors == 1) {
            renderPlane(plane,
                        0, imageHeight,
                        imageWidth, imageHeight,
                        pixelData, background, computeFractal,
                        pm);
            return;
        }

        ExecutorService executorService = Executors.newFixedThreadPool(numProcessors);
        final int numLines = imageHeight / numProcessors;
        final int rest = imageHeight % numProcessors;
        int iy = 0;
        for (int i = 0; i < numProcessors; i++) {
            final int n = numLines + (i == 0 ? rest : 0);
            final int iy0 = iy;
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    renderPlane(plane,
                                iy0, n,
                                imageWidth, imageHeight,
                                pixelData, background,
                                computeFractal,
                                pm);
                }
            });
            iy += n;
        }
        executorService.shutdown();
        try {
            executorService.awaitTermination(7L, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            // ok
        }
    }

    private void renderPlane(Plane plane,
                             int iy0, int numLines,
                             int imageWidth, int imageHeight,
                             int[] pixelData, int background,
                             boolean computeFractal,
                             ProgressMonitor pm) {

        final IFractal fractal = plane.getFractal();
        final IAccumulator accumulator = plane.getAccumulator();
        IIndexer indexer = plane.getIndexer();
        final IColorizer colorizer = plane.getColorizer();
        final boolean trapMode = plane.getTrapMode();
        final boolean decompositionMode = plane.getDecompositionMode();

        final float[] rawData = plane.getRaster().getRawData();
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
        int iy1 = iy0 + numLines - 1;

        for (iy = iy0; iy <= iy1 && !pm.isCanceled(); iy++) {
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
