package z.core.color;

import junit.framework.TestCase;

public class ColorRegistryTest extends TestCase {

    public void testSizeIs193() {
        ColorRegistry registry = ColorRegistry.getInstance();
        ColorRegistry.Entry[] entries = registry.getEntries();
        assertEquals(193, entries.length);
    }

    public void testEntry6() {
        ColorRegistry registry = ColorRegistry.getInstance();
        ColorRegistry.Entry[] entries = registry.getEntries();
        ColorRegistry.Entry entry = entries[5];
        assertEquals("Honiggelb", entry.name);
        assertEquals("RAL 1005", entry.ralName);
        assertEquals(new RGBA(201, 135, 33), entry.colors[0]);
        assertEquals(entry.colors[0].brighter(0.00f), entry.colors[0]);
        assertEquals(entry.colors[0].brighter(0.25f), entry.colors[1]);
        assertEquals(entry.colors[0].brighter(0.50f), entry.colors[2]);
        assertEquals(entry.colors[0].brighter(0.75f), entry.colors[3]);
    }


    public void testFindEntry6() {
        ColorRegistry registry = ColorRegistry.getInstance();
        ColorRegistry.Descriptor descriptor;

        descriptor = registry.lookup(new RGBA(201, 135, 33));
        assertNotNull(descriptor);
        assertSame(registry.getEntry(5), descriptor.entry);
        assertEquals(0.0, descriptor.squareError);

        descriptor = registry.lookup(new RGBA(201, 135, 33).brighter(0.25f));
        assertNotNull(descriptor);
        assertSame(registry.getEntry(5), descriptor.entry);
        assertEquals(1, descriptor.brightnessLevel);
        assertEquals(0.0, descriptor.squareError);

        descriptor = registry.lookup(new RGBA(201, 135, 33).brighter(0.5f));
        assertNotNull(descriptor);
        assertSame(registry.getEntry(5), descriptor.entry);
        assertEquals(2, descriptor.brightnessLevel);
        assertEquals(0.0, descriptor.squareError);

        descriptor = registry.lookup(new RGBA(201, 135, 33).brighter(0.75f));
        assertNotNull(descriptor);
        assertSame(registry.getEntry(5), descriptor.entry);
        assertEquals(3, descriptor.brightnessLevel);
        assertEquals(0.0, descriptor.squareError);

        descriptor = registry.lookup(new RGBA(201, 135, 33).brighter(0.9f));
        assertNotNull(descriptor);
        assertTrue(0.0 < descriptor.squareError);

    }

    public void testLookupBasicColors6() {
        ColorRegistry registry = ColorRegistry.getInstance();
        ColorRegistry.Descriptor descriptor;

        descriptor = registry.lookup(RGBA.BLACK);
        assertEquals("Tiefschwarz", descriptor.entry.name);
        assertEquals(0, descriptor.brightnessLevel);

        descriptor = registry.lookup(RGBA.DARK_GRAY);
        assertEquals("Umbragrau", descriptor.entry.name);
        assertEquals(0, descriptor.brightnessLevel);

        descriptor = registry.lookup(RGBA.GRAY);
        assertEquals("Tiefschwarz", descriptor.entry.name);
        assertEquals(2, descriptor.brightnessLevel);

        descriptor = registry.lookup(RGBA.LIGHT_GRAY);
        assertEquals("Tiefschwarz", descriptor.entry.name);
        assertEquals(3, descriptor.brightnessLevel);

        descriptor = registry.lookup(RGBA.WHITE);
        assertEquals("Signalweiß", descriptor.entry.name);
        assertEquals(0, descriptor.brightnessLevel);

        descriptor = registry.lookup(RGBA.YELLOW);
        assertEquals("Leuchtgelb", descriptor.entry.name);
        assertEquals(0, descriptor.brightnessLevel);

        descriptor = registry.lookup(RGBA.ORANGE);
        assertEquals("Rapsgelb", descriptor.entry.name);
        assertEquals(0, descriptor.brightnessLevel);

        descriptor = registry.lookup(RGBA.RED);
        assertEquals("Leuchthellrot", descriptor.entry.name);
        assertEquals(0, descriptor.brightnessLevel);

        descriptor = registry.lookup(RGBA.GREEN);
        assertEquals("Gelbgrün", descriptor.entry.name);
        assertEquals(0, descriptor.brightnessLevel);

        descriptor = registry.lookup(RGBA.BLUE);
        assertEquals("Himmelblau", descriptor.entry.name);
        assertEquals(0, descriptor.brightnessLevel);
    }
}
