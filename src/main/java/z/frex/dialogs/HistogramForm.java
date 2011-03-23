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
import java.awt.Font;
import java.awt.FontMetrics;
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
        histogramCanvas.setBackground(Color.GRAY);
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
        float pos1 = (model.getCurrentPaletteColorTable().getIndexMin() - statistics.min) / (statistics.max - statistics.min);
        float pos2 = (model.getCurrentPaletteColorTable().getIndexMax() - statistics.min) / (statistics.max - statistics.min);
        pos1 = isInvalidPos(pos1) ? 0.0F : cropPos(pos1);
        pos2 = isInvalidPos(pos2) ? 1.0F : cropPos(pos2);
        ColorPoint[] colorPoints = {
                new ColorPoint(pos1, RGBA.ORANGE),
                new ColorPoint(pos2, RGBA.ORANGE),
        };
        sliderBar.getModel().setColorPoints(colorPoints);
        histogramCanvas.setStatistics(statistics);
    }

    private static boolean isInvalidPos(float pos) {
        return Float.isNaN(pos) || Float.isInfinite(pos);
    }


    private static float cropPos(float pos) {
        if (pos < 0.0F) {
            return 0.0F;
        }
        if (pos > 1.0F) {
            return 1.0F;
        }
        return pos;
    }

    private void updateModel() {
        PlaneRaster.Statistics statistics = model.getCurrentStatistics();
        float index1 = statistics.min + sliderBar.getModel().getPosition(0) * (statistics.max - statistics.min);
        float index2 = statistics.min + sliderBar.getModel().getPosition(1) * (statistics.max - statistics.min);
        model.getCurrentPaletteColorTable().setIndexMin(Math.min(index1, index2));
        model.getCurrentPaletteColorTable().setIndexMax(Math.max(index1, index2));
    }

    private static class HistogramCanvas extends Control {
        public static final Font FONT = new Font("Courier", Font.PLAIN, 10);
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
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                                 RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

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

            g2d.setFont(FONT);
            g2d.setColor(Color.WHITE);
            FontMetrics fontMetrics = g2d.getFontMetrics();

            if (statistics.count == 0) {
                String msg = "No samples.";
                g2d.drawString(msg,
                               clientArea.x + (clientArea.width - fontMetrics.stringWidth(msg)) / 2,
                               clientArea.y + (clientArea.height - fontMetrics.getHeight()) / 2);
            } else {
                g2d.drawString("Count: " + statistics.count, 4, 1 * fontMetrics.getHeight() + 4);
                g2d.drawString("Min:   " + statistics.min, 4, 2 * fontMetrics.getHeight() + 4);
                g2d.drawString("Max:   " + statistics.max, 4, 3 * fontMetrics.getHeight() + 4);
                g2d.drawString("Mean:  " + statistics.mean, 4, 4 * fontMetrics.getHeight() + 4);
            }

        }
    }
}
