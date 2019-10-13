package application.chart;

public class DotProcess {

    public static float truncateError(float value) {
        String[] parts = String.valueOf(value).split("\\.");
        if (parts[0].length() > 2) {
            return Math.round(Float.parseFloat(parts[0]) / 10f) * 10;
        } else {
            if (parts.length > 1 && parts[1].length() > 1) {
                return Math.round(value * 10f) / 10f;
            }
        }
        return value;
    }
}
