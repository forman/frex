package z.frex.actions;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import z.StringLiterals;
import z.core.IColorizer;
import z.core.support.colorizers.PaletteColorTable;
import z.frex.Frex;
import z.ui.FileExtensionFileFilter;
import z.ui.UIUtils;
import z.ui.application.ApplicationWindow;
import z.ui.dialog.MessageDialog;
import z.util.JDOMObjectIO;

import java.io.File;
import java.text.MessageFormat;

public class OpenColorsAction extends PlaneViewAction {
    public static final String ID = "z.frex.actions.loadColors"; // NON-NLS

    public static final FileExtensionFileFilter FILE_FILTER = new FileExtensionFileFilter(
            "zcol",  // NON-NLS
            MessageFormat.format(StringLiterals.getString("gui.fileTypeDescr.layer"),
                                 ".zcol"), // NON-NLS
            ".zcol");  // NON-NLS
    public static final String LAST_COLORS_DIR_PROPERTY = "lastColorsDir";// NON-NLS
    private final Callback callback;

    public OpenColorsAction(ApplicationWindow window, Callback callback) {
        super(window, ID);
        this.callback = callback;
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
            callback.apply(colorizer);
        } catch (Exception e) {
            openError(file, e);
        }
    }

    private File promptForFile() {
        return UIUtils.showOpenDialog(getWindow().getShell(),
                                      StringLiterals.getString("gui.title.openFile"),
                                      LAST_COLORS_DIR_PROPERTY,
                                      getPlaneView().getPlane().getName(),
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

    public interface Callback {
        void apply(IColorizer colorizer);
    }

}
