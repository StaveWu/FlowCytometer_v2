package application.chart.gate;

import application.chart.ChartSettings;
import javafx.scene.Scene;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

public class GatableChartContextMenu extends ContextMenu {

    private GatableChart gatableChart;

    public GatableChartContextMenu(GatableChart gatableChart) {
        this.gatableChart = gatableChart;
        init();
    }

    private void init() {
        MenuItem createRectangleGateItem = new MenuItem("创建矩形圈门");
        createRectangleGateItem.setOnAction(event -> {
            gatableChart.removeGate();
            Gate gate = new RectangleGate();
            gatableChart.setGate(gate);
        });
        MenuItem createPolygonGateItem = new MenuItem("创建多边形圈门");
        createPolygonGateItem.setOnAction(event -> {
            gatableChart.removeGate();
            Gate gate = new PolygonGate();
            gatableChart.setGate(gate);
        });
        MenuItem deleteGateItem = new MenuItem("删除圈门");
        deleteGateItem.setOnAction(event -> {
            gatableChart.removeGate();
        });
        MenuItem settingsItem = new MenuItem("设置");
        settingsItem.setOnAction(event -> {
            if (gatableChart instanceof XYChart) {
                // pop up settings stage
                Stage stage = new Stage();
                stage.setTitle("图设置");
                stage.setScene(new Scene(new ChartSettings((XYChart<Number, Number>) gatableChart)));
                stage.show();
            }
        });
        getItems().add(createRectangleGateItem);
        getItems().add(createPolygonGateItem);
        getItems().add(deleteGateItem);
        getItems().add(settingsItem);
    }
}
