package z.core;

import org.jdom.Element;
import org.jdom.JDOMException;
import z.StringLiterals;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Project extends RenderableNode {
    public static final String ELEMENT_NAME = "project"; // NON-NLS

    public static final String FILENAME_EXTENSION = ".zpr"; // NON-NLS

    public static final String FILEFORMAT_VERSION = "1.0"; // NON-NLS

    private List<Plane> planeList;

    public Project(File file) {
        super(file);
        planeList = new LinkedList<Plane>();
    }

    @Override
    public String getElementName() {
        return ELEMENT_NAME;
    }

    @Override
    public String getFilenameExtension() {
        return FILENAME_EXTENSION;
    }

    @Override
    public String getFileFormatVersion() {
        return FILEFORMAT_VERSION;
    }

    public void addPlane(Plane plane) {
        planeList.add(plane);
        plane.setProject(this);
        fireStateChange();
    }

    public void removePlane(Plane plane) {
        planeList.remove(plane);
        plane.setProject(null);
        fireStateChange();
    }

    public int getNumPlanes() {
        return planeList.size();
    }

    public Plane getPlane(int index) {
        return planeList.get(index);
    }

    public Plane getPlane(String name) {
        for (Plane aPlaneList : planeList) {
            if (aPlaneList.getName().equalsIgnoreCase(name)) {
                return aPlaneList;
            }
        }
        return null;
    }

    public Plane[] getVisiblePlanes() {
        final Plane[] planes = getPlanes();
        final List<Plane> visiblePlaneList = new ArrayList<Plane>(planes.length);
        for (Plane plane : planes) {
            if (plane.isVisible()) {
                visiblePlaneList.add(plane);
            }
        }
        return visiblePlaneList.toArray(new Plane[visiblePlaneList.size()]);
    }

    public Plane[] getPlanes() {
        return planeList.toArray(new Plane[planeList.size()]);
    }

    @Override
    public void zoomRegion(int imageWidth,
                           int imageHeight,
                           double visibleImageRectX,
                           double visibleImageRectY,
                           double visibleImageRectWidth,
                           double visibleImageRectHeight) {
        final Plane[] planes = getPlanes();
        for (Plane plane : planes) {
            plane.zoomRegion(imageWidth,
                             imageHeight,
                             visibleImageRectX,
                             visibleImageRectY,
                             visibleImageRectWidth,
                             visibleImageRectHeight);
        }
    }

    public static Project readProject(File file) throws JDOMException,
            IOException {
        Project project = new Project(file);
        readNode(project);
        return project;
    }

    @Override
    public void readExternal(Element element) throws JDOMException {
        super.readExternal(element);
        readExternalPlaneList(element);
    }

    private void readExternalPlaneList(Element element) throws JDOMException {
        planeList.clear();
        final List children = element.getChildren(Plane.ELEMENT_NAME);
        for (int i = 0; i < children.size(); i++) {
            Plane plane = new Plane(new File(MessageFormat.format(StringLiterals.getString("model.defaultLayerName"), i + 1, FILENAME_EXTENSION)));
            Element child = (Element) children.get(i);
            plane.readExternal(child);
            addPlane(plane);
        }
    }

    @Override
    public void writeExternal(Element element) throws JDOMException {
        super.writeExternal(element);
        writePlaneListExternal(element);
    }

    public void writePlaneListExternal(Element element) throws JDOMException {
        Plane[] planes = getPlanes();
        for (Plane plane : planes) {
            Element child = new Element(Plane.ELEMENT_NAME);
            plane.writeExternal(child);
            element.addContent(child);
        }
    }
}
