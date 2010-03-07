package z.frex.actions;

import z.StringLiterals;
import z.core.Plane;
import z.frex.Frex;
import z.frex.PlaneView;
import z.ui.FileExtensionFileFilter;
import z.ui.application.ApplicationWindow;

import javax.swing.JFileChooser;
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

        String filePath = promptForFile();
        if (filePath == null) {
            canceledByUser = true;
            return;
        }

        final File file = new File(filePath);
        final SaveAction saveAction = new SaveAction(getWindow());
        File oldFile = plane.getFile();
        plane.setFile(file);
        if (!saveAction.save(plane)) {
            plane.setFile(oldFile);
        }
        canceledByUser = saveAction.isCanceledByUser();
    }

    private String promptForFile() {
        String lastDir = Frex.getPreferences().get("lastDir", "."); // NON-NLS
        JFileChooser dialog = new JFileChooser(lastDir);
        dialog.setDialogTitle(StringLiterals.getString("gui.title.saveFile"));
        dialog.setFileSelectionMode(JFileChooser.FILES_ONLY);
        dialog.setAcceptAllFileFilterUsed(false);
        dialog.addChoosableFileFilter(OpenAction.PLANE_FILTER);
        dialog.addChoosableFileFilter(OpenAction.PROJECT_FILTER);
        dialog.setFileFilter(OpenAction.PLANE_FILTER);
        dialog.setSelectedFile(new File(lastDir, getPlaneView().getPlane().getName()));
        int resp = dialog.showSaveDialog(getWindow().getShell());
        if (resp == JFileChooser.APPROVE_OPTION) {
            Frex.getPreferences().put("lastDir", // NON-NLS
                                      dialog.getCurrentDirectory().getPath());
            FileExtensionFileFilter fileFilter = (FileExtensionFileFilter) dialog.getFileFilter();
            File selectedFile = fileFilter.appendMissingFileExtension(dialog.getSelectedFile());
            return selectedFile.getPath();
        }
        return null;
    }

}
