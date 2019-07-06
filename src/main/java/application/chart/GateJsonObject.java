package application.chart;

import application.chart.gate.Gate;
import application.chart.gate.PolygonGate;
import application.chart.gate.RectangleGate;
import javafx.scene.chart.XYChart;

import java.util.ArrayList;
import java.util.List;

public class GateJsonObject<X, Y> {

    public final String type;
    public final List<XYChart.Data<X, Y>> points;

    public GateJsonObject(String type, List<XYChart.Data<X, Y>> points) {
        this.type = type;
        this.points = points;
    }

    public static <X, Y> GateJsonObject fromGate(Gate<X, Y> gate) {
        String type;
        if (gate == null) {
            type = "";
            return new GateJsonObject<>(type, new ArrayList<>());
        }
        if (gate instanceof RectangleGate) {
            type = "Rectangle";
        } else if (gate instanceof PolygonGate) {
            type = "Polygon";
        } else {
            throw new RuntimeException("Unknown gate");
        }
        return new GateJsonObject<>(type, gate.getPoints());
    }
}
