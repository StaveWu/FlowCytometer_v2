package application.chart;

import javafx.geometry.Point2D;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

public class ArrowHead extends Path {

    private MoveTo start = new MoveTo();
    private LineTo end = new LineTo();

    public ArrowHead(double startX, double startY) {
        super();
        setStart(startX, startY);
        setEnd(startX, startY);
        getElements().addAll(start, end);
    }

    public ArrowHead(double startX, double startY, double endX, double endY) {
        setStart(startX, startY);
        setEnd(endX, endY);
        getElements().addAll(start, end);
    }

    public void setStart(double x, double y) {
        start.setX(x);
        start.setY(y);
    }

    public void setEnd(double x, double y) {
        end.setX(x);
        end.setY(y);
    }

    public Point2D getStart() {
        return new Point2D(start.getX(), start.getY());
    }

    public Point2D getEnd() {
        return new Point2D(end.getX(), end.getY());
    }
}
