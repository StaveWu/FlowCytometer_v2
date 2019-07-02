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
    private Rectangle resizeMarkRegion;

    private boolean canResize = false;
    private boolean canDrag = false;
    private DragContext contextForDrag;

    public ChartWrapper(Chart chart) {
        super();
        createTitledPane();
        createBottomPane();
        createRegionForResize();
        bottomPane.getChildren().add(resizeMarkRegion);
        setVgrow(chart, Priority.ALWAYS);

        getChildren().add(titledPane);
        getChildren().add(chart);
        getChildren().add(bottomPane);

        setStyle("-fx-background-color: white;"
                + "-fx-border-color: black;"
                + "-fx-border-width: 1;");
        setPrefWidth(300);
        setPrefHeight(220);

        hookDragHandlers();
        hookResizeHandlers();
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
        resizeMarkRegion = new Rectangle(WIDTH, HEIGHT);
        IntegerProperty xDelta = new SimpleIntegerProperty(WIDTH);
        IntegerProperty yDelta = new SimpleIntegerProperty(HEIGHT);
        resizeMarkRegion.xProperty().bind(bottomPane.widthProperty().subtract(xDelta));
        resizeMarkRegion.yProperty().bind(bottomPane.heightProperty().subtract(yDelta));
        resizeMarkRegion.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
            getScene().setCursor(Cursor.NW_RESIZE);
        });
        resizeMarkRegion.addEventHandler(MouseEvent.MOUSE_EXITED, event -> {
            getScene().setCursor(Cursor.DEFAULT);
        });
    }

    public void hookDragHandlers() {
        contextForDrag = new DragContext();
        addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            if (titledPane.contains(event.getX(), event.getY())) {
                Point2D mouseLoc = localToParent(event.getX(), event.getY());
                contextForDrag.mouseAnchorX = mouseLoc.getX();
                contextForDrag.mouseAnchorY = mouseLoc.getY();
                contextForDrag.initialTranslateX = getTranslateX();
                contextForDrag.initialTranslateY = getTranslateY();
                canDrag = true;
            }
        });
        addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
            if (canDrag) {
                Point2D mouseLoc = localToParent(event.getX(), event.getY());
                setTranslateX(contextForDrag.initialTranslateX
                        + mouseLoc.getX() - contextForDrag.mouseAnchorX);
                setTranslateY(contextForDrag.initialTranslateY
                        + mouseLoc.getY() - contextForDrag.mouseAnchorY);
            }
        });
        addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            if (canDrag) {
                canDrag = false;
            }
        });
    }

    public void hookResizeHandlers() {
        resizeMarkRegion.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            canResize = true;
        });
        addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
            if (canResize) {
                setPrefWidth(event.getX());
                setPrefHeight(event.getY());
            }
        });
        resizeMarkRegion.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            if (canResize) {
                canResize = false;
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
