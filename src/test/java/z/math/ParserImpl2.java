package z.math;

import z.StringLiterals;
import z.math.term.Functor;
import z.math.term.Real;
import z.math.term.Ref;
import z.math.term.Term;

/**
 * An instance of the <code>ParserX</code> class is used to convert a text
 * string representing an arithmetic band expression in a tree of terms which
 * can be evaluated. <p/>
 * <p/>
 * Refer to the <code>GlobalNamespace</code> class for the definition and
 * documentation of the available global constants and basic functions.
 */
public class ParserImpl2 implements Parser {
    static final int TT_EOF = -1;
    static final int TT_UNDEFINED = 0;
    static final int TT_NUMBER = 1;
    static final int TT_IDENTIFIER = 2;
    static final int TT_OPERATOR = 3;
    static final int TT_SPECIAL = 4;

    private Namespace namespace;
    private String code;
    private int pos;
    private boolean pushedBack;
    private int tokenType;
    private StringBuilder token;

    private Term[] termStack;
    private int tSP;

    private Functor[] opStack;
    private int opSP;


    public ParserImpl2(Namespace namespace) {
        this.namespace = namespace;
        termStack = new Term[1024];
        opStack = new Functor[1024];
        token = new StringBuilder(32);
    }

    public Namespace getNamespace() {
        return namespace;
    }


    /**
     * Parses the expression given in the code string.
     *
     * @param code the code string, for the syntax of valid expressions refer to
     *             the class description
     * @return the term
     * @throws z.math.ParseException if a parse reportError occurs
     */
    public Term parse(String code) throws ParseException {
        setCode(code);
        return parseExpr();
    }

    void setCode(String code) {
        if (code == null) {
            throw new IllegalArgumentException("code is null");
        }
        this.code = code;
        this.pos = 0;
        this.opSP = 0;
        this.tSP = 0;
    }

    /**
     * Implements the <code>parse</code> method. Calls
     * <code>parseTerm(false)</code> and throws an exception if the next token
     * is not the end-of-string.
     *
     * @return the term
     * @throws z.math.ParseException if a parse reportError occurs
     */
    private Term parseExpr() throws ParseException {
        Term term = parseExpr(false);
        int tt = nextToken();
        if (tt != TT_EOF) {
            reportError(StringLiterals.getString("ex.parser.incompleteExpr"));
        }
        return term;
    }

    /*
     * Parses a complete expression jex.term. Simply a wrapper for a
     * <code>parseAssign</code> method in order to signal that the assignment
     * operator '=' isSymbolDefined the highest operator precedence.
     */

    private Term parseExpr(boolean required) throws ParseException {

        // todo
        while (true) {
            Term term1 = parsePrimary(required);
            if (term1 == null) {
                break;
            }
            termStack[tSP++] = term1;
        }

        return termStack[0];
    }

    /*
     * Parses a primary expression: a constant number <i>number</i>, a constant
     * vector of numbers <i>'[' num1 ',' num2 ',' ... ']'</i>, a constant
     * string <i>'"'ASCII-characters'"'</i>, a variable reference <i>identifier</i>
     * or a function call <i>identifier '(' arg1 ',' arg2 ',' arg3 ',' ...')'</i>.
     */

    private Term parsePrimary(boolean required) throws ParseException {
        Term x = null;
        int tt = nextToken();
        if (tt == TT_NUMBER) {
            return new Real(getNumToken());
        }
        if (tt == TT_IDENTIFIER) {
            String name = getNameToken();
            Symbol symbol = namespace.getSymbol(name);
            if (symbol != null) {
                return new Ref(symbol);
            }
            if (Namespace.isName(name)) {
                symbol = namespace.addSymbol(name);
                return new Ref(symbol);
            }

        } else if (tt == '(') {
            x = parseExpr(true);
            tt = nextToken();
            if (tt != ')') {
                pushBack();
                reportError(StringLiterals.getString("ex.parser.missingParen"));
            }
        } else {
            if (required) {
                reportError(StringLiterals.getString("ex.parser.exprExpected"));
            }
            pushBack();
        }
        return x;
    }

    /*
     * Throws a <code>ParseException</code> with the given message
     */

    private static void reportError(String message) throws ParseException {
        throw new ParseException(message);
    }

    int getTokenType() {
        return tokenType;
    }

    String getToken() {
        return token.toString();
    }

    String getNameToken() {
        return getToken();
    }

    double getNumToken() {
        return Double.parseDouble(getToken());
    }


    void pushBack() {
        pushedBack = true;
    }

    int nextToken() throws ParseException {
        if (pushedBack) {
            pushedBack = false;
            return tokenType;
        }

        tokenType = TT_UNDEFINED;
        token.setLength(0);
        boolean dotSeen = false;
        while (true) {
            int pos0 = pos;
            char c = nextChar();

            if (tokenType == TT_UNDEFINED) {
                if (Character.isLetter(c) || c == '_') {
                    token.append(c);
                    tokenType = TT_IDENTIFIER;
                } else if (Character.isDigit(c)) {
                    token.append(c);
                    tokenType = TT_NUMBER;
                } else if (c == '-' || c == '+') {
                    char c2 = nextChar();
                    if (Character.isDigit(c2)) {
                        token.append(c);
                        token.append(c2);
                        tokenType = TT_NUMBER;
                    } else {
                        token.append(c);
                        tokenType = TT_OPERATOR;
                        break;
                    }
                } else if (isSpecial(c)) {
                    token.append(c);
                    tokenType = TT_SPECIAL;
                    break;
                } else if (isOperator(c)) {
                    token.append(c);
                    tokenType = TT_OPERATOR;
                    break;
                } else if (Character.isWhitespace(c)) {
                    // ok
                } else if (c == EOF) {
                    tokenType = TT_EOF;
                    break;
                }
            } else if (tokenType == TT_IDENTIFIER) {
                if (Character.isLetterOrDigit(c) || c == '_') {
                    token.append(c);
                } else {
                    pos = pos0;
                    break;
                }
            } else if (tokenType == TT_NUMBER) {
                // todo - consider number correct number syntax. e.g. [digits].[digits][e|E [+|-] digits]
                if (Character.isDigit(c) || (c == '.' && !dotSeen)) {
                    dotSeen = c == '.';
                    token.append(c);
                } else {
                    pos = pos0;
                    break;
                }
            } else {
                throw new ParseException("Illegal character: " + (char) c);
            }
        }

        return tokenType;
    }

    static final char EOF = (char) -1;

    private char nextChar() {
        if (pos == code.length()) {
            return EOF;
        }
        return code.charAt(pos++);
    }

    static boolean isOperator(char c) {
        return "*+-/=^".indexOf(c) >= 0;
    }

    static boolean isSpecial(char c) {
        return "()[]{}".indexOf(c) >= 0;
    }
}