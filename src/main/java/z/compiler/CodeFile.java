package z.compiler;

import javax.tools.SimpleJavaFileObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;

/**
 * A file object used to represent Java source coming from a file.
 */
public class CodeFile extends SimpleJavaFileObject {
    private final File file;

    public CodeFile(File file) {
        super(file.toURI(), Kind.SOURCE);
        this.file = file;
    }

    @Override
    public Reader openReader(boolean ignoreEncodingErrors) throws IOException {
        return new FileReader(file);
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
        BufferedReader reader = new BufferedReader(openReader(true));
        try {
            char[] chars = new char[(int) file.length()];
            reader.read(chars);
            return CharBuffer.wrap(chars);
        } finally {
            reader.close();
        }
    }

    @Override
    public long getLastModified() {
        return file.lastModified();
    }

    @Override
    public boolean delete() {
        return file.delete();
    }
}
