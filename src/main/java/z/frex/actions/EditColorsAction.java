package z.frex.actions;

import z.StringLiterals;
import z.frex.Frex;
import z.frex.dialogs.EditColorsDialog;
import z.ui.application.ApplicationWindow;

import java.awt.Rectangle;

public class EditColorsAction extends PlaneViewAction {
    public static final String ID = "z.frex.actions.editColors"; // NON-NLS
    private Rectangle dialogBounds;

    public EditColorsAction(ApplicationWindow window) {
        super(window, ID);
        setText(StringLiterals.getString("gui.action.text.colors"));
        setToolTipText(StringLiterals.getString("gui.action.tooltip.colors"));
        setSmallIcon(Frex.getIcon(StringLiterals.getString("gui.action.icon.colors")));
    }

    @Override
    public void run() {
        EditColorsDialog dialog = new EditColorsDialog(getPlaneView());
        dialog.setBounds(dialogBounds);
        dialog.open();
        dialogBounds = dialog.getBounds();
    }
}
