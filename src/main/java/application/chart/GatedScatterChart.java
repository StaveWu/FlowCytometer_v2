package application.chart;

import application.chart.gate.RectangleGate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.input.MouseButton;

import java.util.Iterator;

public class GatedScatterChart<X, Y> extends ScatterChart<X, Y> implements Gatable {

    private RectangleGate gate;
    private MyContextMenu contextMenu;

    public GatedScatterChart(Axis<X> xAxis, Axis<Y> yAxis) {
        this(xAxis, yAxis, FXCollections.observableArrayList());
    }

    public GatedScatterChart(Axis<X> xAxis, Axis<Y> yAxis, ObservableList<Series<X, Y>> data) {
        super(xAxis, yAxis, data);
        hookContextMenuAndGateAction();
    }

    private void hookContextMenuAndGateAction() {
        this.setOnMouseClicked(event -> {
            // TODO: handle context menu
            if (event.isPopupTrigger()) {
                if (contextMenu == null) {
                    contextMenu = new MyContextMenu(this);
                }
                contextMenu.show(GatedScatterChart.this,
                        event.getScreenX(), event.getScreenY());
            }
            // TODO: handle gate action
            if (gate != null && gate.isActive()
                    && event.getButton() == MouseButton.PRIMARY) {
                if (!gate.isLocaled()) {
                    getChildren().add(gate.getDelegate());
                }
                gate.setLocation(event.getX(), event.getY());
            }
        });
        // TODO: handle gate action
        this.setOnMouseMoved(event -> {
            if (gate != null && gate.isActive() && gate.isLocaled()) {
                gate.setRunningPoint(event.getX(), event.getY());
                System.out.println(getGatedData().get(0).getData());
            }
        });

    }

    public ObservableList<Series<X, Y>> getGatedData() {
        if (gate == null) { // return an empty list if gate not ready
            return FXCollections.observableArrayList();
        }
        Node chartArea = lookup(".chart-plot-background");
        Bounds bounds = chartArea.sceneToLocal(gate.getLayoutBounds());

        ObservableList<Series<X, Y>> gatedData = FXCollections.observableArrayList();
        for (int seriesIndex=0; seriesIndex < getData().size(); seriesIndex++) {
            Series<X, Y> series = getData().get(seriesIndex);
            Series<X, Y> gatedSeries = new Series<>();
            gatedSeries.setName(series.getName());
            for (Iterator<Data<X, Y>> it = getDisplayedDataIterator(series); it.hasNext(); ) {
                Data<X, Y> item = it.next();
                double x = getXAxis().getDisplayPosition(item.getXValue());
                double y = getYAxis().getDisplayPosition(item.getYValue());
                if (bounds.contains(x, y)) {
                    gatedSeries.getData().add(item);
                }
            }
            gatedData.add(gatedSeries);
        }
        return gatedData;
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        // update gate position
    }

    @Override
    public void setGate(RectangleGate gate) {
        this.gate = gate;
    }

    @Override
    public void removeGate() {
        if (gate != null && getChildren().contains(gate.getDelegate())) {
            getChildren().remove(gate.getDelegate());
        }
    }

}
