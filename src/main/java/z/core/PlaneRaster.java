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
            this.min = count > 0 ? min : Float.NaN;
            this.max = count > 0 ? max : Float.NaN;
            this.mean = count > 0 ? sum / (float) count : Float.NaN;
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
        int totalCount = n;
        float totalSum = 0.0F;
        float totalMin = +Float.MAX_VALUE;
        float totalMax = -Float.MAX_VALUE;
        float v;
        for (int i = 0; i < n; i++) {
            v = rawData[i];
            if (v < 0.0F) {
                v = -1.0f - v;
                innerCount++;
                if (v < innerMin) {
                    innerMin = v;
                }
                if (v > innerMax) {
                    innerMax = v;
                }
                innerSum += v;
            } else {
                outerCount++;
                if (v < outerMin) {
                    outerMin = v;
                }
                if (v > outerMax) {
                    outerMax = v;
                }
                outerSum += v;
            }
            if (v < totalMin) {
                totalMin = v;
            }
            if (v > totalMax) {
                totalMax = v;
            }
        }
        totalSum = innerSum + outerSum;
        final int histLength = 512;
        int[] innerHist = new int[histLength];
        int[] outerHist = new int[histLength];
        int[] totalHist = new int[histLength];
        float innerScale = innerMax > innerMin ? (float) histLength / (innerMax - innerMin) : 0.0f;
        float outerScale = outerMax > outerMin ? (float) histLength / (outerMax - outerMin) : 0.0f;
        float totalScale = totalMax > totalMin ? (float) histLength / (totalMax - totalMin) : 0.0f;
        for (int i = 0; i < n; i++) {
            v = rawData[i];
            if (v < 0.0f) {
                v = -1.0f - v;
                innerHist[histIdx(innerMin, innerScale, v, histLength)]++;
            } else {
                outerHist[histIdx(outerMin, outerScale, v, histLength)]++;
            }

            totalHist[histIdx(totalMin, totalScale, v, histLength)]++;
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

    private static int histIdx(float innerMin, float innerScale, float v, int n) {
        int j = (int) (innerScale * (v - innerMin));
        return j < 0 ? 0 : j >= n ? n - 1 : j;
    }
}