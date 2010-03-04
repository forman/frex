package z.ui;

import z.util.ChangeEvent;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

public class SliderBar extends AbstractSliderBar {

    public SliderBar(SliderBarModel model) {
        this(model, 5);
    }

    public SliderBar(SliderBarModel model, int sliderSize) {
        super(model, sliderSize);
        setPreferredSize(new Dimension(320, 32));
    }

    @Override
    protected void paintSlideArea(Graphics2D gc, Rectangle slideArea) {
        gc.setColor(Color.WHITE);
        gc.fillRect(slideArea.x, slideArea.y, slideArea.width, 3);
        gc.setColor(getForeground());
        gc.drawRect(slideArea.x, slideArea.y, slideArea.width, 3);
    }

    @Override
    protected void modelChanged(ChangeEvent e) {
        repaint();
    }

    @Override
    protected void selectedSliderDoubleClicked(MouseEvent e) {
        repaint();
    }

    @Override
    protected void noSliderClicked(MouseEvent e) {
        repaint();
    }

    @Override
    protected void slideAreaChanged(Rectangle slideArea) {
        repaint();
    }

    @Override
    protected void selectedSliderDraggedOut(MouseEvent e) {
        repaint();
    }
}
