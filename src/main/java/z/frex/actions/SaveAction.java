package z.frex.actions;

import z.StringLiterals;
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
    public static final String ID = "z.frex.actions.save"; // NON-NLS

    private boolean canceledByUser;

    public SaveAction(ApplicationWindow window) {
        super(window, ID);
        setText(StringLiterals.getString("gui.action.text.save"));
        setToolTipText(StringLiterals.getString("gui.action.tooltip.save"));
        setSmallIcon(Frex.getIcon(StringLiterals.getString("gui.action.icon.save")));
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
        File quicklookFile = FileUtils.ensureExtension(file, ".png"); // NON-NLS
        try {
            ImageIO.write(image, "PNG", quicklookFile); // NON-NLS
        } catch (IOException e) {
            // todo: log, but do nothing more, the image is not important
            e.printStackTrace();
        }
    }

    public void openError(File file, Exception e) {
        e.printStackTrace();

        MessageDialog.openError(getWindow().getShell(),
                                StringLiterals.getString("gui.title.saveFile"),
                                MessageFormat.format(StringLiterals.getString("gui.msg.errorSavingFile"),
                                                     file.getPath(),
                                                     e.getClass().getName(),
                                                     e.getLocalizedMessage()));
    }
}
