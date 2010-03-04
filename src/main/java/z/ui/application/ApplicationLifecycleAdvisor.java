package z.ui.application;

import z.util.Assert;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import java.util.HashMap;
import java.util.Map;

public abstract class ApplicationLifecycleAdvisor {

    private Map<String, Action> actionMap;
    private ApplicationWindowConfigurer windowConfigurer;

    protected ApplicationLifecycleAdvisor() {
        this.actionMap = new HashMap<String, Action>(50);
    }

    public ApplicationWindowConfigurer getWindowConfigurer() {
        return windowConfigurer;
    }

    void setWindowConfigurer(ApplicationWindowConfigurer windowConfigurer) {
        this.windowConfigurer = windowConfigurer;
    }

    protected JFrame createShell() {
        return new JFrame();
    }

    protected void configureWindow(DefaultApplicationWindowConfigurer configurer) {
    }

    protected void registerActions(ApplicationWindow window) {
    }

    protected void fillMenuBar(JMenuBar menuBar) {
    }

    protected void fillCoolBar(JPanel coolBar) {
    }

    protected void fillStatusBar(JPanel statusBar) {
    }

    protected void preWindowOpen(ApplicationWindow window) {
    }

    protected void postWindowOpen(ApplicationWindow window) {
    }

    public boolean preWindowShellClose(ApplicationWindow window) {
        return true;
    }

    protected void postWindowClosed(ApplicationWindow window) {
    }

    public void preShutDown() {
    }

    public final void register(Action action) {
        Assert.notNull(action, "");
        Object id = action.getValue(Action.ACTION_COMMAND_KEY);
        Assert.notNull(id, "");
        actionMap.put(id.toString(), action);
    }

    public final Action getAction(String id) {
        Assert.notNull(id, "");
        return actionMap.get(id);
    }

}
