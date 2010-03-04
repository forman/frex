package z.util;


import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import z.compiler.CodeCompiler;
import z.math.Complex;
import z.math.Namespace;
import z.math.Optimize;
import z.math.ParseException;
import z.math.Parser;
import z.math.ParserImpl;
import z.math.Symbol;
import z.math.term.Functor;
import z.math.term.Term;
import z.core.AlgorithmDescriptor;
import z.core.IAlgorithm;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class FractalDef {
    private static final Symbol COMPLEX_UNIT = Symbol.createSymbol("i");

    private String name;
    private String code;
    public static final String MY_FRACTALS_XML = "myFractals.xml";
    public static final File DEFAULT_USER_FRACTALS_FILE = new File(FileUtils.getFrexUserDir(), MY_FRACTALS_XML);

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
        Namespace parserNamespace = new Namespace();
        parserNamespace.addSymbol(COMPLEX_UNIT);
        Parser parser = new ParserImpl(parserNamespace);
        String code = getCode();
        return parser.parse(code);
    }

    public AlgorithmDescriptor compile(Term term) throws ClassNotFoundException, IOException {
        File outputDir = new File(FileUtils.getFrexUserDir(), "myFractals");
        outputDir.mkdirs();
        deleteFiles(outputDir.listFiles());

        File[] classpathFiles = getClasspathFiles();
        for (File classpathFile : classpathFiles) {
            System.out.println("classpathFile = " + classpathFile);
        }

        CodeCompiler compiler = new CodeCompiler(outputDir, classpathFiles);
        String templateCode = getTemplateCode();

        Namespace complexNamespace = new Namespace();
        Complex z = term.createComplex(complexNamespace, COMPLEX_UNIT);
        Term zx = z.getX();
        Term zy = z.getY();
        zx = zx.simplify();
        zy = zy.simplify();
        zx = Optimize.expandPowByIntExp(zx);
        zy = Optimize.expandPowByIntExp(zy);

        List<Symbol> varList = new ArrayList<Symbol>(32);
        Symbol szx = complexNamespace.getSymbol("zx");
        Symbol szy = complexNamespace.getSymbol("zy");
        varList.add(Symbol.createVariable("t1", Functor.mul(Functor.ref(szx), Functor.ref(szx))));
        varList.add(Symbol.createVariable("t2", Functor.mul(Functor.ref(szy), Functor.ref(szy))));
        varList.add(Symbol.createVariable("zzx", zx));
        varList.add(Symbol.createVariable("zzy", zy));
        Optimize.replaceTermOccurences(varList, "t");
        StringBuilder variableDeclarations = new StringBuilder();
        for (Symbol var : varList) {
            variableDeclarations.append("    " + "double " + var.getName() + ";\n");
        }
        StringBuilder variableAssignments = new StringBuilder();
        for (Symbol var : varList) {
            variableAssignments.append("    " + "    " + var.getName() + " = " + var.getValue() + ";\n");
        }

        final String fname = getName().replace(' ', '_').replace('-', '_');
        final String fcode = getCode();
        String tcode = templateCode;
        tcode = tcode.replace("${fractalName}", fname);
        tcode = tcode.replace("${fractalCode}", fcode);
        tcode = tcode.replace("${variableDeclarations}", variableDeclarations);
        tcode = tcode.replace("${variableAssignments}", variableAssignments);

        getLogger().info("Compiling '" + fcode + "' to " + outputDir);
        getLogger().info("Code:\n" + tcode + "\n");
        Class<? extends IAlgorithm> aClass = (Class<? extends IAlgorithm>) compiler.compile("z.contrib.fractals", "Mandelbrot" + fname, tcode);
        AlgorithmDescriptor descriptor = new AlgorithmDescriptor(aClass);
        descriptor.setName(fname);
        descriptor.setOriginator(System.getProperty("user.name"));
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
                getLogger().info("Deleted: " + file);
            } else {
                getLogger().info("Error deleting: " + file);
            }
        }
    }

    public static String getTemplateCode() {
        String resource = "/z/core/support/fractals/Mandelbrot.template";
        InputStream stream = FractalDef.class.getResourceAsStream(resource);
        if (stream == null) {
            throw new IllegalStateException("resource not found: " + resource);
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
        File jdomJarFile = new File(FileUtils.getUserHome(), ".m2/repository/jdom/jdom/1.1/jdom-1.1.jar");
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
                if (scheme.equals("file")) {
                    classPathDir = new File(uri.getPath());
                }
            }
        }
        return classPathDir;
    }


    public static FractalDef[] loadUserFractals() throws JDOMException, IOException {
        return loadFractals(DEFAULT_USER_FRACTALS_FILE);
    }

    public static FractalDef[] loadFractals(File file) throws JDOMException, IOException {
        ArrayList<FractalDef> fractalDefs = new ArrayList<FractalDef>(16);
        final SAXBuilder builder = new SAXBuilder();
        final Document document = builder.build(file);
        List children = document.getRootElement().getChildren("fractal");
        for (Object child : children) {
            Element fractalElement = (Element) child;
            Element nameElement = fractalElement.getChild("name");
            Element codeElement = fractalElement.getChild("code");
            String name = nameElement != null && nameElement.getTextNormalize() != null ? nameElement.getTextNormalize() : "";
            String code = codeElement != null && codeElement.getTextNormalize() != null ? codeElement.getTextNormalize() : "";
            if (!name.isEmpty() && !code.isEmpty()) {
                fractalDefs.add(new FractalDef(name, code));
            }
        }
        return fractalDefs.toArray(new FractalDef[fractalDefs.size()]);
    }

    public static void saveFractals(File file, FractalDef[] fractalDefs) throws JDOMException, IOException {
        final XMLOutputter xmlOutputter = new XMLOutputter();
        FileWriter writer = new FileWriter(file);

        Element fractalsElement = new Element("fractals");
        for (FractalDef fractalDef : fractalDefs) {
            Element fractalElement = new Element("fractal");
            Element nameElement = new Element("name");
            nameElement.setText(fractalDef.getName());
            fractalElement.addContent(nameElement);
            Element codeElement = new Element("code");
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
