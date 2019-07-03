package application.chart.gate;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.XYChart;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polygon;

public class PolygonGate<X, Y> extends Gate<X, Y> {

//    private Polygon node;

    private Path node;

    private boolean isCompleted = false;
    private boolean newPointAdded = false;

    public PolygonGate() {
        node = new Path();
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
        return isCompleted;
    }

    @Override
    public void addPoint(XYChart.Data<X, Y> point) {
        if (isCompleted()) {
            return;
        }
        points.add(point);
        newPointAdded = true;
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
        if (newPointAdded) {
            newPointAdded = false;
            // add new point to node
            Point2D newPoint = toDisplayPosition(points.get(points.size() - 1), xAxis, yAxis);
            if (points.size() == 1) {
                node.getElements().add(new MoveTo(newPoint.getX(), newPoint.getY()));
            } else {
                node.getElements().add(new LineTo(newPoint.getX(), newPoint.getY()));
            }
            // check completed
            if (points.size() >= 3) {
                Point2D startPoint = toDisplayPosition(points.get(0), xAxis, yAxis);
                Point2D endPoint = toDisplayPosition(points.get(points.size() - 1), xAxis, yAxis);
                if (closeEnough(startPoint, endPoint)) {
                    isCompleted = true;
                }
            }
        }

        if (isLocated() && !isCompleted() && runningPoint != null) {
            decideNextPointAnimation(xAxis, yAxis);
        }
        if (isCompleted()) {
            relocateNodeByPoint(xAxis, yAxis);
        }
    }

    private boolean closeEnough(Point2D p1, Point2D p2) {
        return Math.abs(p1.getX() - p2.getX()) < 5
                && Math.abs(p1.getY() - p2.getY()) < 5;
    }

    private LineTo dynamicLine = new LineTo();

    protected void decideNextPointAnimation(Axis<X> xAxis, Axis<Y> yAxis) {
        System.out.println("decide next point...");
        Point2D start = toDisplayPosition(points.get(0), xAxis, yAxis);
        Point2D end = toDisplayPosition(runningPoint, xAxis, yAxis);
        System.out.println(points.get(0) + "," + runningPoint);
        node.setX(start.getX());
        node.setY(start.getY());
        node.setWidth(end.getX() - start.getX());
        node.setHeight(end.getY()- start.getY());

        dynamicLine.setX(end.getX());
        dynamicLine.setY(end.getY());
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
