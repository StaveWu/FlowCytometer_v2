package application.chart.gate;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.XYChart;
import javafx.scene.shape.Rectangle;

public class RectangleGate<X, Y> extends Gate<X, Y> {

    private Rectangle node;

    public RectangleGate() {
        super();
        node = new Rectangle();
        node.setStyle("-fx-fill: transparent; " +
                "-fx-stroke: black; " +
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
    }

    @Override
    public void setRunningPoint(XYChart.Data<X, Y> point) {
        if (!isLocated() || isCompleted()) {
            return;
        }
        runningPoint = point;
    }

    @Override
    public void resizeLocate(Axis<X> xAxis, Axis<Y> yAxis) {
        if (isLocated() && !isCompleted() && runningPoint != null) {
            decideNextPointAnimation(xAxis, yAxis);
        }
        if (isCompleted()) {
            relocateNodeByPoint(xAxis, yAxis);
        }
    }

    protected void decideNextPointAnimation(Axis<X> xAxis, Axis<Y> yAxis) {
        System.out.println("decide next point...");
        Point2D start = toDisplayPosition(points.get(0), xAxis, yAxis);
        Point2D end = toDisplayPosition(runningPoint, xAxis, yAxis);
        System.out.println(points.get(0) + "," + runningPoint);
        node.setX(start.getX());
        node.setY(start.getY());
        node.setWidth(end.getX() - start.getX());
        node.setHeight(end.getY()- start.getY());
    }

    protected void relocateNodeByPoint(Axis<X> xAxis, Axis<Y> yAxis) {
        System.out.println("relocate node...");
        Point2D start = toDisplayPosition(points.get(0), xAxis, yAxis);
        Point2D end = toDisplayPosition(points.get(1), xAxis, yAxis);
        System.out.println(points.get(0) + "," + points.get(1));
        node.setX(start.getX());
        node.setY(start.getY());
        node.setWidth(end.getX() - start.getX());
        node.setHeight(end.getY()- start.getY());
    }
}
