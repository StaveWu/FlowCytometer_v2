package application.chart;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

public class MyContextMenu extends ContextMenu {

    private Gatable gatable;

    public MyContextMenu(Gatable gatable) {
        this.gatable = gatable;
        init();
    }

    private void init() {
        MenuItem item = new MenuItem("创建圈门");
        item.setOnAction(event -> {
            gatable.setGate(new Gate());
            gatable.setGateActive(true);
        });
        getItems().add(item);
}
}
