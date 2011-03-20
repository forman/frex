package z.frex.dialogs;

import z.core.IColorizer;
import z.core.PlaneRaster;
import z.core.support.colorizers.PaletteColorTable;
import z.frex.PlaneView;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class EditColorsModel {


    public enum EditedRegion {
        ALL,
        INNER,
        OUTER;

        public static EditedRegion get(int ordinal) {
            EditedRegion[] values = EditedRegion.values();
            for (int i = 0; i < values.length; i++) {
                EditedRegion value = values[i];
                if (value.ordinal() == ordinal) {
                    return value;
                }
            }
            throw new IllegalStateException();
        }
    }

    private final PlaneView view;
    private EditedRegion editedRegion;
    private final IColorizer[] originalColorizers;
    private final PaletteColorTable[] paletteColorTables;
    private final PlaneRaster.Statistics[] statistics;
    private final PropertyChangeSupport propertyChangeSupport;

    public EditColorsModel(PlaneView view) {
        this.view = view;
        propertyChangeSupport = new PropertyChangeSupport(this);
        editedRegion = EditedRegion.ALL;
        originalColorizers = new IColorizer[3];
        paletteColorTables = new PaletteColorTable[3];
        for (int i = 0; i < 3; i++) {
            originalColorizers[i] = view.getPlane().getColorizer(i);
            if (originalColorizers[i] instanceof PaletteColorTable) {
                paletteColorTables[i] = (PaletteColorTable) originalColorizers[i].clone();
            } else {
                paletteColorTables[i] = new PaletteColorTable();
                paletteColorTables[i].reset();
            }
            paletteColorTables[i].prepare();
            final int index = i;
            paletteColorTables[i].addPropertyChangeListener(new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    propertyChangeSupport.fireIndexedPropertyChange("paletteColorTables", // NON-NLS
                                                                    index,
                                                                    evt.getOldValue(),
                                                                    evt.getNewValue());
                }
            });
        }
        statistics = new PlaneRaster.Statistics[3];
        statistics[0] = view.getPlane().getRaster().getTotalStatistics();
        statistics[1] = view.getPlane().getRaster().getInnerStatistics();
        statistics[2] = view.getPlane().getRaster().getOuterStatistics();
    }

    public PlaneView getView() {
        return view;
    }

    public EditedRegion getEditedRegion() {
        return editedRegion;
    }

    public void setEditedRegion(EditedRegion editedRegion) {
        EditedRegion oldValue = this.editedRegion;
        this.editedRegion = editedRegion;
        propertyChangeSupport.firePropertyChange("editedRegion", // NON-NLS
                                                 oldValue,
                                                 this.editedRegion);
    }

    public IColorizer getCurrentOriginalColorizer() {
        return originalColorizers[editedRegion.ordinal()];
    }

    public PaletteColorTable getCurrentPaletteColorTable() {
        return paletteColorTables[editedRegion.ordinal()];
    }

    public void setCurrentPaletteColorTable(PaletteColorTable colorTable) {
        PaletteColorTable oldValue = paletteColorTables[editedRegion.ordinal()];
        paletteColorTables[editedRegion.ordinal()] = colorTable;
        propertyChangeSupport.firePropertyChange("currentPaletteColorTable", // NON-NLS
                                                 oldValue, colorTable);
    }


    public void apply(boolean modify) {
        apply();
        if (modify) {
            getView().getPlane().setModified(true);
            getView().getPlane().fireStateChange();
        }
    }

    public void apply() {
        getView().getPlane().setColorizer(getCurrentPaletteColorTable());
        getView().generateImage(true);
    }

    public void restore() {
        if (getView().getPlane().getColorizer() != getCurrentOriginalColorizer()) {
            getView().getPlane().setColorizer(getCurrentOriginalColorizer());
            getView().generateImage(true);
        }
    }

    public PlaneRaster.Statistics getCurrentStatistics() {
        return statistics[editedRegion.ordinal()];
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void addPropertyChangeListener(String name, PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(name, listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(String name, PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(name, listener);
    }

}