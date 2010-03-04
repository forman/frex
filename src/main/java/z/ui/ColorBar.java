package z.ui;

import z.core.color.ColorRegistry;
import z.core.color.RGBA;
import z.frex.dialogs.SelectColorDialog;

import javax.swing.SwingUtilities;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ColorBar extends Control {


    private static final RGBA[] DEFAULT_COLORS = new RGBA[]{RGBA.WHITE,
            RGBA.LIGHT_GRAY, RGBA.GRAY, RGBA.DARK_GRAY, RGBA.BLACK, RGBA.RED,
            RGBA.PINK, RGBA.ORANGE, RGBA.YELLOW, RGBA.GREEN, RGBA.MAGENTA,
            RGBA.CYAN, RGBA.BLUE,};


    private int rowCount;

    private int columnCount;

    private int buttonSize;

    private RGBA[] colors;

    private RGBA selectedColor;

    public ColorBar() {
        this(2, 24, 14);
    }

    public ColorBar(int rowCount, int columnCount, int buttonSize) {
        this.rowCount = rowCount;
        this.columnCount = columnCount;
        this.buttonSize = buttonSize;
        this.selectedColor = null;
        initColors();
        Dimension minimumSize = new Dimension(getMinimumWidth(), getMinimumHeight());
        setMinimumSize(minimumSize);
        setPreferredSize(minimumSize);
        MouseHandler l = new MouseHandler();
        addMouseListener(l);
        addMouseMotionListener(l);
    }

    public int getRowCount() {
        return rowCount;
    }

    public int getColumnCount() {
        return columnCount;
    }

    public int getButtonSize() {
        return buttonSize;
    }

    public RGBA getSelectedColor() {
        return selectedColor;
    }

    public void setColor(int index, RGBA color) {
        colors[index] = color;
        repaint();
        fireStateChange();
    }

    public RGBA getColor(int i) {
        return colors[i];
    }

    public RGBA[] getColors() {
        return colors.clone();
    }

    public void setColors(RGBA[] colors) {
        System.arraycopy(colors, 0, this.colors, 0, this.colors.length);
        fireStateChange();
        repaint();
    }

    public Rectangle getButtonRectangle(int index, Rectangle rectangle) {
        if (rectangle == null) {
            rectangle = new Rectangle();
        }
        int i = index % columnCount;
        int j = index / columnCount;
        final Rectangle clientArea = getClientArea();
        int x0 = clientArea.x + 1 + ((clientArea.width - getMinimumWidth()) >> 1);
        int y0 = clientArea.y + 1 + ((clientArea.height - getMinimumHeight()) >> 1);
        rectangle.x = x0 + i * buttonSize;
        rectangle.y = y0 + j * buttonSize;
        rectangle.width = buttonSize;
        rectangle.height = buttonSize;
        return rectangle;
    }

    public int getButtonIndex(int x, int y) {
        final Rectangle clientArea = getClientArea();
        int x0 = clientArea.x + 1 + ((clientArea.width - getMinimumWidth()) >> 1);
        int y0 = clientArea.y + 1 + ((clientArea.height - getMinimumHeight()) >> 1);
        int i = (x - x0) / buttonSize;
        int j = (y - y0) / buttonSize;
        return i + j * columnCount;
    }

    private void initColors() {
        colors = new RGBA[this.rowCount * this.columnCount];
        for (int i = 0; i < colors.length; i++) {
            if (i < DEFAULT_COLORS.length) {
                colors[i] = DEFAULT_COLORS[i];
            } else {
                colors[i] = RGBA.WHITE;
            }
        }
    }

    private int findIndex(Point point) {
        Rectangle rectangle = null;
        int index = -1;
        for (int i = 0; i < colors.length; i++) {
            rectangle = getButtonRectangle(i, rectangle);
            if (rectangle.contains(point)) {
                index = i;
            }
        }
        return index;
    }

    public void setSelectedColor(RGBA selectedColor) {
        this.selectedColor = selectedColor;
        fireStateChange();
    }

    private int getMinimumWidth() {
        return columnCount * buttonSize + 2;
    }

    private int getMinimumHeight() {
        return rowCount * buttonSize + 2;
    }

    @Override
    protected void paintComponent(Graphics2D gc) {
        Rectangle rectangle = null;
        for (int i = 0; i < colors.length; i++) {
            rectangle = getButtonRectangle(i, rectangle);
            final RGBA rgba = getColor(i);
            final Color color = new Color(rgba.getR(), rgba.getG(), rgba.getB());
            gc.setColor(color);
            gc.fill(rectangle);
            gc.setColor(Color.BLACK);
            gc.draw(rectangle);
        }
    }

    private String getToolTipText(int index) {
        return getToolTipText(getColor(index));
    }

    private static String getToolTipText(RGBA color) {
        return ColorRegistry.getInstance().lookup(color).getDescription();
    }

    private class MouseHandler extends MouseAdapter {
        private int mouseOverIndex = -1;

        @Override
        public void mouseClicked(MouseEvent e) {
            Point point = e.getPoint();
            int index = findIndex(point);
            if (index != -1) {
                if (e.getClickCount() > 1) {
                    final SelectColorDialog colorDialog = new SelectColorDialog(SwingUtilities.getWindowAncestor(e.getComponent()),
                                                                                getColor(index));
                    if (colorDialog.open() == SelectColorDialog.ID_OK) {
                        RGBA selectedColor = colorDialog.getSelectedColor();
                        setColor(index, selectedColor);
                        setSelectedColor(selectedColor);
                    }
                } else {
                    setSelectedColor(getColor(index));
                }
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            int index = findIndex(e.getPoint());
            if (index != mouseOverIndex) {
                setToolTipText(index != -1 ? getToolTipText(index) : null);
                mouseOverIndex = index;
            }
        }
    }


    public static class Data {
        final public int index;

        final public RGBA rgb;

        public Data(int index, RGBA rgb) {
            this.index = index;
            this.rgb = rgb;
        }

    }

}
