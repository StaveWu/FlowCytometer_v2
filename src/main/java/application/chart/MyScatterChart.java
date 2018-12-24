package application.chart;

import application.utils.UiUtils;
import javafx.collections.ObservableList;
import javafx.scene.chart.Axis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseButton;
import javafx.scene.shape.Rectangle;

public class MyScatterChart<X, Y> extends ScatterChart<X, Y> implements Gatable {

    private Gate gate;
    private MyContextMenu contextMenu;

    private boolean isGateActive = false;

    public MyScatterChart(Axis<X> xAxis, Axis<Y> yAxis) {
        super(xAxis, yAxis);
        this.setOnMouseClicked(event -> {
            if (event.isPopupTrigger()) {
                if (contextMenu == null) {
                    contextMenu = new MyContextMenu(this);
                }
                contextMenu.show(MyScatterChart.this,
                        event.getScreenX(), event.getScreenY());
            }
            if (isGateActive && event.getButton() == MouseButton.PRIMARY) {
                if (gate == null) {
                    isGateActive = false;
                    UiUtils.getAlert(Alert.AlertType.ERROR, null, "圈门为空").showAndWait();
                    return;
                }
                Rectangle r = new Rectangle(10, 10);
                r.setX(this.getLayoutX());
                r.setY(this.getLayoutY());
                this.getChildren().add(r);
                gate.draw();
            }
        });
    }

    public MyScatterChart(Axis<X> xAxis, Axis<Y> yAxis, ObservableList<Series<X, Y>> data) {
        super(xAxis, yAxis, data);
    }

    public void getGatedData() {
//        gate.getData();
    }

    @Override
    public void setGate(Gate gate) {
        this.gate = gate;
    }

    @Override
    public void setGateActive(boolean active) {
        isGateActive = active;
    }
}
