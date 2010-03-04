package z.ui;

import javax.swing.filechooser.FileFilter;
import java.io.File;

public class FileExtensionFileFilter extends FileFilter {
    private String formatName;

    private String description;

    private String[] extensions;

    public FileExtensionFileFilter(String formatName, String description, String extension) {
        this(formatName, description, new String[]{extension});
    }

    public FileExtensionFileFilter(String formatName, String description, String[] extensions) {
        this.formatName = formatName;
        this.description = description;
        this.extensions = extensions;
    }

    public String getFormatName() {
        return formatName;
    }

    public String getDefaultExtension() {
        return extensions[0];
    }

    public String[] getExtensions() {
        return extensions;
    }

    public File appendMissingFileExtension(File f) {
        if (hasKnownExtension(f)) {
            return f;
        }
        return new File(f.getPath() + getDefaultExtension());
    }

    public boolean hasKnownExtension(File f) {
        String name = f.getName().toLowerCase();
        for (String extension : extensions) {
            if (name.endsWith(extension)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }
        return hasKnownExtension(f);
    }

    @Override
    public String getDescription() {
        return description;
    }
}
