package z.frex.dialogs;

import z.StringLiterals;
import z.frex.Frex;
import z.frex.PlaneView;
import z.frex.actions.OpenColorsAction;
import z.frex.actions.PlaneViewAction;
import z.frex.actions.SafeColorsAction;
import z.ui.application.ApplicationWindow;
import z.ui.dialog.Dialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class EditColorsDialog extends Dialog {

    private final PlaneView view;
    private final EditColorsModel editColorsModel;
    private final EditColorsForm editColorsForm;
    private final HistogramForm histogramForm;
    private JPanel dialogArea;

    public EditColorsDialog(PlaneView view) {
        super(view.getPage().getWindow().getShell());
        this.view = view;
        editColorsModel = new EditColorsModel(view);
        editColorsForm = new EditColorsForm(editColorsModel);
        histogramForm = new HistogramForm(editColorsModel);
    }

    public EditColorsForm getEditColorsForm() {
        return editColorsForm;
    }

    public EditColorsModel getEditColorsModel() {
        return editColorsModel;
    }

    @Override
    protected void createButtonsForButtonBar() {
        createButton(ID_OK, StringLiterals.getString("gui.ok"), false);
        createButton(ID_CANCEL, StringLiterals.getString("gui.cancel"), false);
    }

    @Override
    protected void configureShell(JDialog newShell) {
        super.configureShell(newShell);
        newShell.setTitle(StringLiterals.getString("gui.title.editColors"));
    }

    @Override
    protected void okPressed() {
        // todo: validate fields
        super.okPressed();
        editColorsForm.storeColorBarColors();
        editColorsModel.apply(true);
    }


    @Override
    protected void cancelPressed() {
        super.cancelPressed();
        editColorsModel.restore();
    }

    public PlaneView getView() {
        return view;
    }

    @Override
    protected JComponent createDialogArea() {

        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.add(new OpenColorsAction(view, this));
        toolBar.add(new SafeColorsAction(view, this));
        toolBar.addSeparator();
        toolBar.add(new SwitchHistogramViewAction(view.getPage().getWindow()).createToolBarButton());

        final JRadioButton rb1 = new JRadioButton(StringLiterals.getString("gui.action.allRegions.text"), !view.getPlane().isInnerOuterDisjoined());
        final JRadioButton rb2 = new JRadioButton(StringLiterals.getString("gui.action.innerRegions.text"), view.getPlane().isInnerOuterDisjoined());
        final JRadioButton rb3 = new JRadioButton(StringLiterals.getString("gui.action.outerRegions.text"), false);
        ActionListener rbListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EditColorsModel.EditedRegion editedRegion;
                if (rb1.isSelected()) {
                    editedRegion = EditColorsModel.EditedRegion.ALL;
                } else if (rb2.isSelected()) {
                    editedRegion = EditColorsModel.EditedRegion.INNER;
                } else {
                    editedRegion = EditColorsModel.EditedRegion.OUTER;
                }
                editColorsModel.setEditedRegion(editedRegion);
            }
        };
        rb1.addActionListener(rbListener);
        rb2.addActionListener(rbListener);
        rb3.addActionListener(rbListener);

        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(rb1);
        buttonGroup.add(rb2);
        buttonGroup.add(rb3);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 2));
        buttonPanel.add(rb1);
        buttonPanel.add(rb2);
        buttonPanel.add(rb3);

        toolBar.addSeparator();
        toolBar.add(buttonPanel);

        dialogArea = new JPanel(new BorderLayout(4, 4));
        dialogArea.add(toolBar, BorderLayout.NORTH);

        showEditColorsForm();

        editColorsModel.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                editColorsModel.apply(false);
            }
        });

        return dialogArea;
    }

    private void showHistogramForm() {
        editColorsForm.getPanel().setVisible(false);
        histogramForm.getPanel().setVisible(true);
        dialogArea.remove(editColorsForm.getPanel());
        dialogArea.add(histogramForm.getPanel(), BorderLayout.CENTER);
    }

    private void showEditColorsForm() {
        editColorsForm.getPanel().setVisible(true);
        histogramForm.getPanel().setVisible(false);
        dialogArea.remove(histogramForm.getPanel());
        dialogArea.add(editColorsForm.getPanel(), BorderLayout.CENTER);
    }

    public class SwitchHistogramViewAction extends PlaneViewAction {
        public static final String ID = "z.frex.actions.switchHistogramView"; // NON-NLS

        public SwitchHistogramViewAction(ApplicationWindow window) {
            super(window, ID);
            setSelected(false);
            setText(StringLiterals.getString("gui.action.text.histogram"));
            setToolTipText(StringLiterals.getString("gui.action.tooltip.histogram"));
            setSmallIcon(Frex.getIcon(StringLiterals.getString("gui.action.icon.histogram")));
        }

        @Override
        public void run() {
            if (isSelected()) {
                showHistogramForm();
            } else {
                showEditColorsForm();
            }
        }
    }

}