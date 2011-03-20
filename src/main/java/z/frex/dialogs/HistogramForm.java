package z.frex.dialogs;

import z.core.PlaneRaster;
import z.core.color.ColorPoint;
import z.core.color.RGBA;
import z.frex.PlaneView;
import z.ui.Control;
import z.ui.DefaultSliderBarModel;
import z.ui.SliderBar;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class HistogramForm {
    private final EditColorsModel model;
    private HistogramCanvas histogramCanvas;
    private SliderBar sliderBar;
    private JPanel panel;

    public HistogramForm(EditColorsModel model) {
        this.model = model;
    }

    public JPanel getPanel() {
        if (panel != null) {
            return panel;
        }

        final PlaneView view = model.getView();

        histogramCanvas = new HistogramCanvas();
        histogramCanvas.setBackground(Color.LIGHT_GRAY);
        histogramCanvas.setForeground(Color.DARK_GRAY);
        histogramCanvas.setStatistics(view.getPlane().getRaster().getTotalStatistics());

        DefaultSliderBarModel sliderBarModel = new DefaultSliderBarModel();
        sliderBar = new SliderBar(sliderBarModel);
        sliderBar.setBorder(new EmptyBorder(5, 5, 5, 5));
        sliderBar.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                updateModel();
            }
        });

        panel = new JPanel(new BorderLayout(2, 2));
        panel.add(histogramCanvas, BorderLayout.CENTER);
        panel.add(sliderBar, BorderLayout.SOUTH);

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
        PlaneRaster.Statistics statistics = model.getCurrentStatistics();
        float position1 = (model.getCurrentPaletteColorTable().getIndexMin() - statistics.min) / (statistics.max - statistics.min);
        float position2 = (model.getCurrentPaletteColorTable().getIndexMax() - statistics.min) / (statistics.max - statistics.min);
        System.out.println("position1 = " + position1);  // NON-NLS
        System.out.println("position2 = " + position2);  // NON-NLS
        position1 = Double.isNaN(position1) ? 0 : cropPosition(position1);
        position2 = Double.isNaN(position2) ? 1 : cropPosition(position2);
        ColorPoint[] colorPoints = {
                new ColorPoint(position1, RGBA.WHITE),
                new ColorPoint(position2, RGBA.WHITE),
        };
        sliderBar.getModel().setColorPoints(colorPoints);
        histogramCanvas.setStatistics(statistics);
    }

    private void updateModel() {
        PlaneRaster.Statistics statistics = model.getCurrentStatistics();
        float position1 = statistics.min + sliderBar.getModel().getPosition(0) * (statistics.max - statistics.min);
        float position2 = statistics.min + sliderBar.getModel().getPosition(1) * (statistics.max - statistics.min);
        model.getCurrentPaletteColorTable().setIndexMin(Math.min(position1, position2));
        model.getCurrentPaletteColorTable().setIndexMax(Math.max(position1, position2));
    }

    private float cropPosition(float position) {
        if (position < 0) {
            position = 0;
        }
        if (position > 1) {
            position = 1;
        }
        return position;
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

            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                 RenderingHints.VALUE_ANTIALIAS_ON);

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
