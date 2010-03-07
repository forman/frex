package z.frex.dialogs;

import z.StringLiterals;
import z.core.color.RGBA;
import z.ui.ColorSelector;
import z.ui.dialog.Dialog;
import z.ui.dialog.MessageDialog;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.BorderLayout;
import java.awt.Window;

public class SelectColorDialog extends Dialog {


    private final static String TITLE = StringLiterals.getString("gui.title.selectColor");

    private ColorSelector colorSelector;
    private RGBA selectedColor;


    public SelectColorDialog(Window parentShell, RGBA selectedColor) {
        super(parentShell);
        setSelectedColor(selectedColor);
    }

    public RGBA getSelectedColor() {
        return selectedColor;
    }

    public void setSelectedColor(RGBA selectedColor) {
        this.selectedColor = selectedColor;
    }

    @Override
    protected void configureShell(JDialog shell) {
        super.configureShell(shell);
        shell.setTitle(TITLE);
    }

    @Override
    protected JComponent createDialogArea() {
        colorSelector = new ColorSelector();
        colorSelector.setSelectedColor(selectedColor);
        colorSelector.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (colorSelector.isDoubleClicked()) {
                    okPressed();
                }
            }
        });
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(colorSelector), BorderLayout.CENTER);
        return panel;
    }

    @Override
    protected void okPressed() {
        selectedColor = colorSelector.getSelectedColor();
        if (selectedColor == null) {
            MessageDialog.openError(getShell(),
                                    TITLE,
                                    StringLiterals.getString("gui.msg.mustSelectColor"));
            return;
        }
        super.okPressed();
    }
}
