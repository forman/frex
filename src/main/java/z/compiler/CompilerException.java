package z.compiler;

/**
 * @author Norman Fomferra
 */
public class CompilerException extends Exception {
    private final String compilerOutput;

    public CompilerException(String message) {
        this(message, null, null);
    }

    public CompilerException(String message, Throwable cause) {
        this(message, null, cause);
    }

    public CompilerException(String message, String compilerOutput) {
        this(message, compilerOutput, null);
    }

    public CompilerException(String message, String compilerOutput, Throwable cause) {
        super(message, cause);
        this.compilerOutput = compilerOutput;
    }

    public String getCompilerOutput() {
        return compilerOutput;
    }
}
