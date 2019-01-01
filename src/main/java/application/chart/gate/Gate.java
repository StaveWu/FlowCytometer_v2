package application.chart.gate;

import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.XYChart;
import javafx.scene.shape.Rectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class Gate<X, Y> {

    private static final Logger log = LoggerFactory.getLogger(Gate.class);

    private List<XYChart.Data<X, Y>> points = new ArrayList<>();
    private Rectangle node;

    public Gate() {
        node = new Rectangle();
        node.setStyle("-fx-fill: transparent; " +
                "-fx-stroke: black; " +
                "-fx-stroke-width: 1;");
    }

    private boolean isActive = false;

    public Node getNode() {
        return node;
    }

    public List<XYChart.Data<X, Y>> getPoints() {
        return points;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isLocaled() {
        return points.size() > 0;
    }

    private boolean isCompleted() {
        return points.size() == 2;
    }

    public void addPoints(XYChart.Data<X, Y> point) {
        if (!isActive() || isCompleted()) {
            return;
        }
        points.add(point);
        if (isCompleted()) {
            setActive(false);
        }
    }

    private XYChart.Data<X, Y> runningPoint;

    public void setRunningPoint(XYChart.Data<X, Y> point) {
        if (!isActive()) {
            return;
        }
        runningPoint = point;
    }


    public void resizeLocate(Axis<X> xAxis, Axis<Y> yAxis) {
        if (isLocaled() && !isCompleted() && runningPoint != null) {
            // decide next point action
            System.out.println("decide next point...");
            double x0 = xAxis.getDisplayPosition(points.get(0).getXValue());
            double y0 = yAxis.getDisplayPosition(points.get(0).getYValue());
            double x1 = xAxis.getDisplayPosition(runningPoint.getXValue());
            double y1 = yAxis.getDisplayPosition(runningPoint.getYValue());
            System.out.println(points.get(0) + "," + runningPoint);
            node.setX(x0);
            node.setY(y0);
            node.setWidth(x1 - x0);
            node.setHeight(y1- y0);
        }
        if (isCompleted()) {
            // relocate node according to point
            System.out.println("relocate node...");
            double x0 = xAxis.getDisplayPosition(points.get(0).getXValue());
            double y0 = yAxis.getDisplayPosition(points.get(0).getYValue());
            double x1 = xAxis.getDisplayPosition(points.get(1).getXValue());
            double y1 = yAxis.getDisplayPosition(points.get(1).getYValue());
            System.out.println(points.get(0) + "," + points.get(1));
            node.setX(x0);
            node.setY(y0);
            node.setWidth(x1 - x0);
            node.setHeight(y1- y0);
        }
    }

}
