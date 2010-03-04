package z.frex.actions;

import z.frex.dialogs.HistogramDialog;
import z.ui.application.ApplicationWindow;

public class EditIndexRangeAction extends PlaneViewAction {
    public static final String ID = "z.frex.actions.editIndexRange"; //$NON-NLS-1$

    private HistogramDialog histogramDialog;

    public EditIndexRangeAction(ApplicationWindow window) {
        super(window, EditIndexRangeAction.ID);
        setText("&Histogram");
        setToolTipText("Histogram anzeigen");
    }

    @Override
    public void run() {
//        if (histogramDialog == null) {
//            histogramDialog = new HistogramDialog(getWindow());
//            histogramDialog.pack();
//            UIUtils.centerComponent(histogramDialog);
//        }
//        histogramDialog.setVisible(true);
    }
}
