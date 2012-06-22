package z.frex;

import z.StringLiterals;
import z.frex.actions.*;
import z.ui.UIUtils;
import z.ui.application.Action;
import z.ui.application.*;
import z.ui.dialog.MessageDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.MessageFormat;
import java.util.prefs.BackingStoreException;

public class FrexLifecycleAdvisor extends ApplicationLifecycleAdvisor {
    private static final String ABOUT_TEXT =
            "<html><b>Frex - Version 1.2</b></html>\n\n" +
                    "<html>Copyright &copy; 2008 by Norman Fomferra</html>\n\n" +
                    "<html>Dieses Programm ist freie Software. Sie können es unter den Bedingungen der</html>\n" +
                    "<html>GNU General Public License, wie von der Free Software Foundation veröffentlicht,</html>\n" +
                    "<html>weitergeben und/oder modifizieren, entweder gemäß Version 3 der Lizenz oder</html>\n" +
                    "<html>(nach Ihrer Option) jeder späteren Version.</html>\n\n" +
                    "<html>Die Veröffentlichung dieses Programms erfolgt in der Hoffnung, dass es Ihnen von</html>\n" +
                    "<html>Nutzen sein wird, aber OHNE IRGENDEINE GARANTIE, sogar ohne die implizite Garantie</html>\n" +
                    "<html>der MARKTREIFE oder der VERWENDBARKEIT FÜR EINEN BESTIMMTEN ZWECK.</html></html>\n" +
                    "<html>Details finden Sie in der GNU General Public License.</html>\n\n" +
                    "<html>Sie sollten ein Exemplar der GNU General Public License zusammen mit diesem</html>\n" +
                    "<html>Programm erhalten haben. Falls nicht, siehe <a href=\"http://www.gnu.org/licenses/\">http://www.gnu.org/licenses/</a>.</html>\n";

    private final boolean IS_ON_MAC = System.getProperty("os.name").startsWith("Mac OS");// NON-NLS
    private final boolean HAS_MENU_BAR = IS_ON_MAC;

    public FrexLifecycleAdvisor() {
    }

    @Override
    protected void registerActions(ApplicationWindow window) {
        assert SwingUtilities.isEventDispatchThread();

        register(new ShowMenuAction(window));

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
        register(new ManageUserFractalsAction(window));

        register(new GoBackAction(window));
        register(new GoNextAction(window));
        register(new GoHomeAction(window));

        register(new ZoomInteraction(window));
        register(new PanInteraction(window));

        Action aboutAction = ActionFactory.ABOUT.create(window);
        aboutAction.putValue("aboutTitle", // NON-NLS
                             StringLiterals.getString("gui.title.about"));
        aboutAction.putValue("aboutMessage", // NON-NLS
                             ABOUT_TEXT);
        register(aboutAction);
    }

    @Override
    protected void fillMenuBar(JMenuBar menuBar) {
        assert SwingUtilities.isEventDispatchThread();

        JMenu fileMenu = UIUtils.createMenu(StringLiterals.getString("gui.menu.file"));
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

        JMenu editMenu = UIUtils.createMenu(StringLiterals.getString("gui.menu.edit"));
        editMenu.add(getAction(EditPlanePropertiesAction.ID));
        editMenu.add(getAction(EditColorsAction.ID));
        editMenu.add(getAction(EditImageSizeAction.ID));
        editMenu.addSeparator();
        editMenu.add(getAction(ManageUserFractalsAction.ID));
        menuBar.add(editMenu);

        JMenu helpMenu = UIUtils.createMenu(StringLiterals.getString("gui.menu.help"));
        helpMenu.add(getAction(ActionFactory.ABOUT.getId()));
        menuBar.add(helpMenu);
    }

