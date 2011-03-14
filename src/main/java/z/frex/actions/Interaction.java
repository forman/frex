package z.frex.actions;

import z.frex.PlaneView;
import z.ui.ImageCanvas;
import z.ui.application.ApplicationPage;
import z.ui.application.ApplicationWindow;
import z.ui.application.PageComponent;
import z.ui.application.PageComponentListenerAdapter;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

public class Interaction extends PlaneViewAction {
    private static Map<ApplicationWindow, Interaction> activeInteractions = new HashMap<ApplicationWindow, Interaction>(5);

    private Interactor interactor;

    private PageComponentHandler pageComponentHandler;

    public Interaction(ApplicationWindow window, String id) {
        super(window, id);
        setSelected(false);
        this.pageComponentHandler = new PageComponentHandler();
        window.getPage().addPageComponentListener(pageComponentHandler);
        addPropertyChangeListener(new ActivationHandler());
    }

    protected void setInteractor(Interactor interactor) {
        this.interactor = interactor;
    }

    private void attachInteractor(final PageComponent pageComponent) {
        if (pageComponent instanceof PlaneView) {
            final PlaneView view = (PlaneView) pageComponent;
            if (!interactor.isActivated()) {
                interactor.activate(view);
            }
            ImageCanvas imageCanvas = view.getImageCanvas();
            imageCanvas.addMouseListener(interactor);
            imageCanvas.addMouseMotionListener(interactor);
            imageCanvas.addPaintListener(interactor);
        }
    }

    private void detachInteractor(final PageComponent pageComponent) {
        if (pageComponent instanceof PlaneView) {
            final PlaneView view = (PlaneView) pageComponent;
            if (interactor.isActivated()) {
                interactor.deactivate();
            }
            ImageCanvas imageCanvas = view.getImageCanvas();
            imageCanvas.removeMouseListener(interactor);
            imageCanvas.removeMouseMotionListener(interactor);
            imageCanvas.removePaintListener(interactor);
        }
    }

    private void updateInteractor() {
        ApplicationPage activePage = getWindow().getPage();
        if (activePage != null && activePage.getActivePageComponent() != null) {
            updateInteractor(activePage.getActivePageComponent());
        }
    }

    private void updateInteractor(PageComponent pageComponent) {
        if (isSelected()) {
            attachInteractor(pageComponent);
        } else {
            detachInteractor(pageComponent);
        }
    }

    private void registerActiveInteraction() {
        if (isSelected()) {
            Interaction lastInteraction = activeInteractions.get(getWindow());
            if (lastInteraction != null && lastInteraction != this) {
                lastInteraction.setSelected(false);
            }
            activeInteractions.put(getWindow(), this);
        }
    }

    private final class ActivationHandler implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent event) {
            if (event.getPropertyName().equals(SELECTED_KEY)) {
                System.out.printf("%s: selected = %s%n", this, isSelected());  // NON-NLS
                registerActiveInteraction();
                updateInteractor();
            }
        }
    }

    private final class PageComponentHandler extends PageComponentListenerAdapter {
        @Override
        public void componentActivated(PageComponent pageComponent) {
            updateInteractor(pageComponent);
        }

        @Override
        public void componentDeactivated(PageComponent pageComponent) {
            detachInteractor(pageComponent);
        }

        @Override
        public void componentClosed(PageComponent pageComponent) {
            detachInteractor(pageComponent);
        }
    }
}
