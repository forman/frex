package z.frex.actions;

import z.core.Plane;
import z.core.Project;
import z.frex.Frex;
import z.frex.PlaneView;
import z.ui.FileExtensionFileFilter;
import z.ui.application.Application;
import z.ui.application.ApplicationWindow;
import z.ui.application.PageComponent;
import z.ui.dialog.MessageDialog;
import z.util.ChangeEvent;
import z.util.ChangeListener;
import z.util.RegionHistory;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import java.io.File;
import java.text.MessageFormat;

public class OpenAction extends ApplicationWindowAction {
    public static final String ID = "z.frex.actions.open";//$NON-NLS-1$

    public static final FileExtensionFileFilter PLANE_FILTER = new FileExtensionFileFilter(
            "zplane",  //$NON-NLS-1$
            MessageFormat.format("Frex Ebenen (*{0})", Plane.FILENAME_EXTENSION),
            Plane.FILENAME_EXTENSION);

    public static final FileExtensionFileFilter PROJECT_FILTER = new FileExtensionFileFilter(
            "zproject", //$NON-NLS-1$
            MessageFormat.format("Frex Projekte (*{0})", Project.FILENAME_EXTENSION),
            Project.FILENAME_EXTENSION);

    private boolean canceledByUser;

    public OpenAction(ApplicationWindow window) {
        super(window, ID);
        setText("&Öffnen...");
        setToolTipText("Öffnen");
        setSmallIcon(Frex.getIcon("/icons/folder.png"));//$NON-NLS-1$
    }

    public boolean isCanceledByUser() {
        return canceledByUser;
    }

    @Override
    public void run() {
        canceledByUser = false;

        String filePath = promptForFile();
        if (filePath == null) {
            canceledByUser = true;
            return;
        }

        final File file = new File(filePath);
        try {
            final Plane plane = Plane.readPlane(file);
            openPlane(getWindow(), plane);
        } catch (Exception e) {
            openError(file, e);
        }
    }

    private String promptForFile() {

        String lastDir = Frex.getPreferences().get("lastDir", ".");
        JFileChooser dialog = new JFileChooser(lastDir);
        dialog.setDialogTitle("Öffnen");
        dialog.setFileSelectionMode(JFileChooser.FILES_ONLY);
        dialog.setAcceptAllFileFilterUsed(false);
        dialog.addChoosableFileFilter(PLANE_FILTER);
        dialog.addChoosableFileFilter(PROJECT_FILTER);
        dialog.setFileFilter(PLANE_FILTER);
        int resp = dialog.showOpenDialog(getWindow().getShell());

        if (resp == JFileChooser.APPROVE_OPTION) {
            Frex.getPreferences().put("lastDir", dialog.getCurrentDirectory().getPath());
            return dialog.getSelectedFile().getPath();
        }
        return null;
    }

    public void openError(File file, Exception e) {
        e.printStackTrace();
        final String pattern = "Fehler beim Öffnen der Datei [{0}]."
                + "  Fehler-Typ: {1}\n" + "  Fehler-Meldung: {2}";

        MessageDialog.openError(getWindow().getShell(),
                                "Öffnen",
                                MessageFormat.format(pattern,
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
        window.getShell().setTitle(name + " - Frex");
    }

    public static void openPlane(final ApplicationWindow window,
                                 final Plane plane) {
        boolean reuseExistingWindow = true;
        boolean hasPageComponent = window.getPage().getActivePageComponent() != null;
        if (hasPageComponent) {
            int r = MessageDialog.confirmYesNoCancel(window.getShell(), "Öffnen", "In neuem Fenster öffnen?");
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
            MessageDialog.openError(window.getShell(), "Interner Fehler", e.getMessage());
        }
    }
}
