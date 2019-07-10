package application.chart;

import application.chart.axis.LogarithmicAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ValueAxis;

public class AxisJsonObject {

    public final String type;
    public final String label;
    public final double lowerBound;
    public final double upperBound;
    public final boolean autoRanging;

    public AxisJsonObject(String type, String label,
                          double lowerBound, double upperBound, boolean autoRanging) {
        this.type = type;
        this.label = label;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.autoRanging = autoRanging;
    }

    public static AxisJsonObject fromAxis(ValueAxis<Number> axis) {
        String type;
        if (axis instanceof LogarithmicAxis) {
            type = "Log";
        } else if (axis instanceof NumberAxis) {
            type = "Linear";
        } else {
            throw new RuntimeException("Unknown type of axis");
        }
        return new AxisJsonObject(type, axis.getLabel(),
                axis.getLowerBound(), axis.getUpperBound(), axis.isAutoRanging());
    }

    public void initAxis(ValueAxis<Number> axis) {
        axis.setLabel(label);
        axis.setAutoRanging(autoRanging);
        axis.setLowerBound(lowerBound);
        axis.setUpperBound(upperBound);
    }

    public ValueAxis<Number> toAxis() {
        ValueAxis<Number> axis;
        if (type.equals("Linear")) {
            axis = new NumberAxis();
        }
        else if (type.equals("Log")) {
            axis = new LogarithmicAxis();
        } else {
            throw new RuntimeException("Unknown type of axis");
        }
        axis.setLabel(label);
        axis.setAutoRanging(autoRanging);
        axis.setLowerBound(lowerBound);
        axis.setUpperBound(upperBound);
        return axis;
    }
}
