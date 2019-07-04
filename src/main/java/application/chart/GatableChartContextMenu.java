package application.chart;

import application.chart.gate.GatableChart;
import application.chart.gate.Gate;
import application.chart.gate.PolygonGate;
import application.chart.gate.RectangleGate;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

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
            // pop up settings stage
        });
        getItems().add(createRectangleGateItem);
        getItems().add(createPolygonGateItem);
        getItems().add(deleteGateItem);
        getItems().add(settingsItem);
}
}
