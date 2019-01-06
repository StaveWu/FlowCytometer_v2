package application.chart;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.chart.Chart;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

public class ChartWrapper extends VBox {

    private Pane titledPane;
    private Pane bottomPane;
    private Rectangle regionForResize;

    private boolean canResizable = false;
    private boolean canDraggable = false;
    private DragContext contextForDrag;

    public ChartWrapper(Chart chart) {
        super();
        createTitledPane();
        createBottomPane();
        createRegionForResize();
        bottomPane.getChildren().add(regionForResize);
        setVgrow(chart, Priority.ALWAYS);

        getChildren().add(titledPane);
        getChildren().add(chart);
        getChildren().add(bottomPane);

        setStyle("-fx-background-color: white;"
                + "-fx-border-color: black;"
                + "-fx-border-width: 1;");
        setPrefWidth(300);
        setPrefHeight(220);

        hookDraggableEvent();
        hookResizableEvent();
    }

    private void createTitledPane() {
        titledPane = new FlowPane();
        titledPane.setMinHeight(30);
        titledPane.prefWidthProperty().bind(prefWidthProperty());
        titledPane.setStyle("-fx-background-color: black;");
        titledPane.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
            getScene().setCursor(Cursor.MOVE);
        });
        titledPane.addEventHandler(MouseEvent.MOUSE_EXITED, event -> {
            getScene().setCursor(Cursor.DEFAULT);
        });
    }

    private void createBottomPane() {
        bottomPane = new Pane();
    }

    private void createRegionForResize() {
        final int WIDTH = 10;
        final int HEIGHT = 10;
        regionForResize = new Rectangle(WIDTH, HEIGHT);
        IntegerProperty xDelta = new SimpleIntegerProperty(WIDTH);
        IntegerProperty yDelta = new SimpleIntegerProperty(HEIGHT);
        regionForResize.xProperty().bind(bottomPane.widthProperty().subtract(xDelta));
        regionForResize.yProperty().bind(bottomPane.heightProperty().subtract(yDelta));
        regionForResize.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
            getScene().setCursor(Cursor.NW_RESIZE);
        });
        regionForResize.addEventHandler(MouseEvent.MOUSE_EXITED, event -> {
            getScene().setCursor(Cursor.DEFAULT);
        });
    }

    public void hookDraggableEvent() {
        contextForDrag = new DragContext();
        addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            if (titledPane.contains(event.getX(), event.getY())) {
                Point2D mouseLoc = localToParent(event.getX(), event.getY());
                contextForDrag.mouseAnchorX = mouseLoc.getX();
                contextForDrag.mouseAnchorY = mouseLoc.getY();
                contextForDrag.initialTranslateX = getTranslateX();
                contextForDrag.initialTranslateY = getTranslateY();
                canDraggable = true;
            }
        });
        addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
            if (canDraggable) {
                Point2D mouseLoc = localToParent(event.getX(), event.getY());
                setTranslateX(contextForDrag.initialTranslateX
                        + mouseLoc.getX() - contextForDrag.mouseAnchorX);
                setTranslateY(contextForDrag.initialTranslateY
                        + mouseLoc.getY() - contextForDrag.mouseAnchorY);
            }
        });
        addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            if (canDraggable) {
                canDraggable = false;
            }
        });
    }

    public void hookResizableEvent() {
        regionForResize.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            canResizable = true;
        });
        addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
            if (canResizable) {
                setPrefWidth(event.getX());
                setPrefHeight(event.getY());
            }
        });
        regionForResize.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            if (canResizable) {
                canResizable = false;
            }
        });
    }

    private static final class DragContext {
        double mouseAnchorX;
        double mouseAnchorY;
        double initialTranslateX;
        double initialTranslateY;
    }
}
