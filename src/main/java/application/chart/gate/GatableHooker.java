package application.chart.gate;

import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class GatableHooker {

    private GatableChart gatableChart;
    private GatableChartContextMenu contextMenu;

    public GatableHooker(GatableChart gatableChart) {
        this.gatableChart = gatableChart;
    }

    public void hookContextMenu() {
        gatableChart.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.isPopupTrigger()) {
                if (contextMenu == null) {
                    contextMenu = new GatableChartContextMenu(gatableChart);
                }
                contextMenu.show((Node) gatableChart,
                        event.getScreenX(), event.getScreenY());
            }
        });
    }

    public void hookGateAction() {
        gatableChart.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (gatableChart.isActive()
                    && event.getButton() == MouseButton.PRIMARY) {
                gatableChart.addPoint(event.getSceneX(), event.getSceneY());
            }
        });
        gatableChart.addEventHandler(MouseEvent.MOUSE_MOVED, event -> {
            if (gatableChart.isActive() && gatableChart.isLocated()) {
                gatableChart.setRunningPoint(event.getSceneX(), event.getSceneY());
            }
        });
    }

}
