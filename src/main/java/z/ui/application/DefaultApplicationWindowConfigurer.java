package z.ui.application;

import java.awt.*;

class DefaultApplicationWindowConfigurer implements ApplicationWindowConfigurer {
    ApplicationWindow applicationWindow;
    private Point initialLocation;
    Dimension initialSize;
    String title;
    Image icon;
    boolean showMenuBar;
    boolean showCoolBar;
    boolean showStatusLine;
    boolean showProgressIndicator;

    public DefaultApplicationWindowConfigurer(ApplicationWindow applicationWindow) {
        this.applicationWindow = applicationWindow;
        title = "";
        initialLocation = null;
        initialSize = new Dimension(800, 400);
        showMenuBar = true;
        showCoolBar = false;
        showStatusLine = false;
        showProgressIndicator = false;
    }

    @Override
    public ApplicationWindow getWindow() {
        return applicationWindow;
    }

    @Override
    public Point getInitialLocation() {
        return initialLocation;
    }

    @Override
    public void setInitialLocation(Point initialLocation) {
        this.initialLocation = initialLocation;
    }

    @Override
    public Dimension getInitialSize() {
        return initialSize;
    }

    @Override
    public void setInitialSize(Dimension initialSize) {
        this.initialSize = initialSize;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public Image getIconImage() {
        return icon;
    }

    @Override
    public void setIconImage(Image icon) {
        this.icon = icon;
    }

    @Override
    public boolean getShowMenuBar() {
        return showMenuBar;
    }

    @Override
    public void setShowMenuBar(boolean showMenuBar) {
        this.showMenuBar = showMenuBar;
    }

    @Override
    public boolean getShowCoolBar() {
        return showCoolBar;
    }

    @Override
    public void setShowCoolBar(boolean showCoolBar) {
        this.showCoolBar = showCoolBar;
    }

    @Override
    public boolean getShowStatusLine() {
        return showStatusLine;
    }

    @Override
    public void setShowStatusLine(boolean showStatusLine) {
        this.showStatusLine = showStatusLine;
    }

    @Override
    public boolean getShowProgressIndicator() {
        return showProgressIndicator;
    }

    @Override
    public void setShowProgressIndicator(boolean showProgressIndicator) {
        this.showProgressIndicator = showProgressIndicator;
    }
}
