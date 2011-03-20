package z.frex.dialogs;

import z.StringLiterals;
import z.core.color.ColorPoint;
import z.core.color.RGBA;
import z.frex.Frex;
import z.ui.ColorBar;
import z.ui.DefaultSliderBarModel;
import z.ui.PaletteEditor;
import z.ui.SliderBarModel;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.ParseException;
import java.util.prefs.Preferences;

public class EditColorsForm {

    static final String[] VALUE_SET = new String[]{
            "0.0",
            "0.1", "0.25", "0.5", "0.75",
            "1.0", "2.5", "5.0", "7.5",
            "10.0", "25.0", "50.0", "75.0",
            "100.0", "250.0", "500.0", "750.0",
            "1000.0", "2500.0", "5000.0", "7500.0",
            "10000.0", "25000.0", "50000.0", "75000.0"};

    private final EditColorsModel model;

    private PaletteEditor paletteEditor;
    private ColorBar colorBar;
    private JComboBox numColors;
    private JCheckBox cyclic;
    private JComboBox indexMin;
    private JComboBox indexMax;
    private JPanel panel;

    public EditColorsForm(EditColorsModel model) {
        this.model = model;
    }

    public JPanel getPanel() {
        if (panel != null) {
            return panel;
        }
        panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = EditColorsForm.createGridBagConstraints();

        JPanel numColorsPanel = createNumColorsPanel();
        gbc.gridy = 0;
        gbc.gridx = 0;
        panel.add(numColorsPanel, gbc);

        JPanel indexTransformPanel = createIndexTransformPanel();
        gbc.gridx = 1;
        panel.add(indexTransformPanel, gbc);

        final SliderBarModel paletteModel = new DefaultSliderBarModel(model.getCurrentPaletteColorTable().getColorPoints());
        paletteEditor = new PaletteEditor(paletteModel);
        paletteEditor.setBorder(new EmptyBorder(2, 5, 2, 5));
        paletteEditor.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                ColorPoint[] value = paletteEditor.getModel().getColorPoints();
                model.getCurrentPaletteColorTable().setColorPoints(value);
            }
        });
        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        panel.add(paletteEditor, gbc);

        colorBar = new ColorBar(2, 26, 14);
        colorBar.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                paletteEditor.setSelectedIndexColor(colorBar.getSelectedColor());
            }
        });
        gbc.gridy = 2;
        panel.add(colorBar, gbc);

        loadColorBarColors();

        updateUI();

        model.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                updateUI();
            }
        });

        return panel;
    }

    public void updateUI() {
        cyclic.setSelected(model.getCurrentPaletteColorTable().isCyclic());
        numColors.setSelectedItem(String.valueOf(model.getCurrentPaletteColorTable().getNumColors()));
        paletteEditor.getModel().setColorPoints(model.getCurrentPaletteColorTable().getColorPoints());
        indexMin.setSelectedItem(String.valueOf(model.getCurrentPaletteColorTable().getIndexMin()));
        indexMax.setSelectedItem(String.valueOf(model.getCurrentPaletteColorTable().getIndexMax()));
    }

    public void loadColorBarColors() {
        final Preferences section = getDialogSettingsSection();
        final RGBA[] colors = colorBar.getColors();
        for (int i = 0; i < colors.length; i++) {
            final String key = getColorSettingsKey(i);
            final String value = section.get(key, colors[i].toString());
            if (value != null) {
                try {
                    colors[i] = RGBA.parseRGBA(value);
                } catch (ParseException e) {
                    // todo: handle exception here...
                    e.printStackTrace();
                }
            }
        }
        colorBar.setColors(colors);
    }

    public void storeColorBarColors() {
        final Preferences section = getDialogSettingsSection();
        final RGBA[] colors = colorBar.getColors();
        for (int i = 0; i < colors.length; i++) {
            final String key = getColorSettingsKey(i);
            final String value = colors[i].toString();
            section.put(key, value);
        }
    }

    static Preferences getDialogSettingsSection() {
        final Preferences dialogSettings = Frex.getDialogSettings();
        return dialogSettings.node(EditColorsDialog.class.getName());
    }

    static String getColorSettingsKey(int i) {
        return "colors." + i; // NON-NLS
    }

    JPanel createNumColorsPanel() {
        JPanel numColorsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = createGridBagConstraints();

        gbc.gridy = 0;
        gbc.gridx = 0;
        numColorsPanel.add(new JLabel(StringLiterals.getString("gui.label.numColors")), gbc);

        numColors = new JComboBox(new String[]{
                "2", "4", "8", "16", "32", "64", // NON-NLS
                "128", "256", "512",   // NON-NLS
                "1024", "2048", "4096", "8192" // NON-NLS
        });
        numColors.setEditable(true);
        numColors.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int value = Integer.parseInt(numColors.getSelectedItem().toString());
                model.getCurrentPaletteColorTable().setNumColors(value);
            }
        });
        gbc.gridx = 1;
        numColorsPanel.add(numColors, gbc);

        cyclic = new JCheckBox(StringLiterals.getString("gui.label.cycleColors"));
        cyclic.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean value = cyclic.isSelected();
                model.getCurrentPaletteColorTable().setCyclic(value);
            }
        });
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        numColorsPanel.add(cyclic, gbc);

        return numColorsPanel;
    }

    JPanel createIndexTransformPanel() {
        JPanel indexTransformPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = createGridBagConstraints();

        gbc.gridy = 0;
        gbc.gridx = 0;
        indexTransformPanel.add(new JLabel(StringLiterals.getString("gui.label.minimum")), gbc);

        indexMin = new JComboBox(VALUE_SET);
        indexMin.setEditable(true);
        indexMin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                float value = Float.parseFloat(indexMin.getSelectedItem().toString());
                model.getCurrentPaletteColorTable().setIndexMin(value);
            }
        });
        gbc.gridx = 1;
        indexTransformPanel.add(indexMin, gbc);


        gbc.gridy++;
        gbc.gridx = 0;
        indexTransformPanel.add(new JLabel(StringLiterals.getString("gui.label.maximum")), gbc);
        indexMax = new JComboBox(VALUE_SET);
        indexMax.setEditable(true);
        indexMax.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                float value = Float.parseFloat(indexMax.getSelectedItem().toString());
                model.getCurrentPaletteColorTable().setIndexMax(value);
            }
        });
        gbc.gridx = 1;
        indexTransformPanel.add(indexMax, gbc);

        return indexTransformPanel;
    }

    static GridBagConstraints createGridBagConstraints() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets.left = 2;
        gbc.insets.right = 2;
        gbc.insets.top = 2;
        gbc.insets.bottom = 2;
        return gbc;
    }


}