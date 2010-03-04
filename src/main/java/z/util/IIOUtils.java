/*
 * Created at 31.01.2004 17:30:13
 * Copyright (c) 2004 by Norman Fomferra
 */
package z.util;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class IIOUtils {
    public static String[] getUniqueImageFormatNames() {
        final String[] writerFormatNames = ImageIO.getWriterFormatNames();
        final Set<String> formatSet = new HashSet<String>();
        for (int i = 0; i < writerFormatNames.length; i++) {
            final String writerFormatName = writerFormatNames[i];
            System.out.println("writerFormatName = " + writerFormatName);
            formatSet.add(writerFormatName.toUpperCase());
        }
        final String[] uniqueFormatNames = new String[formatSet.size()];
        formatSet.toArray(uniqueFormatNames);
        Arrays.sort(uniqueFormatNames);
        return uniqueFormatNames;
    }

    public static ImageWriter[] getAllImageWriters() {
        final String[] uniqueImageFormatNames = getUniqueImageFormatNames();
        ImageWriter[] imageWriters = new ImageWriter[uniqueImageFormatNames.length];
        for (int i = 0; i < uniqueImageFormatNames.length; i++) {
            String formatName = uniqueImageFormatNames[i];
            final Iterator iterator = ImageIO.getImageWritersByFormatName(formatName);
            imageWriters[i] = (ImageWriter) iterator.next();
        }
        return imageWriters;
    }
}
