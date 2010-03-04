package z.frex.actions;

import z.core.Plane;
import z.core.Region;
import z.frex.Frex;
import z.ui.application.ApplicationWindow;
import z.util.RegionHistory;

public class GoHomeAction extends GoAction {
    public static final String ID = "z.frex.actions.goHome";//$NON-NLS-1$

    public GoHomeAction(ApplicationWindow window) {
        super(window, ID);
        setText("Anfang");
        setToolTipText("Anfang");
        setSmallIcon(Frex.getIcon("/icons/16x16/actions/go-home.png"));//$NON-NLS-1$
    }

    @Override
    public void run() {
        final Plane plane = getPlaneView().getPlane();
        plane.setRegion(plane.getFractal().getStartRegion());
        getPlaneView().generateImage(false);

        final Region currentRegion = getPlaneView().getPlane().getFractal().getStartRegion();
        getPlaneView().getPlane().setRegion(currentRegion);

        RegionHistory regionHistory = getRegionHistory(getPlaneView());
        regionHistory.setCurrentRegion(currentRegion);

        getPlaneView().generateImage(false);
    }
}
