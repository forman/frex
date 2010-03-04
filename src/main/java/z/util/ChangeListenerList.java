package z.util;

import java.util.LinkedList;
import java.util.List;

public class ChangeListenerList {
    private List<ChangeListener> listeners;

    public ChangeListenerList() {
    }

    public void addChangeListener(ChangeListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("listener == null");
        }
        if (listeners == null) {
            listeners = new LinkedList<ChangeListener>();
        }
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeChangeListener(ChangeListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("listener == null");
        }
        if (listeners != null) {
            listeners.remove(listener);
            if (listeners.isEmpty()) {
                listeners = null;
            }
        }
    }

    public ChangeListener[] getChangeListeners() {
        if (listeners == null) {
            return new ChangeListener[0];
        }
        ChangeListener[] array = new ChangeListener[listeners.size()];
        return listeners.toArray(array);
    }

    public void fireStateChange(Object source) {
        if (listeners != null) {
            ChangeListener[] changeListeners = getChangeListeners();
            ChangeEvent event = new ChangeEvent(source);
            for (ChangeListener changeListener : changeListeners) {
                changeListener.stateChanged(event);
            }
        }
    }

    public void clear() {
        if (listeners != null) {
            listeners.clear();
            listeners = null;
        }
    }
}
