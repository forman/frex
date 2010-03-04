package z.ui.application;

public interface PageComponentListener {
    public void componentOpened(PageComponent pageComponent);

    public void componentActivated(PageComponent pageComponent);

    public void componentShown(PageComponent pageComponent);

    public void componentInputChanged(PageComponent pageComponent);

    public void componentHidden(PageComponent pageComponent);

    public void componentDeactivated(PageComponent pageComponent);

    public void componentClosed(PageComponent pageComponent);

    public void componentBroughtToTop(PageComponent pageComponent);
}
