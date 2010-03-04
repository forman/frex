package z.frex.actions;

import z.frex.dialogs.ManageUserFractalsDialog;
import z.frex.Frex;
import z.ui.application.ApplicationWindow;

import java.awt.Rectangle;

public class ManageUserFractalsAction extends ApplicationWindowAction {
    public static final String ID = "z.frex.actions.manageUserFractals"; //$NON-NLS-1$
    private Rectangle dialogBounds;

    public ManageUserFractalsAction(ApplicationWindow window) {
        super(window, ID);
        setText("Eigene Fractals...");
        setToolTipText("Eigene Fractals bearbeiten");
        setSmallIcon(Frex.getIcon("/icons/user.png"));//$NON-NLS-1$
    }

    @Override
    public void run() {
        ManageUserFractalsDialog dialog = new ManageUserFractalsDialog(getWindow().getShell());
        dialog.setBounds(dialogBounds);
        dialog.open();
        dialogBounds = dialog.getBounds();
    }
}