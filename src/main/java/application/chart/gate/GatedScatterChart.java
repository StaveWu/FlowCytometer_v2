package application.chart.gate;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GatedScatterChart extends ScatterChart<Number, Number>
        implements Gatable, GatableChart<Number, Number>, GateCompletedListener {

    private Gate<Number, Number> gate;
    private List<KVData> dataList = new ArrayList<>();
    private List<GateLifeCycleListener> listeners = new ArrayList<>();

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
        gate.addPoint(getDataForDisplay(x, y));
    }

    @Override
    public void setGate(Gate<Number, Number> gate) {
        this.gate = gate;
        gate.addCompletedListener(this);
        if (!getPlotChildren().contains(gate.getNode())) {
            getPlotChildren().add(gate.getNode());
        }
    }

    @Override
    public void removeGate() {
        if (gate != null) {
            getPlotChildren().remove(gate.getNode());
            gate = null;
            listeners.forEach(GateLifeCycleListener::afterDestroy);
        }
    }

    @Override
    public Gate<Number, Number> getGate() {
        return gate;
    }

    @Override
    public void addData(KVData data) {
        dataList.add(data);
        plotData(data);
    }

    private void plotData(KVData data) {
        // check axis label
        if (!checkLabel(getXAxis()) || !checkLabel(getYAxis())) {
            return;
        }
        // plot data
        Float xValue = data.getValueByName(getXAxis().getLabel());
        Float yValue = data.getValueByName(getYAxis().getLabel());
        getData().get(0).getData().add(new Data<>(xValue, yValue));
    }

    @Override
    public void clearAllData() {
        dataList.clear();
        renewSeries();
    }

    @Override
    public void replotChartData() {
        renewSeries();
        dataList.forEach(this::plotData);
    }

    @Override
    public List<KVData> getGatedData() {
        return dataList.stream()
                .filter(this::isGated)
                .collect(Collectors.toList());
    }

    private void renewSeries() {
        getData().clear();
        getData().add(new XYChart.Series<>());
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

    @Override
    public void addGateLifeCycleListener(GateLifeCycleListener listener) {
        listeners.add(listener);
    }

    @Override
    public List<KVData> getKVData() {
        return dataList;
    }

    @Override
    public void onCompleted() {
        listeners.forEach(GateLifeCycleListener::afterComplete);
    }
}
