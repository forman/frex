package z.ui.application;

import javax.swing.JComponent;

public interface ApplicationPage {
    JComponent getControl();

    ApplicationWindow getWindow();

    PageComponent getActivePageComponent();

    PageComponent[] getPageComponents();

    void openPageComponent(Object input, String id) throws Exception;

    void closePageComponent(PageComponent pageComponent, boolean b);

    void addPageComponentListener(PageComponentListener listener);

    void removePageComponentListener(PageComponentListener listener);
}
