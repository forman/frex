package z.ui;

import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

public class Control extends JComponent {
    private List<PaintListener> paintListeners = new ArrayList<PaintListener>(3);
    private List<ChangeListener> changeListeners = new ArrayList<ChangeListener>(3);

    public void addChangeListener(ChangeListener listener) {
        changeListeners.add(listener);
    }

    public void removeChangeListener(ChangeListener listener) {
        changeListeners.remove(listener);
    }

    protected void fireStateChange() {
        ChangeEvent e = new ChangeEvent(this);
        for (ChangeListener changeListener : changeListeners) {
            changeListener.stateChanged(e);
        }
    }

    public void addPaintListener(PaintListener listener) {
        paintListeners.add(listener);
    }

    public void removePaintListener(PaintListener listener) {
        paintListeners.remove(listener);
    }

    protected void firePaint(Graphics2D g2d) {
        for (PaintListener paintListener : paintListeners) {
            paintListener.paintControl(g2d);
        }
    }

    @Override
    protected final void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        paintComponent(g2d);
        firePaint(g2d);
    }

    protected void paintComponent(Graphics2D g2d) {
        super.paintComponent(g2d);
    }

    public Rectangle getClientArea() {
        Insets insets = getInsets();
        return new Rectangle(insets.left,
                             insets.top,
                             getWidth() - (insets.left + insets.right),
                             getHeight() - (insets.top + insets.bottom));
    }

    public void dispose() {
    }
}
