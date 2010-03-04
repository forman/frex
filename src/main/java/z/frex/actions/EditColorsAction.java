package z.frex.actions;

import z.frex.Frex;
import z.frex.dialogs.EditColorsDialog;
import z.ui.application.ApplicationWindow;

import java.awt.Rectangle;

public class EditColorsAction extends PlaneViewAction {
    public static final String ID = "z.frex.actions.editColors"; //$NON-NLS-1$
    private Rectangle dialogBounds;

    public EditColorsAction(ApplicationWindow window) {
        super(window, ID);
        setText("Farben...");
        setToolTipText("Farben bearbeiten");
        setSmallIcon(Frex.getIcon("/icons/color_wheel.png"));//$NON-NLS-1$
    }

    @Override
    public void run() {

        EditColorsDialog dialog = new EditColorsDialog(getPlaneView());
        dialog.setBounds(dialogBounds);
        dialog.open();
        dialogBounds = dialog.getBounds();
    }
}
