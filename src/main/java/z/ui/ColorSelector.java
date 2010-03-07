package z.ui;

import z.core.color.ColorRegistry;
import z.core.color.RGBA;

import javax.swing.Scrollable;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ColorSelector extends Control implements Scrollable {

    private static final int PREFERRED_CELL_W = 80;

    private static final int CELL_H = 24;

    private static final int VISIBLE_ROW_COUNT = 10;

    private static final int GAP = 3;

    private static final int FONT_SIZE = 10;

    private final int rowCount;

    private final int columnCount;

    private int selectedRow;

    private int selectedColoumn;

    private boolean doubleClicked;

    public ColorSelector() {
        rowCount = ColorRegistry.getInstance().getEntryCount();
        columnCount = ColorRegistry.getInstance().getBrightnessLevelCount();

        setPreferredSize(new Dimension(GAP + columnCount * (PREFERRED_CELL_W + GAP),
                                       GAP + rowCount * (CELL_H + GAP)));

        addMouseListener(new MouseHandler());
    }

    public boolean isDoubleClicked() {
        return doubleClicked;
    }

    public void setDoubleClicked(boolean doubleClicked) {
        this.doubleClicked = doubleClicked;
    }

    public RGBA getSelectedColor() {
        if (selectedColoumn != -1 && selectedRow != -1) {
            return ColorRegistry.getInstance().getColor(selectedRow, selectedColoumn);
        }
        return null;
    }

    public void setSelectedColor(RGBA selectedColor) {
        if (selectedColor == null) {
            setSelectedCell(-1, -1);
            return;
        }
        ColorRegistry.Descriptor descriptor = ColorRegistry.getInstance().lookup(selectedColor);
        setSelectedCell(descriptor.entry.index, descriptor.brightnessLevel);
    }

    private void setSelectedCell(int row, int column) {
        selectedRow = row;
        selectedColoumn = column;
        System.out.println("selectedRow = " + selectedRow);  // NON-NLS
        System.out.println("selectedColoumn = " + selectedColoumn);  // NON-NLS
        repaint();
        fireStateChange();
    }

    @Override
    protected void paintComponent(Graphics2D g2d) {
        Dimension size = getSize();

        g2d.setColor(getBackground());
        g2d.fillRect(0, 0, size.width, size.height);

        g2d.setFont(g2d.getFont().deriveFont((float) FONT_SIZE));

        for (int row = 0; row < rowCount; row++) {
            ColorRegistry.Entry entry = ColorRegistry.getInstance().getEntry(row);

            for (int column = columnCount - 1; column >= 0; column--) {
                final RGBA rgba = entry.colors[column];
                final Rectangle rectangle = getBounds(row, column);
                final Color color = new Color(rgba.getR(), rgba.getG(), rgba.getB());

                g2d.setColor(color);
                g2d.fill(rectangle);

                if (column == 0) {
                    if (rgba.getMax() >= 160) {
                        g2d.setColor(Color.BLACK);
                    } else {
                        g2d.setColor(Color.WHITE);
                    }
                    g2d.drawString(entry.name,
                                   rectangle.x + 2,
                                   rectangle.y + 2 + FONT_SIZE);
                }

            }
        }

        if (selectedColoumn != -1 && selectedRow != -1) {
            final Rectangle bounds = getBounds(selectedRow, selectedColoumn);
            g2d.setColor(Color.BLUE);
            bounds.x--;
            bounds.y--;
            bounds.width++;
            bounds.height++;
            g2d.draw(bounds);
            bounds.x--;
            bounds.y--;
            bounds.width += 2;
            bounds.height += 2;
            g2d.draw(bounds);
        }
    }

    private Rectangle getBounds(int row, int column) {
        int cellWidth = getCellWidth();
        return new Rectangle(GAP + column * (cellWidth + GAP),
                             GAP + row * (CELL_H + GAP),
                             cellWidth,
                             CELL_H);
    }

    private int getCellWidth() {
        return (getWidth() - GAP) / columnCount - GAP;
    }

    public Dimension getPreferredScrollableViewportSize() {
        return new Dimension(GAP + columnCount * (PREFERRED_CELL_W + GAP),
                             GAP + VISIBLE_ROW_COUNT * (CELL_H + GAP));
    }

    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return CELL_H;
    }

    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 4 * CELL_H;
    }

    public boolean getScrollableTracksViewportWidth() {
        return true;
    }

    public boolean getScrollableTracksViewportHeight() {
        return false;
    }

    private class MouseHandler extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            setDoubleClicked(e.getClickCount() > 1);

            final int x = e.getX();
            final int y = e.getY();

            int column = Math.max(0, (x - GAP) / (getCellWidth() + GAP));
            if (column < 0) {
                column = 0;
            }
            if (column >= columnCount) {
                column = columnCount - 1;
            }
            int row = Math.max(0, (y - GAP) / (CELL_H + GAP));
            if (row < 0) {
                row = 0;
            }
            if (row >= rowCount) {
                row = rowCount - 1;
            }
            setSelectedCell(row, column);
        }
    }
}
