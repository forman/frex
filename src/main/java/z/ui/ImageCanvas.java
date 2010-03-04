package z.ui;

import javax.swing.JViewport;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class ImageCanvas extends Control implements Scrollable {
    private int maxUnitIncrement = 1;
    private BufferedImage image;

    public ImageCanvas() {
        setOpaque(false);
        setForeground(Color.WHITE);
        setBackground(Color.DARK_GRAY);
    }

    public int getMaxUnitIncrement() {
        return maxUnitIncrement;
    }

    public void setMaxUnitIncrement(int pixels) {
        maxUnitIncrement = pixels;
    }

    public BufferedImage getImage() {
        return image;
    }

    @Override
    public void fireStateChange() {
        super.fireStateChange();
    }


    public void setImage(BufferedImage image) {
        BufferedImage oldImage = this.image;
        if (oldImage != image) {
            this.image = image;
            revalidate();
            repaint();
            fireStateChange();
        }
    }

    @Override
    protected void paintComponent(Graphics2D g2d) {
        g2d.setColor(getBackground());
        g2d.fillRect(0, 0, getWidth(), getHeight());

        if (image == null) {
            return;
        }

        Rectangle imageBounds = getImageBounds();

        g2d.drawImage(image, null, imageBounds.x, imageBounds.y);

        g2d.setColor(getForeground());
        g2d.drawRect(imageBounds.x - 1, imageBounds.y - 1, imageBounds.width + 1, imageBounds.height + 1);
    }


    @Override
    public Dimension getPreferredSize() {

        int width = 400;
        int height = 400;

        if (image != null) {
            width = image.getWidth();
            height = image.getHeight();
        }

        Container parent = getParent();
        if (parent instanceof JViewport) {
            JViewport viewport = (JViewport) parent;
            Dimension extentSize = viewport.getExtentSize();
            width = Math.max(extentSize.width, width);
            height = Math.max(extentSize.height, height);
        }

        return new Dimension(width, height);
    }

    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    public int getScrollableUnitIncrement(Rectangle visibleRect,
                                          int orientation,
                                          int direction) {
        //Get the current position.
        int currentPosition = 0;
        if (orientation == SwingConstants.HORIZONTAL) {
            currentPosition = visibleRect.x;
        } else {
            currentPosition = visibleRect.y;
        }

        //Return the number of pixels between currentPosition
        //and the nearest tick mark in the indicated direction.
        if (direction < 0) {
            int newPosition = currentPosition -
                    (currentPosition / maxUnitIncrement)
                            * maxUnitIncrement;
            return (newPosition == 0) ? maxUnitIncrement : newPosition;
        } else {
            return ((currentPosition / maxUnitIncrement) + 1)
                    * maxUnitIncrement
                    - currentPosition;
        }
    }

    public int getScrollableBlockIncrement(Rectangle visibleRect,
                                           int orientation,
                                           int direction) {
        if (orientation == SwingConstants.HORIZONTAL) {
            return visibleRect.width - maxUnitIncrement;
        } else {
            return visibleRect.height - maxUnitIncrement;
        }
    }

    public boolean getScrollableTracksViewportWidth() {
        if (image == null) {
            return true;
        }
        Container parent = getParent();
        if (parent instanceof JViewport) {
            JViewport viewport = (JViewport) parent;
            Dimension extentSize = viewport.getExtentSize();
            return image.getWidth() <= extentSize.width;
        }
        return false;
    }

    public boolean getScrollableTracksViewportHeight() {
        if (image == null) {
            return true;
        }
        Container parent = getParent();
        if (parent instanceof JViewport) {
            JViewport viewport = (JViewport) parent;
            Dimension extentSize = viewport.getExtentSize();
            return image.getHeight() <= extentSize.height;
        }
        return false;
    }

    public Rectangle getImageBounds() {
        Dimension size = getSize();
        int offsetX = 0;
        int offsetY = 0;
        int width = 0;
        int height = 0;
        if (image != null) {
            width = image.getWidth();
            height = image.getHeight();
            if (size.width > width) {
                offsetX = (size.width - width) >> 1;
            }
            if (size.height > height) {
                offsetY = (size.height - height) >> 1;
            }
        }
        return new Rectangle(offsetX, offsetY, width, height);
    }
}
