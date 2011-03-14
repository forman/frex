package z.frex.dialogs;

import z.StringLiterals;
import z.core.PlaneRaster;
import z.core.color.ColorPoint;
import z.core.color.RGBA;
import z.frex.PlaneView;
import z.ui.Control;
import z.ui.DefaultSliderBarModel;
import z.ui.SliderBar;

import javax.swing.ButtonGroup;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicBorders;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Rectangle;

public class HistogramDialog extends JDialog {
    private EditColorsDialog dialog;
    private PlaneView view;
    private HistogramCanvas histogramCanvas;
    private SliderBar sliderBar;
    private PlaneRaster.Statistics statistics;

    public HistogramDialog(EditColorsDialog dialog) {
        super(dialog.getShell(), ModalityType.MODELESS);

        this.dialog = dialog;

        setTitle(StringLiterals.getString("gui.title.histo"));
        setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);

        view = dialog.getView();
        statistics = view.getPlane().getRaster().getTotalStatistics();

        JPanel histogramEditor = new JPanel(new BorderLayout(2,2));

        histogramCanvas = new HistogramCanvas();
        histogramCanvas.setBackground(new Color(230, 230, 230));
        histogramCanvas.setStatistics(statistics);
        histogramEditor.add(histogramCanvas, BorderLayout.CENTER);
        float position1 = (dialog.getIndexMin() - statistics.min) / (statistics.max - statistics.min);
        float position2 = (dialog.getIndexMax() - statistics.min) / (statistics.max - statistics.min);
        System.out.println("position1 = " + position1);  // NON-NLS
        System.out.println("position2 = " + position2);  // NON-NLS
        position1 = Double.isNaN(position1) ? 0 : crop(position1);
        position2 = Double.isNaN(position2) ? 1 : crop(position2);
        DefaultSliderBarModel model = new DefaultSliderBarModel(new ColorPoint[]{
                new ColorPoint(position1, RGBA.WHITE),
                new ColorPoint(position2, RGBA.WHITE),
        });
        sliderBar = new SliderBar(model);
        sliderBar.setBorder(new EmptyBorder(5, 5, 5, 5));
        sliderBar.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                float position1 = statistics.min + sliderBar.getModel().getPosition(0) * (statistics.max - statistics.min);
                float position2 = statistics.min + sliderBar.getModel().getPosition(1) * (statistics.max - statistics.min);
                HistogramDialog.this.dialog.setIndexMinMax(
                        Math.min(position1, position2),
                        Math.max(position1, position2)
                );
            }
        });
        histogramEditor.add(sliderBar, BorderLayout.SOUTH);


        JRadioButton rb1 = new JRadioButton("All", true);
        JRadioButton rb2 = new JRadioButton("Inner", false);
        JRadioButton rb3 = new JRadioButton("Outer", false);

        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(rb1);
        buttonGroup.add(rb2);
        buttonGroup.add(rb3);

        JPanel statSelector = new JPanel(new GridLayout(3, 1));
        statSelector.add(rb1);
        statSelector.add(rb2);
        statSelector.add(rb3);

        JPanel jPanel1 = new JPanel(new BorderLayout(2,2));
        jPanel1.add(statSelector, BorderLayout.NORTH);

        JPanel jPanel2 = new JPanel(new BorderLayout(2,2));
        jPanel2.add(histogramEditor, BorderLayout.CENTER);
        jPanel2.add(jPanel1, BorderLayout.EAST);

        setContentPane(jPanel2);
    }

    private float crop(float position1) {
        if (position1 < 0) {
            position1 = 0;
        }
        if (position1 > 1) {
            position1 = 1;
        }
        return position1;
    }

    private static class HistogramCanvas extends Control {
        PlaneRaster.Statistics statistics;

        public HistogramCanvas() {
            setPreferredSize(new Dimension(200, 200));
        }

        public void setStatistics(PlaneRaster.Statistics statistics) {
            this.statistics = statistics;
            invalidate();
            validate();
            repaint();
        }


        @Override
        protected void paintComponent(Graphics2D g2d) {

            if (statistics == null) {
                return;
            }

            Rectangle clientArea = getClientArea();
            clientArea.grow(-4, -4);
            g2d.setColor(getBackground());
            g2d.fill(clientArea);
            g2d.setColor(getForeground());

            float sx = (float) clientArea.width / (float) statistics.histogram.length;
            float sy = statistics.histogramMax > 0 ? (float) clientArea.height / (float) statistics.histogramMax : 0.0f;
            int y1 = clientArea.y + clientArea.height;
            for (int i = 0; i < statistics.histogram.length; i++) {
                int x = clientArea.x + (int) (sx * i);
                int v = statistics.histogram[i];
                int h = (int) (sy * v);
                int y2 = y1 - h;
                // System.out.println("v=" + v + ", sy="+sy+", y2="+y2);
//                g2d.fillRect(x-1, y2, 3, h);
                g2d.drawLine(x, y1, x, y2);
            }
        }
    }
}
