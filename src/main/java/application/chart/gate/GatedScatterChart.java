package application.chart.gate;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class GatedScatterChart extends ScatterChart<Number, Number> implements GatableChart {

    private Gate<Number, Number> gate;
    private List<KVData> dataList = new ArrayList<>();

    public GatedScatterChart(Axis<Number> xAxis, Axis<Number> yAxis) {
        this(xAxis, yAxis, FXCollections.observableArrayList());
    }

    public GatedScatterChart(Axis<Number> xAxis, Axis<Number> yAxis,
                             ObservableList<Series<Number, Number>> data) {
        super(xAxis, yAxis, data);
        // add empty series
        getData().add(new XYChart.Series<>());
        // use user data to store axis names
        setAnimated(false);
        GatableHooker gatableHooker = new GatableHooker(this);
        gatableHooker.hookContextMenu();
        gatableHooker.hookGateAction();
    }

    private Data<Number, Number> getDataForDisplay(double x, double y) {
        Point2D local = getPlotArea().sceneToLocal(new Point2D(x, y));
        Number xValue = getXAxis().getValueForDisplay(local.getX());
        Number yValue = getYAxis().getValueForDisplay(local.getY());
        return new Data<>(xValue, yValue);
    }

    private Node getPlotArea() {
        return lookup(".chart-plot-background");
    }

//    public ObservableList<Series<X, Y>> getGatedData() {
//        if (gate == null) { // return an empty list if gate not ready
//            return FXCollections.observableArrayList();
//        }
//
//        ObservableList<Series<X, Y>> res = FXCollections.observableArrayList();
//        for (int seriesIndex = 0; seriesIndex < getData().size(); seriesIndex++) {
//            Series<X, Y> series = getData().get(seriesIndex);
//            Series<X, Y> gatedSeries = new Series<>();
//            gatedSeries.setName(series.getName());
//            for (Iterator<Data<X, Y>> it = getDisplayedDataIterator(series); it.hasNext(); ) {
//                Data<X, Y> item = it.next();
//                if (gateContains(item.getXValue(), item.getYValue())) {
//                    gatedSeries.getData().add(item);
//                }
//            }
//            res.add(gatedSeries);
//        }
//        return res;
//    }

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
        gate.paint(getXAxis(), getYAxis());
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

    @Override
    public void addData(KVData data) {
        dataList.add(data);
        // check axis label
        if (!checkLabel(getXAxis()) || !checkLabel(getYAxis())) {
            return;
        }
        // plot data
        System.out.println("plot data");
        Float xValue = data.getValueByName(getXAxis().getLabel());
        Float yValue = data.getValueByName(getYAxis().getLabel());
        Platform.runLater(() -> {
            getData().get(0).getData().add(new Data<>(xValue, yValue));
        });
    }

    private boolean checkLabel(Axis axis) {
        return !(axis.getLabel() == null || axis.getLabel().equals(""));
    }

    @Override
    public boolean isGated(KVData data) {
        if (gate == null) {
            return true;
        }
        Float xValue = data.getValueByName(getXAxis().getLabel());
        Float yValue = data.getValueByName(getYAxis().getLabel());
        double x = getXAxis().getDisplayPosition(xValue);
        double y = getYAxis().getDisplayPosition(yValue);
        return gate.getNode().contains(x, y);
    }

    @Override
    public void setAxisCandidateNames(List<String> names) {
        getXAxis().setUserData(names);
        getYAxis().setUserData(names);
    }

}
