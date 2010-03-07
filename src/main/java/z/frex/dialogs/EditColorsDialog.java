package z.frex.dialogs;

import z.StringLiterals;
import z.core.IColorizer;
import z.core.color.RGBA;
import z.core.support.colorizers.PaletteColorTable;
import z.frex.Frex;
import z.frex.PlaneView;
import z.ui.ColorBar;
import z.ui.DefaultSliderBarModel;
import z.ui.PaletteEditor;
import z.ui.SliderBarModel;
import z.ui.UIUtils;
import z.ui.dialog.Dialog;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.util.prefs.Preferences;

public class EditColorsDialog extends Dialog {

    private PlaneView view;

    private IColorizer originalColorizer;

    private PaletteEditor paletteEditor;

    private ColorBar colorBar;

    private JComboBox numColors;

    private JCheckBox cyclic;

    private JComboBox indexMin;

    private JComboBox indexMax;

    private PaletteColorTable paletteColorTable;
    private static final String[] VALUE_SET = new String[]{
            "0.0",
            "0.1", "0.25", "0.5", "0.75",
            "1.0", "2.5", "5.0", "7.5",
            "10.0", "25.0", "50.0", "75.0",
            "100.0", "250.0", "500.0", "750.0",
            "1000.0", "2500.0", "5000.0", "7500.0",
            "10000.0", "25000.0", "50000.0", "75000.0"};

    public EditColorsDialog(PlaneView view) {
        super(view.getPage().getWindow().getShell());

        this.view = view;
        originalColorizer = view.getPlane().getColorizer();

        if (originalColorizer instanceof PaletteColorTable) {
            paletteColorTable = (PaletteColorTable) originalColorizer.clone();
        } else {
            paletteColorTable = new PaletteColorTable();
            paletteColorTable.reset();
        }
        paletteColorTable.prepare();
    }

    public PlaneView getView() {
        return view;
    }

    @Override
    protected void createButtonsForButtonBar() {
        createButton(ID_CLIENT + 1, StringLiterals.getString("gui.action.text.histo"), false);
        createButton(ID_OK, StringLiterals.getString("gui.ok"), false);
        createButton(ID_CANCEL, StringLiterals.getString("gui.cancel"), false);
    }

    @Override
    protected void configureShell(JDialog newShell) {
        super.configureShell(newShell);
        newShell.setTitle(StringLiterals.getString("gui.title.editColors"));
    }

    HistogramDialog histogramDialog;

    @Override
    protected void buttonPressed(int buttonId) {
        if (buttonId == ID_CLIENT + 1) {
            if (histogramDialog == null) {
                histogramDialog = new HistogramDialog(this);
                histogramDialog.pack();
                UIUtils.centerComponent(histogramDialog);
            }
            histogramDialog.setVisible(true);
        } else {
            super.buttonPressed(buttonId);
        }
    }

    @Override
    protected void okPressed() {
        // todo: validate fields
        updatePaletteColorTable();
        super.okPressed();
        applyPaletteColorTable();
        storeColorBarColors();
        view.getPlane().setModified(true);
        view.getPlane().fireStateChange();
    }

    @Override
    protected void cancelPressed() {
        super.cancelPressed();
        if (view.getPlane().getColorizer() != originalColorizer) {
            view.getPlane().setColorizer(originalColorizer);
            view.generateImage(true);
        }
    }

    protected void apply() {
        updatePaletteColorTable();
        applyPaletteColorTable();
    }

    public float getIndexMin() {
        return Float.parseFloat(indexMin.getSelectedItem().toString());
    }

    public float getIndexMax() {
        return Float.parseFloat(indexMax.getSelectedItem().toString());
    }

    public void setIndexMinMax(float min, float max) {
        indexMin.setSelectedItem(min + "");
        indexMax.setSelectedItem(max + "");
    }

    private void updatePaletteColorTable() {
        paletteColorTable.setCyclic(cyclic.isSelected());
        paletteColorTable.setNumColors(Integer.parseInt(numColors.getSelectedItem().toString()));
        paletteColorTable.setIndexMin(getIndexMin());
        paletteColorTable.setIndexMax(getIndexMax());
        paletteColorTable.setColorPoints(paletteEditor.getModel().getColorPoints());
    }

    private void applyPaletteColorTable() {
        view.getPlane().setColorizer(paletteColorTable);
        view.generateImage(true);
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

    private static Preferences getDialogSettingsSection() {
        final Preferences dialogSettings = Frex.getDialogSettings();
        return dialogSettings.node(EditColorsDialog.class.getName());
    }

    private static String getColorSettingsKey(int i) {
        return "colors." + i; // NON-NLS
    }

    @Override
    protected JComponent createDialogArea() {
        JPanel dialogArea = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = createGridBagConstraints();


        JPanel numColorsPanel = createNumColorsPanel();
        gbc.gridy = 0;
        gbc.gridx = 0;
        dialogArea.add(numColorsPanel, gbc);

        JPanel indexTransformPanel = createIndexTransformPanel();
        gbc.gridx = 1;
        dialogArea.add(indexTransformPanel, gbc);

        final SliderBarModel paletteModel = new DefaultSliderBarModel(paletteColorTable.getColorPoints());
        paletteEditor = new PaletteEditor(paletteModel);
        paletteEditor.setBorder(new EmptyBorder(2, 5, 2, 5));
        paletteEditor.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                apply();
            }
        });
        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        dialogArea.add(paletteEditor, gbc);

        colorBar = new ColorBar(2, 26, 14);
        colorBar.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                paletteEditor.setSelectedIndexColor(colorBar.getSelectedColor());
                apply();
            }
        });
        gbc.gridy = 2;
        dialogArea.add(colorBar, gbc);

        loadColorBarColors();

        return dialogArea;
    }

    private JPanel createNumColorsPanel() {
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
        numColors.setSelectedItem(String.valueOf(paletteColorTable.getNumColors()));
        numColors.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                apply();
            }
        });
        gbc.gridx = 1;
        numColorsPanel.add(numColors, gbc);

        cyclic = new JCheckBox(StringLiterals.getString("gui.label.cycleColors"));
        cyclic.setSelected(paletteColorTable.isCyclic());
        cyclic.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                apply();
            }
        });
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        numColorsPanel.add(cyclic, gbc);


        return numColorsPanel;
    }

    private JPanel createIndexTransformPanel() {
        JPanel indexTransformPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = createGridBagConstraints();

        gbc.gridy = 0;
        gbc.gridx = 0;
        indexTransformPanel.add(new JLabel(StringLiterals.getString("gui.label.minimum")), gbc);

        indexMin = new JComboBox(VALUE_SET);
        indexMin.setEditable(true);
        indexMin.setSelectedItem(String.valueOf(paletteColorTable.getIndexMin()));
        indexMin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                apply();
            }
        });
        gbc.gridx = 1;
        indexTransformPanel.add(indexMin, gbc);


        gbc.gridy++;
        gbc.gridx = 0;
        indexTransformPanel.add(new JLabel(StringLiterals.getString("gui.label.maximum")), gbc);
        indexMax = new JComboBox(VALUE_SET);
        indexMax.setEditable(true);
        indexMax.setSelectedItem(String.valueOf(paletteColorTable.getIndexMax()));
        indexMax.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                apply();
            }
        });
        gbc.gridx = 1;
        indexTransformPanel.add(indexMax, gbc);

        return indexTransformPanel;
    }


    private static GridBagConstraints createGridBagConstraints() {
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