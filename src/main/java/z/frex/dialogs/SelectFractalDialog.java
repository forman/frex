package z.frex.dialogs;

import org.jdom.JDOMException;
import z.StringLiterals;
import z.core.AlgorithmDescriptor;
import z.core.AlgorithmRegistry;
import z.core.IFractal;
import z.ui.dialog.Dialog;
import z.ui.dialog.MessageDialog;
import z.util.FractalDef;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;

public class SelectFractalDialog extends Dialog {

    private static final String TITLE = StringLiterals.getString("gui.title.selectFractal");
    private JList fractalList;
    private IFractal selectedFractal;

    public SelectFractalDialog(JFrame frame) {
        super(frame);

    }

    public IFractal getSelectedFractal() {
        return selectedFractal;
    }

    @Override
    protected void configureShell(JDialog dialog) {
        super.configureShell(dialog);
        dialog.setTitle(TITLE);
    }

    @Override
    protected JComponent createDialogArea() {
        JPanel contentPane = new JPanel(new BorderLayout(2, 2));

        AlgorithmDescriptor[] algorithmDescriptors = AlgorithmRegistry.instance().getFractals().getAll();
        ArrayList<AlgorithmDescriptor> list = new ArrayList<AlgorithmDescriptor>();
        list.addAll(Arrays.asList(algorithmDescriptors));
        fractalList = new JList(list.toArray(new AlgorithmDescriptor[list.size()]));
        fractalList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        contentPane.add(new JLabel(StringLiterals.getString("gui.label.fractals")), BorderLayout.NORTH);
        contentPane.add(new JScrollPane(fractalList), BorderLayout.CENTER);

        fractalList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() > 1) {
                    okPressed();
                }
            }
        });
        return contentPane;
    }

    @Override
    protected void okPressed() {
        selectedFractal = null;
        AlgorithmDescriptor d = (AlgorithmDescriptor) fractalList.getSelectedValue();
        if (d != null) {
            try {
                selectedFractal = (IFractal) d.getAlgorithmClass().newInstance();
            } catch (Exception e) {
                selectedFractal = null;
                MessageDialog.openError(getShell(),
                                        TITLE,
                                        MessageFormat.format(StringLiterals.getString("gui.msg.failedToInstantiateFractal"), e.getLocalizedMessage()));
            }
        } else {
            MessageDialog.openError(getShell(),
                                    TITLE,
                                    StringLiterals.getString("gui.msg.mustSelectFractal"));
        }
        if (selectedFractal != null) {
            super.okPressed();
        }
    }

}
