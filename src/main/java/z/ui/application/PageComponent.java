package z.ui.application;

import z.core.progress.ProgressMonitor;

import javax.swing.JComponent;

public interface PageComponent {
    String getId();

    String getDisplayName();

    void setDisplayName(String name);

    JComponent getControl();

    ApplicationPage getPage();

    void setPage(ApplicationPage page);

    void setInput(Object input);

    Object getInput();

    void setFocus();

    void doSave(ProgressMonitor monitor);

    void doSaveAs();

    boolean isSaveAsAllowed();

    boolean isDirty();

    void dispose();
}
