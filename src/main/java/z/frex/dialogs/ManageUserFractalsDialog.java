package z.frex.dialogs;

import org.jdom.JDOMException;
import z.math.ParseException;
import z.ui.dialog.Dialog;
import z.util.FileUtils;
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
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;

public class ManageUserFractalsDialog extends Dialog {

    private ArrayList<FractalDef> fractals;
    private ManageUserFractalsDialog.FractalTableModel tableModel;
    private JButton addButton;
    private JButton removeButton;
    private JButton moveUpButton;
    private JButton moveDownButton;
    private JTable table;

    public ManageUserFractalsDialog(JFrame parentShell) {
        super(parentShell);
        File fractalsFile = FractalDef.DEFAULT_USER_FRACTALS_FILE;
        if (fractalsFile.exists()) {
            setFractals(load(parentShell, fractalsFile));
        } else {
            setFractals(new FractalDef[0]);
        }
    }

    public FractalDef[] getFractals() {
        return fractals.toArray(new FractalDef[fractals.size()]);
    }

    public void setFractals(FractalDef[] fractals) {
        this.fractals = new ArrayList<FractalDef>(fractals.length);
        this.fractals.addAll(Arrays.asList(fractals));
    }

    @Override
    protected void okPressed() {

        FractalDef[] fractalDefs = getFractals();
        for (int i = 0; i < fractalDefs.length; i++) {
            FractalDef fractalDef = fractalDefs[i];
            try {
                fractalDef.parse();
            } catch (ParseException e) {
                table.getSelectionModel().setSelectionInterval(i, i);
                showError("Fehler in Formel",
                          MessageFormat.format("Fehler in Formel ''{0}'':\n{1}",
                                               fractalDef.getCode(),
                                               e.getLocalizedMessage())
                );
                return;
            }
        }

        File frexUserDir = FileUtils.getFrexUserDir();
        if (!frexUserDir.exists()) {
            frexUserDir.mkdir();
        }
        save(getShell(), new File(frexUserDir, FractalDef.MY_FRACTALS_XML), getFractals());
        super.okPressed();
    }


    private void showError(String title, String message) {
        JOptionPane.showMessageDialog(getShell(),
                                      message,
                                      title,
                                      JOptionPane.ERROR_MESSAGE);
    }


    private static FractalDef[] load(Component parentComponent, File fractalsFile) {
        FractalDef[] defs = new FractalDef[0];
        try {
            defs = FractalDef.loadFractals(fractalsFile);
        } catch (JDOMException e) {
            JOptionPane.showMessageDialog(parentComponent, fractalsFile + ":\nUngültiges Dateiformat.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(parentComponent, fractalsFile + ":\n" + e.getLocalizedMessage());
        }
        return defs;
    }

    private static void save(Component parentComponent, File fractalsFile, FractalDef[] fractalDefs) {
        try {
            FractalDef.saveFractals(fractalsFile, fractalDefs);
        } catch (JDOMException e) {
            JOptionPane.showMessageDialog(parentComponent, fractalsFile + ":\nUngültiges Dateiformat.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(parentComponent, fractalsFile + ":\n" + e.getLocalizedMessage());
        }
    }

    @Override
    protected void configureShell(JDialog newShell) {
        super.configureShell(newShell);
        newShell.setTitle("Eigene Fractals");
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

        addButton = new JButton();
        addButton.setToolTipText("Hinzufügen");
        addButton.setIcon(new ImageIcon(getClass().getResource("/icons/add.png")));
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = tableModel.addRow();
                table.requestFocus();
                table.getSelectionModel().addSelectionInterval(selectedRow, selectedRow);
                table.editCellAt(selectedRow, 0);
            }
        });

        removeButton = new JButton();
        removeButton.setToolTipText("Entfernen");
        removeButton.setIcon(new ImageIcon(getClass().getResource("/icons/delete.png")));
        removeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0 && selectedRow < table.getRowCount()) {
                    int i = JOptionPane.showConfirmDialog(getShell(), "Entfernen?", "Fractal Entfernen", JOptionPane.YES_NO_OPTION);
                    if (i == JOptionPane.YES_OPTION) {
                        tableModel.removeRow(selectedRow);
                        table.getSelectionModel().addSelectionInterval(selectedRow, selectedRow);
                    }
                }
            }
        });

        moveUpButton = new JButton();
        moveUpButton.setToolTipText("Nach oben");
        moveUpButton.setIcon(new ImageIcon(getClass().getResource("/icons/arrow_up.png")));
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
        moveDownButton.setToolTipText("Nach unten");
        moveDownButton.setIcon(new ImageIcon(getClass().getResource("/icons/arrow_down.png")));
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
        @Override
        public int getRowCount() {
            return fractals.size();
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public String getColumnName(int columnIndex) {
            if (columnIndex == 0) {
                return "Fractal Name";
            } else {
                return "Fractal Formel f(z,c) = ?";
            }
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return String.class;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (columnIndex == 0) {
                return fractals.get(rowIndex).getName();
            } else {
                return fractals.get(rowIndex).getCode();
            }
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            if (columnIndex == 0) {
                fractals.get(rowIndex).setName((String) aValue);
            } else {
                fractals.get(rowIndex).setCode((String) aValue);
            }
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