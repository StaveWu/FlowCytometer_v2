package application.chart.gate;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.Axis;
import javafx.scene.chart.XYChart;

public class CursorChart extends AreaChart<Number, Number> implements Gatable {

    private Gate<Number, Number> gate;

    public CursorChart(Axis<Number> xAxis, Axis<Number> yAxis) {
        this(xAxis, yAxis, FXCollections.observableArrayList());
    }

    public CursorChart(Axis<Number> xAxis, Axis<Number> yAxis, ObservableList<Series<Number, Number>> data) {
        super(xAxis, yAxis, data);
        // add empty series
        getData().add(new XYChart.Series<>());
        // use user data to store axis names
        setAnimated(false);
        GatableHooker gatableHooker = new GatableHooker(this);
        gatableHooker.hookGateAction();
    }

    private Node getPlotArea() {
        return lookup(".chart-plot-background");
    }

    private Data<Number, Number> getDataForDisplay(double x, double y) {
        Point2D local = getPlotArea().sceneToLocal(new Point2D(x, y));
        Number xValue = getXAxis().getValueForDisplay(local.getX());
        Number yValue = getYAxis().getValueForDisplay(local.getY());
        return new Data<>(xValue, yValue);
    }

    @Override
    public boolean isActive() {
        return gate != null && !gate.isCompleted();
    }

    @Override
    public boolean isLocated() {
        return gate != null && gate.isLocated();
    }

    @Override
    public void setRunningPoint(double x, double y) {
        if (gate == null) {
            return;
        }
        gate.setRunningPoint(getDataForDisplay(x, y));
        requestChartLayout();
    }

    @Override
    public void addPoint(double x, double y) {
        if (gate == null) {
            return;
        }
        gate.addPoint(getDataForDisplay(x, y));
    }
}
