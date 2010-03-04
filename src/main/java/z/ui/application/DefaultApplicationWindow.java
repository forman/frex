package z.ui.application;

import javax.swing.JFrame;

class DefaultApplicationWindow implements ApplicationWindow {
    private JFrame shell;
    private ApplicationPage page;

    public DefaultApplicationWindow(JFrame shell) {
        this.shell = shell;
        this.page = new DefaultApplicationPage(this);
    }

    public JFrame getShell() {
        return shell;
    }

    public ApplicationPage getPage() {
        return page;
    }

    public void close() {
        PageComponent[] pageComponents = page.getPageComponents();
        for (PageComponent pageComponent : pageComponents) {
            page.closePageComponent(pageComponent, true);
        }
        // todo:  save preferences, etc
        shell.dispose();
    }

    public void setStatusMessage(String text) {
        // todo
    }
}
