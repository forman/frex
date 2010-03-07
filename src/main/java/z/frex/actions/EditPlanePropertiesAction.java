package z.frex.actions;

import z.StringLiterals;
import z.core.AlgorithmRegistry;
import z.core.IFractal;
import z.core.Plane;
import z.frex.Frex;
import z.frex.PlaneView;
import z.frex.dialogs.EditPlanePropertiesDialog;
import z.ui.application.ApplicationWindow;

import java.awt.Rectangle;

public class EditPlanePropertiesAction extends PlaneViewAction {
    public static final String ID = "z.frex.actions.editPlaneProperties"; //$NON-NLS-1$

    private boolean adjustColors = true;
    private Rectangle dialogBounds;

    public EditPlanePropertiesAction(ApplicationWindow window) {
        super(window, ID);
        setText(StringLiterals.getString("gui.action.text.properties"));
        setToolTipText(StringLiterals.getString("gui.action.tooltip.properties"));
        setSmallIcon(Frex.getIcon(StringLiterals.getString("gui.action.icon.properties")));
    }

    @Override
    public void run() {
        final PlaneView view = getPlaneView();
        final Plane plane = view.getPlane();
        final IFractal fractal = plane.getFractal();

        EditPlanePropertiesDialog.Data data = new EditPlanePropertiesDialog.Data();
        data.fractal = AlgorithmRegistry.instance().getFractals().lookup(fractal.getClass());
        data.iterMax = fractal.getIterMax();
        data.bailOut = fractal.getBailOut();
        data.accumulator = plane.getAccumulator();
        data.indexer = plane.getIndexer();
        data.trapMode = plane.getTrapMode();
        data.decompositionMode = plane.getDecompositionMode();
        data.juliaMode = fractal.isJuliaMode();
        if (data.juliaMode) {
            data.juliaCX = fractal.getJuliaCX();
            data.juliaCY = fractal.getJuliaCY();
        } else {
            data.juliaCX = plane.getRegion().getCenterX();
            data.juliaCY = plane.getRegion().getCenterY();
        }
        data.adjustColors = adjustColors;

        EditPlanePropertiesDialog dialog = new EditPlanePropertiesDialog(getWindow().getShell(), data);
        dialog.setBounds(dialogBounds);
        int code = dialog.open();
        dialogBounds = dialog.getBounds();
        if (code == EditPlanePropertiesDialog.ID_OK) {
            data = dialog.getData();
            fractal.setIterMax(data.iterMax);
            fractal.setBailOut(data.bailOut);
            fractal.setJuliaMode(data.juliaMode);
            fractal.setJuliaCX(data.juliaCX);
            fractal.setJuliaCY(data.juliaCY);
            plane.setAccumulator(data.accumulator);
            plane.setIndexer(data.indexer);
            plane.setTrapMode(data.trapMode);
            plane.setDecompositionMode(data.decompositionMode);
            adjustColors = data.adjustColors;
            plane.setModified(true);
            plane.fireStateChange();
            view.generateImage(adjustColors, false);
        }
    }
}
