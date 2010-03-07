package z.frex.actions;

import z.StringLiterals;
import z.core.Region;
import z.frex.Frex;
import z.ui.application.ApplicationWindow;
import z.util.RegionHistory;

public class GoNextAction extends GoAction {
    public static final String ID = "z.frex.actions.goNext";//$NON-NLS-1$

    public GoNextAction(ApplicationWindow window) {
        super(window, ID);
        setText(StringLiterals.getString("gui.action.text.next"));
        setToolTipText(StringLiterals.getString("gui.action.tooltip.next"));
        setSmallIcon(Frex.getIcon(StringLiterals.getString("gui.action.icon.next")));
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
