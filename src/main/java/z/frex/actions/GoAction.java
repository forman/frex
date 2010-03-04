package z.frex.actions;

import z.frex.Frex;
import z.frex.PlaneView;
import z.ui.application.ApplicationWindow;
import z.util.ChangeEvent;
import z.util.ChangeListener;
import z.util.RegionHistory;

public abstract class GoAction extends PlaneViewAction {
    private ChangeListener changeHandler;

    public GoAction(ApplicationWindow window, String id) {
        super(window, id);
        changeHandler = new ChangeListener() {
            public void stateChanged(ChangeEvent event) {
                updateState();
            }
        };
    }

    @Override
    public void dispose() {
        if (changeHandler != null) {
            final PlaneView view = getPlaneView();
            if (view != null) {
                getRegionHistory(view).removeChangeListener(changeHandler);
            }
            changeHandler = null;
        }
        super.dispose();
    }

    @Override
    public void onPlaneViewOpened(PlaneView view) {
        getRegionHistory(view).addChangeListener(changeHandler);
    }

    @Override
    public void onPlaneViewClosed(PlaneView view) {
        getRegionHistory(view).removeChangeListener(changeHandler);
    }

    @Override
    public void onPlaneViewActivated(PlaneView view) {
        getRegionHistory(view).addChangeListener(changeHandler);
    }

    @Override
    public void onPlaneViewDeactivated(PlaneView view) {
        getRegionHistory(view).removeChangeListener(changeHandler);
    }

    public static RegionHistory getRegionHistory(PlaneView view) {
        return Frex.getRegionHistory(view.getPlane());
    }
}
