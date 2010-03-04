package z.ui.application;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class DefaultApplicationPage implements ApplicationPage {
    private ApplicationWindow window;
    private List<PageComponentListener> pageComponentListeners;
    private PageComponent activePageComponent;
    private JPanel control;

    public DefaultApplicationPage(ApplicationWindow window) {
        this.window = window;
        control = new JPanel(new BorderLayout());
        control.setBackground(Color.DARK_GRAY);
        pageComponentListeners = new ArrayList<PageComponentListener>(3);

        window.getShell().addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {
                if (activePageComponent != null) {
                    fireComponentActivated(activePageComponent);
                }
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
                if (activePageComponent != null) {
                    fireComponentDeactivated(activePageComponent);
                }
            }
        });

    }

    public ApplicationWindow getWindow() {
        return window;
    }

    public JFrame getShell() {
        return getWindow().getShell();
    }

    public JComponent getControl() {
        return control;
    }

    public PageComponent[] getPageComponents() {
        return activePageComponent != null ? new PageComponent[]{activePageComponent} : new PageComponent[0];
    }

    public void addPageComponentListener(PageComponentListener listener) {
        pageComponentListeners.add(listener);
    }

    public void removePageComponentListener(PageComponentListener listener) {
        pageComponentListeners.remove(listener);
    }

    public PageComponent getActivePageComponent() {
        return activePageComponent;
    }

    public void openPageComponent(Object input, String id) throws Exception {
        if (activePageComponent != null) {
            closePageComponent(activePageComponent, false);
        }

        Class<?> aClass = Class.forName(id);
        PageComponent pageComponent = (PageComponent) aClass.newInstance();
        pageComponent.setPage(this);
        pageComponent.setInput(input);
        JComponent pageComponentControl = pageComponent.getControl();

        activePageComponent = pageComponent;

        control.removeAll();
        control.add(pageComponentControl, BorderLayout.CENTER);
        control.invalidate();
        control.validate();
        control.repaint();
        pageComponentControl.requestFocus();

        fireComponentOpened(activePageComponent);
        fireComponentVisible(activePageComponent);
        fireComponentActivated(activePageComponent);
    }


    public void closePageComponent(PageComponent pageComponent, boolean b) {
        control.removeAll();
        control.invalidate();
        control.validate();
        control.repaint();

        if (pageComponent == activePageComponent) {

            fireComponentDeactivated(activePageComponent);
            fireComponentHidden(activePageComponent);
            fireComponentClosed(activePageComponent);

            activePageComponent.dispose();
            activePageComponent = null;
        }
    }

    public ApplicationPage getPage() {
        return this;
    }

    private void fireComponentOpened(PageComponent pageComponent) {
//        System.out.println("fireComponentOpened: pageComponent = " + pageComponent);
        for (PageComponentListener listener : pageComponentListeners) {
            listener.componentOpened(pageComponent);
        }
    }

    private void fireComponentClosed(PageComponent pageComponent) {
//        System.out.println("fireComponentClosed: pageComponent = " + pageComponent);
        for (PageComponentListener listener : pageComponentListeners) {
            listener.componentClosed(pageComponent);
        }
    }

    private void fireComponentVisible(PageComponent pageComponent) {
//        System.out.println("fireComponentVisible: pageComponent = " + pageComponent);
        for (PageComponentListener listener : pageComponentListeners) {
            listener.componentShown(pageComponent);
        }
    }

    private void fireComponentHidden(PageComponent pageComponent) {
//        System.out.println("fireComponentHidden: pageComponent = " + pageComponent);
        for (PageComponentListener listener : pageComponentListeners) {
            listener.componentHidden(pageComponent);
        }
    }

    private void fireComponentActivated(PageComponent pageComponent) {
//        System.out.println("fireComponentActivated: pageComponent = " + pageComponent);
        for (PageComponentListener listener : pageComponentListeners) {
            listener.componentActivated(pageComponent);
        }
    }

    private void fireComponentDeactivated(PageComponent pageComponent) {
//        System.out.println("fireComponentDeactivated: pageComponent = " + pageComponent);
        for (PageComponentListener listener : Collections.unmodifiableList(this.pageComponentListeners)) {
            listener.componentDeactivated(pageComponent);
        }
    }
}
