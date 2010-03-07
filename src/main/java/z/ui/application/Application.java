package z.ui.application;

import z.ui.UIUtils;
import z.util.Assert;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;

public class Application {
    private static Application instance;
    private ApplicationLifecycleAdvisor applicationLifecycleAdvisor;
    private Map<JFrame, ApplicationWindow> applicationWindows;

    public Application(ApplicationLifecycleAdvisor applicationLifecycleAdvisor) {
        Assert.notNull(applicationLifecycleAdvisor, "applicationLifecycleAdvisor"); // NON-NLS
        this.applicationLifecycleAdvisor = applicationLifecycleAdvisor;
        this.applicationWindows = new HashMap<JFrame, ApplicationWindow>(11);
        load(this);
    }

    public static Application instance() {
        return instance;
    }

    public static void load(Application instance) {
        Application.instance = instance;
    }

    public int getWindowCount() {
        return applicationWindows.size();
    }

    public ApplicationWindow[] getWindows() {
        return applicationWindows.values().toArray(new ApplicationWindow[applicationWindows.size()]);
    }

    public ApplicationWindow openWindow() {
        final JFrame shell = applicationLifecycleAdvisor.createShell();
        final DefaultApplicationWindow applicationWindow = new DefaultApplicationWindow(shell);
        if (SwingUtilities.isEventDispatchThread()) {
            openWindow(applicationWindow);
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    openWindow(applicationWindow);
                }
            });
        }
        return applicationWindow;
    }

    private void openWindow(final ApplicationWindow applicationWindow) {
        JFrame shell = applicationWindow.getShell();
        final DefaultApplicationWindowConfigurer applicationWindowConfigurer = new DefaultApplicationWindowConfigurer(applicationWindow);
        applicationLifecycleAdvisor.setWindowConfigurer(applicationWindowConfigurer);
        applicationLifecycleAdvisor.preWindowOpen(applicationWindow);
        applicationLifecycleAdvisor.configureWindow(applicationWindowConfigurer);
        applicationLifecycleAdvisor.registerActions(applicationWindow);

        shell.setTitle(applicationWindowConfigurer.getTitle());
        shell.setIconImage(applicationWindowConfigurer.getIconImage());
        shell.setSize(applicationWindowConfigurer.getInitialSize());

        if (applicationWindowConfigurer.getShowMenuBar()) {
            final JMenuBar menuBar = new JMenuBar();
            shell.setJMenuBar(menuBar);
            applicationLifecycleAdvisor.fillMenuBar(menuBar);
        }

        if (applicationWindowConfigurer.getShowCoolBar()) {
            final JPanel coolBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 2));
            shell.getContentPane().add(coolBar, BorderLayout.NORTH);
            applicationLifecycleAdvisor.fillCoolBar(coolBar);
        }

        if (applicationWindowConfigurer.isShowStatusLine()) {
            final JPanel statusBar = new JPanel(new FlowLayout());
            shell.getContentPane().add(statusBar, BorderLayout.SOUTH);
            applicationLifecycleAdvisor.fillStatusBar(statusBar);
        }

        shell.getContentPane().add(applicationWindow.getPage().getControl(), BorderLayout.CENTER);
        shell.addWindowListener(new WindowAdapter() {

            @Override
            public void windowOpened(WindowEvent e) {
                applicationWindows.put(applicationWindow.getShell(), applicationWindow);
                applicationLifecycleAdvisor.postWindowOpen(applicationWindow);
            }

            @Override
            public void windowClosing(WindowEvent e) {
                if (applicationLifecycleAdvisor.preWindowShellClose(applicationWindow)) {
                    applicationWindow.close();
                }
            }

            @Override
            public void windowClosed(WindowEvent e) {
                applicationWindows.remove(applicationWindow.getShell());
                applicationLifecycleAdvisor.postWindowClosed(applicationWindow);
                if (applicationWindows.isEmpty()) {
                    applicationLifecycleAdvisor.preShutDown();
                }
            }
        });

        shell.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        UIUtils.centerComponent(shell);
        shell.setVisible(true);
    }

}
