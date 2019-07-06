package application.chart;

import application.chart.gate.*;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Hyperlink;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class ChartWrapper extends VBox implements LinkedNode, GateLifeCycleListener {

    private FlowPane titledPane;
    private Pane bottomPane;
    private Rectangle resizeMarkRegion;

    private boolean canResize = false;
    private boolean canDrag = false;
    private DragContext dragContext;

    private LinkedNode prevNode;
    private LinkedNode nextNode;

    private XYChart chart;

    private List<ChartRemovedListener> listeners = new ArrayList<>();

    public ChartWrapper(XYChart chart) {
        super();
        this.chart = chart;
        if (chart instanceof GatableChart) {
            ((GatableChart) chart).addGateLifeCycleListener(this);
        }
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
            // fire remove event
            listeners.forEach(ChartRemovedListener::chartRemoved);
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

    @Override
    public void afterComplete() {
        System.out.println("afterComplete");
        propagateToNextChart();
    }

    @Override
    public void afterDestroy() {
        System.out.println("afterDestroy");
        propagateToNextChart();
    }

    private void propagateToNextChart() {
        if (nextNode == null) {
            return;
        }
        GatableChart gatableChart = (GatableChart) chart;
        gatableChart.getKVData().stream()
                .filter(gatableChart::isGated)
                .forEach(kvData -> {
                    ChartWrapper nextChart = (ChartWrapper) nextNode.getNextNode();
                    nextChart.addData(kvData);
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
            if (nextNode != null && gatableChart.isGated(data)) {
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

    public void addChartRemovedListener(ChartRemovedListener listener) {
        listeners.add(listener);
    }

    public XYChart getChart() {
        return chart;
    }

    public JsonObject toJsonObject() {
        // get type
        String type;
        if (chart instanceof GatedScatterChart) {
            type = "Scatter";
        } else if (chart instanceof GatedHistogram) {
            type = "Histogram";
        } else {
            throw new RuntimeException("Unknown type of chart");
        }
        return new JsonObject(type, getLayoutX(), getLayoutY(),
                getPrefWidth(), getPrefHeight(),
                AxisJsonObject.fromAxis((NumberAxis) chart.getXAxis()),
                AxisJsonObject.fromAxis((NumberAxis) chart.getYAxis()));
    }

    public static ChartWrapper fromJsonObject(JsonObject json) {
        XYChart chart;
        if (json.type.equals("Scatter")) {
            chart = new GatedScatterChart(
                    new NumberAxis(),
                    new NumberAxis());
        } else {
            chart = new GatedHistogram(
                    new NumberAxis(),
                    new NumberAxis());
        }
        json.xAxisJson.initAxis((NumberAxis) chart.getXAxis());
        json.yAxisJson.initAxis((NumberAxis) chart.getYAxis());

//        Gate<Number, Number> gate;
//        if (json.gateJson.type.equals("Rectangle")) {
//            gate = new RectangleGate<>();
//        } else {
//            gate = new PolygonGate<>();
//        }
//        json.gateJson.points.forEach(p -> gate.addPoint((XYChart.Data<Number, Number>) p));
//        ((GatableChart) chart).setGate(gate);

        ChartWrapper wrapper = new ChartWrapper(chart);
        wrapper.setLayoutX(json.x);
        wrapper.setLayoutY(json.y);
        wrapper.setPrefWidth(json.width);
        wrapper.setPrefHeight(json.height);
        return wrapper;
    }

    public class JsonObject {
        public final String type;
        public final double x;
        public final double y;
        public final double width;
        public final double height;
        public final AxisJsonObject xAxisJson;
        public final AxisJsonObject yAxisJson;
//        public final GateJsonObject gateJson;

        public JsonObject(String type, double x, double y, double width, double height,
                          AxisJsonObject xAxisJson, AxisJsonObject yAxisJson) {
            this.type = type;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.xAxisJson = xAxisJson;
            this.yAxisJson = yAxisJson;
//            this.gateJson = gateJson;
        }
    }
}
