package z.compiler;

import junit.framework.TestCase;

public class CodeCompilerTest extends TestCase {
    public void testGetClassName() {
        assertEquals("A", CodeCompiler.getClassName("A")); // NON-NLS
        assertEquals("B", CodeCompiler.getClassName("B.java")); // NON-NLS
        assertEquals("C", CodeCompiler.getClassName("/a/b/C.class")); // NON-NLS
        assertEquals("D", CodeCompiler.getClassName("/D.class")); // NON-NLS
    }
}
