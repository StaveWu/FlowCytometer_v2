package application.chart;

import application.chart.gate.Gatable;
import application.chart.gate.Gate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.ScatterChart;

import java.util.Iterator;

public class GatedScatterChart<X, Y> extends ScatterChart<X, Y> implements Gatable {

    private Gate<X, Y> gate;

    public GatedScatterChart(Axis<X> xAxis, Axis<Y> yAxis) {
        this(xAxis, yAxis, FXCollections.observableArrayList());
    }

    public GatedScatterChart(Axis<X> xAxis, Axis<Y> yAxis, ObservableList<Series<X, Y>> data) {
        super(xAxis, yAxis, data);
        GatableEventHooker gatableEventHooker = new GatableEventHooker(this);
        gatableEventHooker.hookContextMenu();
        gatableEventHooker.hookGateAction();
    }


    public Data<X, Y> getDataForDisplay(double x, double y) {
        Point2D local = getPlotArea().sceneToLocal(new Point2D(x, y));
        X xValue = getXAxis().getValueForDisplay(local.getX());
        Y yValue = getYAxis().getValueForDisplay(local.getY());
        return new Data<>(xValue, yValue);
    }

    public Node getPlotArea() {
        return lookup(".chart-plot-background");
    }

    public ObservableList<Series<X, Y>> getGatedData() {
        if (gate == null) { // return an empty list if gate not ready
            return FXCollections.observableArrayList();
        }

        ObservableList<Series<X, Y>> res = FXCollections.observableArrayList();
        for (int seriesIndex=0; seriesIndex < getData().size(); seriesIndex++) {
            Series<X, Y> series = getData().get(seriesIndex);
            Series<X, Y> gatedSeries = new Series<>();
            gatedSeries.setName(series.getName());
            for (Iterator<Data<X, Y>> it = getDisplayedDataIterator(series); it.hasNext(); ) {
                Data<X, Y> item = it.next();
                double x = getXAxis().getDisplayPosition(item.getXValue());
                double y = getYAxis().getDisplayPosition(item.getYValue());
                if (gate.getNode().contains(x, y)) {
                    gatedSeries.getData().add(item);
                }
            }
            res.add(gatedSeries);
        }
        return res;
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
        if (gate != null && getPlotChildren().contains(gate.getNode())) {
            getPlotChildren().remove(gate.getNode());
        }
    }

}
