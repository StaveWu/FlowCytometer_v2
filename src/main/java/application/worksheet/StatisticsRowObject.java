package application.worksheet;

public class StatisticsRowObject {

    private String name;

    private double mean;

    private double median;

    private String coefficientOfVariation;

    public StatisticsRowObject(String name, double mean, double median, double cv) {
        this.name = name;
        // keep 3 decimal places
        this.mean = Double.valueOf(String.format("%.3f", mean));
        this.median = Double.valueOf(String.format("%.3f", median));
        this.coefficientOfVariation = String.format("%.3f", cv * 100) + "%";
    }

    public String getName() {
        return name;
    }

    public double getMean() {
        return mean;
    }

    public double getMedian() {
        return median;
    }

    public String getCoefficientOfVariation() {
        return coefficientOfVariation;
    }
}
