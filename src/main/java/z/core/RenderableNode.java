package z.core;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import z.StringLiterals;
import z.util.Assert;
import z.util.ChangeListener;
import z.util.ChangeListenerList;
import z.util.JDOMExternalizable;
import z.util.JDOMHelper;
import z.util.JDOMObjectIO;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.MessageFormat;

public abstract class RenderableNode implements JDOMExternalizable {

    private ChangeListenerList changeListenerList;

    private File file;

    private String name;

    private ImageInfo imageInfo;

    private boolean modified;

    protected RenderableNode(File file) {
        Assert.notNull(file, "file"); // NON-NLS 
        this.file = file;
        this.name = getNameFromFile(file);
        changeListenerList = new ChangeListenerList();
        imageInfo = new ImageInfo();
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        Assert.notNull(file, "file"); // NON-NLS
        if (!this.file.equals(file)) {
            this.file = file;
            this.name = getNameFromFile(file);
            System.out.println("name = " + name); // NON-NLS
            fireStateChange();
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        Assert.notNull(name, "name");  // NON-NLS
        if (!this.name.equals(name)) {
            this.file = new File(this.file.getParentFile(), name
                    + getFilenameExtension());
            this.name = name;
            fireStateChange();
        }
    }

    public boolean isModified() {
        return modified;
    }

    public void setModified(boolean modified) {
        if (this.modified != modified) {
            this.modified = modified;
            fireStateChange();
        }
    }

    public ImageInfo getImageInfo() {
        return imageInfo;
    }

    public void dispose() {
        changeListenerList.clear();
    }

    public abstract void zoomRegion(int imageWidth,
                                    int imageHeight,
                                    double visibleImageRectX,
                                    double visibleImageRectY,
                                    double visibleImageRectWidth,
                                    double visibleImageRectHeight);

    public abstract String getElementName();

    public abstract String getFileFormatVersion();

    public abstract String getFilenameExtension();

    protected static void readNode(final RenderableNode node) throws JDOMException, IOException {
        Assert.notNull(node, "node");   // NON-NLS
        final SAXBuilder builder = new SAXBuilder();
        final Document document = builder.build(node.getFile());
        node.readExternal(document.getRootElement());
    }

    public void write() throws JDOMException, IOException {
        write(getFile());
        setModified(false);
    }

    public void write(final File file) throws JDOMException, IOException {
        Assert.notNull(file, "file");   // NON-NLS
        final FileWriter fileWriter = new FileWriter(file);
        try {
            write(fileWriter);
        } finally {
            fileWriter.close();
        }
    }

    public void write(final Writer writer) throws JDOMException, IOException {
        Assert.notNull(writer, "writer");  // NON-NLS
        Element element = new Element(getElementName());
        writeExternal(element);
        final XMLOutputter xmlOutputter = new XMLOutputter();
        xmlOutputter.setFormat(Format.getPrettyFormat());
        xmlOutputter.output(element, writer);
    }

    public void readExternal(Element element) throws JDOMException {
        Assert.notNull(element, "element"); // NON-NLS
        checkVersion(element);
        imageInfo = (ImageInfo) JDOMObjectIO.readObjectFromChild(element,
                                                                 ImageInfo.ELEMENT_NAME,
                                                                 ImageInfo.class);
    }

    public void writeExternal(Element element) throws JDOMException {
        Assert.notNull(element, "element");  // NON-NLS
        element.setAttribute("version", getFileFormatVersion());
        JDOMObjectIO.writeObjectToChild(element,
                                        ImageInfo.ELEMENT_NAME,
                                        imageInfo,
                                        ImageInfo.class);
    }

    public void addChangeListener(ChangeListener listener) {
        Assert.notNull(listener, "listener"); // NON-NLS
        changeListenerList.addChangeListener(listener);
    }

    public void removeChangeListener(ChangeListener listener) {
        Assert.notNull(listener, "listener"); // NON-NLS
        changeListenerList.removeChangeListener(listener);
    }

    public ChangeListener[] getChangeListeners() {
        return changeListenerList.getChangeListeners();
    }

    public void fireStateChange() {
        changeListenerList.fireStateChange(this);
    }

    // ///////////////////////////////////////////////////////////////////////
    // private

    protected void checkVersion(Element element) throws JDOMException {
        Assert.notNull(element, "element"); // NON-NLS
        final String currentVersion = getFileFormatVersion();
        String detectedVersion = JDOMHelper.getAttributeString(element,
                                                               "version", // NON-NLS
                                                               currentVersion);
        if (detectedVersion.compareTo(currentVersion) > 0) {
            throw new JDOMException(MessageFormat.format(StringLiterals.getString("ex.msg.formatVersionRequired"), currentVersion));
        }
    }

    protected String getNameFromFile(final File file) {
        Assert.notNull(file, "file"); // NON-NLS
        String name = file.getName();
        if (name.endsWith(getFilenameExtension())) {
            name = name.substring(0, name.length()
                    - getFilenameExtension().length());
        }
        return name;
    }

}
