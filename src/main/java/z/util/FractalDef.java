package z.util;


import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import z.StringLiterals;
import z.compiler.CodeCompiler;
import z.core.AlgorithmDescriptor;
import z.core.IAlgorithm;
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

public class FractalDef {
    public static final String MY_FRACTALS_FILE_NAME = "myFractals.xml"; // NON-NLS
    public static final File MY_FRACTALS_FILE = new File(FileUtils.getFrexUserDir(), MY_FRACTALS_FILE_NAME);

    private static final Symbol COMPLEX_UNIT = Symbol.createSymbol("i"); // NON-NLS

    private static FractalDef[] fractalDefs;

    private String name;
    private String code;
    private Namespace parserNamespace;

    public FractalDef() {
        this("", "");
    }

    public FractalDef(String name, String code) {
        this.name = name;
        this.code = code;
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

    public Term parse() throws ParseException {
        if (parserNamespace == null) {
            parserNamespace = new Namespace();
            parserNamespace.addSymbol(COMPLEX_UNIT);
        }
        Parser parser = new ParserImpl(parserNamespace);
        String code = getCode();
        return parser.parse(code);
    }

    public AlgorithmDescriptor compile(Term term) throws ClassNotFoundException, IOException {
        File outputDir = new File(FileUtils.getFrexUserDir(), "myFractals"); // NON-NLS
        outputDir.mkdirs();
        deleteFiles(outputDir.listFiles());

        File[] classpathFiles = getClasspathFiles();
        for (File classpathFile : classpathFiles) {
            System.out.printf("classpathFile = %s%n", classpathFile);  // NON-NLS
        }

        CodeCompiler compiler = new CodeCompiler(outputDir, classpathFiles);
        String templateCode = getTemplateCode();

        Namespace complexNamespace = new Namespace();

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
// todo - add derivative code
/*
        Symbol sz = parserNamespace.getSymbol("z");
        if (sz == null) {
            sz = parserNamespace.addSymbol("z");
        }
        Term dterm = term.derivate(sz).simplify(); // NON-NLS
        Complex dz = dterm.createComplex(complexNamespace, COMPLEX_UNIT);
        Term dzx = Optimize.expandPowByIntExp(dz.getX().simplify());
        Term dzy = Optimize.expandPowByIntExp(dz.getY().simplify());
        varList.add(Symbol.createVariable("_dzx", dzx)); // NON-NLS
        varList.add(Symbol.createVariable("_dzy", dzy)); // NON-NLS
*/
        Optimize.replaceTermOccurences(varList, "t"); // NON-NLS
        StringBuilder variableDeclarations = new StringBuilder();
        for (Symbol var : varList) {
            variableDeclarations.append(String.format("        double %s;\n", var.getName())); // NON-NLS
        }
        StringBuilder variableAssignments = new StringBuilder();
        for (Symbol var : varList) {
            variableAssignments.append(String.format("            %s = %s;\n", var.getName(), var.getValue())); // NON-NLS
        }

        final String fname = getName().replace(' ', '_').replace('-', '_');
        final String fcode = getCode();
        String tcode = templateCode;
        tcode = tcode.replace("${fractalName}", fname); // NON-NLS
        tcode = tcode.replace("${fractalCode}", fcode); // NON-NLS
        tcode = tcode.replace("${variableDeclarations}", variableDeclarations); // NON-NLS
        tcode = tcode.replace("${variableAssignments}", variableAssignments); // NON-NLS

        getLogger().info(MessageFormat.format(StringLiterals.getString("log.compiling.0.to.1"), fcode, outputDir));
        getLogger().info(MessageFormat.format(StringLiterals.getString("log.code.n.0.n"), tcode));
        Class<? extends IAlgorithm> aClass = (Class<? extends IAlgorithm>) compiler.compile("z.contrib.fractals", // NON-NLS
                                                                                            "Mandelbrot" + fname, tcode); // NON-NLS
        AlgorithmDescriptor descriptor = new AlgorithmDescriptor(aClass);
        descriptor.setName(getName());
        descriptor.setOriginator(System.getProperty("user.name")); // NON-NLS
        return descriptor;
    }

    public static Logger getLogger() {
        return Logger.getAnonymousLogger();
    }

    public static void deleteFiles(File[] files) {
        if (files == null) {
            return;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                deleteFiles(file.listFiles());
            }
            if (file.delete()) {
                getLogger().info(MessageFormat.format(StringLiterals.getString("log.deleted.0"), file));
            } else {
                getLogger().info(MessageFormat.format(StringLiterals.getString("log.error.deleting.0"), file));
            }
        }
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

    public static AlgorithmDescriptor[] getUserAlgorithmDescriptors() {
        if (fractalDefs == null) {
            reloadUserFractals();
        }
        if (fractalDefs == null) {
            return new AlgorithmDescriptor[0];
        }
        ArrayList<AlgorithmDescriptor> descriptorArrayList = new ArrayList<AlgorithmDescriptor>();
        for (FractalDef fractalDef : fractalDefs) {
            try {
                Term term = fractalDef.parse();
                AlgorithmDescriptor descriptor = fractalDef.compile(term);
                descriptorArrayList.add(descriptor);
            } catch (Exception e) {
                Logger.getAnonymousLogger().log(Level.SEVERE, e.getMessage(), e);
            }
        }
        return descriptorArrayList.toArray(new AlgorithmDescriptor[descriptorArrayList.size()]);
    }

    public static FractalDef[] reloadUserFractals() {
        fractalDefs = null;
        if (MY_FRACTALS_FILE.exists()) {
            try {
                fractalDefs = loadFractals(MY_FRACTALS_FILE);
            } catch (Exception e) {
                // todo - log
            }
        }
        return fractalDefs;
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
                String name = nameElement != null && nameElement.getTextNormalize() != null ? nameElement.getTextNormalize() : "";
                String code = codeElement != null && codeElement.getTextNormalize() != null ? codeElement.getTextNormalize() : "";
                if (!name.isEmpty() && !code.isEmpty()) {
                    fractalDefs.add(new FractalDef(name, code));
                }
            } catch (Exception e) {
                // todo - log
            }
        }
        return fractalDefs.toArray(new FractalDef[fractalDefs.size()]);
    }

    public static void saveFractals(File file, FractalDef[] fractalDefs) throws JDOMException, IOException {
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
