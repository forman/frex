package z.frex.actions;

import z.frex.PlaneView;
import z.ui.PaintListener;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public abstract class Interactor implements MouseListener, MouseMotionListener, PaintListener {

    private PlaneView view;

    protected PlaneView getPlaneView() {
        return view;
    }

    public void activate(PlaneView view) {
        System.out.println(this + ".activate(): view = " + view);
        this.view = view;
    }

    public void deactivate() {
        System.out.println(this + ".deactivate(): view = " + view);
        this.view = null;
    }

    public boolean isActivated() {
        return view != null;
    }

    public void mouseClicked(MouseEvent e) {

    }

    public void mousePressed(MouseEvent e) {

    }

    public void mouseReleased(MouseEvent e) {

    }

    public void mouseEntered(MouseEvent e) {

    }

    public void mouseExited(MouseEvent e) {

    }

    public void mouseDragged(MouseEvent e) {

    }

    public void mouseMoved(MouseEvent e) {

    }

    public void paintControl(Graphics2D g2d) {

    }
}
