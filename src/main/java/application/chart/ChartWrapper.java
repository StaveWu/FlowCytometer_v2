package application.chart;

import application.chart.gate.GatableChart;
import application.chart.gate.GatedHistogram;
import application.chart.gate.GatedScatterChart;
import application.chart.gate.KVData;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.chart.Chart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Hyperlink;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextAlignment;

import java.util.Arrays;
import java.util.List;

public class ChartWrapper extends VBox implements LinkedNode {

    private FlowPane titledPane;
    private Pane bottomPane;
    private Rectangle resizeMarkRegion;

    private boolean canResize = false;
    private boolean canDrag = false;
    private DragContext dragContext;

    private LinkedNode prevNode;
    private LinkedNode nextNode;

    private XYChart chart;

    public ChartWrapper(XYChart chart) {
        super();
        this.chart = chart;
        createTitledPane();
        createBottomPane();
        createResizeMarkRegion();
        bottomPane.getChildren().add(resizeMarkRegion);
        setVgrow(chart, Priority.ALWAYS);

        getChildren().add(titledPane);
        getChildren().add(chart);
        getChildren().add(bottomPane);

        setStyle("-fx-background-color: white;"
                + "-fx-border-color: dimgray;"
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
        titledPane.setStyle("-fx-background-color: dimgray;");
        titledPane.setAlignment(Pos.CENTER_RIGHT);
        titledPane.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
            getScene().setCursor(Cursor.MOVE);
        });
        titledPane.addEventHandler(MouseEvent.MOUSE_EXITED, event -> {
            getScene().setCursor(Cursor.DEFAULT);
        });
        Hyperlink hyperlink = new Hyperlink("CLOSE");
        hyperlink.setStyle("-fx-graphic: url(\"icons/close.png\");"
                + "-fx-content-display: graphic-only;"
                + "-fx-opacity: 0.5;");
        hyperlink.hoverProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                hyperlink.setStyle("-fx-graphic: url(\"icons/close.png\");"
                        + "-fx-content-display: graphic-only;"
                        + "-fx-opacity: 1;");
            } else {
                hyperlink.setStyle("-fx-graphic: url(\"icons/close.png\");"
                        + "-fx-content-display: graphic-only;"
                        + "-fx-opacity: 0.5;");
            }
        });
        hyperlink.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            // unbind arrowhead
            if (this.nextNode != null) {
                this.nextNode.getNextNode().setPrevNode(null);
                this.nextNode.setNextNode(null);
                this.nextNode.setPrevNode(null);
                ((Pane) getParent()).getChildren().remove(this.nextNode);
                this.setNextNode(null);
            }
            if (this.prevNode != null) {
                this.prevNode.getPrevNode().setNextNode(null);
                this.prevNode.setPrevNode(null);
                this.prevNode.setNextNode(null);
                ((Pane) getParent()).getChildren().remove(this.prevNode);
                this.setPrevNode(null);
            }
            // remove self
            ((Pane) getParent()).getChildren().remove(ChartWrapper.this);
        });
        titledPane.getChildren().add(hyperlink);
    }

    private void createBottomPane() {
        bottomPane = new Pane();
    }

    private void createResizeMarkRegion() {
        final int WIDTH = 10;
        final int HEIGHT = 10;
        resizeMarkRegion = new Rectangle(WIDTH, HEIGHT);
        resizeMarkRegion.setStyle("-fx-fill: dimgray;");
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
        dragContext = new DragContext();
        addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            if (titledPane.contains(event.getX(), event.getY())) {
                Point2D mouseLoc = localToParent(event.getX(), event.getY());
                dragContext.mouseAnchorX = mouseLoc.getX();
                dragContext.mouseAnchorY = mouseLoc.getY();
                dragContext.initialLayoutX = getLayoutX();
                dragContext.initialLayoutY = getLayoutY();
                canDrag = true;
            }
        });
        addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
            if (canDrag) {
                Point2D mouseLoc = localToParent(event.getX(), event.getY());
                setLayoutX(dragContext.initialLayoutX
                        + mouseLoc.getX() - dragContext.mouseAnchorX);
                setLayoutY(dragContext.initialLayoutY
                        + mouseLoc.getY() - dragContext.mouseAnchorY);
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
        double initialLayoutX;
        double initialLayoutY;
    }

    @Override
    public LinkedNode getPrevNode() {
        return prevNode;
    }

    @Override
    public void setPrevNode(LinkedNode node) {
        prevNode = node;
    }

    @Override
    public LinkedNode getNextNode() {
        return nextNode;
    }

    @Override
    public void setNextNode(LinkedNode node) {
        nextNode = node;
    }

    public void addData(KVData data) {
        if (chart instanceof GatableChart) {
            GatableChart gatableChart = (GatableChart) chart;
            gatableChart.addData(data);
            if (gatableChart.isGated(data) && nextNode != null) {
                ChartWrapper nextChart = (ChartWrapper) nextNode.getNextNode();
                nextChart.addData(data);
            }
        }
    }

    public void setAxisCandidateNames(List<String> names) {
        if (chart instanceof GatableChart) {
            ((GatableChart) chart).setAxisCandidateNames(names);
        }
    }
}
