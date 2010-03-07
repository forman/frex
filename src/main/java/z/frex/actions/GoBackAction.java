package z.frex.actions;

import z.StringLiterals;
import z.core.Region;
import z.frex.Frex;
import z.ui.application.ApplicationWindow;
import z.util.RegionHistory;

public class GoBackAction extends GoAction {
    public static final String ID = "z.frex.actions.goBack";// NON-NLS

    public GoBackAction(ApplicationWindow window) {
        super(window, ID);
        setText(StringLiterals.getString("gui.action.text.back"));
        setToolTipText(StringLiterals.getString("gui.action.tooltip.back"));
        setSmallIcon(Frex.getIcon(StringLiterals.getString("gui.action.icon.back")));
    }

    @Override
    public void updateState() {
        if (getPlaneView() != null) {
            RegionHistory regionHistory = getRegionHistory(getPlaneView());
            boolean enabled = regionHistory.getCurrentIndex() > 0;
            setEnabled(enabled);
        } else {
            super.updateState();
        }
    }

    @Override
    public void run() {
        RegionHistory regionHistory = getRegionHistory(getPlaneView());
        if (regionHistory.gotoPreviousRegion()) {
            final Region currentRegion = regionHistory.getCurrentRegion();
            getPlaneView().getPlane().setRegion(currentRegion);
            getPlaneView().generateImage(false);
        }
    }
}
