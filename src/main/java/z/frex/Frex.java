package z.frex;

import z.core.Plane;
import z.core.Project;
import z.ui.application.Application;
import z.ui.application.ApplicationWindow;
import z.util.FractalDef;
import z.util.RegionHistory;

import javax.swing.*;
import java.net.URL;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

public class Frex {

    private static Logger logger = Logger.getLogger(Frex.class.getPackage().getName());
    private static Preferences preferences = Preferences.userNodeForPackage(Frex.class);
    private static Map<ApplicationWindow, Project> projects = new HashMap<ApplicationWindow, Project>(3);
    private static Map<Plane, RegionHistory> regionHistories = new HashMap<Plane, RegionHistory>(3);

    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            FractalDef.buildAll(false);
        } catch (Exception e) {
            logger.log(Level.SEVERE, MessageFormat.format("Failed to compile ''{0}''.",
                                                          FractalDef.MY_FRACTALS_FILE), e);
        }

        try {
            new Application(new FrexLifecycleAdvisor()).openWindow();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns an image descriptor for the image file at the given
     * plug-in relative path.
     *
     * @param path the path
     * @return the image descriptor
     */
    public static ImageIcon getIcon(String path) {
        URL resource = Frex.class.getResource(path);
        return new ImageIcon(resource);
    }


    public static Project getProject(ApplicationWindow window) {
        return projects.get(window);
    }

    public static void putProject(ApplicationWindow window, Project project) {
        projects.put(window, project);
    }

    public static void removeProject(ApplicationWindow window) {
        projects.remove(window);
    }

    public static RegionHistory getRegionHistory(Plane plane) {
        RegionHistory regionHistory = regionHistories.get(plane);
        if (regionHistory == null) {
            regionHistory = new RegionHistory();
            putRegionHistory(plane, regionHistory);
        }
        return regionHistory;
    }

    public static void putRegionHistory(Plane plane, RegionHistory regionHistory) {
        regionHistories.put(plane, regionHistory);
    }

    public static void removeRegionHistory(Plane plane) {
        regionHistories.remove(plane);
    }

    public static Preferences getDialogSettings() {
        return getPreferences().node("dialog-data");  // NON-NLS
    }

    public static Preferences getPreferences() {
        return preferences;
    }

    public static Logger getLogger() {
        return logger;
    }

    public static void log(String message) {
        Logger logger = getLogger();
        logger.info(message);
    }
}
