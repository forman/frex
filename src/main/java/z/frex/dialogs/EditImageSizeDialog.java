package z.frex.dialogs;

import z.ui.dialog.Dialog;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EditImageSizeDialog extends Dialog {

    public static class Data {
        public boolean usingWindowSize;

        public int imageWidth;

        public int imageHeight;
    }

    private Data data;

    private JCheckBox usingWindowSize;

    private JComboBox imageWidth;

    private JComboBox imageHeight;

    public EditImageSizeDialog(JFrame parentShell, Data data) {
        super(parentShell);
        setData(data);
    }

    public Data getData() {
        return data;
    }

    private void setData(Data data) {
        this.data = new Data();
        this.data.usingWindowSize = data.usingWindowSize;
        this.data.imageWidth = data.imageWidth;
        this.data.imageHeight = data.imageHeight;
    }

    private void updateState() {
        imageWidth.setEnabled(!usingWindowSize.isSelected());
        imageHeight.setEnabled(!usingWindowSize.isSelected());
    }

    @Override
    protected void configureShell(JDialog shell) {
        super.configureShell(shell);
        shell.setTitle("Bildgröße");
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

        usingWindowSize = new JCheckBox();
        usingWindowSize.setText("Bildgröße an Fenster anpassen");
        usingWindowSize.setSelected(data.usingWindowSize);
        usingWindowSize.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                updateState();
            }
        });

        gbc.gridwidth = 3;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets.bottom = 10;
        dialogArea.add(usingWindowSize, gbc);
        gbc.insets.bottom = 2;
        gbc.gridwidth = 1;
        gbc.gridy++;

        JLabel widthLabel = new JLabel();
        widthLabel.setText("Breite: ");
        dialogArea.add(widthLabel, gbc);
        gbc.gridx++;

        imageWidth = new JComboBox(createProposals());
        imageWidth.setEditable(true);
        imageWidth.setSelectedItem(String.valueOf(data.imageWidth));
        imageWidth.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateState();
            }
        });
        dialogArea.add(imageWidth, gbc);
        gbc.gridx++;

        JLabel pixelLabel1 = new JLabel();
        pixelLabel1.setText("Pixel");
        dialogArea.add(pixelLabel1, gbc);
        gbc.gridx = 0;
        gbc.gridy++;

        JLabel heightLabel = new JLabel();
        heightLabel.setText("Höhe: ");
        dialogArea.add(heightLabel, gbc);
        gbc.gridx++;

        imageHeight = new JComboBox(createProposals());
        imageHeight.setEditable(true);
        imageHeight.setSelectedItem(String.valueOf(data.imageHeight));
        imageHeight.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateState();
            }
        });
        dialogArea.add(imageHeight, gbc);
        gbc.gridx++;

        JLabel pixelLabel2 = new JLabel();
        pixelLabel2.setText("Pixel");
        dialogArea.add(pixelLabel2, gbc);

        updateState();
        return dialogArea;
    }

    private static String[] createProposals() {
        return new String[]{"128", "256", "480", "512", "640",
                "1024", "1280", "1536", "2048", "2560", "3072", "4096", "5120", "6144", "7168", "8192"};
    }

    @Override
    protected void okPressed() {
        // todo: validate fields
        data.usingWindowSize = usingWindowSize.isSelected();
        data.imageWidth = Integer.parseInt(imageWidth.getSelectedItem().toString());
        data.imageHeight = Integer.parseInt(imageHeight.getSelectedItem().toString());
        super.okPressed();
    }
}
