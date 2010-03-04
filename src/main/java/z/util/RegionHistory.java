package z.util;

import z.core.Region;

import java.util.Vector;

public class RegionHistory {
    // private Plane plane;
    private Vector<Region> regionList;
    private int currentIndex;
    private ChangeListenerList changeListenerList;

    public RegionHistory() { // Plane plane) {
        // this.plane = plane;
        regionList = new Vector<Region>(16);
        currentIndex = -1;
        changeListenerList = new ChangeListenerList();
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public int getSize() {
        return regionList.size();
    }

    public Region getRegion(int index) {
        return regionList.get(index);
    }

    public Region getCurrentRegion() {
        if (currentIndex >= 0 && currentIndex < regionList.size()) {
            return regionList.get(currentIndex);
        }
        return null;
    }

    public void setCurrentRegion(Region region) {
        currentIndex++;
        regionList.setSize(currentIndex + 1);
        regionList.set(currentIndex, region);
        fireStateChange();
    }

    public boolean gotoNextRegion() {
        if (currentIndex < regionList.size() - 1) {
            currentIndex++;
            fireStateChange();
            return true;
        }
        return false;
    }

    public boolean gotoPreviousRegion() {
        if (currentIndex > 0) {
            currentIndex--;
            fireStateChange();
            return true;
        }
        return false;
    }

    public void addChangeListener(ChangeListener listener) {
        changeListenerList.addChangeListener(listener);
    }

    public void removeChangeListener(ChangeListener listener) {
        changeListenerList.removeChangeListener(listener);
    }

    public ChangeListener[] getChangeListeners() {
        return changeListenerList.getChangeListeners();
    }

    protected void fireStateChange() {
        changeListenerList.fireStateChange(this);
    }
}
