package application.worksheet;

public class StatisticsRowObject {

    private String name;

    private double mean;

    private double median;

    public StatisticsRowObject(String name, double mean, double median) {
        this.name = name;
        this.mean = mean;
        this.median = median;
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

}
