package application.chart;

import javafx.scene.chart.NumberAxis;

public class AxisJsonObject {

    public final String label;
    public final double lowerBound;
    public final double upperBound;
    public final boolean autoRanging;
    public final double tickUnit;

    public AxisJsonObject(String label,
                          double lowerBound, double upperBound, boolean autoRanging, double tickUnit) {
        this.label = label;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.autoRanging = autoRanging;
        this.tickUnit = tickUnit;
    }

    public static AxisJsonObject fromAxis(NumberAxis axis) {
        return new AxisJsonObject(axis.getLabel(),
                axis.getLowerBound(), axis.getUpperBound(), axis.isAutoRanging(), axis.getTickUnit());
    }

    public void initAxis(NumberAxis axis) {
        axis.setAutoRanging(autoRanging);
        axis.setTickUnit(tickUnit);
        axis.setLowerBound(lowerBound);
        axis.setUpperBound(upperBound);
    }
}
