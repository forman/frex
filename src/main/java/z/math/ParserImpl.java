package z.math;

import z.math.term.Functor;
import z.math.term.Real;
import z.math.term.Ref;
import z.math.term.Term;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;

/**
 * An instance of the <code>ParserX</code> class is used to convert a text
 * string representing an arithmetic band expression in a tree of terms which
 * can be evaluated. <p/>
 * <p/>
 * Refer to the <code>GlobalNamespace</code> class for the definition and
 * documentation of the available global constants and basic functions.
 */
public class ParserImpl implements Parser {

    /**
     * The environment used to resolve names.
     */
    private Namespace namespace;

    /**
     * The tokenizer used by this parser.
     */
    private StreamTokenizer tokenizer;

    public ParserImpl(Namespace namespace) {
        this.namespace = namespace;
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
     * @throws ParseException if a parse reportError occurs
     */
    public Term parse(String code) throws ParseException {
        if (code == null) {
            throw new IllegalArgumentException("code is null");
        }
        tokenizer = new StreamTokenizer(new StringReader(code));
        tokenizer.resetSyntax();
        tokenizer.whitespaceChars(0, 32);
        tokenizer.ordinaryChars(33, 255);
        tokenizer.wordChars('a', 'z');
        tokenizer.wordChars('A', 'Z');
        tokenizer.wordChars('_', '_');
        tokenizer.wordChars('.', '.');
        tokenizer.wordChars('0', '9');
        tokenizer.eolIsSignificant(false);
        tokenizer.slashSlashComments(false);
        tokenizer.slashStarComments(false);
        Term term = parseExpr();
        tokenizer = null;
        return term;
    }

    /**
     * Implements the <code>parse</code> method. Calls
     * <code>parseTerm(false)</code> and throws an exception if the next token
     * is not the end-of-string.
     *
     * @return the term
     * @throws ParseException if a parse reportError occurs
     */
    private Term parseExpr() throws ParseException {
        Term term = parseExpr(false);
        int tt = nextToken();
        if (tt != StreamTokenizer.TT_EOF) {
            reportError("Incomplete expression."); /* I18N */
        }
        return term;
    }

    /*
     * Parses a complete expression jex.term. Simply a wrapper for a
     * <code>parseAssign</code> method in order to signal that the assignment
     * operator '=' isSymbolDefined the highest operator precedence.
     */
    private Term parseExpr(boolean required) throws ParseException {
        return parseAdd(required);
    }

    /*
     * Parses an additive expression <i>x '+' y</i> or <i>x '-' y</i>.
     */
    private Term parseAdd(boolean required) throws ParseException {
        Term x1 = parseMul(required);
        while (x1 != null) {
            int tt = nextToken();
            if (tt == '+') {
                Term x2 = parseMul(true);
                x1 = Functor.add(x1, x2);
            } else if (tt == '-') {
                Term x2 = parseMul(true);
                x1 = Functor.sub(x1, x2);
            } else {
                tokenizer.pushBack();
                break;
            }
        }
        return x1;
    }

    /*
     * Parses a multiplicative expression <i>x '*' y</i>, <i>x '/' y</i>.
     */
    private Term parseMul(boolean required) throws ParseException {
        Term x1 = parsePow(required);
        while (x1 != null) {
            int tt = nextToken();
            if (tt == '*') {
                Term x2 = parsePow(true);
                x1 = Functor.mul(x1, x2);
            } else if (tt == '/') {
                Term x2 = parsePow(true);
                x1 = Functor.div(x1, x2);
            } else {
                tokenizer.pushBack();
                break;
            }
        }
        return x1;
    }

    /*
     * Parses a power expression <i>x '^' y</i>.
     */
    private Term parsePow(boolean required) throws ParseException {
        Term x1 = parseUnary(required);
        while (x1 != null) {
            int tt = nextToken();
            if (tt == '^') {
                Term x2 = parseUnary(true);
                x1 = Functor.pow(x1, x2);
            } else {
                tokenizer.pushBack();
                break;
            }
        }
        return x1;
    }

    /*
     * Parses an unary expression <i>'+' x</i>, <i>'-' x</i>, <i>'!' x</i>,
     * <i>'sin' x</i>, ...
     */
    private Term parseUnary(boolean required) throws ParseException {
        Term x1 = null;
        int tt = nextToken();
        if (tt == '+') {
            x1 = parseUnary(true);
        } else if (tt == '-') {
            Term x2 = parseUnary(true);
            if (x2 instanceof Real) {
                x1 = new Real(-x2.evaluate());
            } else {
                x1 = Functor.neg(x2);
            }
        } else if (tt == StreamTokenizer.TT_WORD) {
            String name = tokenizer.sval;
            if (name.equalsIgnoreCase("sin")) {
                Term x2 = parseUnary(true);
                x1 = Functor.sin(x2);
            } else if (name.equalsIgnoreCase("cos")) {
                Term x2 = parseUnary(true);
                x1 = Functor.cos(x2);
            } else if (name.equalsIgnoreCase("sinh")) {
                Term x2 = parseUnary(true);
                x1 = Functor.sinh(x2);
            } else if (name.equalsIgnoreCase("cosh")) {
                Term x2 = parseUnary(true);
                x1 = Functor.cosh(x2);
            } else if (name.equalsIgnoreCase("exp")) {
                Term x2 = parseUnary(true);
                x1 = Functor.exp(x2);
            } else if (name.equalsIgnoreCase("sqrt")) {
                Term x2 = parseUnary(true);
                x1 = Functor.sqrt(x2);
            } else {
                tokenizer.pushBack();
                x1 = parsePrimary(required);
            }
        } else {
            tokenizer.pushBack();
            x1 = parsePrimary(required);
        }
        return x1;
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
        if (tt == StreamTokenizer.TT_NUMBER) {
            x = new Real(tokenizer.nval);
        } else if (tt == StreamTokenizer.TT_WORD) {
            String name = tokenizer.sval;
            Symbol symbol = namespace.getSymbol(name);
            if (symbol != null) {
                x = new Ref(symbol);
            } else if (Namespace.isName(name)) {
                symbol = namespace.addSymbol(name);
                x = new Ref(symbol);
            } else { // maybe a number with exp part not handdled by stream
                // tokenizer, e.g. 0.25E-03
                try {
                    double value = Double.parseDouble(tokenizer.sval);
                    x = new Real(value);
                } catch (NumberFormatException e) {
                }
            }
        } else if (tt == '(') {
            x = parseExpr(true);
            tt = nextToken();
            if (tt != ')') {
                tokenizer.pushBack();
                reportError("Missing ')'."); /* I18N */
            }
        } else {
            if (required) {
                reportError("Expression expected."); /* I18N */
            }
            tokenizer.pushBack();
        }
        return x;
    }

    /*
     * Throws a <code>ParseException</code> with the given message
     */
    private static void reportError(String message) throws ParseException {
        throw new ParseException(message);
    }

    private int nextToken() throws ParseException {
        try {
            return tokenizer.nextToken();
        } catch (IOException e) {
            reportError("internal squareError: " + e.getLocalizedMessage());
        }
        return 0;
    }
}
