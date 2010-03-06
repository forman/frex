package z.frex.actions;

import z.core.ImageInfo;
import z.core.Plane;
import z.frex.Frex;
import z.frex.PlaneView;
import z.frex.dialogs.EditImageSizeDialog;
import z.ui.application.ApplicationWindow;
import z.ui.dialog.Dialog;

public class EditImageSizeAction extends PlaneViewAction {
    public static final String ID = "z.frex.actions.editImageSize"; //$NON-NLS-1$

    public EditImageSizeAction(ApplicationWindow window) {
        super(window, ID);
        setText("Bildgröße...");
        setToolTipText("Bildgröße bearbeiten");
        setSmallIcon(Frex.getIcon("/icons/16x16/frex/image-size.png"));//$NON-NLS-1$
    }

    @Override
    public void run() {
        final PlaneView view = getPlaneView();
        final Plane plane = view.getPlane();
        final ImageInfo imageInfo = plane.getImageInfo();
        EditImageSizeDialog.Data data = new EditImageSizeDialog.Data();
        data.imageWidth = imageInfo.getImageWidth();
        data.imageHeight = imageInfo.getImageHeight();
        data.usingWindowSize = imageInfo.isUsingWindowSize();
        EditImageSizeDialog dialog = new EditImageSizeDialog(getWindow().getShell(),
                                                             data);
        int code = dialog.open();
        if (code == Dialog.ID_OK) {
            data = dialog.getData();
            imageInfo.setImageWidth(data.imageWidth);
            imageInfo.setImageHeight(data.imageHeight);
            imageInfo.setUsingWindowSize(data.usingWindowSize);
            plane.setModified(true);
            plane.fireStateChange();
            view.generateImage(false);
        }
    }
}
