package z.ui.application;

import java.awt.*;

public interface ApplicationWindowConfigurer {
    ApplicationWindow getWindow();

    Point getInitialLocation();

    void setInitialLocation(Point location);

    Dimension getInitialSize();

    void setInitialSize(Dimension size);

    String getTitle();

    void setTitle(String s);

    Image getIconImage();

    void setIconImage(Image icon);

    boolean getShowMenuBar();

    void setShowMenuBar(boolean b);

    boolean getShowCoolBar();

    void setShowCoolBar(boolean b);

    boolean getShowStatusLine();

    void setShowStatusLine(boolean b);

    boolean getShowProgressIndicator();

    void setShowProgressIndicator(boolean b);
}
