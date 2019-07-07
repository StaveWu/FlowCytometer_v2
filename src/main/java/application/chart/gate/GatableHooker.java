package application.chart.gate;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class GatableHooker {

    private Gatable gatable;

    public GatableHooker(Gatable gatable) {
        this.gatable = gatable;
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
