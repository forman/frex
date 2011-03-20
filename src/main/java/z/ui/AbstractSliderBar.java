package z.ui;

import z.core.color.RGBA;
import z.util.ChangeEvent;
import z.util.ChangeListener;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public abstract class AbstractSliderBar extends Control {
    protected SliderBarModel model;
    protected Shape sliderShape;
    protected int sliderSize;
    protected Rectangle slideArea;
    private ModelChangeHandler modelChangeHandler;

    protected AbstractSliderBar(SliderBarModel model, int sliderSize) {
        this.model = model;
        this.sliderSize = sliderSize;
        sliderShape = UIUtils.createSliderShape((float) sliderSize);
        slideArea = new Rectangle(0, 0, 1, 1);
        MouseHandler mouseHandler = new MouseHandler();
        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
        addComponentListener(new ResizeHandler());
        this.modelChangeHandler = new ModelChangeHandler();
        this.model.addChangeListener(modelChangeHandler);
        addPropertyChangeListener("model", // NON-NLS
                                  new PropertyChangeListener() {
                                      public void propertyChange(PropertyChangeEvent evt) {
                                          SliderBarModel oldModel = (SliderBarModel) evt.getOldValue();
                                          if (oldModel != null) {
                                              oldModel.removeChangeListener(modelChangeHandler);
                                          }
                                          SliderBarModel newModel = (SliderBarModel) evt.getNewValue();
                                          if (newModel != null) {
                                              newModel.addChangeListener(modelChangeHandler);
                                          }
                                      }
                                  });
        setPreferredSize(new Dimension(320, sliderSize * 2));
    }

    protected Rectangle getSlideArea() {
        return slideArea;
    }

    public int getSelectedIndex() {
        return getModel().getSelectedIndex();
    }

    public void setSelectedPointIndex(int selectedIndex) {
        getModel().setSelectedIndex(selectedIndex);
        repaint();
    }

    public RGBA getSelectedPointIndexColor() {
        int selectedIndex = getSelectedIndex();
        return selectedIndex != -1 ? getModel().getColor(selectedIndex) : null;
    }

    public void setSelectedIndexColor(RGBA selectedColor) {
        RGBA selectedIndexColor = getSelectedPointIndexColor();
        if (selectedIndexColor != null && !selectedColor.equals(selectedIndexColor)) {
            getModel().setColor(getSelectedIndex(), selectedColor);
            fireStateChange();
        }
    }

    protected int getSlideX(float position) {
        return slideArea.x + Math.round((float) slideArea.width * position);
    }

    protected int getSlideY() {
        return slideArea.y + slideArea.height;
    }

    public SliderBarModel getModel() {
        return model;
    }

    public void setModel(SliderBarModel model) {
        SliderBarModel oldModel = this.model;
        if (oldModel != model) {
            this.model = model;
            firePropertyChange("model", oldModel, this.model);  // NON-NLS
            repaint();
        }
    }

    public int findPointIndex(MouseEvent e) {
        return findPointIndex(e.getX(), e.getY());
    }

    private int findPointIndex(int x, int y) {
        int minDistSqr = 10 * 10;
        int index = -1;
        for (int i = 0; i < getModel().getColorPointCount(); i++) {
            float position = getModel().getPosition(i);
            int px = getSlideX(position);
            int py = getSlideY();
            int distSqr = (px - x) * (px - x) + (py - y) * (py - y);
            if (distSqr < minDistSqr) {
                minDistSqr = distSqr;
                index = i;
            }
        }
        return index;
    }

    public float getPosition(MouseEvent e) {
        float position = (float) (e.getX() - slideArea.x) / (float) slideArea.width;
        if (position < 0.0f) {
            position = 0.0f;
        } else if (position > 1.0f) {
            position = 1.0f;
        }
        return position;
    }


    @Override
    protected void paintComponent(Graphics2D gc) {

        paintSlideArea(gc, slideArea);

        gc.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

        SliderBarModel model = getModel();
        for (int i = 0; i < model.getColorPointCount(); i++) {
            float position = model.getPosition(i);
            final RGBA rgba = model.getColor(i);
            final Color color = new Color(rgba.getR(), rgba.getG(), rgba.getB());

            gc.translate(getSlideX(position), getSlideY());

            if (i == getSelectedIndex()) {
                gc.setColor(Color.BLUE);
                Rectangle bounds = sliderShape.getBounds();
                gc.fillRect(bounds.x, bounds.y + bounds.height + 2, bounds.width, 2);
            }

            gc.setColor(color);
            gc.fill(sliderShape);

            if (rgba.getMax() > 160) {
                gc.setColor(Color.BLACK);
            } else {
                gc.setColor(Color.WHITE);
            }
            gc.draw(sliderShape);

            gc.translate(-getSlideX(position), -getSlideY());
        }

        gc.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
    }

    protected abstract void paintSlideArea(Graphics2D gc, Rectangle slideArea);

    protected abstract void slideAreaChanged(Rectangle slideArea);

    protected abstract void selectedSliderDoubleClicked(MouseEvent e);

    protected abstract void selectedSliderDraggedOut(MouseEvent e);

    protected abstract void noSliderClicked(MouseEvent e);

    protected abstract void modelChanged(ChangeEvent e);

    protected class MouseHandler extends MouseAdapter {

        private boolean dragged = false;

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() > 1) {
                setSelectedPointIndex(findPointIndex(e));
                selectedSliderDoubleClicked(e);
            }
            dragged = false;
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (getSelectedIndex() != -1) {
                Rectangle clientArea = getClientArea();
                if (e.getY() > clientArea.y + clientArea.height + 10) {
                    selectedSliderDraggedOut(e);
                } else {
                    float position = getPosition(e);
                    getModel().setPosition(getSelectedIndex(), position);
                }
            }
            dragged = true;
        }

        @Override
        public void mousePressed(MouseEvent e) {
            int index = findPointIndex(e);
            if (index != -1) {
                setSelectedPointIndex(index);
            }
            dragged = false;
        }


        @Override
        public void mouseReleased(MouseEvent e) {
            if (dragged) {
                fireStateChange();
            } else {
                int index = findPointIndex(e);
                if (index == -1) {
                    noSliderClicked(e);
                }
            }
            dragged = false;
        }
    }

    protected class ResizeHandler extends ComponentAdapter {

        @Override
        public void componentResized(ComponentEvent e) {
            int sliderExtraSpace = (sliderSize + 1);
            int selectionIndicatorSpace = 3;
            Rectangle clientArea = getClientArea();
            slideArea.x = clientArea.x;
            slideArea.y = clientArea.y + sliderExtraSpace;
            slideArea.width = clientArea.width;
            slideArea.height = clientArea.height - 2 * sliderExtraSpace - selectionIndicatorSpace;
            slideAreaChanged(slideArea);
        }
    }

    private class ModelChangeHandler implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            modelChanged(e);
        }

    }
}
