package z.frex;

import z.frex.actions.CloseAction;
import z.frex.actions.EditColorsAction;
import z.frex.actions.EditImageSizeAction;
import z.frex.actions.EditPlanePropertiesAction;
import z.frex.actions.FitImageSizeAction;
import z.frex.actions.GoBackAction;
import z.frex.actions.GoHomeAction;
import z.frex.actions.GoNextAction;
import z.frex.actions.ManageUserFractalsAction;
import z.frex.actions.NewPlaneAction;
import z.frex.actions.OpenAction;
import z.frex.actions.PanInteraction;
import z.frex.actions.SaveAction;
import z.frex.actions.SaveAsAction;
import z.frex.actions.SaveImageAction;
import z.frex.actions.ZoomInteraction;
import z.ui.UIUtils;
import z.ui.application.Action;
import z.ui.application.ActionFactory;
import z.ui.application.Application;
import z.ui.application.ApplicationLifecycleAdvisor;
import z.ui.application.ApplicationWindow;
import z.ui.application.ApplicationWindowConfigurer;
import z.ui.application.PageComponent;
import z.ui.dialog.MessageDialog;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import java.awt.Dimension;
import java.text.MessageFormat;
import java.util.prefs.BackingStoreException;

public class FrexLifecycleAdvisor extends ApplicationLifecycleAdvisor {
    private static final String ABOUT_TEXT = "" +
            "<html><b>Frex - Version 1.2</b></html>\n" +
            "\n" +
            "<html>Copyright &copy; 2008 by Norman Fomferra</html>\n" +
            "\n" +
            "<html>Dieses Programm ist freie Software. Sie können es unter den Bedingungen der</html>\n" +
            "<html>GNU General Public License, wie von der Free Software Foundation veröffentlicht,</html>\n" +
            "<html>weitergeben und/oder modifizieren, entweder gemäß Version 3 der Lizenz oder</html>\n" +
            "<html>(nach Ihrer Option) jeder späteren Version.</html>\n" +
            "\n" +
            "<html>Die Veröffentlichung dieses Programms erfolgt in der Hoffnung, daß es Ihnen von</html>\n" +
            "<html>Nutzen sein wird, aber OHNE IRGENDEINE GARANTIE, sogar ohne die implizite Garantie</html>\n" +
            "<html>der MARKTREIFE oder der VERWENDBARKEIT FÜR EINEN BESTIMMTEN ZWECK.</html></html>\n" +
            "<html>Details finden Sie in der GNU General Public License.</html>\n" +
            "\n" +
            "<html>Sie sollten ein Exemplar der GNU General Public License zusammen mit diesem</html>\n" +
            "<html>Programm erhalten haben. Falls nicht, siehe <a href=\"http://www.gnu.org/licenses/\">http://www.gnu.org/licenses/</a>.</html>\n";

    public FrexLifecycleAdvisor() {
    }

    @Override
    protected void registerActions(ApplicationWindow window) {
        assert SwingUtilities.isEventDispatchThread();

        register(new NewPlaneAction(window));
        register(new OpenAction(window));
        register(new CloseAction(window));
        register(new SaveAction(window));
        register(new SaveAsAction(window));
        register(new SaveImageAction(window));
        register(ActionFactory.QUIT.create(window));

        register(new EditColorsAction(window));
        register(new EditImageSizeAction(window));
        register(new FitImageSizeAction(window));
        register(new EditPlanePropertiesAction(window));
        // register(new EditIndexRangeAction(window));
        register(new ManageUserFractalsAction(window));

        register(new GoBackAction(window));
        register(new GoNextAction(window));
        register(new GoHomeAction(window));

        register(new ZoomInteraction(window));
        register(new PanInteraction(window));

        Action aboutAction = ActionFactory.ABOUT.create(window);
        aboutAction.putValue("aboutTitle", "Über Frex");
        aboutAction.putValue("aboutMessage", ABOUT_TEXT);
        register(aboutAction);
    }

