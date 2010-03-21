package z.core.color;

import z.StringLiterals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.StringTokenizer;

public class ColorRegistry {

    private static final String DATA_RESOURCE_PATH = "ColorRegistry.data"; // NON-NLS
    private static final int BRIGHTNESS_LEVEL_COUNT = 4;
    private static final float BRIGHTNESS_INCREMENT = 1.0f / (float) BRIGHTNESS_LEVEL_COUNT;

    private static ColorRegistry instance = new ColorRegistry();
    private Entry[] entries;
    private Map<Integer, Descriptor> descriptors;

    private ColorRegistry() {
        if (entries == null) {
            try {
                entries = loadEntries();
                descriptors = new HashMap<Integer, Descriptor>((2 + BRIGHTNESS_LEVEL_COUNT) * entries.length);
                for (int brightnessLevel = 0; brightnessLevel < BRIGHTNESS_LEVEL_COUNT; brightnessLevel++) {
                    for (Entry entry : entries) {
                        Descriptor descriptor = new Descriptor(entry, brightnessLevel, 0.0);
                        int value = entry.colors[brightnessLevel].getValue();
                        if (descriptors.get(value) == null) {
                            descriptors.put(value, descriptor);
                        }
                    }
                }
            } catch (Throwable e) {
                throw new IllegalStateException("failed to load RAL colour definition resource " + DATA_RESOURCE_PATH, e);
            }
        }
    }

    public static ColorRegistry getInstance() {
        return instance;
    }

    public Descriptor lookup(RGBA color) {
        Descriptor descriptor = descriptors.get(color.getValue());
        if (descriptor != null) {
            return descriptor;
        }

        Entry bestEntry = null;
        int bestBrightnessLevel = -1;
        int bestDistance = Integer.MAX_VALUE;
        for (int brightnessLevel = 0; brightnessLevel < BRIGHTNESS_LEVEL_COUNT; brightnessLevel++) {
            for (Entry entry : entries) {
                RGBA otherColor = entry.colors[brightnessLevel];
                int dr = otherColor.getR() - color.getR();
                int dg = otherColor.getG() - color.getG();
                int db = otherColor.getB() - color.getB();
                int distance = dr * dr + dg * dg + db * db;
                if (distance < bestDistance) {
                    bestDistance = distance;
                    bestEntry = entry;
                    bestBrightnessLevel = brightnessLevel;
                }
            }
        }
        return new Descriptor(bestEntry, bestBrightnessLevel, (double) bestDistance / (255.0 * 255.0));
    }

    public Entry getEntry(int index) {
        return entries[index];
    }

    public RGBA getColor(int index) {
        return entries[index].colors[0];
    }

    public RGBA getColor(int index, int brightnessLevel) {
        return entries[index].colors[brightnessLevel];
    }

    public int getEntryCount() {
        return entries.length;
    }

    public int getBrightnessLevelCount() {
        return BRIGHTNESS_LEVEL_COUNT;
    }

    public Entry[] getEntries() {
        return entries.clone();
    }

    private static Entry[] loadEntries() throws IOException, ParseException {
        final InputStream stream = ColorRegistry.class.getResourceAsStream(DATA_RESOURCE_PATH);
        final BufferedReader reader = new BufferedReader(new InputStreamReader(stream, Charset.forName("UTF-8"))); // NON-NLS
        try {
            return loadEntries(reader);
        } finally {
            reader.close();
        }
    }

    private static Entry[] loadEntries(final BufferedReader reader) throws IOException, ParseException {
        LinkedList<Entry> list = new LinkedList<Entry>();
        int index = 0;
        while (true) {
            String line = reader.readLine();
            if (line == null) {
                break;
            }
            line = line.trim();
            if (line.length() != 0) {
                final StringTokenizer st = new StringTokenizer(line.trim(), ";", false);
                final String ralName = st.nextToken().trim();
                final String name = st.nextToken().trim();
                final String rgbStr = st.nextToken().trim();
                final RGBA rgb = RGBA.parseRGBA(rgbStr);
                final Entry entry = new Entry(index, ralName, name, rgb);
                list.add(entry);
                index++;
            }
        }
        return list.toArray(new Entry[list.size()]);
    }

    public static class Descriptor {
        public final Entry entry;
        public final int brightnessLevel;
        public final double squareError;

        public Descriptor(Entry entry, int brightnessLevel, double squareError) {
            this.entry = entry;
            this.brightnessLevel = brightnessLevel;
            this.squareError = squareError;
        }

        @Override
        public String toString() {
            return getClass().getName() + "[" + getDescription() + "]";
        }

        public String getDescription() {
            String s0;
            if (brightnessLevel == 0) {
                s0 = entry.ralName;
            } else {
                s0 = MessageFormat.format(StringLiterals.getString("gui.color.name.brighter"), entry.ralName, brightnessLevel * 25);
            }
            return MessageFormat.format(StringLiterals.getString("gui.color.name"), entry.name, s0);
        }
    }

    public static class Entry {
        public final int index;

        public final String ralName;

        public final String name;

        public final RGBA[] colors;

        public Entry(int index, String ralName, String name, RGBA color) {
            this.index = index;
            this.ralName = ralName;
            this.name = name;
            this.colors = new RGBA[4];
            for (int i = 0; i < colors.length; i++) {
                colors[i] = color.brighter((float) i * BRIGHTNESS_INCREMENT);
            }
        }

        @Override
        public String toString() {
            return getClass().getName() + "[index=" + index + ",name=" + name + ",color=" + colors[0] + "]";   // NON-NLS
        }
    }
}