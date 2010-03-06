package z.frex.actions;

import z.core.Region;
import z.frex.Frex;
import z.ui.application.ApplicationWindow;
import z.util.RegionHistory;

public class GoNextAction extends GoAction {
    public static final String ID = "z.frex.actions.goNext";//$NON-NLS-1$

    public GoNextAction(ApplicationWindow window) {
        super(window, ID);
        setText("Nächste");
        setToolTipText("Nächste");
        setSmallIcon(Frex.getIcon("/icons/16x16/actions/go-next.png"));//$NON-NLS-1$
    }

    @Override
    public void updateState() {
        if (getPlaneView() != null) {
            RegionHistory regionHistory = getRegionHistory(getPlaneView());
            boolean enabled = regionHistory.getCurrentIndex() < regionHistory.getSize() - 1;
            setEnabled(enabled);
        } else {
            super.updateState();
        }
    }

    @Override
    public void run() {
        RegionHistory regionHistory = getRegionHistory(getPlaneView());
        if (regionHistory.gotoNextRegion()) {
            final Region currentRegion = regionHistory.getCurrentRegion();
            getPlaneView().getPlane().setRegion(currentRegion);
            getPlaneView().generateImage(false);
        }
    }
}
