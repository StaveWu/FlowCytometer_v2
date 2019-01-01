package application.chart;

import application.chart.gate.Gate;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

public class MyContextMenu extends ContextMenu {

    private Gatable gatable;

    public MyContextMenu(Gatable gatable) {
        this.gatable = gatable;
        init();
    }

    private void init() {
        MenuItem createGateItem = new MenuItem("创建圈门");
        createGateItem.setOnAction(event -> {
            gatable.removeGate();
            Gate gate = new Gate();
            gate.setActive(true);
            gatable.setGate(gate);
        });
        MenuItem deleteGateItem = new MenuItem("删除圈门");
        deleteGateItem.setOnAction(event -> {
            gatable.removeGate();
        });
        getItems().add(createGateItem);
        getItems().add(deleteGateItem);
}
}
