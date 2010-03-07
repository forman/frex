package z.frex.actions;

import z.StringLiterals;
import z.core.ImageInfo;
import z.core.Plane;
import z.frex.Frex;
import z.frex.PlaneView;
import z.ui.application.ApplicationWindow;

public class FitImageSizeAction extends PlaneViewAction {
    public static final String ID = "z.frex.actions.fitImage"; //$NON-NLS-1$

    public FitImageSizeAction(ApplicationWindow window) {
        super(window, FitImageSizeAction.ID);
        setText(StringLiterals.getString("gui.action.text.fitImageSize"));
        setToolTipText(StringLiterals.getString("gui.action.tooltip.fitImageSize"));
        setSmallIcon(Frex.getIcon(StringLiterals.getString("gui.action.icon.fitImageSize")));
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
