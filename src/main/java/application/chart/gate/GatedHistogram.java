package application.chart.gate;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.Axis;
import javafx.scene.chart.XYChart;

import java.util.*;

public class GatedHistogram extends AreaChart<Number, Number>
        implements Gatable, GatableChart<Number, Number>, GateCompletedListener {

    private Gate<Number, Number> gate;
    private List<KVData> dataList = new ArrayList<>();
    private List<GateLifeCycleListener> listeners = new ArrayList<>();

    public GatedHistogram(Axis<Number> xAxis, Axis<Number> yAxis) {
        this(xAxis, yAxis, FXCollections.observableArrayList());
    }

    public GatedHistogram(Axis<Number> xAxis, Axis<Number> yAxis,
                          ObservableList<Series<Number, Number>> data) {
        super(xAxis, yAxis, data);
        // add empty series
        getData().add(new XYChart.Series<>());
        // use user data to store axis names
        getYAxis().setLabel("Count");
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
        if (!getPlotChildren().contains(gate.getNode())) {
            getPlotChildren().add(gate.getNode());
        }
        gate.addPoint(getDataForDisplay(x, y));
    }

    @Override
    public void setGate(Gate gate) {
        this.gate = gate;
        this.gate.addCompletedListener(this);
    }

    @Override
    public void removeGate() {
        if (gate != null) {
            getPlotChildren().remove(gate.getNode());
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
        if (!checkLabel(getXAxis())) {
            return;
        }
        Float xValue = data.getValueByName(getXAxis().getLabel());
        // traverse existing count from xydata, if not, add new one.
        Platform.runLater(() -> {
            Optional<Data<Number, Number>> existing = getData().get(0).getData().stream()
                    .filter(d -> d.getXValue().floatValue() == xValue)
                    .findFirst();

            if (existing.isPresent()) {
                System.out.println("presented");
                existing.get().setYValue(existing.get().getYValue().intValue() + 1);
            } else {
                System.out.println("new in");
                getData().get(0).getData().add(new Data<>(xValue, 1));
            }
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
        getYAxis().setUserData(new ArrayList<>(Arrays.asList("Count")));
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
