package application.chart.gate;

import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.XYChart;

import java.util.List;

public interface Gate<X, Y> {

    boolean isCompleted();

    boolean isLocated();

    void setRunningPoint(XYChart.Data<X, Y> point);

    void addPoint(XYChart.Data<X, Y> point);

    List<XYChart.Data<X, Y>> getPoints();

    Node getNode();

    void paint(Axis<X> xAxis, Axis<Y> yAxis);

    void addCompletedListener(GateCompletedListener listener);
}
