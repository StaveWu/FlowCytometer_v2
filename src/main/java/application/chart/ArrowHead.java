package application.chart;

import javafx.geometry.Point2D;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

public class ArrowHead extends Path implements LinkedNode {

    // draw arrow body
    private MoveTo start = new MoveTo();
    private LineTo end = new LineTo();

    // draw arrow head
    private MoveTo left = new MoveTo();
    private LineTo mid = new LineTo();
    private LineTo right = new LineTo();

    private static final int HEAD_LEN = 15;

    private LinkedNode prevNode;
    private LinkedNode nextNode;

    public ArrowHead(double startX, double startY) {
        super();
        setStart(startX, startY);
        setEnd(startX, startY);
        calculateLeft();
        calculateMid();
        calculateRight();
        getElements().addAll(start, end, left, mid, right);
    }

    public ArrowHead(double startX, double startY, double endX, double endY) {
        setStart(startX, startY);
        setEnd(endX, endY);
        calculateLeft();
        calculateMid();
        calculateRight();
        getElements().addAll(start, end, left, mid, right);
    }

    public void setStart(double x, double y) {
        start.setX(x);
        start.setY(y);
        calculateLeft();
        calculateMid();
        calculateRight();
    }

    public void setEnd(double x, double y) {
        end.setX(x);
        end.setY(y);
        calculateLeft();
        calculateMid();
        calculateRight();
    }

    private void calculateLeft() {
        double angle = Math.atan2(getEnd().getY() - getStart().getY(), getEnd().getX() - getStart().getX());
        double x = getEnd().getX() - HEAD_LEN * Math.cos(angle - Math.PI / 6);
        double y = getEnd().getY() - HEAD_LEN * Math.sin(angle - Math.PI / 6);
        left.setX(x);
        left.setY(y);
    }

    private void calculateMid() {
        mid.setX(getEnd().getX());
        mid.setY(getEnd().getY());
    }

    private void calculateRight() {
        double angle = Math.atan2(getEnd().getY() - getStart().getY(), getEnd().getX() - getStart().getX());
        double x = getEnd().getX() - HEAD_LEN * Math.cos(angle + Math.PI / 6);
        double y = getEnd().getY() - HEAD_LEN * Math.sin(angle + Math.PI / 6);
        right.setX(x);
        right.setY(y);
    }

    public Point2D getStart() {
        return new Point2D(start.getX(), start.getY());
    }

    public Point2D getEnd() {
        return new Point2D(end.getX(), end.getY());
    }

    @Override
    public LinkedNode getPrevNode() {
        return prevNode;
    }

    @Override
    public void setPrevNode(LinkedNode node) {
        prevNode = node;
    }

    @Override
    public LinkedNode getNextNode() {
        return nextNode;
    }

    @Override
    public void setNextNode(LinkedNode node) {
        nextNode = node;
    }
}
