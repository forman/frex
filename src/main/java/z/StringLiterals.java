package z;

import java.util.ResourceBundle;

public class StringLiterals {
    private static ResourceBundle bundle = ResourceBundle.getBundle("z/StringLiterals"); // NON-NLS

    public static String getString(String key) {
        return bundle.getString(key);
    }
}
