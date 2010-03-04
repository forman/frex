package z.frex.actions;

import z.core.Plane;
import z.frex.Frex;
import z.ui.application.ApplicationWindow;
import z.ui.dialog.MessageDialog;
import z.util.FileUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;

public class SaveAction extends PlaneViewAction {
    public static final String ID = "z.frex.actions.save";//$NON-NLS-1$

    private boolean canceledByUser;

    public SaveAction(ApplicationWindow window) {
        super(window, ID);
        setText("&Speichern");
        setToolTipText("Speichern");
        setSmallIcon(Frex.getIcon("/icons/disk.png"));//$NON-NLS-1$
    }

    public boolean isCanceledByUser() {
        return canceledByUser;
    }

    @Override
    public void updateState() {
        super.updateState();
        if (isEnabled()) {
            final Plane plane = getPlaneView().getPlane();
            setEnabled(plane.getFile().getParent() != null && plane.isModified());
        }
    }

    @Override
    public void run() {
        final Plane plane = getPlaneView().getPlane();
        save(plane);
    }

    public boolean save(Plane plane) {
        File file = plane.getFile();
        try {
            plane.write(file);
            BufferedImage image = getPlaneView().getImageCanvas().getImage();
            // todo: make it a preference setting, if a quicklook shall be always be stored
            boolean saveQuicklook = true;
            if (saveQuicklook) {
                writeQuicklookFile(file, image);
            }
        } catch (Exception e) {
            openError(file, e);
            return false;
        }
        return true;
    }

    private void writeQuicklookFile(File file, BufferedImage image) {
        File quicklookFile = FileUtils.ensureExtension(file, ".png");
        try {
            ImageIO.write(image, "png", quicklookFile);
        } catch (IOException e) {
            // todo: log, but do nothing more, the image is not important
            e.printStackTrace();
        }
    }

    public void openError(File file, Exception e) {
        e.printStackTrace();
        final String pattern = "Fehler beim Speichern der Datei [{0}]." +
                "  Fehler-Typ: {1}\n" +
                "  Fehler-Meldung: {2}";

        String msg = MessageFormat.format(pattern,
                                          file.getPath(),
                                          e.getClass().getName(),
                                          e.getLocalizedMessage());
        MessageDialog.openError(getWindow().getShell(),
                                "Speichern",
                                msg);
    }
}
