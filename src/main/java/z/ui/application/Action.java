package z.ui.application;

import z.ui.UIUtils;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import java.awt.event.ActionEvent;

public abstract class Action extends AbstractAction {
    /**
     * Defines an <code>Action</code> object with the specified ID.
     */
    protected Action(String id) {
        setId(id);
    }

    protected void setId(String id) {
        putValue(ACTION_COMMAND_KEY, id);
    }

    public String getId() {
        Object value = getValue(ACTION_COMMAND_KEY);
        return value != null ? value.toString() : null;
    }

    public boolean isSelectable() {
        return getValue(SELECTED_KEY) != null;
    }

    public boolean isSelected() {
        return Boolean.TRUE.equals(getValue(SELECTED_KEY));
    }

    public void setSelected(boolean selected) {
        putValue(AbstractAction.SELECTED_KEY, selected);
    }

    public void setToolTipText(String s) {
        putValue(AbstractAction.SHORT_DESCRIPTION, s);
    }

    public void setText(String s) {
        UIUtils.MenuText menuText = UIUtils.MenuText.create(s);
        putValue(NAME, menuText.getText());
        if (menuText.getMnemonic() != -1) {
            putValue(MNEMONIC_KEY, menuText.getMnemonic());
        }
    }

    public void setSmallIcon(ImageIcon icon) {
        putValue(AbstractAction.SMALL_ICON, icon);
    }

    public final void actionPerformed(ActionEvent e) {
//        if (isSelectable()) {
//            boolean selected = isSelected();
//            System.out.println("pre: selected = " + selected);
//            setSelected(!selected);
//            System.out.println("post: selected = " + isSelected());
//        }
        System.out.printf("%s: selected = %s%n", this, isSelected());  // NON-NLS
        run();
    }

    public void run() {
    }

    public AbstractButton createToolBarButton() {
        final AbstractButton b;
        if (isSelectable()) {
            b = new JToggleButton();
        } else {
            b = new JButton();
        }
        if (getValue(Action.SMALL_ICON) != null || getValue(Action.LARGE_ICON_KEY) != null) {
            b.setHideActionText(true);
        }
        b.setHorizontalTextPosition(SwingConstants.CENTER);
        b.setVerticalTextPosition(SwingConstants.BOTTOM);
        b.setAction(this);
        return b;
    }


    public AbstractButton createMenuItem() {
        final AbstractButton b;
        if (isSelectable()) {
            b = new JCheckBoxMenuItem();
        } else {
            b = new JMenuItem();
        }
        b.setHorizontalTextPosition(JButton.TRAILING);
        b.setVerticalTextPosition(JButton.CENTER);
        b.setAction(this);
        return b;
    }
}
