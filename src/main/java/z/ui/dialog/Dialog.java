package z.ui.dialog;

import z.StringLiterals;
import z.ui.UIUtils;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public abstract class Dialog {
    public static final int ID_UNKNOWN = -1;
    public static final int ID_OK = JOptionPane.OK_OPTION;
    public static final int ID_YES = JOptionPane.YES_OPTION;
    public static final int ID_NO = JOptionPane.NO_OPTION;
    public static final int ID_CANCEL = JOptionPane.CANCEL_OPTION;
    public static final int ID_CLIENT = 1000;

    private Window frame;
    private JDialog shell;
    private int buttonId;
    private JButton defaultButton;
    private JPanel buttonBar;
    private Rectangle bounds;

    protected Dialog(Window frame) {
        this.frame = frame;
    }

    public Window getFrame() {
        return frame;
    }

    public JDialog getShell() {
        return shell;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void setBounds(Rectangle bounds) {
        this.bounds = bounds;
    }

    protected void configureShell(JDialog shell) {
    }

    protected abstract JComponent createDialogArea();

    protected void createButtonsForButtonBar() {
        createButton(Dialog.ID_OK, StringLiterals.getString("gui.ok"), true);
        createButton(Dialog.ID_CANCEL, StringLiterals.getString("gui.cancel"), false);
    }

    protected void createButton(int code, String text, boolean isDefault) {
        JButton button = UIUtils.createButton(text);
        button.addActionListener(new ButtonActionListener(code));
        if (isDefault) {
            defaultButton = button;
        }
        buttonBar.add(button);
    }

    protected void buttonPressed(int buttonId) {
        setButtonId(buttonId);
        if (buttonId == ID_OK) {
            okPressed();
        } else if (buttonId == ID_CANCEL) {
            cancelPressed();
        }
    }

    protected void okPressed() {
        setButtonId(ID_OK);
        close();
    }

    protected void cancelPressed() {
        setButtonId(ID_CANCEL);
        close();
    }


    public int open() {
        shell = new JDialog(frame, JDialog.ModalityType.APPLICATION_MODAL);
        shell.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        shell.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                buttonPressed(ID_CANCEL);
            }
        });
        shell.getContentPane().setLayout(new BorderLayout(4, 4));

        buttonBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 2, 2));
        createButtonsForButtonBar();

        JComponent dialogArea = createDialogArea();

        JPanel contentPane = new JPanel(new BorderLayout(4, 4));
        contentPane.setBorder(new EmptyBorder(4, 4, 4, 4));
        contentPane.add(dialogArea, BorderLayout.CENTER);
        contentPane.add(buttonBar, BorderLayout.SOUTH);
        shell.setContentPane(contentPane);

        if (defaultButton != null) {
            shell.getRootPane().setDefaultButton(defaultButton);
        }

        configureShell(shell);

        shell.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                dialogOpened();
            }

            @Override
            public void windowClosing(WindowEvent e) {
                bounds = shell.getBounds();
                dialogClosing();
            }

            @Override
            public void windowClosed(WindowEvent e) {
                bounds = shell.getBounds();
                dialogClosed();
            }

            @Override
            public void windowActivated(WindowEvent e) {
                dialogActivated();
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
                dialogDeactivated();
            }
        });

        if (bounds != null) {
            shell.setBounds(bounds);
        } else {
            shell.pack();
            UIUtils.centerComponent(shell);
        }
        buttonId = ID_UNKNOWN;
        shell.setVisible(true);
        return buttonId;
    }

    public void dialogOpened() {
    }

    public void dialogClosing() {
    }

    public void dialogClosed() {
    }

    public void dialogActivated() {
    }

    public void dialogDeactivated() {
    }

    protected boolean close() {
        shell.dispose();
        return true;
    }

    private void setButtonId(int buttonId) {
        this.buttonId = buttonId;
    }

    protected void showError(String title, String message) {
        JOptionPane.showMessageDialog(getShell(),
                                      message,
                                      title,
                                      JOptionPane.ERROR_MESSAGE);
    }


    private class ButtonActionListener implements ActionListener {
        private final int buttonId;

        public ButtonActionListener(int buttonId) {
            this.buttonId = buttonId;
        }

        public void actionPerformed(ActionEvent e) {
            buttonPressed(buttonId);
        }
    }
}
