/*
 * $Id
 *
 * Copyright (C) 2009 by Norman Fomferra
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package z.util;

import junit.framework.TestCase;

import java.io.File;

public class FileUtilsTest extends TestCase {
    public void testEnsureExtension() {
        File f = new File("default.plane");
        assertSame(f, FileUtils.ensureExtension(f, ".plane"));
        assertEquals(new File("default.plane"), FileUtils.ensureExtension(new File("default.data"), ".plane"));
        assertEquals(new File("default.plane"), FileUtils.ensureExtension(new File("default.data"), ".plane"));
        assertEquals(new File("default.plane"), FileUtils.ensureExtension(new File("default"), ".plane"));
        assertEquals(new File("default.plane"), FileUtils.ensureExtension(new File("default."), ".plane"));
        assertEquals(new File(".default.plane"), FileUtils.ensureExtension(new File(".default"), ".plane"));
        assertEquals(new File("default.x.plane"), FileUtils.ensureExtension(new File("default.x.data"), ".plane"));
        assertEquals(new File("/home/norman/frex/default.plane"), FileUtils.ensureExtension(new File("/home/norman/frex/default.data"), ".plane"));
        assertEquals(new File("C:\\Programme\frex\\default.plane"), FileUtils.ensureExtension(new File("C:\\Programme\frex\\default.data"), ".plane"));
    }
}
