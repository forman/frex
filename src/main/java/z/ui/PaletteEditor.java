package z.ui;

import z.core.color.ColorInterpolator;
import z.core.color.RGBA;
import z.frex.dialogs.SelectColorDialog;
import z.util.ChangeEvent;

import javax.swing.SwingUtilities;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class PaletteEditor extends AbstractSliderBar {

    private BufferedImage paletteImage;

    public PaletteEditor(SliderBarModel model) {
        this(model, 5);
    }

    public PaletteEditor(SliderBarModel model, int sliderSize) {
        super(model, sliderSize);
        setPreferredSize(new Dimension(360, 36));
    }

    @Override
    public void dispose() {
        paletteImage = null;
        super.dispose();
    }

    @Override
    protected void modelChanged(ChangeEvent e) {
        updatePaletteImage();
    }

    @Override
    protected void paintSlideArea(Graphics2D gc, Rectangle slideArea) {
        if (paletteImage != null) {
            gc.drawImage(paletteImage, null, slideArea.x, slideArea.y);
        }
        gc.setColor(getBackground());
        gc.draw(slideArea);
    }

    @Override
    protected void selectedSliderDoubleClicked(MouseEvent e) {
        if (getSelectedIndex() != -1) {
            final SelectColorDialog colorDialog = new SelectColorDialog(SwingUtilities.getWindowAncestor(e.getComponent()),
                                                                        getSelectedPointIndexColor());
            if (colorDialog.open() == SelectColorDialog.ID_OK) {
                setSelectedIndexColor(colorDialog.getSelectedColor());
            }
        }
    }

    @Override
    protected void noSliderClicked(MouseEvent e) {
        int newIndex = getModel().getColorPointCount();
        getModel().addColorPoint(getPosition(e), RGBA.WHITE);
        setSelectedPointIndex(newIndex);
    }

    @Override
    protected void slideAreaChanged(Rectangle slideArea) {
        updatePaletteImage();
    }

    @Override
    protected void selectedSliderDraggedOut(MouseEvent e) {
        getModel().removeColorPoint(getSelectedIndex());
        setSelectedPointIndex(-1);
    }

    private void updatePaletteImage() {
        if (!slideArea.isEmpty()) {
            if (paletteImage == null
                    || paletteImage.getWidth() != slideArea.width
                    || paletteImage.getHeight() != slideArea.height) {
                paletteImage = new BufferedImage(slideArea.width, slideArea.height, BufferedImage.TYPE_INT_RGB);
            }
            RGBA[] colors = ColorInterpolator.createColors(getModel().getColorPoints(), paletteImage.getWidth(), null);
            Graphics2D paletteGC = paletteImage.createGraphics();
            try {
                for (int i = 0; i < colors.length; i++) {
                    Color color = new Color(
                            colors[i].getR(),
                            colors[i].getG(),
                            colors[i].getB());
                    paletteGC.setColor(color);
                    paletteGC.drawLine(i, 0, i, paletteImage.getHeight());
                }
            } finally {
                paletteGC.dispose();
            }
        } else {
            paletteImage = null;
        }
        repaint();
    }

}
