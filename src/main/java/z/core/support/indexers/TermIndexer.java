package z.core.support.indexers;

import z.core.Indexer;
import z.math.Namespace;
import z.math.ParseException;
import z.math.ParserImpl;
import z.math.Symbol;
import z.math.term.Real;
import z.math.term.Term;

public final class TermIndexer extends Indexer {
    private final Namespace namespace;

    private final Real xTerm;

    private final Real yTerm;

    private String expression;

    private Term term;

    public TermIndexer() {
        xTerm = new Real(0.0);
        yTerm = new Real(0.0);
        namespace = new Namespace();
        namespace.addSymbol(Symbol.createVariable("x", xTerm)); // NON-NLS
        namespace.addSymbol(Symbol.createVariable("y", yTerm)); // NON-NLS
    }

    public void reset() {
        super.reset();
        expression = "x"; // NON-NLS
        term = null;
    }

    public void prepare() {
        super.prepare();
        try {
            term = new ParserImpl(namespace).parse(expression);
        } catch (ParseException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        String oldExpression = expression;
        this.expression = expression;
        this.term = null;
        propertyChangeSupport.firePropertyChange("expression", // NON-NLS 
                                                 oldExpression,
                                                 expression);
    }

    public Term getTerm() {
        return term;
    }

    public final double computeIndex(double x, double y) {
        xTerm.setValue(x);
        yTerm.setValue(y);
        return term.evaluate();
    }
}
