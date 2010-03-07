package z.core.support.colorizers;

import org.jdom.Element;
import org.jdom.JDOMException;
import z.core.ColorTable;
import z.core.color.RGBA;
import z.util.JDOMHelper;

public class SineColorTable extends ColorTable {

    private Function[] functions;

    public Function getFunction(int i) {
        return functions[i];
    }

    public void setFunction(int i, Function functions) {
        this.functions[i] = functions;
    }

    public Object clone() {
        SineColorTable c = (SineColorTable) super.clone();
        c.functions = new Function[functions.length];
        for (int i = 0; i < 4; i++) {
            final Function f = getFunction(i);
            c.setFunction(i, new Function(f.x1, f.x2, f.a, f.b));
        }
        return c;
    }

    @Override
    public void reset() {
        super.reset();
        functions = new Function[4];
        functions[0] = new Function(0, 255, 1.0, 0.0);
        functions[1] = new Function(0, 255, 2.0, 0.0);
        functions[2] = new Function(0, 255, 3.0, 0.0);
        functions[3] = new Function(255, 255, 1.0, 0.0);
    }

    @Override
    protected void fillRgbaArray(int[] rgbaArray) {
        for (int i = 0; i < rgbaArray.length; i++) {
            double index = i / (rgbaArray.length - 1.0);
            rgbaArray[i] = new RGBA(functions[0].compute(index),
                                    functions[1].compute(index),
                                    functions[2].compute(index),
                                    functions[3].compute(index)).getValue();
        }
    }

    static String[] functionElementNames = new String[]{
            "redParams", // NON-NLS
            "greenParams", // NON-NLS
            "blueParams", // NON-NLS
            "alphaParams" // NON-NLS
    };

    @Override
    public void writeExternal(Element element) throws JDOMException {
        super.writeExternal(element);
        for (int i = 0; i < functions.length; i++) {
            Element functionElement = new Element(functionElementNames[i]);
            JDOMHelper.setAttributeDouble(functionElement,
                                          "x1", // NON-NLS
                                          functions[i].getX1());
            JDOMHelper.setAttributeDouble(functionElement,
                                          "x2", // NON-NLS
                                          functions[i].getX2());
            JDOMHelper.setAttributeDouble(functionElement,
                                          "a", // NON-NLS
                                          functions[i].getA());
            JDOMHelper.setAttributeDouble(functionElement,
                                          "b", // NON-NLS
                                          functions[i].getB());
            element.addContent(functionElement);
        }
    }

    @Override
    public void readExternal(Element element) throws JDOMException {
        super.readExternal(element);
        for (int i = 0; i < functions.length; i++) {
            Element functionElement = JDOMHelper.getChild(element,
                                                          functionElementNames[i]);
            int x1 = JDOMHelper.getAttributeInt(functionElement, "x1"); // NON-NLS
            int x2 = JDOMHelper.getAttributeInt(functionElement, "x2"); // NON-NLS
            double a = JDOMHelper.getAttributeDouble(functionElement, "a"); // NON-NLS
            double b = JDOMHelper.getAttributeDouble(functionElement, "b"); // NON-NLS
            functions[i] = new Function(x1, x2, a, b);
        }
    }

    public static class Function {
        private final int x1;

        private final int x2;

        private final double a;

        private final double b;

        public Function(int x1, int x2, double a, double b) {
            this.a = a;
            this.b = b;
            this.x1 = x1;
            this.x2 = x2;
        }

        public double getA() {
            return a;
        }

        public double getB() {
            return b;
        }

        public int getX1() {
            return x1;
        }

        public int getX2() {
            return x2;
        }

        public int compute(double index) {
            return x1
                    + (int) (0.5 + (x2 - x1)
                    * 0.5
                    * (1.0 + Math.sin(2.0 * Math.PI
                    * ((1.0 + a) * index + (0.75 + b)))));
        }
    }

}
