package application.chart.gate;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.XYChart;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PolygonGate<X, Y> implements Gate<X, Y> {

    private Path node;
    private List<XYChart.Data<X, Y>> points = new ArrayList<>();
    private XYChart.Data<X, Y> runningPoint;
    private LinkedList<XYChart.Data<X, Y>> memoryPoints = new LinkedList<>();

    private List<GateCompletedListener> listeners = new ArrayList<>();

    private LineTo dynamicLine = new LineTo();

    private boolean isCompleted = false;
    private boolean newPointAdded = false;

    public PolygonGate() {
        node = new Path();
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
        return isCompleted;
    }

    @Override
    public void addPoint(XYChart.Data<X, Y> point) {
        if (isCompleted()) {
            return;
        }
        if (!newPointAdded) {
            points.add(point);
            newPointAdded = true;
        }
        else { // means the previous point has not been draw yet, so remember it first.
            memoryPoints.addLast(point);
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
        // handle points that are too late to draw, usually
        // when points loading first from repository
        if (!newPointAdded && !memoryPoints.isEmpty()) {
            points.add(memoryPoints.removeFirst());
            newPointAdded = true;
        }

        // accept the drawing of new in point
        if (newPointAdded) {
            newPointAdded = false;
            // add new point to node
            Point2D newPoint = RealPixelTranslator
                    .getDisplayPosition(points.get(points.size() - 1), xAxis, yAxis);
            if (points.size() == 1) {
                node.getElements().add(new MoveTo(newPoint.getX(), newPoint.getY()));
            } else {
                // remove dynamic line first
                node.getElements().remove(dynamicLine);
                node.getElements().add(new LineTo(newPoint.getX(), newPoint.getY()));
            }
            // re-add dynamic line
            dynamicLine.setX(newPoint.getX());
            dynamicLine.setY(newPoint.getY());
            node.getElements().add(dynamicLine);
            // check completed
            if (points.size() >= 3) {
                Point2D startPoint = RealPixelTranslator
                        .getDisplayPosition(points.get(0), xAxis, yAxis);
                Point2D endPoint = RealPixelTranslator
                        .getDisplayPosition(points.get(points.size() - 1), xAxis, yAxis);
                if (closeEnough(startPoint, endPoint)) {
                    isCompleted = true;
                    // isCompleted rising edge action:
                    // remove dynamic line
                    node.getElements().remove(dynamicLine);
                    // correct last element to make node closure
                    points.get(0).setXValue(points.get(points.size() - 1).getXValue());
                    points.get(0).setYValue(points.get(points.size() - 1).getYValue());
                    // fire event
                    listeners.forEach(GateCompletedListener::onCompleted);
                }
            }
        }

        if (isLocated() && !isCompleted() && runningPoint != null) {
            decideNextPointAnimation(xAxis, yAxis);
        }
        if (isCompleted()) {
            relocateNode(xAxis, yAxis);
        }
    }

    @Override
    public void addCompletedListener(GateCompletedListener listener) {
        listeners.add(listener);
    }

    private boolean closeEnough(Point2D p1, Point2D p2) {
        return Math.abs(p1.getX() - p2.getX()) < 5
                && Math.abs(p1.getY() - p2.getY()) < 5;
    }

    protected void decideNextPointAnimation(Axis<X> xAxis, Axis<Y> yAxis) {
//        System.out.println("decide next point...");
        Point2D pos = RealPixelTranslator.getDisplayPosition(runningPoint, xAxis, yAxis);
//        System.out.println(runningPoint);
        dynamicLine.setX(pos.getX());
        dynamicLine.setY(pos.getY());
    }

    protected void relocateNode(Axis<X> xAxis, Axis<Y> yAxis) {
//        System.out.println("relocate node...");
        for (int i = 0; i < node.getElements().size(); i++) {
            Point2D p = RealPixelTranslator.getDisplayPosition(points.get(i), xAxis, yAxis);
            if (i == 0) {
                MoveTo moveTo = (MoveTo) node.getElements().get(i);
                moveTo.setX(p.getX());
                moveTo.setY(p.getY());
            } else {
                LineTo lineTo = (LineTo) node.getElements().get(i);
                lineTo.setX(p.getX());
                lineTo.setY(p.getY());
            }
        }
    }

}
