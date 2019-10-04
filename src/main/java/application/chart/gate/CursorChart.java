package application.chart.gate;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.Axis;
import javafx.scene.chart.XYChart;

import java.util.ArrayList;
import java.util.List;

public class CursorChart extends AreaChart<Number, Number> implements Gatable {

    private Gate<Number, Number> gate;
    private List<Float> data;

    public CursorChart(Axis<Number> xAxis, Axis<Number> yAxis) {
        this(xAxis, yAxis, FXCollections.observableArrayList());
    }

    public CursorChart(Axis<Number> xAxis, Axis<Number> yAxis, ObservableList<Series<Number, Number>> data) {
        super(xAxis, yAxis, data);
        // add empty series
        getData().add(new Series<>());
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

    @Override
    protected void layoutPlotChildren() {
        super.layoutPlotChildren();
        if (gate == null) {
            return;
        }
        gate.paint(getXAxis(), getYAxis());
    }

    public void setGate(Gate<Number, Number> gate) {
        removeGate();
        this.gate = gate;
        if (!getPlotChildren().contains(gate.getNode())) {
            getPlotChildren().add(gate.getNode());
        }
    }

    public Gate<Number, Number> getGate() {
        return gate;
    }

    private void removeGate() {
        if (gate != null) {
            getPlotChildren().remove(gate.getNode());
            gate = null;
        }
    }

    public void setData(List<Float> data) {
        if (data == null) {
            return;
        }
        // re-plot data
        getData().clear();
        Series<Number, Number> series = new Series<>();
        for (int i = 0; i < data.size(); i++) {
            series.getData().add(new Data<>(i, data.get(i)));
        }
    }

    public List<Float> getGatedData() {
        if (gate == null || !gate.isCompleted()) {
            return new ArrayList<>();
        }
        List<Float> res = new ArrayList<>();
        ObservableList<Data<Number, Number>> data = getData().get(0).getData();
        for (Data<Number, Number> ele : data) {
            double x = getXAxis().getDisplayPosition(ele.getXValue());
            double y = getYAxis().getDisplayPosition(ele.getYValue());
            if (gate.getNode().contains(x, y)) {
                res.add((float) y);
            }
        }
        return res;
    }
}
