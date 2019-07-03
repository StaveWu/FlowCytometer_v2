package application.chart.gate;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.XYChart;
import javafx.scene.shape.Rectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public abstract class Gate<X, Y> {

    protected List<XYChart.Data<X, Y>> points = new ArrayList<>();
    protected XYChart.Data<X, Y> runningPoint;

    public abstract Node getNode();

    public abstract boolean isLocated();

    public abstract boolean isCompleted();

    public abstract void addPoint(XYChart.Data<X, Y> point);

    public abstract void setRunningPoint(XYChart.Data<X, Y> point);

    public abstract void resizeLocate(Axis<X> xAxis, Axis<Y> yAxis);

    protected Point2D toDisplayPosition(XYChart.Data<X, Y> realPoint, Axis<X> xAxis, Axis<Y> yAxis) {
        double x = xAxis.getDisplayPosition(realPoint.getXValue());
        double y = yAxis.getDisplayPosition(realPoint.getYValue());
        return new Point2D(x, y);
    }


}
