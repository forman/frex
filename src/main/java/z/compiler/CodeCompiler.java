package z.compiler;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
import java.io.*;
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

    public Class<?> compile(String packageName, String className, String code) throws IOException, ClassNotFoundException, CompilerException {
        return compile(new Code(packageName + '.' + className, code));
    }

    public Class<?> compile(JavaFileObject file) throws CompilerException {
        performCompilerTask(file);
        String s = file.getName();
        String className = getClassName(s);
        try {
            return loader.loadClass(className);
        } catch (ClassNotFoundException e) {
            throw new CompilerException("Failed to load compiled code: " + className); // I18N
        }
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

    private void performCompilerTask(JavaFileObject... source) throws CompilerException {
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
        outputDir.mkdirs();
        try {
            fileManager.setLocation(StandardLocation.CLASS_OUTPUT, Arrays.asList(outputDir));
            fileManager.setLocation(StandardLocation.CLASS_PATH, Arrays.asList(classPath));
        } catch (IOException e) {
            throw new CompilerException("Failed to initialise Java compiler.", e); // I18N
        }
        StringWriter compilerOutput = new StringWriter();
        try {
            JavaCompiler.CompilationTask task = compiler.getTask(new PrintWriter(compilerOutput, true),
                                                                 fileManager,
                                                                 null,
                                                                 null,
                                                                 null,
                                                                 Arrays.asList(source));
            if (!task.call()) {
                throw new CompilerException("Compilation error:\n" + compilerOutput); // I18N
            }
        } catch (Exception e) {
            throw new CompilerException("Compilation error: " + e.getMessage() + ":\n" + compilerOutput, e); // I18N
        }
    }
}
