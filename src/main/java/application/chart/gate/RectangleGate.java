package application.chart.gate;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.XYChart;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class RectangleGate<X, Y> implements Gate<X, Y> {

    private Rectangle node;
    private List<XYChart.Data<X, Y>> points = new ArrayList<>();
    private XYChart.Data<X, Y> runningPoint;

    private List<GateCompletedListener> listeners = new ArrayList<>();

    public RectangleGate() {
        super();
        node = new Rectangle();
        node.setStyle("-fx-fill: transparent; " +
                "-fx-stroke: blue; " +
                "-fx-stroke-width: 1;");
    }

    @Override
    public Node getNode() {
        return node;
    }

    @Override
    public boolean isLocated() {
        return points.size() > 0;
    }

    @Override
    public boolean isCompleted() {
        return points.size() == 2;
    }

    @Override
    public void addPoint(XYChart.Data<X, Y> point) {
        if (isCompleted()) {
            return;
        }
        points.add(point);
        if (isCompleted()) {
            listeners.forEach(GateCompletedListener::onCompleted);
        }
    }

    @Override
    public List<XYChart.Data<X, Y>> getPoints() {
        return points;
    }

    @Override
    public void setRunningPoint(XYChart.Data<X, Y> point) {
        if (!isLocated() || isCompleted()) {
            return;
        }
        runningPoint = point;
    }

    @Override
    public void paint(Axis<X> xAxis, Axis<Y> yAxis) {
        if (isLocated() && !isCompleted() && runningPoint != null) {
            decideNextPointAnimation(xAxis, yAxis);
        }
        if (isCompleted()) {
            relocateNodeByPoint(xAxis, yAxis);
        }
    }

    @Override
    public void addCompletedListener(GateCompletedListener listener) {
        listeners.add(listener);
    }

    protected void decideNextPointAnimation(Axis<X> xAxis, Axis<Y> yAxis) {
//        System.out.println("decide next point...");
        Point2D start = RealPixelTranslator.getDisplayPosition(points.get(0), xAxis, yAxis);
        Point2D end = RealPixelTranslator.getDisplayPosition(runningPoint, xAxis, yAxis);
//        System.out.println(points.get(0) + "," + runningPoint);
        node.setX(start.getX());
        node.setY(start.getY());
        node.setWidth(end.getX() - start.getX());
        node.setHeight(end.getY()- start.getY());
    }

    protected void relocateNodeByPoint(Axis<X> xAxis, Axis<Y> yAxis) {
//        System.out.println("relocate node...");
        Point2D start = RealPixelTranslator.getDisplayPosition(points.get(0), xAxis, yAxis);
        Point2D end = RealPixelTranslator.getDisplayPosition(points.get(1), xAxis, yAxis);
//        System.out.println(points.get(0) + "," + points.get(1));
        node.setX(start.getX());
        node.setY(start.getY());
        node.setWidth(end.getX() - start.getX());
        node.setHeight(end.getY()- start.getY());
    }
}
