package z.core;

import org.jdom.JDOMException;
import z.StringLiterals;
import z.math.term.Term;
import z.util.FileUtils;
import z.util.FractalDef;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AlgorithmRegistry {

    private static AlgorithmRegistry instance = new AlgorithmRegistry();

    private ClassLoader pluginClassLoader;

    private final AlgorithmSubRegistry fractals = new AlgorithmSubRegistry(IFractal.class);

    private final AlgorithmSubRegistry accumulators = new AlgorithmSubRegistry(IAccumulator.class);

    private final AlgorithmSubRegistry indexers = new AlgorithmSubRegistry(IIndexer.class);

    private final AlgorithmSubRegistry colorizers = new AlgorithmSubRegistry(IColorizer.class);

    public static AlgorithmRegistry instance() {
        return instance;
    }

    public ClassLoader getPluginClassLoader() {
        return pluginClassLoader;
    }

    public AlgorithmSubRegistry getFractals() {
        return fractals;
    }

    public AlgorithmSubRegistry getAccumulators() {
        return accumulators;
    }

    public AlgorithmSubRegistry getIndexers() {
        return indexers;
    }

    public AlgorithmSubRegistry getColorizers() {
        return colorizers;
    }

    private AlgorithmRegistry() {

        pluginClassLoader = AlgorithmRegistry.class.getClassLoader();

        extendPluginClassLoader();

        try {
            loadPlugins();
        } catch (Throwable e) {
            logError(e);
        }

        try {
            loadUserFractals();
        } catch (Throwable e) {
            logError(e);
        }
    }

    private void loadUserFractals() throws JDOMException, IOException {
        FractalDef[] fractalDefs = FractalDef.loadUserFractals();
        for (FractalDef fractalDef : fractalDefs) {
            try {
                Term term = fractalDef.parse();
                assert term != null;
                AlgorithmDescriptor descriptor = fractalDef.compile(term);
                fractals.register(descriptor);
            } catch (Throwable e) {
                logError(e);
            }
        }
    }

    private void extendPluginClassLoader() {
        File[] pluginFiles = getPlugins();
        if (pluginFiles != null) {
            pluginClassLoader = createPluginClassLoader(pluginFiles, pluginClassLoader);
        }
    }

    private static ClassLoader createPluginClassLoader(File[] pluginFiles, ClassLoader cl) {
        ArrayList<URL> u = new ArrayList<URL>(pluginFiles.length);
        for (File pluginFile : pluginFiles) {
            URL pluginUrl = null;
            try {
                pluginUrl = pluginFile.toURI().toURL();
            } catch (MalformedURLException e) {
                logError(e);
            }
            logInfo(MessageFormat.format(StringLiterals.getString("log.plugin.found.0"), pluginFile));
            u.add(pluginUrl);
        }
        cl = new URLClassLoader(u.toArray(new URL[u.size()]), cl);
        return cl;
    }

    private static File[] getPlugins() {
        File home = FileUtils.getFrexHome();
        File pluginDir = new File(home, "plugins");  // NON-NLS
        logInfo(MessageFormat.format(StringLiterals.getString("log.scanning.plugin.directory.0"), pluginDir));
        return pluginDir.listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                return (pathname.isFile()
                        && pathname.getName().toLowerCase().endsWith(".jar"))  // NON-NLS
                        || pathname.isDirectory();
            }
        });
    }

    private static void logInfo(String message) {
        getLogger().info(message);
    }

    private static Logger getLogger() {
        return Logger.getAnonymousLogger();
    }

    private static void logError(Throwable t) {
        getLogger().log(Level.SEVERE, t.getMessage(), t);
    }

    private void loadPlugins() throws IOException {
        Enumeration<URL> algorithmSpiUrls = pluginClassLoader.getResources(String.format("META-INF/services/%s", // NON-NLS
                                                                                         AlgorithmSpi.class.getName()));
        while (algorithmSpiUrls.hasMoreElements()) {
            URL algorithmSpiUrl = algorithmSpiUrls.nextElement();
            try {
                loadAlgorithmSpi(algorithmSpiUrl);
            } catch (IOException e) {
                logError(e);
            }
        }
    }

    private void loadAlgorithmSpi(URL algorithmSpiUrl) throws IOException {
        InputStream inputStream = algorithmSpiUrl.openStream();
        try {
            loadPlugin(inputStream);
        } catch (Exception e) {
            logError(e);
        } finally {
            inputStream.close();
        }
    }

    private void loadPlugin(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        while (true) {
            String className = bufferedReader.readLine();
            if (className == null) {
                break;
            }
            className = className.trim();
            if (!className.isEmpty()) {
                try {
                    Class<?> algorithmSpiClass = pluginClassLoader.loadClass(className);
                    AlgorithmSpi algorithmSpi = (AlgorithmSpi) algorithmSpiClass.newInstance();
                    algorithmSpi.registerAlgorithms(this);
                    logInfo(MessageFormat.format(StringLiterals.getString("log.registered.algorithms.of.0"), algorithmSpiClass.getName()));
                } catch (Exception e) {
                    logError(e);
                }
            }
        }
    }

}
