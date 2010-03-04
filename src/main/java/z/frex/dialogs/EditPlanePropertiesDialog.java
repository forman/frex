package z.frex.dialogs;

import z.core.Algorithm;
import z.core.AlgorithmDescriptor;
import z.core.AlgorithmRegistry;
import z.core.IAccumulator;
import z.core.IIndexer;
import z.ui.dialog.Dialog;
import z.ui.dialog.MessageDialog;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class EditPlanePropertiesDialog extends Dialog {

    public static class Data {
        public AlgorithmDescriptor fractal;

        public int iterMax;

        public double bailOut;

        public IAccumulator accumulator;

        public IIndexer indexer;

        public boolean trapMode;

        public boolean decompositionMode;

        public boolean juliaMode;

        public double juliaCX;

        public double juliaCY;

        public boolean adjustColors;
    }

    private Data data;

    private JComboBox iterMax;

    private JTextField bailOut;

    private AlgorithmControls accumulator;

    private AlgorithmControls indexer;

    private JCheckBox orbitUsed;

    private JPanel orbitPanel;

    private JCheckBox trapMode;

    private JCheckBox decompositionMode;

    private JCheckBox juliaMode;

    private JTextField juliaCX;

    private JTextField juliaCY;

    private JPanel juliaPanel;

    private JCheckBox adjustColors;

    public EditPlanePropertiesDialog(JFrame parentShell, Data data) {
        super(parentShell);
        setData(data);
    }

    public Data getData() {
        return data;
    }

    private void setData(Data data) {
        this.data = new Data();

        this.data.fractal = data.fractal;

        this.data.iterMax = data.iterMax;
        this.data.bailOut = data.bailOut;

        this.data.trapMode = data.trapMode;
        this.data.accumulator = data.accumulator != null ? (IAccumulator) data.accumulator.clone()
                : null;
        this.data.indexer = data.indexer != null ? (IIndexer) data.indexer.clone()
                : null;

        this.data.decompositionMode = data.decompositionMode;
        this.data.juliaMode = data.juliaMode;
        this.data.juliaCX = data.juliaCX;
        this.data.juliaCY = data.juliaCY;
        this.data.adjustColors = data.adjustColors;
    }

    @Override
    protected void okPressed() {
        // todo: validate fields
        data.iterMax = Integer.parseInt(iterMax.getSelectedItem().toString());
        data.bailOut = Double.parseDouble(bailOut.getText());
        if (orbitUsed.isSelected()) {
            if (data.accumulator == null) {
                MessageDialog.openError(getShell(),
                                        "Akkumulator",
                                        "Bitte einen Akkumulator auswählen");
                return;
            }
            if (!data.accumulator.computesIndex() && data.indexer == null) {
                MessageDialog.openError(getShell(),
                                        "Indexer",
                                        "Bitte einen Indexer auswählen");
                return;
            }
            data.trapMode = trapMode.isSelected();
        } else {
            data.accumulator = null;
            data.indexer = null;
            data.trapMode = false;
        }
        data.decompositionMode = decompositionMode.isSelected();
        data.juliaMode = juliaMode.isSelected();
        data.juliaCX = Double.parseDouble(juliaCX.getText());
        data.juliaCY = Double.parseDouble(juliaCY.getText());
        data.adjustColors = adjustColors.isSelected();
        super.okPressed();
    }

    @Override
    protected void configureShell(JDialog newShell) {
        super.configureShell(newShell);
        newShell.setTitle("Ebenen-Eigenschaften");
    }

    @Override
    protected JComponent createDialogArea() {
        ActionListener actionListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateState();
            }
        };

        JPanel contentPane = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets.left = 2;
        gbc.insets.right = 2;
        gbc.insets.top = 2;
        gbc.insets.bottom = 2;

        gbc.gridy = 0;
        gbc.gridx = 0;
        contentPane.add(new JLabel("Fractal:"), gbc);

        JTextField fractalName = new JTextField(data.fractal.getName());
        fractalName.setEditable(false);
        gbc.gridx = 1;
        contentPane.add(fractalName, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        contentPane.add(new JLabel("Anzahl Iterationen:"), gbc);

        iterMax = new JComboBox(new String[]{"100", "250", "500", "750", "1000", "2500", "5000", "7500", "10000"});
        iterMax.setEditable(true);
        iterMax.setSelectedItem(String.valueOf(data.iterMax));
        iterMax.addActionListener(actionListener);
        gbc.gridx = 1;
        contentPane.add(iterMax, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        contentPane.add(new JLabel("Schwellwert:"), gbc);

        bailOut = new JTextField(8);
        bailOut.setText(String.valueOf(data.bailOut));
        bailOut.addActionListener(actionListener);
        gbc.gridx = 1;
        contentPane.add(bailOut, gbc);

        decompositionMode = new JCheckBox("Y-Dekomposition berechnen");
        decompositionMode.setSelected(data.decompositionMode);
        gbc.gridwidth = 2;
        gbc.gridy++;
        gbc.gridx = 0;
        contentPane.add(decompositionMode, gbc);

        juliaMode = new JCheckBox("Juliamenge berechnen");
        juliaMode.setSelected(data.juliaMode);
        juliaMode.addActionListener(actionListener);
        gbc.gridy++;
        gbc.gridx = 0;
        contentPane.add(juliaMode, gbc);

        juliaPanel = createJuliaPanel();
        gbc.gridy++;
        gbc.gridx = 0;
        contentPane.add(juliaPanel, gbc);

        orbitUsed = new JCheckBox("Orbit-Algorithmen benutzen");
        orbitUsed.setSelected(data.accumulator != null);
        orbitUsed.addActionListener(actionListener);
        gbc.gridy++;
        gbc.gridx = 0;
        contentPane.add(orbitUsed, gbc);

        orbitPanel = createOrbitPanel();
        gbc.gridy++;
        gbc.gridx = 0;
        contentPane.add(orbitPanel, gbc);

        adjustColors = new JCheckBox("Farbbereich anpassen");
        adjustColors.setSelected(data.adjustColors);
        adjustColors.addActionListener(actionListener);
        gbc.gridy++;
        gbc.gridx = 0;
        contentPane.add(adjustColors, gbc);

        updateState();
        return contentPane;
    }

    private JPanel createOrbitPanel() {
        final JPanel orbitPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets.left = 2;
        gbc.insets.right = 2;
        gbc.insets.top = 2;
        gbc.insets.bottom = 2;

        gbc.gridy = -1;
        gbc.gridx = 0;

        final ActionListener accumulatorHandler = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                EditAlgorithmDialog d = new EditAlgorithmDialog(getShell(),
                                                                (Algorithm) data.accumulator);
                if (d.open() == ID_OK) {
                    data.accumulator = (IAccumulator) d.getAlgorithm();
                }
            }
        };
        accumulator = createAlgorithmControls(orbitPanel,
                                              gbc, "Akkumulator",
                                              AlgorithmRegistry.instance().getAccumulators().getAll(),
                                              (Algorithm) data.accumulator,
                                              accumulatorHandler);
        accumulator.combo.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                updateState();
            }
        });


        final ActionListener indexerHandler = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                EditAlgorithmDialog d = new EditAlgorithmDialog(getShell(),
                                                                (z.core.Algorithm) data.indexer);
                if (d.open() == ID_OK) {
                    data.indexer = (IIndexer) d.getAlgorithm();
                }
            }
        };
        indexer = createAlgorithmControls(orbitPanel,
                                          gbc, "Indexer",
                                          AlgorithmRegistry.instance().getIndexers().getAll(),
                                          (Algorithm) data.indexer,
                                          indexerHandler);

        trapMode = new JCheckBox("Orbitfalle einsetzen");
        trapMode.setSelected(data.trapMode);
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 3;
        orbitPanel.add(trapMode, gbc);

        return orbitPanel;
    }

    private JPanel createJuliaPanel() {
        final JPanel juliaPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets.left = 2;
        gbc.insets.right = 2;
        gbc.insets.top = 2;
        gbc.insets.bottom = 2;

        gbc.gridy = 0;
        gbc.gridx = 0;
        juliaPanel.add(new JLabel("Julia CX:"), gbc);

        juliaCX = new JTextField(20);
        juliaCX.setText(String.valueOf(data.juliaCX));
        gbc.gridx = 1;
        juliaPanel.add(juliaCX, gbc);

        gbc.gridy = 1;
        gbc.gridx = 0;
        juliaPanel.add(new JLabel("Julia CY:"), gbc);

        juliaCY = new JTextField(20);
        juliaCY.setText(String.valueOf(data.juliaCY));
        gbc.gridx = 1;
        juliaPanel.add(juliaCY, gbc);

        return juliaPanel;
    }

    private AlgorithmControls createAlgorithmControls(final JPanel orbitPanel,
                                                      GridBagConstraints gbc, final String labelText,
                                                      final AlgorithmDescriptor[] algorithmDescriptors,
                                                      final z.core.Algorithm algorithm,
                                                      final ActionListener editSelectionListener) {
        gbc.gridy++;

        gbc.gridx = 0;
        JLabel label = new JLabel(labelText + ':');
        orbitPanel.add(label, gbc);

        JComboBox combo = new JComboBox(algorithmDescriptors);
        combo.setEditable(false);
        final AlgorithmDescriptor selection;
        if (algorithm != null) {
            selection = new AlgorithmDescriptor(algorithm.getClass());
        } else {
            selection = algorithmDescriptors[0];
        }
        combo.setSelectedItem(selection);
        combo.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                updateState();
            }
        });
        gbc.gridx = 1;
        orbitPanel.add(combo, gbc);

        JButton button = new JButton("...");
        button.setToolTipText(labelText + " Parameter bearbeiten");
        button.addActionListener(editSelectionListener);
        gbc.gridx = 2;
        orbitPanel.add(button, gbc);

        return new AlgorithmControls(label, combo, button);
    }

    private void setAccumulator(AlgorithmDescriptor ad) {
        if (data.accumulator == null
                || !data.accumulator.getClass().equals(ad.getAlgorithmClass())) {
            try {
                data.accumulator = (IAccumulator) ad.getAlgorithmClass().newInstance();
                data.accumulator.reset();
            } catch (Exception e) {
                MessageDialog.openError(getShell(),
                                        "Accumulator",
                                        "Fehlerhafter Accumulator.\nMeldung: "
                                                + e.getLocalizedMessage());
            }
        }
    }

    private void setIndexer(AlgorithmDescriptor ad) {
        if (data.indexer == null
                || !data.indexer.getClass()
                .equals(ad.getAlgorithmClass())) {
            try {
                data.indexer = (IIndexer) ad.getAlgorithmClass().newInstance();
                data.indexer.reset();
            } catch (Exception e) {
                MessageDialog.openError(getShell(),
                                        "Indexer",
                                        "Fehlerhafter Indexer.\nMeldung: "
                                                + e.getLocalizedMessage());
            }
        }
    }

    private void updateState() {
        setEnabled(orbitPanel, orbitUsed.isSelected());
        if (orbitUsed.isSelected()) {
            setAccumulator((AlgorithmDescriptor) accumulator.combo.getSelectedItem());
            if (data.accumulator != null) {
                if (data.accumulator.computesIndex()) {
                    indexer.label.setEnabled(false);
                    indexer.combo.setEnabled(false);
                    indexer.button.setEnabled(false);
                } else {
                    setIndexer((AlgorithmDescriptor) indexer.combo.getSelectedItem());
                }
                trapMode.setEnabled(data.accumulator.canTrap());
            } else {
                indexer.label.setEnabled(false);
                indexer.combo.setEnabled(false);
                indexer.button.setEnabled(false);
                trapMode.setSelected(false);
                trapMode.setEnabled(false);
            }
        }
        setEnabled(juliaPanel, juliaMode.isSelected());
    }

    private static void setEnabled(final Container composite,
                                   final boolean enabled) {
        final Component[] children = composite.getComponents();
        for (Component child : children) {
            if (child instanceof Container) {
                Container childComposite = (Container) child;
                setEnabled(childComposite, enabled);
            } else {
                child.setEnabled(enabled);
            }
        }
        composite.setEnabled(enabled);
    }

    private static class AlgorithmControls {
        JLabel label;
        JComboBox combo;
        JButton button;

        public AlgorithmControls(JLabel label, JComboBox combo, JButton button) {
            this.label = label;
            this.combo = combo;
            this.button = button;
        }
    }
}
