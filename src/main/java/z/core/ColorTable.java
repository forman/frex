package z.core;

import org.jdom.Element;
import org.jdom.JDOMException;
import z.StringLiterals;
import z.util.JDOMHelper;

public abstract class ColorTable extends Colorizer {

    private boolean cyclic;

    private int numColors;

    private float indexFactor;

    private float indexMin;

    private float indexMax;

    private int[] rgbaArray;

    public ColorTable() {
        reset();
    }

    @Override
    public ColorTable clone() {
        ColorTable c = (ColorTable) super.clone();
        c.rgbaArray = rgbaArray != null ? rgbaArray.clone() : null;
        return c;
    }

    public final boolean isCyclic() {
        return cyclic;
    }

    public final void setCyclic(boolean cyclic) {
        boolean oldValue = this.cyclic;
        this.cyclic = cyclic;
        firePropertyChange("cyclic", // NON-NLS
                           oldValue,
                           this.cyclic);
    }

    public final int getNumColors() {
        return numColors;
    }

    public final void setNumColors(int numColors) {
        int oldValue = this.numColors;
        this.numColors = numColors;
        firePropertyChange("numColors", // NON-NLS
                           oldValue,
                           this.numColors);
    }

    public float getIndexMin() {
        return indexMin;
    }

    public void setIndexMin(float indexMin) {
        float oldValue = this.indexMin;
        this.indexMin = indexMin;
        firePropertyChange("indexMin", // NON-NLS
                           oldValue,
                           this.indexMin);
    }

    public float getIndexMax() {
        return indexMax;
    }

    public void setIndexMax(float indexMax) {
        float oldValue = this.indexMax;
        this.indexMax = indexMax;
        firePropertyChange("indexMax", // NON-NLS
                           oldValue,
                           this.indexMax);
    }

    public final int getRgba(int index) {
        return rgbaArray[index % numColors];
    }

    public int getRgba(float index) {
        final int n = numColors;
        int i = (int) ((index - indexMin) * indexFactor);
        if (i < 0) {
            if (cyclic) {
                i = -i;
            } else {
                i = 0;
            }
        }
        if (i >= n) {
            if (cyclic) {
                i %= n;
            } else {
                i = n - 1;
            }
        }
        return rgbaArray[i];
    }

    @Override
    public void prepare() {
        super.prepare();
        if (rgbaArray == null || rgbaArray.length != numColors) {
            rgbaArray = new int[numColors];
        }
        updateIndexFactor();
        fillRgbaArray(rgbaArray);
    }

    private void updateIndexFactor() {
        indexFactor = (float) numColors / (indexMax - indexMin);
    }

    protected abstract void fillRgbaArray(int[] rgbaArray);

    @Override
    public void reset() {
        super.reset();
        cyclic = true;
        numColors = 256;
        indexMin = 0.0f;
        indexMax = 100.0f;
        updateIndexFactor();
    }

    @Override
    public void writeExternal(Element element) throws JDOMException {
        JDOMHelper.setAttributeInt(element, "numColors", numColors); // NON-NLS
        JDOMHelper.setAttributeBoolean(element, "cyclic", cyclic, true); // NON-NLS
        JDOMHelper.setAttributeFloat(element, "indexMin", indexMin, 0.0f); // NON-NLS
        JDOMHelper.setAttributeFloat(element, "indexMax", indexMax, 1.0f); // NON-NLS
    }

    @Override
    public void readExternal(Element element) throws JDOMException {
        numColors = JDOMHelper.getAttributeInt(element, "numColors", 100);// NON-NLS
        cyclic = JDOMHelper.getAttributeBoolean(element, "cyclic", true); // NON-NLS
        if (element.getAttribute("indexBias") != null || element.getAttribute("indexFactor") != null) { // NON-NLS
            indexMin = JDOMHelper.getAttributeFloat(element, "indexBias", 0.0f); // NON-NLS
            indexFactor = JDOMHelper.getAttributeFloat(element, "indexFactor", 1.0f); // NON-NLS
            indexMax = indexMin + (float) numColors / indexFactor;
            // todo: log
            System.out.println(String.format(StringLiterals.getString("log.warning.index.deprecated"),
                                             "indexBias", indexMin, "indexFactor", indexFactor, "indexMin", indexMin, "indexMax", indexMax)); // NON-NLS
        } else {
            indexMin = JDOMHelper.getAttributeFloat(element, "indexMin", 0.0f); // NON-NLS
            indexMax = JDOMHelper.getAttributeFloat(element, "indexMax", 1.0f); // NON-NLS
            updateIndexFactor();
        }
    }
}
