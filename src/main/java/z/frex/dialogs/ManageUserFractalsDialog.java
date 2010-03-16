package z.frex.dialogs;

import org.jdom.JDOMException;
import z.StringLiterals;
import z.math.ParseException;
import z.ui.dialog.Dialog;
import z.util.FractalDef;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;

public class ManageUserFractalsDialog extends Dialog {

    private ArrayList<FractalDef> fractals;
    private ArrayList<FractalDef> clonedFractals;

    private JButton addButton;
    private JButton removeButton;
    private JButton moveUpButton;
    private JButton moveDownButton;

    private JTable table;
    private ManageUserFractalsDialog.FractalTableModel tableModel;

    public ManageUserFractalsDialog(JFrame parentShell) {
        super(parentShell);

        FractalDef[] fractalDefs = load(parentShell);
        if (fractalDefs == null) {
            fractalDefs = new FractalDef[0];
        }

        this.fractals = new ArrayList<FractalDef>(fractalDefs.length);
        this.fractals.addAll(Arrays.asList(fractalDefs));

        this.clonedFractals = new ArrayList<FractalDef>(fractalDefs.length);
        this.clonedFractals.addAll(fractals);
        for (int i = 0; i < fractals.size(); i++) {
            clonedFractals.set(i, fractals.get(i).clone());
        }
    }

    public FractalDef[] getFractals() {
        return fractals.toArray(new FractalDef[fractals.size()]);
    }

    @Override
    protected void okPressed() {
        if (!tableModel.isModified()) {
            return;
        }

        // todo - compile in separate thread
        for (int i = 0; i < fractals.size(); i++) {
            FractalDef fractalDef = fractals.get(i);
            try {
                fractalDef.parse();
            } catch (ParseException e) {
                table.getSelectionModel().setSelectionInterval(i, i);
                showError(StringLiterals.getString("gui.title.errorInFormula"),
                          MessageFormat.format(StringLiterals.getString("gui.msg.errorInFormula"),
                                               fractalDef.getCode(),
                                               e.getLocalizedMessage())
                );
                return;
            }
        }

        save(getShell(), getFractals());

        try {
            FractalDef.buildAll();
        } catch (JDOMException e) {
            showError("JDOMException", e.getMessage());  // TODO i18n
        } catch (IOException e) {
            showError("IOException", e.getMessage());    // TODO i18n
        }

        super.okPressed();
    }

    private static FractalDef[] load(Component parentComponent) {
        FractalDef[] defs = new FractalDef[0];
        try {
            defs = FractalDef.loadMyFractals();
        } catch (JDOMException e) {
            JOptionPane.showMessageDialog(parentComponent, MessageFormat.format(StringLiterals.getString("gui.msg.invalidFileFormat"),
                                                                                FractalDef.MY_FRACTALS_FILE));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(parentComponent, MessageFormat.format(StringLiterals.getString("gui.msg.ioError"),
                                                                                FractalDef.MY_FRACTALS_FILE, e.getLocalizedMessage()));
        }
        return defs;
    }

    private static void save(Component parentComponent, FractalDef[] defs) {
        try {
            FractalDef.saveMyFractals(defs);
        } catch (JDOMException e) {
            JOptionPane.showMessageDialog(parentComponent, MessageFormat.format(StringLiterals.getString("gui.msg.invalidFileFormat"),
                                                                                FractalDef.MY_FRACTALS_FILE));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(parentComponent, MessageFormat.format(StringLiterals.getString("gui.msg.ioError"),
                                                                                FractalDef.MY_FRACTALS_FILE, e.getLocalizedMessage()));
        }
    }

    @Override
    protected void configureShell(JDialog newShell) {
        super.configureShell(newShell);
        newShell.setTitle(StringLiterals.getString("gui.title.myFractals"));
    }

