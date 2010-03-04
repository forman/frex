package z.math;

import z.math.term.Term;

public interface Parser {
    Namespace getNamespace();

    /**
     * Parses the expression given in the code string.
     *
     * @param code the code string, for the syntax of valid expressions refer to
     *             the class description
     * @return the term
     * @throws z.math.ParseException if a parse reportError occurs
     */
    Term parse(String code) throws ParseException;
}
