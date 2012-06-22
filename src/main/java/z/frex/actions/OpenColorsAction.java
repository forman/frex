package z.frex.actions;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import z.StringLiterals;
import z.core.IColorizer;
import z.core.Plane;
import z.core.support.colorizers.PaletteColorTable;
import z.frex.Frex;
import z.frex.PlaneView;
import z.frex.dialogs.EditColorsDialog;
import z.ui.FileExtensionFileFilter;
import z.ui.UIUtils;
import z.ui.dialog.MessageDialog;
import z.util.JDOMObjectIO;

import java.io.File;
import java.text.MessageFormat;

public class OpenColorsAction extends ApplicationWindowAction {
    public static final String ID = "z.frex.actions.loadColors"; // NON-NLS

    public static final FileExtensionFileFilter FILE_FILTER = new FileExtensionFileFilter(
            "zcol",  // NON-NLS
            MessageFormat.format(StringLiterals.getString("gui.fileTypeDescr.layer"),
                                 ".zcol"), // NON-NLS
            ".zcol");  // NON-NLS
    public static final String LAST_COLORS_DIR_PROPERTY = "lastColorsDir";// NON-NLS

    private final PlaneView view;
    private final EditColorsDialog editColorsDialog;

    public OpenColorsAction(PlaneView view, EditColorsDialog editColorsDialog) {
        super(view.getPage().getWindow(), ID);
        this.view = view;
        this.editColorsDialog = editColorsDialog;
        setText(StringLiterals.getString("gui.action.text.loadColors"));
        setToolTipText(StringLiterals.getString("gui.action.tooltip.loadColors"));
        setSmallIcon(Frex.getIcon(StringLiterals.getString("gui.action.icon.open")));
    }

    @Override
    public void run() {

        final File file = promptForFile();
        if (file == null) {
            return;
        }

        try {
            final SAXBuilder builder = new SAXBuilder();
            final Document document = builder.build(file);
            Element element = document.getRootElement();
            IColorizer colorizer = (IColorizer) JDOMObjectIO.readObject(element, PaletteColorTable.class);
            if (colorizer instanceof PaletteColorTable) {
                PaletteColorTable paletteColorTable = (PaletteColorTable) colorizer;
                editColorsDialog.getEditColorsModel().setCurrentPaletteColorTable(paletteColorTable);
            } else {
                Plane plane = view.getPlane();
                plane.setColorizer(colorizer);
                view.generateImage(true);
            }
        } catch (Exception e) {
            openError(file, e);
        }
    }

    private File promptForFile() {
        return UIUtils.showOpenDialog(getWindow().getShell(),
                                      StringLiterals.getString("gui.title.openFile"),
                                      LAST_COLORS_DIR_PROPERTY,
                                      view.getPlane().getName(),
                                      FILE_FILTER);
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

}
