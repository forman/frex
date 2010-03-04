package z.frex.actions;

import z.frex.Frex;
import z.ui.FileExtensionFileFilter;
import z.ui.application.ApplicationWindow;
import z.ui.dialog.MessageDialog;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class SaveImageAction extends PlaneViewAction {

    private static FileExtensionFileFilter PNG_FILTER = new FileExtensionFileFilter("png",//$NON-NLS-1$
                                                                                    "PNG (*.png)",
                                                                                    ".png");//$NON-NLS-1$

    private static FileExtensionFileFilter JPEG_FILTER = new FileExtensionFileFilter("jpeg",//$NON-NLS-1$
                                                                                     "JPEG (*.jpeg;*.jpg)",
                                                                                     new String[]{".jpeg", ".jpg",});//$NON-NLS-1$

    private static FileExtensionFileFilter WBMP_FILTER = new FileExtensionFileFilter("wbmp",//$NON-NLS-1$
                                                                                     "Wireless BitMap (*.wbmp)",
                                                                                     ".wbmp");//$NON-NLS-1$

    private static FileExtensionFileFilter BMP_FILTER = new FileExtensionFileFilter("bmp",//$NON-NLS-1$
                                                                                    "Windows Bitmap Format (*.bmp)",
                                                                                    ".bmp");//$NON-NLS-1$

    private static FileExtensionFileFilter GIF_FILTER = new FileExtensionFileFilter("gif",//$NON-NLS-1$
                                                                                    "GIF (*.gif)",
                                                                                    ".gif");//$NON-NLS-1$

    public static final String ID = "z.frex.actions.saveImage";//$NON-NLS-1$
    private FileExtensionFileFilter lastImageFormat = PNG_FILTER;

    public SaveImageAction(ApplicationWindow window) {
        super(window, ID);
        setText("Bild speichern...");
        setToolTipText("Bild speichern");
        setSmallIcon(Frex.getIcon("/icons/picture_save.png"));//$NON-NLS-1$

    }


    @Override
    public void run() {

        String lastDir = Frex.getPreferences().get("lastImageDir", Frex.getPreferences().get("lastDir", "."));
        JFileChooser dialog = new JFileChooser(lastDir);
        dialog.setFileSelectionMode(JFileChooser.FILES_ONLY);
        dialog.setDialogTitle("Bild speichern");
        dialog.setAcceptAllFileFilterUsed(false);
        dialog.addChoosableFileFilter(PNG_FILTER);
        dialog.addChoosableFileFilter(JPEG_FILTER);
        dialog.addChoosableFileFilter(WBMP_FILTER);
        dialog.addChoosableFileFilter(BMP_FILTER);
        dialog.addChoosableFileFilter(GIF_FILTER);
        dialog.setFileFilter(lastImageFormat);
        int code = dialog.showSaveDialog(getWindow().getShell());

        if (code == JFileChooser.APPROVE_OPTION) {
            Frex.getPreferences().put("lastImageDir", dialog.getCurrentDirectory().getPath());
            FileExtensionFileFilter selectedImageFormat = (FileExtensionFileFilter) dialog.getFileFilter();
            lastImageFormat = selectedImageFormat;
            File selectedFile = selectedImageFormat.appendMissingFileExtension(dialog.getSelectedFile());
            BufferedImage image = getPlaneView().getImageCanvas().getImage();
            try {
                ImageIO.write(image, selectedImageFormat.getFormatName(), selectedFile);
            } catch (IOException e) {
                MessageDialog.openError(getWindow().getShell(),
                                        "Bild speichern",
                                        "Fehler beim Speichern des Bildes nach\n"
                                                + "[" + selectedFile + "]\n" + "Meldung: "
                                                + e.getLocalizedMessage());
            }
        }
    }
}
