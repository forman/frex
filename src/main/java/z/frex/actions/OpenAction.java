package z.frex.actions;

import z.StringLiterals;
import z.core.Plane;
import z.core.Project;
import z.frex.Frex;
import z.frex.PlaneView;
import z.ui.FileExtensionFileFilter;
import z.ui.UIUtils;
import z.ui.application.Application;
import z.ui.application.ApplicationWindow;
import z.ui.application.PageComponent;
import z.ui.dialog.MessageDialog;
import z.util.ChangeEvent;
import z.util.ChangeListener;
import z.util.RegionHistory;

import javax.swing.JOptionPane;
import java.io.File;
import java.text.MessageFormat;

public class OpenAction extends ApplicationWindowAction {
    public static final String ID = "z.frex.actions.open"; // NON-NLS

    public static final FileExtensionFileFilter PLANE_FILTER = new FileExtensionFileFilter(
            "zplane",  // NON-NLS
            MessageFormat.format(StringLiterals.getString("gui.fileTypeDescr.layer"), Plane.FILENAME_EXTENSION),
            Plane.FILENAME_EXTENSION);

    public static final FileExtensionFileFilter PROJECT_FILTER = new FileExtensionFileFilter(
            "zproject",  // NON-NLS
            MessageFormat.format(StringLiterals.getString("gui.fileTypeDescr.projects"), Project.FILENAME_EXTENSION),
            Project.FILENAME_EXTENSION);

    private boolean canceledByUser;

    public OpenAction(ApplicationWindow window) {
        super(window, ID);
        setText(StringLiterals.getString("gui.action.text.open"));
        setToolTipText(StringLiterals.getString("gui.action.tooltip.open"));
        setSmallIcon(Frex.getIcon(StringLiterals.getString("gui.action.icon.open")));
    }

    public boolean isCanceledByUser() {
        return canceledByUser;
    }

    @Override
    public void run() {
        canceledByUser = false;

        File file = promptForFile();
        if (file == null) {
            canceledByUser = true;
            return;
        }

        try {
            final Plane plane = Plane.readPlane(file);
            openPlane(getWindow(), plane);
        } catch (Exception e) {
            openError(file, e);
        }
    }

    private File promptForFile() {
        return UIUtils.showOpenDialog(getWindow().getShell(),
                                      StringLiterals.getString("gui.title.openFile"),
                                      "lastDir",  // NON-NLS
                                      "",    // NON-NLS
                                      PLANE_FILTER, PROJECT_FILTER);
    }

    public void openError(File file, Exception e) {
        e.printStackTrace();
        MessageDialog.showError(getWindow().getShell(),
                                StringLiterals.getString("gui.title.openFile"),
                                MessageFormat.format(StringLiterals.getString("gui.msg.errorOpeningFile"),
                                                     file.getPath(),
                                                     e.getClass().getName(),
                                                     e.getLocalizedMessage()));
    }

    public static void updateShellTitle(final ApplicationWindow window) {
        final PageComponent activeComponent = window.getPage().getActivePageComponent();
        final Object input = activeComponent.getInput();
        String name = ((Plane) input).getName();
        if (activeComponent.isDirty()) {
            name = "*" + name;
        }
        window.getShell().setTitle(MessageFormat.format(StringLiterals.getString("gui.frame.titleWithDoc"), name));
    }

    public static void openPlane(final ApplicationWindow window,
                                 final Plane plane) {
        boolean reuseExistingWindow = true;
        boolean hasPageComponent = window.getPage().getActivePageComponent() != null;
        if (hasPageComponent) {
            int r = MessageDialog.confirmYesNoCancel(window.getShell(),
                                                     StringLiterals.getString("gui.title.openFile"),
                                                     StringLiterals.getString("gui.msg.askOpenInNewWindow"));
            if (r == JOptionPane.CANCEL_OPTION) {
                return;
            }
            reuseExistingWindow = (r == JOptionPane.NO_OPTION);
        }
        openPlane0(reuseExistingWindow ? window : Application.instance().openWindow(), plane);
    }

    private static void openPlane0(final ApplicationWindow window, Plane plane) {
        try {
            window.getPage().openPageComponent(plane, PlaneView.ID);
            plane.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent event) {
                    OpenAction.updateShellTitle(window);
                }
            });
            OpenAction.updateShellTitle(window);

            RegionHistory regionHistory = Frex.getRegionHistory(plane);
            regionHistory.setCurrentRegion(plane.getRegion());
        } catch (Exception e) {
            e.printStackTrace();
            MessageDialog.showError(window.getShell(), StringLiterals.getString("gui.title.internalError"), e.getMessage());
        }
    }
}
