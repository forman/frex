package z.frex.actions;

import z.StringLiterals;
import z.core.ImageInfo;
import z.core.Plane;
import z.frex.Frex;
import z.frex.PlaneView;
import z.frex.dialogs.EditImageSizeDialog;
import z.ui.application.ApplicationWindow;
import z.ui.dialog.Dialog;

public class EditImageSizeAction extends PlaneViewAction {
    public static final String ID = "z.frex.actions.editImageSize"; // NON-NLS

    public EditImageSizeAction(ApplicationWindow window) {
        super(window, ID);
        setText(StringLiterals.getString("gui.action.text.imageSize"));
        setToolTipText(StringLiterals.getString("gui.action.tooltip.imageSize"));
        setSmallIcon(Frex.getIcon(StringLiterals.getString("gui.action.icon.imageSize")));
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
