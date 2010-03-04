package z.util;

import z.core.AlgorithmRegistry;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;

public class FileUtils {
    public static File ensureExtension(File f, String ext) {

        String name = f.getName();
        if (name.endsWith(ext)) {
            return f;
        }

        int pos = name.lastIndexOf('.');
        if (pos <= 0) {
            return new File(f.getPath() + ext);
        }

        String newName = name.substring(0, pos) + ext;
        String parent = f.getParent();
        if (parent != null) {
            return new File(parent, newName);
        } else {
            return new File(newName);
        }
    }

    public static File getFrexHome() {
        return new File(System.getProperty("frex.home", "."));
    }

    public static File getUserHome() {
        return new File(System.getProperty("user.home"));
    }

    public static File getFrexLibDir() {
        return new File(getFrexHome(), "lib");
    }

    public static File getFrexUserDir() {
        return new File(getUserHome(), ".frex");
    }
}
