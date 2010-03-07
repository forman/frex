/**
 * Created by IntelliJ IDEA.
 * User: fomferra
 * Date: Jan 6, 2003
 * Time: 9:14:08 PM
 * To change this template use Options | File Templates.
 */
package z.math;

import z.math.term.Real;

import java.util.HashMap;
import java.util.Map;

public class Namespace {
    private final Map<String, Symbol> symbolMap = new HashMap<String, Symbol>(64);

    public Namespace() {
        addSymbol(Symbol.createConstant("PI", new Real(Math.PI))); // NON-NLS
        addSymbol(Symbol.createConstant("E", new Real(Math.E)));   // NON-NLS
    }

    public boolean isSymbolDefined(String name) {
        return getSymbol(name) != null;
    }

    public Symbol getSymbol(String name) {
        return symbolMap.get(name);
    }

    public Symbol addSymbol(String name) {
        Symbol symbol = getSymbol(name);
        if (symbol == null) {
            symbol = Symbol.createVariable(name, null);
            addSymbol(symbol);
        }
        return symbol;
    }

    public void addSymbol(Symbol symbol) {
        symbolMap.put(symbol.getName(), symbol);
    }

    public Symbol removeSymbol(Symbol symbol) {
        return symbolMap.remove(symbol.getName());
    }

    public String[] getAllSymbolNames() {
        return symbolMap.keySet().toArray(new String[symbolMap.size()]);
    }

    public Symbol[] getAllSymbols() {
        return symbolMap.values().toArray(new Symbol[symbolMap.size()]);
    }

    public static boolean isName(String token) {
        if (!Character.isLetter(token.charAt(0))) {
            return false;
        }
        for (int i = 1; i < token.length(); i++) {
            if (!Character.isLetterOrDigit(token.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
