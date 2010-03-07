package z.frex.actions;

import z.StringLiterals;
import z.core.IFractal;
import z.core.Plane;
import z.core.Region;
import z.frex.Frex;
import z.frex.dialogs.SelectFractalDialog;
import z.ui.application.ApplicationWindow;
import z.ui.dialog.Dialog;

import java.io.File;

public class NewPlaneAction extends ApplicationWindowAction {
    public static final String ID = "z.frex.actions.newPlane";// NON-NLS

    private static int planeCount = 0;

    private boolean canceledByUser;

    public NewPlaneAction(ApplicationWindow window) {
        super(window, ID);
        setText(StringLiterals.getString("gui.action.text.newFractal"));
        setToolTipText(StringLiterals.getString("gui.action.tooltip.newFractal"));
        setSmallIcon(Frex.getIcon(StringLiterals.getString("gui.action.icon.newFractal")));
    }

    public boolean isCanceledByUser() {
        return canceledByUser;
    }

    @Override
    public void run() {
        canceledByUser = false;
        final Plane plane = createPlane(getWindow());
        if (plane != null) {
            OpenAction.openPlane(getWindow(), plane);
        } else {
            canceledByUser = true;
        }
    }

    public static Plane createPlane(final ApplicationWindow window) {
        SelectFractalDialog dialog = new SelectFractalDialog(window.getShell());
        int code = dialog.open();
        if (code != Dialog.ID_OK) {
            return null;
        }

        final IFractal selectedFractal = dialog.getSelectedFractal();
        final Region startRegion = selectedFractal.getStartRegion();

        planeCount++;
        String planeName = selectedFractal.getClass().getName();
        int pos = planeName.lastIndexOf('.');
        if (pos > 0) {
            planeName = planeName.substring(pos + 1);
        }
        planeName += "_" + planeCount + Plane.FILENAME_EXTENSION;

        final Plane plane = new Plane(new File(planeName));
        plane.setFractal(selectedFractal);
        plane.setRegion(startRegion);

        plane.getImageInfo().setUsingWindowSize(true);

        return plane;
    }
}
