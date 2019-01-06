package application.chart;

import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class GatableEventHooker {

    private Gatable gatable;
    private MyContextMenu contextMenu;

    public GatableEventHooker(Gatable gatable) {
        this.gatable = gatable;
    }

    public void hookContextMenu() {
        gatable.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.isPopupTrigger()) {
                if (contextMenu == null) {
                    contextMenu = new MyContextMenu(gatable);
                }
                contextMenu.show((Node) gatable,
                        event.getScreenX(), event.getScreenY());
            }
        });
    }

    public void hookGateAction() {
        gatable.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (gatable.isActive()
                    && event.getButton() == MouseButton.PRIMARY) {
                gatable.addPoint(event.getSceneX(), event.getSceneY());
            }
        });
        gatable.addEventHandler(MouseEvent.MOUSE_MOVED, event -> {
            if (gatable.isActive() && gatable.isLocated()) {
                gatable.setRunningPoint(event.getSceneX(), event.getSceneY());
            }
        });
    }

}
