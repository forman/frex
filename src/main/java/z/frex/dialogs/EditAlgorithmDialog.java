package z.frex.dialogs;

import z.core.Algorithm;
import z.ui.dialog.Dialog;
import z.ui.dialog.MessageDialog;
import z.util.Property;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class EditAlgorithmDialog extends Dialog {

    private Algorithm algorithm;

    private HashMap<Property, JComponent> controls;

    public EditAlgorithmDialog(JDialog parentShell, Algorithm algorithm) {
        super(parentShell);
        this.algorithm = (Algorithm) algorithm.clone();
        this.controls = new HashMap<Property, JComponent>(8);
    }

    public Algorithm getAlgorithm() {
        return algorithm;
    }

    @Override
    public boolean close() {
        controls.clear();
        return super.close();
    }

    @Override
    protected void configureShell(JDialog shell) {
        super.configureShell(shell);
        shell.setTitle("Algorithmus Parameter");
    }

    @Override
    protected JComponent createDialogArea() {
        JPanel dialogArea = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets.left = 2;
        gbc.insets.right = 2;
        gbc.insets.top = 2;
        gbc.insets.bottom = 2;

        gbc.gridy = 0;

        final Property[] properties = algorithm.getProperties();
        for (Property property : properties) {
            try {
                addPropertyToDialogArea(dialogArea, gbc, property);
            } catch (Exception e) {
                e.printStackTrace();
                MessageDialog.openError(getShell(), "Fehler", "Feld '"
                        + property.getName()
                        + "' konnte nicht gelesen werden.\n"
                        + e.getClass().getName() + ",\nMeldung: "
                        + e.getLocalizedMessage());
                close();
                return dialogArea;
            }
            gbc.gridy++;
        }

        return dialogArea;
    }

    private void addPropertyToDialogArea(JPanel dialogArea, GridBagConstraints gbc, Property property) throws IllegalAccessException,
            InvocationTargetException {
        JComponent component;
        if (property.getType() == Boolean.TYPE) {
            JCheckBox button = new JCheckBox("");
            boolean value = (Boolean) property.getGetter().invoke(algorithm);
            button.setSelected(value);
            component = button;
        } else if (property.getType() == Integer.TYPE
                || property.getType() == Float.TYPE
                || property.getType() == Double.TYPE
                || property.getType() == String.class) {
            JTextField text = new JTextField(12);
            Object object = property.getGetter().invoke(algorithm);
            text.setText(object.toString());
            component = text;
        } else {
            component = new JLabel("?");
        }

        gbc.gridx = 0;
        dialogArea.add(new JLabel(property.getName() + ':'), gbc);

        gbc.gridx = 1;
        dialogArea.add(component, gbc);
        controls.put(property, component);
    }

    @Override
    protected void okPressed() {
        // todo: validate fields
        final Property[] properties = algorithm.getProperties();
        for (final Property property : properties) {
            try {
                assignField(property);
            } catch (Exception e) {
                e.printStackTrace();
                MessageDialog.openError(getShell(), "Fehler", "Feld '"
                        + property.getName()
                        + "' konnte nicht gesetzt werden.\n"
                        + e.getClass().getName() + ",\nMeldung: "
                        + e.getLocalizedMessage());
                return;
            }
        }
        super.okPressed();
    }

    @SuppressWarnings("boxing")
    private void assignField(Property property) throws IllegalAccessException,
            InvocationTargetException {
        Object value = null;
        if (property.getType() == Boolean.TYPE) {
            final JCheckBox button = (JCheckBox) controls.get(property);
            value = button.isSelected();
        } else if (property.getType() == Integer.TYPE) {
            final JTextField text = (JTextField) controls.get(property);
            value = Integer.parseInt(text.getText());
        } else if (property.getType() == Float.TYPE) {
            final JTextField text = (JTextField) controls.get(property);
            value = Float.parseFloat(text.getText());
        } else if (property.getType() == Double.TYPE) {
            final JTextField text = (JTextField) controls.get(property);
            value = Double.parseDouble(text.getText());
        } else if (property.getType() == String.class) {
            final JTextField text = (JTextField) controls.get(property);
            value = text.getText();
        }
        if (value != null) {
            property.getSetter().invoke(algorithm, value);
        }
    }
}
