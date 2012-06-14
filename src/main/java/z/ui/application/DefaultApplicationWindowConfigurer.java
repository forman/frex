package z.ui.application;

import java.awt.Dimension;
import java.awt.Image;

class DefaultApplicationWindowConfigurer implements ApplicationWindowConfigurer {
    ApplicationWindow applicationWindow;
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
        initialSize = new Dimension(800, 400);
        showMenuBar = true;
        showCoolBar = false;
        showStatusLine = false;
        showProgressIndicator = false;
    }

    public ApplicationWindow getWindow() {
        return applicationWindow;
    }

    public Dimension getInitialSize() {
        return initialSize;
    }

    public void setInitialSize(Dimension initialSize) {
        this.initialSize = initialSize;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Image getIconImage() {
        return icon;
    }

    public void setIconImage(Image icon) {
        this.icon = icon;
    }

    public boolean getShowMenuBar() {
        return showMenuBar;
    }

    public void setShowMenuBar(boolean showMenuBar) {
        this.showMenuBar = showMenuBar;
    }

    public boolean getShowCoolBar() {
        return showCoolBar;
    }

    public void setShowCoolBar(boolean showCoolBar) {
        this.showCoolBar = showCoolBar;
    }

    public boolean getShowStatusLine() {
        return showStatusLine;
    }

    public void setShowStatusLine(boolean showStatusLine) {
        this.showStatusLine = showStatusLine;
    }

    public boolean getShowProgressIndicator() {
        return showProgressIndicator;
    }

    public void setShowProgressIndicator(boolean showProgressIndicator) {
        this.showProgressIndicator = showProgressIndicator;
    }


}
