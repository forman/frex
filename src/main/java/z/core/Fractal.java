/*
 * Created at 06.01.2004 20:51:45
 * Copyright (c) 2004 by Norman Fomferra
 */
package z.core;

public abstract class Fractal extends Algorithm implements IFractal {

    protected int iterMax;

    protected double bailOut;

    protected double bailOutSqr; // dependent on bailOut

    protected boolean juliaMode;

    protected double juliaCX;

    protected double juliaCY;

    protected Fractal() {
        reset();
    }

    @Override
    public void reset() {
        super.reset();
        setIterMax(100);
        setBailOut(100.0);
        setJuliaMode(false);
        setJuliaCX(0.0);
        setJuliaCY(0.0);
    }

    public int getIterMax() {
        return iterMax;
    }

    public void setIterMax(int iterMax) {
        int oldValue = this.iterMax;
        this.iterMax = iterMax;
        propertyChangeSupport.firePropertyChange("iterMax", oldValue, iterMax); // NON-NLS
    }

    public double getBailOut() {
        return bailOut;
    }

    public void setBailOut(double bailOut) {
        double oldValue = this.bailOut;
        this.bailOut = bailOut;
        bailOutSqr = this.bailOut * this.bailOut;
        propertyChangeSupport.firePropertyChange("bailOut", // NON-NLS
                                                 oldValue,
                                                 this.bailOut);
    }

    public boolean isJuliaMode() {
        return juliaMode;
    }

    public void setJuliaMode(boolean juliaMode) {
        boolean oldValue = this.juliaMode;
        this.juliaMode = juliaMode;
        propertyChangeSupport.firePropertyChange("juliaMode", // NON-NLS
                                                 oldValue,
                                                 this.juliaMode);
    }

    public double getJuliaCX() {
        return juliaCX;
    }

    public void setJuliaCX(double juliaCX) {
        double oldValue = this.juliaCX;
        this.juliaCX = juliaCX;
        propertyChangeSupport.firePropertyChange("juliaCX", // NON-NLS
                                                 oldValue,
                                                 this.juliaCX);
    }

    public double getJuliaCY() {
        return juliaCY;
    }

    public void setJuliaCY(double juliaCY) {
        double oldValue = this.juliaCY;
        this.juliaCY = juliaCY;
        propertyChangeSupport.firePropertyChange("juliaCY", // NON-NLS
                                                 oldValue,
                                                 this.juliaCY);
    }

}
