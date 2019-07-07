package application.chart;

import application.chart.gate.Gate;
import application.chart.gate.PolygonGate;
import application.chart.gate.RectangleGate;
import javafx.scene.chart.XYChart;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GateJsonObject<X, Y> {

    public final String type;
    public final List<Point<X, Y>> points;

    public GateJsonObject(String type, List<Point<X, Y>> points) {
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
            throw new RuntimeException("Unknown type of gate");
        }
        List<Point<X, Y>> purePoints = gate.getPoints().stream()
                .map(p -> new Point<>(p.getXValue(), p.getYValue()))
                .collect(Collectors.toList());
        return new GateJsonObject<>(type, purePoints);
    }

    public void initGate(Gate<X, Y> gate) {
        points.forEach(p -> gate.addPoint(new XYChart.Data<>(p.x, p.y)));
    }

}
