package application.chart;

import application.chart.gate.Gatable;
import application.chart.gate.Gate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.Axis;

public class GatedHistogram<X, Y> extends AreaChart<X, Y> implements Gatable {

    private Gate<X, Y> gate;

    public GatedHistogram(Axis<X> xAxis, Axis<Y> yAxis) {
        this(xAxis, yAxis, FXCollections.observableArrayList());
    }

    public GatedHistogram(Axis<X> xAxis, Axis<Y> yAxis, ObservableList<Series<X, Y>> data) {
        super(xAxis, yAxis, data);
        GatableEventHooker gatableEventHooker = new GatableEventHooker(this);
        gatableEventHooker.hookContextMenu();
        gatableEventHooker.hookGateAction();
    }

    private Data<X, Y> getDataForDisplay(double x, double y) {
        Point2D local = getPlotArea().sceneToLocal(new Point2D(x, y));
        X xValue = getXAxis().getValueForDisplay(local.getX());
        Y yValue = getYAxis().getValueForDisplay(local.getY());
        return new Data<>(xValue, yValue);
    }

    private Node getPlotArea() {
        return lookup(".chart-plot-background");
    }

    /**
     * this method is valid on plot area, different with layoutChildren() method, which
     * belongs to any node class.
     */
    @Override
    protected void layoutPlotChildren() {
        super.layoutPlotChildren();
        if (gate == null) {
            return;
        }
        gate.resizeLocate(getXAxis(), getYAxis());
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
        if (!getPlotChildren().contains(gate.getNode())) {
            getPlotChildren().add(gate.getNode());
        }
        gate.addPoint(getDataForDisplay(x, y));
    }

    @Override
    public void setGate(Gate gate) {
        this.gate = gate;
    }

    @Override
    public void removeGate() {
        if (gate != null) {
            getPlotChildren().remove(gate.getNode());
        }
    }
}
