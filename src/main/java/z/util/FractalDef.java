package z.util;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import z.compiler.Code;
import z.compiler.CodeCompiler;
import z.compiler.CompiledCode;
import z.core.AlgorithmDescriptor;
import z.core.AlgorithmRegistry;
import z.core.AlgorithmSubRegistry;
import z.math.Complex;
import z.math.Namespace;
import z.math.Optimize;
import z.math.ParseException;
import z.math.Parser;
import z.math.ParserImpl;
import z.math.Symbol;
import z.math.term.Functor;
import z.math.term.Term;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FractalDef implements Cloneable {
    public static final String MY_FRACTALS_FILE_NAME = "myFractals.xml"; // NON-NLS
    public static final String MY_FRACTALS_DIR_NAME = "myFractals"; // NON-NLS
    public static final File MY_FRACTALS_FILE = new File(FileUtils.getFrexUserDir(), MY_FRACTALS_FILE_NAME);
    public static final File MY_FRACTALS_DIR = new File(FileUtils.getFrexUserDir(), MY_FRACTALS_DIR_NAME);

    private static final Symbol COMPLEX_UNIT = Symbol.createSymbol("i"); // NON-NLS

    private String name;
    private String code;
    private boolean perturbation;
    private static final String PERTUBATION_BLOCK = "            if (_zy < _zx) {\n" +
    "                double t = _zx;\n" +
    "                _zx = _zy;\n" +
    "                _zy = t;\n" +
    "            }";

    public FractalDef() {
        this("", "", false);
    }

    public FractalDef(String name, String code, boolean perturbation) {
        this.name = name;
        this.code = code;
        this.perturbation = perturbation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isPerturbation() {
        return perturbation;
    }

    public void setPerturbation(boolean perturbation) {
        this.perturbation = perturbation;
    }

    @Override
    public String toString() {
        return "FractalDef{" +
                "name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", perturbation=" + perturbation +
                '}';
    }

    @SuppressWarnings({"CloneDoesntDeclareCloneNotSupportedException"})
    @Override
    public FractalDef clone() {
        try {
            return (FractalDef) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    String getClassName() {
        return "Mandelbrot_" + getJavaName(); // NON-NLS
    }

    File getJavaFile() {
        return new File(MY_FRACTALS_DIR, getClassName() + ".java"); // NON-NLS
    }

    File getClassFile() {
        return new File(MY_FRACTALS_DIR, getClassName() + ".class"); // NON-NLS
    }

    public static boolean buildAll() throws JDOMException, IOException {

        if (!MY_FRACTALS_FILE.exists()
                || MY_FRACTALS_DIR.exists() && !isBuildDirModified()) {
            return false;
        }

        MY_FRACTALS_DIR.mkdirs();
        File[] files = MY_FRACTALS_DIR.listFiles();
        for (File file : files) {
            file.delete();
        }

        FractalDef[] fractals = loadMyFractals();

        AlgorithmDescriptor[] algorithmDescriptors = compileAll(fractals);

        if (algorithmDescriptors.length > 0) {
            AlgorithmSubRegistry algorithmSubRegistry = AlgorithmRegistry.instance().getFractals();

            for (AlgorithmDescriptor descriptor : algorithmSubRegistry.getAll()) {
                if (CompiledCode.class.isAssignableFrom(descriptor.getAlgorithmClass())) {
                    algorithmSubRegistry.unregister(descriptor);
                }
            }

            for (AlgorithmDescriptor algorithmDescriptor : algorithmDescriptors) {
                algorithmSubRegistry.register(algorithmDescriptor);
            }
        }


        return true;
    }

    private static AlgorithmDescriptor[] compileAll(FractalDef[] fractalDefs) {
        CodeCompiler compiler = new CodeCompiler(MY_FRACTALS_DIR, getClasspathFiles());
        List<AlgorithmDescriptor> algorithmDescriptors = new ArrayList<AlgorithmDescriptor>();
        for (FractalDef fractalDef : fractalDefs) {
            try {
                String code = fractalDef.generateJavaCode();
                File file = new File(MY_FRACTALS_DIR, fractalDef.getClassName() + ".java");  // NON-NLS
                FileWriter writer = new FileWriter(file);
                try {
                    writer.write(code);
                    Class<?> compiledClass = compiler.compile(new Code(fractalDef.getClassName(), code));
                    AlgorithmDescriptor descriptor = new AlgorithmDescriptor(compiledClass);
                    descriptor.setName(fractalDef.getName());
                    descriptor.setOriginator(System.getProperty("user.name")); // NON-NLS
                    algorithmDescriptors.add(descriptor);
                } finally {
                    writer.close();
                }
            } catch (IOException e) {
                // todo
                getLogger().log(Level.SEVERE, e.getMessage(), e);
            } catch (ParseException e) {
                // todo
                getLogger().log(Level.SEVERE, e.getMessage(), e);
            } catch (ClassNotFoundException e) {
                // todo
                getLogger().log(Level.SEVERE, e.getMessage(), e);
            }
        }
        return algorithmDescriptors.toArray(new AlgorithmDescriptor[algorithmDescriptors.size()]);
    }

    private String generateJavaCode() throws ParseException {

        List<Symbol> varList = parse();
        StringBuilder variableDeclarations = new StringBuilder();
        for (Symbol var : varList) {
            variableDeclarations.append(String.format("        double %s;\n", var.getName())); // NON-NLS
        }
        StringBuilder variableAssignments = new StringBuilder();
        for (Symbol var : varList) {
            variableAssignments.append(String.format("            %s = %s;\n", var.getName(), var.getValue())); // NON-NLS
        }

        String javaCode = getTemplateCode();
        javaCode = javaCode.replace("${fractalName}", getJavaName()); // NON-NLS
        javaCode = javaCode.replace("${fractalCode}", getCode()); // NON-NLS
        javaCode = javaCode.replace("${variableDeclarations}", variableDeclarations); // NON-NLS
        javaCode = javaCode.replace("${variableAssignments}", variableAssignments); // NON-NLS
        javaCode = javaCode.replace("${perturbationBlock}", isPerturbation() ? PERTUBATION_BLOCK  : ""); // NON-NLS
        return javaCode;
    }

    public List<Symbol> parse() throws ParseException {
        Namespace complexNamespace = new Namespace();
        Namespace parserNamespace = new Namespace();
        parserNamespace.addSymbol(COMPLEX_UNIT);

        Parser parser = new ParserImpl(parserNamespace);
        Term term = parser.parse(code);
        Complex z = term.createComplex(complexNamespace, COMPLEX_UNIT);
        Term zx = Optimize.expandPowByIntExp(z.getX().simplify());
        Term zy = Optimize.expandPowByIntExp(z.getY().simplify());

        List<Symbol> varList = new ArrayList<Symbol>(32);
        Symbol szx = complexNamespace.getSymbol("zx"); // NON-NLS
        Symbol szy = complexNamespace.getSymbol("zy"); // NON-NLS
        varList.add(Symbol.createVariable("zxx", Functor.mul(Functor.ref(szx), Functor.ref(szx)))); // NON-NLS
        varList.add(Symbol.createVariable("zyy", Functor.mul(Functor.ref(szy), Functor.ref(szy)))); // NON-NLS
        varList.add(Symbol.createVariable("_zx", zx)); // NON-NLS
        varList.add(Symbol.createVariable("_zy", zy)); // NON-NLS

        Optimize.replaceTermOccurences(varList, "t"); // NON-NLS

        return varList;
    }

    private String getJavaName() {
        return getName().replace(' ', '_').replace('-', '_');
    }

    private static boolean isBuildDirModified() throws JDOMException, IOException {
        boolean modified = false;
        FractalDef[] fractalDefs = loadMyFractals();
        for (FractalDef fractalDef : fractalDefs) {
            File javaFile = fractalDef.getJavaFile();
            if (!javaFile.exists() || javaFile.lastModified() < MY_FRACTALS_FILE.lastModified()) {
                modified = true;
            }
            File classFile = fractalDef.getClassFile();
            if (!classFile.exists() || classFile.lastModified() < javaFile.lastModified()) {
                modified = true;
            }
        }
        return modified;
    }

    public static Logger getLogger() {
        return Logger.getAnonymousLogger();
    }

    public static String getTemplateCode() {
        String resource = "/z/core/support/fractals/Mandelbrot.template"; // NON-NLS
        InputStream stream = FractalDef.class.getResourceAsStream(resource);
        if (stream == null) {
            throw new IllegalStateException(MessageFormat.format("Resource not found: {0}", resource)); // NON-NLS
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        try {
            StringBuilder sb = new StringBuilder();
            try {
                String s;
                while ((s = reader.readLine()) != null) {
                    sb.append(s);
                    sb.append("\n");
                }
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
            return sb.toString();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }

    public static File[] getClasspathFiles() {
        ArrayList<File> classpath = new ArrayList<File>();
        File classpathFile = getClasspathFile();
        if (classpathFile != null) {
            classpath.add(classpathFile);
        }
        // This hard-coded dependency is only used while compiling from the IDE
        File jdomJarFile = new File(FileUtils.getUserHome(),
                                    ".m2/repository/jdom/jdom/1.1/jdom-1.1.jar");  // NON-NLS
        if (jdomJarFile.exists()) {
            classpath.add(jdomJarFile);
        }
        File[] libFiles = FileUtils.getFrexLibDir().listFiles();
        if (libFiles != null) {
            classpath.addAll(Arrays.asList(libFiles));
        }
        return classpath.toArray(new File[classpath.size()]);
    }

    public static File getClasspathFile() {
        CodeSource source = FractalDef.class.getProtectionDomain().getCodeSource();
        File classPathDir = null;
        if (source != null) {
            URL location = source.getLocation();
            if (location != null) {
                URI uri;
                try {
                    uri = location.toURI();
                } catch (URISyntaxException e) {
                    throw new IllegalStateException(e);
                }
                String scheme = uri.getScheme();
                if (scheme.equals("file")) { // NON-NLS
                    classPathDir = new File(uri.getPath());
                }
            }
        }
        return classPathDir;
    }


    public static FractalDef[] loadMyFractals() throws JDOMException, IOException {
        return loadFractals(MY_FRACTALS_FILE);
    }

    public static FractalDef[] loadFractals(File file) throws JDOMException, IOException {
        ArrayList<FractalDef> fractalDefs = new ArrayList<FractalDef>(16);
        final SAXBuilder builder = new SAXBuilder();
        final Document document = builder.build(file);
        List children = document.getRootElement().getChildren("fractal"); // NON-NLS
        for (Object child : children) {
            try {
                Element fractalElement = (Element) child;
                Element nameElement = fractalElement.getChild("name"); // NON-NLS
                Element codeElement = fractalElement.getChild("code"); // NON-NLS
                Element diffElement = fractalElement.getChild("perturbation"); // NON-NLS
                String name = nameElement != null && nameElement.getTextNormalize() != null ? nameElement.getTextNormalize() : "";
                String code = codeElement != null && codeElement.getTextNormalize() != null ? codeElement.getTextNormalize() : "";
                boolean diff = diffElement != null
                        && diffElement.getTextNormalize() != null
                        && Boolean.parseBoolean(diffElement.getTextNormalize());
                if (!name.isEmpty() && !code.isEmpty()) {
                    fractalDefs.add(new FractalDef(name, code, diff));
                }
            } catch (Exception e) {
                // todo - log
            }
        }
        return fractalDefs.toArray(new FractalDef[fractalDefs.size()]);
    }

    public static void saveMyFractals(FractalDef[] fractalDefs) throws JDOMException, IOException {
        saveFractals(fractalDefs, MY_FRACTALS_FILE);
    }

    public static void saveFractals(FractalDef[] fractalDefs, File file) throws JDOMException, IOException {
        final XMLOutputter xmlOutputter = new XMLOutputter();
        FileWriter writer = new FileWriter(file);

        Element fractalsElement = new Element("fractals"); // NON-NLS
        for (FractalDef fractalDef : fractalDefs) {
            Element fractalElement = new Element("fractal"); // NON-NLS

            Element nameElement = new Element("name"); // NON-NLS
            nameElement.setText(fractalDef.getName());
            fractalElement.addContent(nameElement);

            Element codeElement = new Element("code"); // NON-NLS
            codeElement.setText(fractalDef.getCode());
            fractalElement.addContent(codeElement);

            Element diffElement = new Element("perturbation"); // NON-NLS
            diffElement.setText(fractalDef.isPerturbation() + "");
            fractalElement.addContent(diffElement);

            fractalsElement.addContent(fractalElement);
        }

        xmlOutputter.setFormat(Format.getPrettyFormat());
        try {
            xmlOutputter.output(fractalsElement, writer);
        } finally {
            writer.close();
        }
    }
}
