/**
 * Created by IntelliJ IDEA.
 * User: fomferra
 * Date: Jan 6, 2003
 * Time: 11:10:07 PM
 * To change this template use Options | File Templates.
 */
package z.math;

public class ParseException extends Exception {

    /**
     * Constructs a new exception with the specified detail message. The cause
     * is not initialized, and may subsequently be initialized by a call to
     * {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for later
     *                retrieval by the {@link #getMessage()} method.
     */
    public ParseException(String message) {
        super(message);
    }
}
