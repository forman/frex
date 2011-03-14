package z.ui.dialog;

import javax.swing.JOptionPane;
import java.awt.Component;
import java.awt.Window;

public class MessageDialog {
    public static void openInfo(Component parent, String title, String message) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showError(Component parent, String title, String message) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.ERROR_MESSAGE);
    }

    public static void openWarning(Component parent, String title, String message) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.WARNING_MESSAGE);
    }

    public static int confirmYesNo(Window shell, String title, String message) {
        return JOptionPane.showConfirmDialog(shell,
                                             message,
                                             title,
                                             JOptionPane.YES_NO_OPTION,
                                             JOptionPane.QUESTION_MESSAGE);
    }

    public static int confirmYesNoCancel(Window shell, String title, String message) {
        return JOptionPane.showConfirmDialog(shell,
                                             message,
                                             title,
                                             JOptionPane.YES_NO_CANCEL_OPTION,
                                             JOptionPane.QUESTION_MESSAGE);
    }
}
