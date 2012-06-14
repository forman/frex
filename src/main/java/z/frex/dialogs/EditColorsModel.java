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
    private final boolean originalInnerOuterDisjoined;
    private final IColorizer originalInnerColorizer;
    private final IColorizer originalOuterColorizer;
    private final PaletteColorTable[] paletteColorTables;
    private final PlaneRaster.Statistics[] statistics;
    private final PropertyChangeSupport propertyChangeSupport;

    public EditColorsModel(PlaneView view) {
        this.view = view;
        propertyChangeSupport = new PropertyChangeSupport(this);
        paletteColorTables = new PaletteColorTable[3];
        editedRegion = view.getPlane().isInnerOuterDisjoined() ? EditedRegion.INNER : EditColorsModel.EditedRegion.ALL;
        originalInnerOuterDisjoined = view.getPlane().isInnerOuterDisjoined();
        originalInnerColorizer = view.getPlane().getInnerColorizer();
        originalOuterColorizer = view.getPlane().getOuterColorizer();
        setCopyOfColorPaletteTable(0, originalOuterColorizer);
        setCopyOfColorPaletteTable(1, originalInnerColorizer);
        setCopyOfColorPaletteTable(2, originalOuterColorizer);
        statistics = new PlaneRaster.Statistics[3];
        statistics[0] = view.getPlane().getRaster().getTotalStatistics();
        statistics[1] = view.getPlane().getRaster().getInnerStatistics();
        statistics[2] = view.getPlane().getRaster().getOuterStatistics();
    }

    private void setCopyOfColorPaletteTable(int i, IColorizer colorizer) {
        if (colorizer instanceof PaletteColorTable) {
            paletteColorTables[i] = (PaletteColorTable) colorizer.clone();
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

    public PaletteColorTable getCurrentPaletteColorTable() {
        return paletteColorTables[editedRegion.ordinal()];
    }

    public void setCurrentPaletteColorTable(PaletteColorTable colorTable) {
        PaletteColorTable oldValue = paletteColorTables[editedRegion.ordinal()];
        paletteColorTables[editedRegion.ordinal()] = colorTable;
        propertyChangeSupport.firePropertyChange("currentPaletteColorTable", // NON-NLS
                                                 oldValue, colorTable);
    }

    public void apply(boolean confirmed) {
        getView().getPlane().setInnerOuterDisjoined(editedRegion != EditedRegion.ALL);
        if (editedRegion == EditedRegion.INNER) {
            getView().getPlane().setInnerColorizer(getCurrentPaletteColorTable());
        } else {
            getView().getPlane().setOuterColorizer(getCurrentPaletteColorTable());
        }
        getView().generateImage(true);
        if (confirmed) {
            getView().getPlane().setModified(true);
            getView().getPlane().fireStateChange();
        }
    }

    public void restore() {
        getView().getPlane().setInnerOuterDisjoined(originalInnerOuterDisjoined);
        getView().getPlane().setInnerColorizer(originalInnerColorizer);
        getView().getPlane().setOuterColorizer(originalOuterColorizer);
        getView().generateImage(true);
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