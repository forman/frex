package z.ui;

import z.core.progress.ProgressMonitor;

import javax.swing.SwingWorker;

public abstract class ProgressSwingWorker<T, V> extends SwingWorker<T, V> {
    private ProgressMonitor progressMonitor;

    protected ProgressSwingWorker(ProgressMonitor progressMonitor) {
        this.progressMonitor = progressMonitor;
    }

    public ProgressMonitor getProgressMonitor() {
        return progressMonitor;
    }

    public void cancel() {
        getProgressMonitor().setCanceled(true);
        cancel(true);
    }

    @Override
    protected final T doInBackground() throws Exception {
        return doInBackground(progressMonitor);
    }

    protected abstract T doInBackground(ProgressMonitor progressMonitor) throws Exception;
}
