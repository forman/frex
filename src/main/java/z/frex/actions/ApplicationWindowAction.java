package z.frex.actions;

import z.ui.application.Action;
import z.ui.application.ApplicationWindow;
import z.util.Assert;

public abstract class ApplicationWindowAction extends Action {
    /**
     * The application window; or <code>null</code> if this action has been
     * <code>dispose</code>d.
     */
    private ApplicationWindow window;

    protected ApplicationWindowAction(ApplicationWindow window) {
        this(window, null);
    }

    protected ApplicationWindowAction(ApplicationWindow window, String id) {
        super(id);
        Assert.notNull(window, "window"); // NON-NLS
        this.window = window;
    }


    public ApplicationWindow getWindow() {
        return window;
    }
}
