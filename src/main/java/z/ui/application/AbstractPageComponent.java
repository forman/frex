package z.ui.application;

import z.core.progress.ProgressMonitor;

public abstract class AbstractPageComponent implements PageComponent {

    private ApplicationPage page;
    private Object input;
    private String displayName;

    protected AbstractPageComponent() {
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String name) {
        this.displayName = name;
    }

    public void setInput(Object input) {
        this.input = input;
    }

    public Object getInput() {
        return input;
    }

    public final ApplicationPage getPage() {
        return page;
    }

    public final void setPage(ApplicationPage page) {
        this.page = page;
    }

    public void setFocus() {
    }

    public void doSave(ProgressMonitor monitor) {
    }

    public void doSaveAs() {
    }

    public boolean isSaveAsAllowed() {
        return true;
    }

    public boolean isDirty() {
        return false;
    }

    public void dispose() {
    }

    @Override
    public String toString() {
        return getClass().getName() + "[id=" + getId() + ",displayName=" + displayName + "]";
    }
}
