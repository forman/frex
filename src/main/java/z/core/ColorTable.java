package z.core;

import org.jdom.Element;
import org.jdom.JDOMException;
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

    public final boolean isCyclic() {
        return cyclic;
    }

    public final void setCyclic(boolean cyclic) {
        this.cyclic = cyclic;
    }

    public final int getNumColors() {
        return numColors;
    }

    public final void setNumColors(int numColors) {
        this.numColors = numColors;
    }

    public float getIndexMin() {
        return indexMin;
    }

    public void setIndexMin(float indexMin) {
        this.indexMin = indexMin;
    }

    public float getIndexMax() {
        return indexMax;
    }

    public void setIndexMax(float indexMax) {
        this.indexMax = indexMax;
    }

    public final int getRgba(int index) {
        return rgbaArray[index % numColors];
    }

    public final void setRgba(int index, int rgba) {
        rgbaArray[index] = rgba;
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
        cyclic = true;
        numColors = 256;
        indexMin = 0.0f;
        indexMax = 100.0f;
        updateIndexFactor();
    }

    @Override
    public void writeExternal(Element element) throws JDOMException {
        JDOMHelper.setAttributeInt(element, "numColors", numColors);
        JDOMHelper.setAttributeBoolean(element, "cyclic", cyclic, true);
        JDOMHelper.setAttributeFloat(element, "indexMin", indexMin, 0.0f);
        JDOMHelper.setAttributeFloat(element, "indexMax", indexMax, 1.0f);
    }

    @Override
    public void readExternal(Element element) throws JDOMException {
        numColors = JDOMHelper.getAttributeInt(element, "numColors", 100);
        cyclic = JDOMHelper.getAttributeBoolean(element, "cyclic", true);
        if (element.getAttribute("indexBias") != null || element.getAttribute("indexFactor") != null) {
            indexMin = JDOMHelper.getAttributeFloat(element, "indexBias", 0.0f);
            indexFactor = JDOMHelper.getAttributeFloat(element, "indexFactor", 1.0f);
            indexMax = indexMin + (float) numColors / indexFactor;
            // todo: log
            System.out.println(String.format("WARNING: %s=%f, %s=%f is deprecated, converted to %s=%f, %s=%f",
                                             "indexBias", indexMin, "indexFactor", indexFactor, "indexMin", indexMin, "indexMax", indexMax));
        } else {
            indexMin = JDOMHelper.getAttributeFloat(element, "indexMin", 0.0f);
            indexMax = JDOMHelper.getAttributeFloat(element, "indexMax", 1.0f);
            updateIndexFactor();
        }
    }
}
