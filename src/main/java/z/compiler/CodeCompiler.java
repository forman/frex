package z.compiler;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;

/**
 * A Java code compiler.
 */
public class CodeCompiler {
    private final JavaCompiler compiler;
    private final File outputDir;
    private final File[] classPath;
    private final URLClassLoader loader;

    public CodeCompiler(File outputDir, File[] classPath) {
        this(ToolProvider.getSystemJavaCompiler(), outputDir, classPath);
    }

    public CodeCompiler(JavaCompiler compiler, File outputDir, File[] classPath) {
        this.compiler = compiler;
        this.outputDir = outputDir;
        this.classPath = classPath.clone();

        try {
            URL[] urls = new URL[classPath.length + 1];
            urls[0] = outputDir.toURI().toURL();
            for (int i = 1; i < urls.length; i++) {
                urls[i] = classPath[i - 1].toURI().toURL();
            }
            this.loader = new URLClassLoader(urls, Thread.currentThread().getContextClassLoader());
        } catch (MalformedURLException e) {
            throw new IllegalStateException(e);
        }

    }

    public Class<?> compile(String packageName, String className, String code) throws IOException, ClassNotFoundException {
        return compile(new Code(packageName + '.' + className, code));
    }

    public Class<?> compile(JavaFileObject file) throws IOException, ClassNotFoundException {
        final boolean status = performCompilerTask(file);
        if (!status) {
            // todo - include compiler error info (nf, 01.10.2008)
            throw new RuntimeException("Code compilation failed.");
        }
        String s = file.getName();
        String className = getClassName(s);
        return loader.loadClass(className);
    }

    static String getClassName(String path) {
        int i = path.lastIndexOf('/');
        if (i >= 0) {
            path = path.substring(i + 1);
        }
        int j = path.lastIndexOf('.');
        if (j >= 0) {
            path = path.substring(0, j);
        }
        return path;
    }

    private boolean performCompilerTask(JavaFileObject... source) throws IOException {
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
        outputDir.mkdirs();
        fileManager.setLocation(StandardLocation.CLASS_OUTPUT, Arrays.asList(outputDir));
        fileManager.setLocation(StandardLocation.CLASS_PATH, Arrays.asList(classPath));
        JavaCompiler.CompilationTask task = compiler.getTask(new PrintWriter(new OutputStreamWriter(System.err), true), 
                                                             fileManager,
                                                             null,
                                                             null,
                                                             null,
                                                             Arrays.asList(source));
        return task.call();
    }
}
