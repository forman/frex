package z.frex.dialogs;

import z.StringLiterals;
import z.core.Algorithm;
import z.frex.Frex;
import z.ui.dialog.Dialog;
import z.ui.dialog.MessageDialog;
import z.util.GreyscaleImage;
import z.util.Property;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
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
        shell.setTitle(StringLiterals.getString("gui.title.algorithm.parameter"));
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
                String pattern = StringLiterals.getString("gui.msg.cannotReadField");
                MessageDialog.showError(getShell(), StringLiterals.getString("gui.title.error"),
                                        MessageFormat.format(pattern, property.getName(), e.getClass().getName(), e.getLocalizedMessage()));
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
        } else if (property.getType() == GreyscaleImage.class) {
            GreyscaleImage image = (GreyscaleImage) property.getGetter().invoke(algorithm);
            component = new GreyscaleImagePanel(image);
        } else {
            component = new JLabel("?");
        }

        gbc.gridx = 0;
        dialogArea.add(new JLabel(property.getLabel() + ':'), gbc);

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
                String pattern = StringLiterals.getString("gui.msg.cannotSetField");
                MessageDialog.showError(getShell(),
                                        StringLiterals.getString("gui.title.error"),
                                        MessageFormat.format(pattern, property.getName(), e.getClass().getName(), e.getLocalizedMessage()));
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
        } else if (property.getType() == GreyscaleImage.class) {
            final GreyscaleImagePanel panel = (GreyscaleImagePanel) controls.get(property);
            value = panel.getImage();
        }
        if (value != null) {
            property.getSetter().invoke(algorithm, value);
        }
    }


    public class GreyscaleImagePanel extends JPanel {
         GreyscaleImage image;

        public GreyscaleImagePanel(GreyscaleImage image) {
            super(new BorderLayout(2,2));
            this.image = image;
            final JTextField filenameField = new JTextField();
            filenameField.setEditable(false);
            filenameField.setColumns(24);
            filenameField.setText(image.getSourceFile() != null ? image.getSourceFile().getName() : "");
            add(filenameField);
            final JButton button = new JButton("...");
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String lastDir = Frex.getPreferences().get("lastImageDir", "."); // NON-NLS
                    JFileChooser dialog = new JFileChooser(lastDir);
                    dialog.setDialogTitle(StringLiterals.getString("gui.title.open"));
                    dialog.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    dialog.setAcceptAllFileFilterUsed(true);
                    FileNameExtensionFilter imageFilter = new FileNameExtensionFilter("Image files",
                                                                                 "jpg",
                                                                                 "png",
                                                                                 "gif",
                                                                                 "tiff",
                                                                                 "bmp");
                    dialog.addChoosableFileFilter(imageFilter);
                    dialog.setFileFilter(imageFilter);
                    int resp = dialog.showOpenDialog(EditAlgorithmDialog.this.getShell());

                    if (resp == JFileChooser.APPROVE_OPTION) {
                        Frex.getPreferences().put("lasImagetDir", dialog.getCurrentDirectory().getPath()); // NON-NLS
                        File selectedFile = dialog.getSelectedFile();
                        try {
                            filenameField.setText("[Loading...]");
                            GreyscaleImage image = GreyscaleImage.create(selectedFile);
                            setImage(image);
                            filenameField.setText(image.getSourceFile().getName());
                        } catch (IOException e1) {
                           MessageDialog.showError(getShell(),
                                                   StringLiterals.getString("gui.title.error"),
                                                   "Failed to load image.");
                        }
                    }
                }
            });
            add(filenameField, BorderLayout.CENTER);
            add(button, BorderLayout.EAST);
        }

        public GreyscaleImage getImage() {
            return image;
        }

        public void setImage(GreyscaleImage image) {
            this.image = image;
        }
    }

}