    @Override
    protected void fillMenuBar(JMenuBar menuBar) {
        assert SwingUtilities.isEventDispatchThread();

        JMenu fileMenu = UIUtils.createMenu("&Datei");
        fileMenu.add(getAction(NewPlaneAction.ID));
        fileMenu.add(getAction(OpenAction.ID));
        fileMenu.addSeparator();
        fileMenu.add(getAction(CloseAction.ID));
        fileMenu.addSeparator();
        fileMenu.add(getAction(SaveAction.ID));
        fileMenu.add(getAction(SaveAsAction.ID));
        fileMenu.add(getAction(SaveImageAction.ID));
        fileMenu.addSeparator();
        fileMenu.add(getAction(ActionFactory.QUIT.getId()));
        menuBar.add(fileMenu);

        JMenu editMenu = UIUtils.createMenu("&Bearbeiten");
        editMenu.add(getAction(EditPlanePropertiesAction.ID));
        editMenu.add(getAction(EditColorsAction.ID));
        editMenu.add(getAction(EditImageSizeAction.ID));
//        editMenu.add(getAction(EditIndexRangeAction.ID));
        editMenu.addSeparator();
        editMenu.add(getAction(ManageUserFractalsAction.ID));
        menuBar.add(editMenu);

        final JMenu helpMenu = UIUtils.createMenu("&?");
        helpMenu.add(getAction(ActionFactory.ABOUT.getId()));
        menuBar.add(helpMenu);
    }

    @Override
    protected void fillCoolBar(JPanel coolBar) {
        assert SwingUtilities.isEventDispatchThread();

        JToolBar toolBar = new JToolBar();
        toolBar.add(getAction(NewPlaneAction.ID));
        toolBar.add(getAction(OpenAction.ID));
        toolBar.add(getAction(SaveAction.ID));
        toolBar.add(getAction(SaveImageAction.ID));
        toolBar.addSeparator();
        toolBar.add(getAction(GoHomeAction.ID));
        toolBar.add(getAction(GoBackAction.ID));
        toolBar.add(getAction(GoNextAction.ID));
        toolBar.addSeparator();
        toolBar.add(getAction(ZoomInteraction.ID).createToolBarButton());
        toolBar.add(getAction(PanInteraction.ID).createToolBarButton());
        toolBar.addSeparator();
        toolBar.add(getAction(EditPlanePropertiesAction.ID));
        toolBar.add(getAction(EditColorsAction.ID));
        toolBar.add(getAction(EditImageSizeAction.ID));
        toolBar.add(getAction(FitImageSizeAction.ID));
        toolBar.addSeparator();
        toolBar.add(getAction(ManageUserFractalsAction.ID));

        coolBar.add(toolBar);

        getAction(ZoomInteraction.ID).setSelected(true);
    }

    @Override
    public void preWindowOpen(ApplicationWindow window) {
        assert SwingUtilities.isEventDispatchThread();
        ApplicationWindowConfigurer configurer = getWindowConfigurer();
        configurer.setInitialSize(new Dimension(600, 680));
        configurer.setShowMenuBar(true);
        configurer.setShowCoolBar(true);
        configurer.setShowStatusLine(false);
        configurer.setShowProgressIndicator(false);
        configurer.setTitle("Frex");
        configurer.setIconImage(Frex.getIcon("/icons/frex-16.png").getImage());
    }

    @Override
    public void postWindowOpen(ApplicationWindow window) {
        if (Application.instance().getWindowCount() == 0) {
            final NewPlaneAction action = new NewPlaneAction(window);
            action.run();
        }
    }


    @Override
    public boolean preWindowShellClose(ApplicationWindow window) {
        assert SwingUtilities.isEventDispatchThread();
        String title;
        if (Application.instance().getWindowCount() > 1) {
            title = "Schließen";
        } else {
            title = "Beenden";
        }
        PageComponent[] views = window.getPage().getPageComponents();
        for (PageComponent view : views) {
            if (view.isDirty() && view instanceof PlaneView) {
                final PlaneView planeView = (PlaneView) view;
                String pattern = "''{0}'' wurde verändert.\nSpeichern?";
                int a = MessageDialog.confirmYesNoCancel(window.getShell(), title, MessageFormat.format(pattern, view.getDisplayName()));
                if (a == JOptionPane.CANCEL_OPTION) {
                    return false;
                } else if (a == JOptionPane.YES_OPTION) {
                    final SaveAsAction action = new SaveAsAction(window);
                    action.saveAs(planeView.getPlane());
                    if (action.isCanceledByUser()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }


    @Override
    public void preShutDown() {
        try {
            Frex.getPreferences().flush();
        } catch (BackingStoreException e) {
            // todo: handle exception here...
            e.printStackTrace();
        }
    }

}
