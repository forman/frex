package z.frex.actions;

import z.StringLiterals;
import z.core.Plane;
import z.frex.Frex;
import z.ui.application.ApplicationWindow;
import z.util.RegionHistory;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

public class PanInteraction extends Interaction {
    public static final String ID = "z.frex.actions.pan";//$NON-NLS-1$

    public PanInteraction(ApplicationWindow window) {
        super(window, ID);
        setText(StringLiterals.getString("gui.interaction.text.pan"));
        setToolTipText(StringLiterals.getString("gui.interaction.tooltip.pan"));
        setSmallIcon(Frex.getIcon(StringLiterals.getString("gui.interaction.icon.pan")));
        setInteractor(new PanInteractor());
    }

    private static final class PanInteractor extends Interactor {
        private Rectangle rectangle;
        private Rectangle imageBounds;
        private Point point;

        @Override
        public void mousePressed(MouseEvent e) {
            imageBounds = getPlaneView().getImageCanvas().getImageBounds();
            rectangle = getPlaneView().getImageCanvas().getImageBounds();
            point = e.getPoint();
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (rectangle != null) {
                rectangle.x = e.getX() - point.x;
                rectangle.y = e.getY() - point.y;
                getPlaneView().getImageCanvas().repaint();
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (rectangle != null) {
                final Plane plane = getPlaneView().getPlane();
                plane.zoomRegion(rectangle.width,
                                 rectangle.height,
                                 rectangle.getX() - (double) imageBounds.x,
                                 rectangle.getY() - (double) imageBounds.y,
                                 rectangle.getWidth(),
                                 rectangle.getHeight());

                RegionHistory regionHistory = Frex.getRegionHistory(plane);
                if (regionHistory != null) {
                    regionHistory.setCurrentRegion(plane.getRegion());
                }

                getPlaneView().generateImage(false);
                rectangle = null;
                point = null;
            }
        }

        @Override
        public void paintControl(Graphics2D gc) {
            if (rectangle != null) {
                gc.setColor(new Color(255, 255, 255, 50));
                gc.fill(rectangle);
                gc.setColor(Color.WHITE);
                gc.draw(rectangle);
            }
        }
    }
}