    @Override
    protected void fillCoolBar(JPanel coolBar) {
        assert SwingUtilities.isEventDispatchThread();

        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        if (!HAS_MENU_BAR) {
            toolBar.add(getAction(ShowMenuAction.ID));
            toolBar.addSeparator();
        }

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

        try {
            int frameX = Integer.parseInt(Frex.getPreferences().get("frameX", "-1"));
            int frameY = Integer.parseInt(Frex.getPreferences().get("frameY", "-1"));
            if (frameX >= 0 && frameY >= 0) {
                configurer.setInitialLocation(new Point(frameX, frameY));
            }
        } catch (NumberFormatException e) {
            // ?
        }

        Dimension size = new Dimension(600, 680);
        try {
            int frameWidth = Integer.parseInt(Frex.getPreferences().get("frameWidth", "-1"));
            int frameHeight = Integer.parseInt(Frex.getPreferences().get("frameHeight", "-1"));
            if (frameWidth > 0 && frameHeight > 0) {
                size = new Dimension(frameWidth, frameHeight);
            }
        } catch (NumberFormatException e) {
            // ?
        }
        configurer.setInitialSize(size);

        configurer.setShowMenuBar(HAS_MENU_BAR);
        configurer.setShowCoolBar(true);
        configurer.setShowStatusLine(false);
        configurer.setShowProgressIndicator(false);
        configurer.setTitle(StringLiterals.getString("gui.frame.title"));
        configurer.setIconImage(Frex.getIcon(StringLiterals.getString("gui.frame.icon")).getImage());

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
            title = StringLiterals.getString("gui.close.name");
        } else {
            title = StringLiterals.getString("gui.action.text.close");
        }
        PageComponent[] views = window.getPage().getPageComponents();
        for (PageComponent view : views) {
            if (view.isDirty() && view instanceof PlaneView) {
                final PlaneView planeView = (PlaneView) view;
                String pattern = StringLiterals.getString("gui.text.askSave");
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
        Rectangle frameBounds = window.getShell().getBounds();
        Frex.getPreferences().put("frameX", frameBounds.x + "");
        Frex.getPreferences().put("frameY", frameBounds.y + "");
        Frex.getPreferences().put("frameWidth", frameBounds.width + "");
        Frex.getPreferences().put("frameHeight", frameBounds.height + "");
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

    public class ShowMenuAction extends ApplicationWindowAction {
        public static final String ID = "z.frex.actions.showMenu";// NON-NLS


        public ShowMenuAction(ApplicationWindow window) {
            super(window, ID);
            setText(StringLiterals.getString("gui.action.text.showMenu"));
            setToolTipText(StringLiterals.getString("gui.action.tooltip.showMenu"));
            //putValue(javax.swing.Action.SMALL_ICON, Frex.getIcon("/icons/16x16/frex/triangle_down.gif"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Component source = (Component) e.getSource();

            JMenu editMenu = UIUtils.createMenu(StringLiterals.getString("gui.menu.edit"));
            editMenu.add(getAction(EditPlanePropertiesAction.ID));
            editMenu.add(getAction(EditColorsAction.ID));
            editMenu.add(getAction(EditImageSizeAction.ID));
            editMenu.addSeparator();
            editMenu.add(getAction(ManageUserFractalsAction.ID));

            JPopupMenu mainMenu = new JPopupMenu();
            mainMenu.add(getAction(NewPlaneAction.ID));
            mainMenu.add(getAction(OpenAction.ID));
            mainMenu.add(editMenu);
            mainMenu.addSeparator();
            mainMenu.add(getAction(CloseAction.ID));
            mainMenu.addSeparator();
            mainMenu.add(getAction(SaveAction.ID));
            mainMenu.add(getAction(SaveAsAction.ID));
            mainMenu.add(getAction(SaveImageAction.ID));
            mainMenu.addSeparator();
            mainMenu.add(getAction(ActionFactory.ABOUT.getId()));
            mainMenu.addSeparator();
            mainMenu.add(getAction(ActionFactory.QUIT.getId()));

            mainMenu.show(source, source.getX(), source.getY() + source.getHeight());
        }
    }
}
