package z.frex.actions;

import z.StringLiterals;
import z.frex.Frex;
import z.frex.dialogs.ManageUserFractalsDialog;
import z.ui.application.ApplicationWindow;

import java.awt.Rectangle;

public class ManageUserFractalsAction extends ApplicationWindowAction {
    public static final String ID = "z.frex.actions.manageUserFractals"; // NON-NLS
    private Rectangle dialogBounds;

    public ManageUserFractalsAction(ApplicationWindow window) {
        super(window, ID);
        setText(StringLiterals.getString("gui.action.text.myFractals"));
        setToolTipText(StringLiterals.getString("gui.action.tooltip.myFractals"));
        setSmallIcon(Frex.getIcon(StringLiterals.getString("gui.action.icon.myFractals")));
    }

    @Override
    public void run() {
        ManageUserFractalsDialog dialog = new ManageUserFractalsDialog(getWindow().getShell());
        dialog.setBounds(dialogBounds);
        dialog.open();
        dialogBounds = dialog.getBounds();
    }
}