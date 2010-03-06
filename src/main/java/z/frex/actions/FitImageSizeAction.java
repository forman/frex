package z.frex.actions;

import z.core.ImageInfo;
import z.core.Plane;
import z.frex.Frex;
import z.frex.PlaneView;
import z.ui.application.ApplicationWindow;

public class FitImageSizeAction extends PlaneViewAction {
    public static final String ID = "z.frex.actions.fitImage"; //$NON-NLS-1$

    public FitImageSizeAction(ApplicationWindow window) {
        super(window, FitImageSizeAction.ID);
        setText("Bild anpassen");
        setToolTipText("Bildgröße an Fenster anpassen");
        setSmallIcon(Frex.getIcon("/icons/16x16/actions/view-fullscreen.png"));//$NON-NLS-1$
    }

    @Override
    public void run() {
        final PlaneView view = getPlaneView();
        final Plane plane = view.getPlane();
        final ImageInfo imageInfo = plane.getImageInfo();
        imageInfo.setUsingWindowSize(true);
        plane.setModified(true);
        plane.fireStateChange();
        view.generateImage(false);
    }
}
