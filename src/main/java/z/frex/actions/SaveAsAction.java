package z.frex.actions;

import z.StringLiterals;
import z.core.Plane;
import z.frex.PlaneView;
import z.ui.FileExtensionFileFilter;
import z.ui.UIUtils;
import z.ui.application.ApplicationWindow;

import java.io.File;

public class SaveAsAction extends PlaneViewAction {
    public static final String ID = "z.frex.actions.saveAs";//$NON-NLS-1$

    private boolean canceledByUser;

    public SaveAsAction(ApplicationWindow window) {
        super(window, ID);
        setText(StringLiterals.getString("gui.action.text.saveAs"));
        setToolTipText(StringLiterals.getString("gui.action.tooltip.saveAs"));
        // setSmallIcon(Frex.getIcon(StringLiterals.getString("gui.action.icon.saveAs")));
    }

    public boolean isCanceledByUser() {
        return canceledByUser;
    }

    @Override
    public void run() {
        final PlaneView planeView = getPlaneView();
        if (planeView != null) {
            saveAs(planeView.getPlane());
        }
    }

    public void saveAs(Plane plane) {
        canceledByUser = false;

        File file = promptForFile();
        if (file == null) {
            canceledByUser = true;
            return;
        }

        final SaveAction saveAction = new SaveAction(getWindow());
        File oldFile = plane.getFile();
        plane.setFile(file);
        if (!saveAction.save(plane)) {
            plane.setFile(oldFile);
        }

        canceledByUser = saveAction.isCanceledByUser();
    }

    private File promptForFile() {
        return UIUtils.showSafeDialog(getWindow().getShell(),
                                      StringLiterals.getString("gui.title.saveFile"),
                                      "lastDir",  //$NON-NLS-1$
                                      getPlaneView().getPlane().getName(),
                                      new FileExtensionFileFilter[1],
                                      OpenAction.PLANE_FILTER,
                                      OpenAction.PROJECT_FILTER);
    }

}
