package z.frex;

import z.core.IColorizer;
import z.core.ImageInfo;
import z.core.Plane;
import z.core.PlaneRaster;
import z.core.PlaneRenderer;
import z.core.progress.NullProgressMonitor;
import z.core.progress.ProgressMonitor;
import z.core.progress.ProgressMonitorWrapper;
import z.core.support.colorizers.PaletteColorTable;
import z.ui.ImageCanvas;
import z.ui.ProgressSwingWorker;
import z.ui.application.AbstractPageComponent;
import z.ui.application.PageComponent;
import z.ui.application.PageComponentListenerAdapter;
import z.ui.dialog.MessageDialog;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class PlaneView extends AbstractPageComponent {

    public static final String ID = PlaneView.class.getName();

    private static final long IMAGE_UPDATE_MILLIS = 300L;

    private ImageCanvas imageCanvas;

    private boolean imageGenerated;

    private MouseMoveHandler mouseMoveHandler;

    private PageComponentHandler pageComponentHandler;
    private BufferedImage image;
    private GenerateImageJob currentJob;

    private Plane plane;
    private JScrollPane control;

    public PlaneView() {
        mouseMoveHandler = new MouseMoveHandler();
        pageComponentHandler = new PageComponentHandler();
    }

    public String getId() {
        return ID;
    }

    public Plane getPlane() {
        return plane;
    }

    @Override
    public void setInput(Object input) {
        super.setInput(input);
        plane = (Plane) input;
        getPage().addPageComponentListener(pageComponentHandler);
        setDisplayName(plane.getName());
    }

    private Plane[] getPlanes() {
        return new Plane[]{getPlane()};  // todo:  extend for multiple planes
    }

    public ImageCanvas getImageCanvas() {
        return imageCanvas;
    }

    // todo:  extract into separate GenerateImageAction
    public void generateImage(boolean colorizeOnly) {
        generateImage(false, colorizeOnly);
    }

    // todo:  extract into separate GenerateImageAction
    public synchronized void generateImage(boolean adjustColors, boolean colorizeOnly) {
        if (currentJob != null) {
            System.out.println("currentJob != null: currentJob = " + currentJob);
            currentJob.cancel();
            currentJob = null;
        }

        final ImageInfo imageInfo = getPlane().getImageInfo();
        final Rectangle clientArea = getImageCanvas().getClientArea();
        if (!clientArea.isEmpty() && imageInfo.isUsingWindowSize()) {
            // todo: must fix bug: images can only grow, not shring
            imageInfo.setImageWidth(clientArea.width);
            imageInfo.setImageHeight(clientArea.height);
        }
        final Plane[] planes = getPlanes();

        int width = imageInfo.getImageWidth();
        int height = imageInfo.getImageHeight();

        if (image == null || width != image.getWidth() || height != image.getHeight()) {
            image = createImage(width, height);
            getImageCanvas().setImage(image);
        }

        // todo: use real UI progress monitor
        ProgressMonitor progressMonitor = new NullProgressMonitor();
        currentJob = new GenerateImageJob(getPlane().getImageInfo(),
                                          adjustColors,
                                          colorizeOnly,
                                          planes,
                                          new ImageUpdater(progressMonitor));
        currentJob.execute();
    }

    // todo:  extract into separate GenerateImageAction
    public synchronized void adjustColorTableIndexRange() {
        final Plane[] planes = getPlanes(); // todo:  extend for multiple planes
        for (Plane plane : planes) {
            IColorizer colorizer = plane.getColorizer();
            if (colorizer instanceof PaletteColorTable) {
                PaletteColorTable paletteColorTable = (PaletteColorTable) colorizer;
                PlaneRaster.Statistics totalStatistics = plane.getRaster().getTotalStatistics();
                float minRasterIndex = totalStatistics.min;
                float maxRasterIndex = totalStatistics.max;
                System.out.println("minRasterIndex = " + minRasterIndex);
                System.out.println("maxRasterIndex = " + maxRasterIndex);
                paletteColorTable.setIndexMin(minRasterIndex);
                paletteColorTable.setIndexMax(maxRasterIndex);

                // todo: the following code skips 2.5% of the pixels from the lower and upper bounds
                // make autoAdjustMinMax an preferences setting
                boolean autoAdjustMinMax = false;
                if (autoAdjustMinMax && maxRasterIndex > minRasterIndex) {

                    int[] histogram = totalStatistics.histogram;
                    float skipSum = (2.5f / 100.0f) * (float) totalStatistics.count;
                    float s = (totalStatistics.max - totalStatistics.min) / (float) histogram.length;

                    float sum = 0.0f;
                    for (int i = 0; i < histogram.length; i++) {
                        sum += histogram[i];
                        if (sum > skipSum) {
                            minRasterIndex += s * (float) i;
                            break;
                        }
                    }

                    sum = 0.0f;
                    for (int i = 0; i < histogram.length; i++) {
                        sum += histogram[histogram.length - 1 - i];
                        if (sum > skipSum) {
                            maxRasterIndex -= s * (float) i;
                            break;
                        }
                    }

                    paletteColorTable.setIndexMin(minRasterIndex);
                    paletteColorTable.setIndexMax(maxRasterIndex);
                }
            }
        }
        // todo: use real UI progress monitor
        ProgressMonitor progressMonitor = new NullProgressMonitor();
        GenerateImageJob job = new GenerateImageJob(getPlane().getImageInfo(), false, true, planes, new ImageUpdater(progressMonitor));
        job.execute();
    }

    private static BufferedImage createImage(int width, int height) {
        return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    }

    private static int[] getImageData(BufferedImage image) {
        return ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
    }

    private void updateImage() {
        getImageCanvas().repaint();
    }


    @Override
    public void dispose() {
        if (control != null) {
            getPlane().dispose();
            getPage().removePageComponentListener(pageComponentHandler);
            imageCanvas = null;
            control = null;
            mouseMoveHandler = null;
        }
        super.dispose();
    }

    public JComponent getControl() {
        if (control == null) {
            imageCanvas = new ImageCanvas();
            imageCanvas.addMouseMotionListener(mouseMoveHandler);
            control = new JScrollPane(imageCanvas);
        }
        return control;
    }

    @SuppressWarnings("boxing")
    protected void onMouseMove(MouseEvent e) {
//        String text = "";
//        if (imageCanvas.getImage() != null) {
//            final Rectangle imageBounds = imageCanvas.getImageBounds();
//            final int x = e.getX() - imageBounds.x;
//            final int y = e.getY() - imageBounds.y;
//            if (x >= 0 && x < imageBounds.width && y >= 0 && y < imageBounds.height) {
//                final Region region = getPlane().getRegion();
//                final double pixelSize = 2.0 * region.getRadius() / imageBounds.height;
//                final double zx = region.getCenterX() + pixelSize * (x - 0.5 * imageBounds.width);
//                final double zy = region.getCenterY() - pixelSize * (y - 0.5 * imageBounds.height);
//                PlaneRaster raster = getPlane().getRaster();
//                if (raster != null) {
//                    final float v = raster.getRawData()[y * imageBounds.width + x];
//                    text = String.format("p=%d;%d | z=%f;%f | v=%f", x, y, zx, zy, v);
//                } else {
//                    text = String.format("p=%d;%d | z=%f;%f", x, y, zx, zy);
//                }
//            } else {
//                text = String.format("p=%d;%d", x, y);
//            }
//        }
    }

    @Override
    public void setFocus() {
        imageCanvas.requestFocus();
    }

    @Override
    public void doSave(ProgressMonitor monitor) {
        // todo: auto-generated method stub
        System.out.println("PlaneView.doSave()");
    }

    @Override
    public void doSaveAs() {
        // todo: auto-generated method stub
        System.out.println("PlaneView.doSaveAs()");
    }

    @Override
    public boolean isSaveAsAllowed() {
        return true;
    }

    @Override
    public boolean isDirty() {
        return getPlane().isModified();
    }

    private final class MouseMoveHandler extends MouseMotionAdapter {
        @Override
        public void mouseMoved(MouseEvent e) {
            if (currentJob == null) {
                onMouseMove(e);
            }
        }
    }

    private class GenerateImageJob extends ProgressSwingWorker<Object, Object> {
        private ImageInfo imageInfo;
        private boolean adjustColors;
        private boolean colorizeOnly;
        private final Plane[] planes;

        public GenerateImageJob(ImageInfo imageInfo, boolean adjustColors, boolean colorizeOnly, Plane[] planes, ProgressMonitor progressMonitor) {
            super(progressMonitor);
            this.imageInfo = imageInfo;
            this.adjustColors = adjustColors;
            this.colorizeOnly = colorizeOnly;
            this.planes = planes;
        }

        @Override
        protected Object doInBackground(ProgressMonitor progressMonitor) throws Exception {
            final PlaneRenderer planeProcessor = new PlaneRenderer(imageInfo, colorizeOnly);
            int[][] dataBuffers = new int[planes.length][];
            dataBuffers[0] = getImageData(PlaneView.this.image);
            planeProcessor.renderPlanes(planes, dataBuffers, progressMonitor);
            updateImage();
            return null;
        }

        @Override
        protected void done() {
            if (!isCancelled()) {
                try {
                    // get() causes exception occured in doInBackground() to be re-thrown here
                    GenerateImageJob.this.get();
                    if (adjustColors) {
                        adjustColorTableIndexRange();
                    }
                } catch (Throwable e) {
                    handleError(e);
                }
            }
            currentJob = null;
            imageCanvas.fireStateChange();
        }

        private void handleError(Throwable e) {
            e.printStackTrace();
            MessageDialog.openError(getControl(), "Image Generator", "Interner Fehler:\n" +
                    e.getClass().getName() + ":\n"
                    + e.getLocalizedMessage());
        }
    }

    private class ImageUpdater extends ProgressMonitorWrapper {
        private long t0;

        public ImageUpdater(ProgressMonitor progressMonitor) {
            super(progressMonitor);
            t0 = System.currentTimeMillis();
        }

        @Override
        public void internalWorked(double work) {
            long t1 = System.currentTimeMillis();
            if (t1 - t0 >= IMAGE_UPDATE_MILLIS) {
                t0 = t1;
                imageCanvas.repaint();
            }
        }
    }

    private final class PageComponentHandler extends PageComponentListenerAdapter {

        @Override
        public void componentActivated(PageComponent pageComponent) {
            System.out.println("maybeGenerateImage: pageComponent = " + pageComponent);
            if (pageComponent == PlaneView.this) {
                if (!imageGenerated) {
                    final Rectangle clientArea = getImageCanvas().getClientArea();
                    if (!clientArea.isEmpty()) {
                        imageGenerated = true;
                        generateImage(false);
                    }
                }
            }
        }
    }
}