    @Override
    protected JComponent createDialogArea() {

        tableModel = new FractalTableModel();
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setColumnSelectionAllowed(false);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setResizingAllowed(true);
        table.setSurrendersFocusOnKeystroke(true);
        table.setGridColor(Color.LIGHT_GRAY);
        table.getColumnModel().getColumn(0).setPreferredWidth(32);
        table.getColumnModel().getColumn(1).setPreferredWidth(200);
        table.getColumnModel().getColumn(2).setPreferredWidth(16);

        addButton = new JButton();
        addButton.setToolTipText(StringLiterals.getString("gui.action.tooltip.addFractal"));
        addButton.setIcon(new ImageIcon(getClass().getResource(StringLiterals.getString("gui.action.icon.addFractal"))));
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = tableModel.addRow();
                table.requestFocus();
                table.getSelectionModel().addSelectionInterval(selectedRow, selectedRow);
                table.editCellAt(selectedRow, 0);
            }
        });

        removeButton = new JButton();
        removeButton.setToolTipText(StringLiterals.getString("gui.action.tooltip.removeFractal"));
        removeButton.setIcon(new ImageIcon(getClass().getResource(StringLiterals.getString("gui.action.icon.removeFractal"))));
        removeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0 && selectedRow < table.getRowCount()) {
                    int i = JOptionPane.showConfirmDialog(getShell(), StringLiterals.getString("gui.msg.removeFractal"),
                                                          StringLiterals.getString("gui.title.removeFractal"), JOptionPane.YES_NO_OPTION);
                    if (i == JOptionPane.YES_OPTION) {
                        tableModel.removeRow(selectedRow);
                        table.getSelectionModel().addSelectionInterval(selectedRow, selectedRow);
                    }
                }
            }
        });

        moveUpButton = new JButton();
        moveUpButton.setToolTipText(StringLiterals.getString("gui.action.tooltip.moveUp"));
        moveUpButton.setIcon(new ImageIcon(getClass().getResource(StringLiterals.getString("gui.action.icon.moveUp"))));
        moveUpButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    selectedRow = tableModel.moveUp(selectedRow);
                    table.getSelectionModel().addSelectionInterval(selectedRow, selectedRow);
                }
            }
        });

        moveDownButton = new JButton();
        moveDownButton.setToolTipText(StringLiterals.getString("gui.action.tooltip.moveDown"));
        moveDownButton.setIcon(new ImageIcon(getClass().getResource(StringLiterals.getString("gui.action.icon.moveDown"))));
        moveDownButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    selectedRow = tableModel.moveDown(selectedRow);
                    table.getSelectionModel().addSelectionInterval(selectedRow, selectedRow);
                }
            }
        });

        JPanel buttonPanel = new JPanel(new GridLayout(-1, 1));
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(moveUpButton);
        buttonPanel.add(moveDownButton);

        JPanel eastPanel = new JPanel(new BorderLayout(2, 2));
        eastPanel.add(buttonPanel, BorderLayout.NORTH);

        JPanel contentPane = new JPanel(new BorderLayout(2, 2));
        contentPane.add(new JScrollPane(table), BorderLayout.CENTER);
        contentPane.add(eastPanel, BorderLayout.EAST);

        updateState();

        table.getModel().addTableModelListener(new TableModelListener() {
            public void tableChanged(TableModelEvent e) {
                updateState();
            }
        });
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                updateState();
            }
        });

        return contentPane;
    }

    private void updateState() {
        int count = table.getRowCount();
        removeButton.setEnabled(count > 0 && table.getSelectedRow() != -1);
        moveUpButton.setEnabled(count > 1 && table.getSelectedRow() > 0);
        moveDownButton.setEnabled(count > 1 && table.getSelectedRow() >= 0 && table.getSelectedRow() < fractals.size() - 1);
    }


    private class FractalTableModel extends AbstractTableModel {
        private boolean modified;

        public boolean isModified() {
            return modified;
        }

        @Override
        public int getRowCount() {
            return fractals.size();
        }

        @Override
        public int getColumnCount() {
            return 3;
        }

        @Override
        public String getColumnName(int columnIndex) {
            if (columnIndex == 0) {
                return StringLiterals.getString("gui.column.text.fractalName");
            } else if (columnIndex == 1) {
                return StringLiterals.getString("gui.column.text.fractalCode");
            } else if (columnIndex == 2) {
                return StringLiterals.getString("gui.column.text.fractalPert");
            } else {
                return null;
            }
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == 2) {
                return Boolean.class;
            }
            return String.class;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (columnIndex == 0) {
                return fractals.get(rowIndex).getName();
            } else if (columnIndex == 1) {
                return fractals.get(rowIndex).getCode();
            } else if (columnIndex == 2) {
                return fractals.get(rowIndex).isPerturbation();
            }
            return null;
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            if (columnIndex == 0) {
                fractals.get(rowIndex).setName((String) aValue);
            } else if (columnIndex == 1) {
                fractals.get(rowIndex).setCode((String) aValue);
            } else if (columnIndex == 2) {
                fractals.get(rowIndex).setPerturbation((Boolean) aValue);
            }
            modified = true;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return true;
        }

        public int addRow() {
            fractals.add(new FractalDef());
            fireTableDataChanged();
            return fractals.size() - 1;
        }

        public void removeRow(int rowIndex) {
            fractals.remove(rowIndex);
            fireTableDataChanged();
        }

        public int moveUp(int rowIndex) {
            if (rowIndex > 0) {
                FractalDef fractal = fractals.remove(rowIndex);
                fractals.add(rowIndex - 1, fractal);
                fireTableDataChanged();
                return rowIndex - 1;
            }
            return rowIndex;
        }

        public int moveDown(int rowIndex) {
            if (rowIndex < fractals.size() - 1) {
                FractalDef fractal = fractals.remove(rowIndex);
                fractals.add(rowIndex + 1, fractal);
                fireTableDataChanged();
                return rowIndex + 1;
            }
            return rowIndex;
        }
    }


}