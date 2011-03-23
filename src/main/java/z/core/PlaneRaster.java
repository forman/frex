/**
 *
 */
package z.core;

public class PlaneRaster {
    private final int width;

    private final int height;

    private final float[] rawData;

    private final int[] pixelData;

    private Statistics innerStatistics;

    private Statistics outerStatistics;

    private Statistics totalStatistics;

    public static class Statistics {
        public final float min;

        public final float max;

        public final float mean;

        public final int count;

        public final int[] histogram;

        public final int histogramMin;

        public final int histogramMax;

        public Statistics(int count, float min, float max, float sum, int[] histogram) {
            this.count = count;
            if (count == 0) {
                this.min = 0.0F;
                this.max = 0.0F;
                this.mean = 0.0F;
            } else {
                this.min = isValidFloat(min) ? min : 0.0F;
                this.max = isValidFloat(max) ? max : 0.0F;
                this.mean = (isValidFloat(sum) ? sum : 0.0F) / (float) count;
            }
            this.histogram = histogram;
            this.histogramMin = histMin(histogram);
            this.histogramMax = histMax(histogram);
        }

        @Override
        public String toString() {
            return "PlaneRaster[count=" + count + ",min=" + min
                    + ",max=" + max + ",mean=" + mean + "]";
        }
    }

    public PlaneRaster(int width, int height, int[] pixelData) {
        if (pixelData.length != width * height) {
            throw new IllegalArgumentException();
        }
        this.width = width;
        this.height = height;
        this.rawData = new float[width * height];
        this.pixelData = pixelData;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public float[] getRawData() {
        return rawData;
    }

    public int[] getPixelData() {
        return pixelData;
    }

    public Statistics getInnerStatistics() {
        if (innerStatistics == null) {
            updateStatistics();
        }
        return innerStatistics;
    }

    public Statistics getOuterStatistics() {
        if (outerStatistics == null) {
            updateStatistics();
        }
        return outerStatistics;
    }

    public Statistics getTotalStatistics() {
        if (totalStatistics == null) {
            updateStatistics();
        }
        return totalStatistics;
    }

    public void clearStatistics() {
        innerStatistics = null;
        outerStatistics = null;
        totalStatistics = null;
    }

    public void updateStatistics() {
        final float[] rawData = this.rawData;
        final int n = rawData.length;
        int innerCount = 0;
        float innerSum = 0.0F;
        float innerMin = +Float.MAX_VALUE;
        float innerMax = -Float.MAX_VALUE;
        int outerCount = 0;
        float outerSum = 0.0F;
        float outerMin = +Float.MAX_VALUE;
        float outerMax = -Float.MAX_VALUE;
        int totalCount = 0;
        float totalSum = 0.0F;
        float totalMin = +Float.MAX_VALUE;
        float totalMax = -Float.MAX_VALUE;
        float v;
        for (int i = 0; i < n; i++) {
            v = rawData[i];
            if (isValidFloat(v)) {
                if (v < 0.0F) {
                    v = -1.0f - v;
                    innerCount++;
                    innerSum += v;
                    if (v < innerMin) {
                        innerMin = v;
                    }
                    if (v > innerMax) {
                        innerMax = v;
                    }
                } else {
                    outerCount++;
                    outerSum += v;
                    if (v < outerMin) {
                        outerMin = v;
                    }
                    if (v > outerMax) {
                        outerMax = v;
                    }
                }
                totalCount++;
                totalSum += v;
                if (v < totalMin) {
                    totalMin = v;
                }
                if (v > totalMax) {
                    totalMax = v;
                }
            }
        }
        final int histSize = 512;
        int[] innerHist = new int[histSize];
        int[] outerHist = new int[histSize];
        int[] totalHist = new int[histSize];
        float innerScale = innerMax > innerMin ? (float) histSize / (innerMax - innerMin) : 0.0F;
        float outerScale = outerMax > outerMin ? (float) histSize / (outerMax - outerMin) : 0.0F;
        float totalScale = totalMax > totalMin ? (float) histSize / (totalMax - totalMin) : 0.0F;
        for (int i = 0; i < n; i++) {
            v = rawData[i];
            if (isValidFloat(v)) {
                if (v < 0.0F) {
                    v = -1.0F - v;
                    innerHist[getHistogramIndex(innerMin, innerScale, v, histSize)]++;
                } else {
                    outerHist[getHistogramIndex(outerMin, outerScale, v, histSize)]++;
                }
                totalHist[getHistogramIndex(totalMin, totalScale, v, histSize)]++;
            }
        }

        innerStatistics = new Statistics(innerCount,
                                         innerMin,
                                         innerMax,
                                         innerSum,
                                         innerHist);
        outerStatistics = new Statistics(outerCount,
                                         outerMin,
                                         outerMax,
                                         outerSum,
                                         outerHist);
        totalStatistics = new Statistics(totalCount,
                                         totalMin,
                                         totalMax,
                                         totalSum,
                                         totalHist);
    }

    private static boolean isValidFloat(float v) {
        return !Float.isNaN(v) && !Float.isInfinite(v);
    }

    private static int histMin(int[] histogram) {
        int min = Integer.MAX_VALUE;
        for (int v : histogram) {
            if (v < min) {
                min = v;
            }
        }
        return min;
    }

    private static int histMax(int[] histogram) {
        int max = Integer.MIN_VALUE;
        for (int v : histogram) {
            if (v > max) {
                max = v;
            }
        }
        return max;
    }

    private static int getHistogramIndex(float min, float scale, float value, int size) {
        int index = (int) (scale * (value - min));
        if (index <= 0) {
            return 0;
        } else if (index >= size) {
            return size - 1;
        } else {
            return index;
        }
    }
}