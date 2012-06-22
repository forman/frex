package z.compiler;

/**
 * @author Norman Fomferra
 */
public class CompilerException extends Exception {
    public CompilerException(String message) {
        super(message);
    }

    public CompilerException(String message, Throwable cause) {
        super(message, cause);
    }
}
