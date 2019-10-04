package application.utils;

import java.util.Arrays;

public class MathUtils {

    public static double getMean(double[] data) {
        if (data == null || data.length == 0) {
            return 0.0;
        }
        double sum = 0.0;
        for (double ele :
                data) {
            sum += ele;
        }
        return sum / (double) data.length;
    }

    public static double getMedian(double[] data) {
        if (data == null || data.length == 0) {
            return 0.0;
        }
        double[] dataCopy = new double[data.length];
        System.arraycopy(data, 0, dataCopy, 0, data.length);

        Arrays.sort(dataCopy);
        double median;
        if (dataCopy.length % 2 == 0)
            median = (dataCopy[dataCopy.length/2] + dataCopy[dataCopy.length/2 - 1])/2;
        else
            median = dataCopy[dataCopy.length/2];
        return median;
    }

    public static double getVariance(double[] data) {
        double mean = getMean(data);
        double sum = 0.0;
        for (double ele :
                data) {
            sum += (ele - mean) * (ele - mean);
        }
        return sum / (double)data.length;
    }

    public static double getStdDev(double[] data) {
        return Math.sqrt(getVariance(data));
    }

    public static double getCoefficientOfVariation(double[] data) {
        double mean = getMean(data);
        double std = getStdDev(data);
        return std / mean;
    }
}
