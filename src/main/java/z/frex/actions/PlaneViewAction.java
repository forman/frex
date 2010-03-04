package z.frex.actions;


import z.frex.PlaneView;
import z.ui.application.ApplicationPage;
import z.ui.application.ApplicationWindow;
import z.ui.application.PageComponent;
import z.ui.application.PageComponentListener;

public abstract class PlaneViewAction extends ApplicationWindowAction {

    private final PageComponentHandler pageComponentHandler;

    protected PlaneViewAction(ApplicationWindow window, String id) {
        super(window, id);
        pageComponentHandler = new PageComponentHandler();
        window.getPage().addPageComponentListener(pageComponentHandler);
        updateState();
    }

    @Override
    public void dispose() {
        getWindow().getPage().removePageComponentListener(pageComponentHandler);
        super.dispose();
    }

    public PlaneView getPlaneView() {
        final ApplicationPage activePage = getWindow().getPage();
        if (activePage == null) {
            return null;
        }
        final PageComponent activeView = activePage.getActivePageComponent();
        if (activeView instanceof PlaneView) {
            return (PlaneView) activeView;
        }
        return null;
    }

    public void updateState() {
        setEnabled(getPlaneView() != null);
    }

    @SuppressWarnings("unused")
    public void onPlaneViewActivated(PlaneView view) {
    }

    @SuppressWarnings("unused")
    public void onPlaneViewDeactivated(PlaneView view) {
    }

    @SuppressWarnings("unused")
    public void onPlaneViewOpened(PlaneView view) {
    }

    @SuppressWarnings("unused")
    public void onPlaneViewClosed(PlaneView view) {
    }

    private static boolean isPlaneView(PageComponent pageComponent) {
        return PlaneView.ID.equals(pageComponent.getId());
    }

    private static PlaneView getPlaneView(PageComponent pageComponent) {
        return (PlaneView) pageComponent;
    }

    private final class PageComponentHandler implements PageComponentListener {
        public void componentActivated(PageComponent pageComponent) {
            if (isPlaneView(pageComponent)) {
                onPlaneViewActivated(getPlaneView(pageComponent));
                updateState();
            }
        }

        public void componentDeactivated(PageComponent pageComponent) {
            if (isPlaneView(pageComponent)) {
                onPlaneViewDeactivated(getPlaneView(pageComponent));
                updateState();
            }
        }

        public void componentOpened(PageComponent pageComponent) {
            if (isPlaneView(pageComponent)) {
                onPlaneViewOpened(getPlaneView(pageComponent));
                updateState();
            }
        }

        public void componentClosed(PageComponent pageComponent) {
            if (isPlaneView(pageComponent)) {
                onPlaneViewClosed(getPlaneView(pageComponent));
                updateState();
            }
        }

        public void componentInputChanged(PageComponent pageComponent) {
            if (isPlaneView(pageComponent)) {
                updateState();
            }
        }

        public void componentShown(PageComponent pageComponent) {
            if (isPlaneView(pageComponent)) {
                updateState();
            }
        }

        public void componentHidden(PageComponent pageComponent) {
            if (isPlaneView(pageComponent)) {
                updateState();
            }
        }

        public void componentBroughtToTop(PageComponent pageComponent) {
            if (PlaneView.ID.equals(pageComponent.getId())) {
                updateState();
            }
        }
    }
}
