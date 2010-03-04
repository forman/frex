package z.io;

import org.jdom.Element;

public class AlgoIO implements IO {

    private Algo algo;

    public AlgoIO(Algo algo) {
        this.algo = algo;
    }

    public Algo getAlgo() {
        return algo;
    }

    public String getElementName() {
        return "algo";
    }

    public void read(IOManager ioManager, Element element) {
        algo.setThreshold(Double.parseDouble(element.getAttributeValue("threshold")));
        algo.setMode(Integer.parseInt(element.getAttributeValue("mode")));
    }

    public void write(IOManager ioManager, Element element) {
        element.setAttribute("threshold", String.valueOf(algo.getThreshold()));
        element.setAttribute("mode", String.valueOf(algo.getMode()));
    }

    public static class Provider implements ExtensionProvider<IO> {

        public IO getExtension(Object extensibleObject, Class<IO> extensionType) {
            if (extensibleObject.getClass() == Algo.class) {
                return new AlgoIO((Algo) extensibleObject);
            }
            return null;
        }

    }
}