package application.chart;

import application.chart.gate.*;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.chart.Axis;
import javafx.scene.chart.ValueAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WrappedChart extends VBox implements LinkedNode,
        GatableChart<Number, Number>, GateLifeCycleListener {

    private String uniqueId;

    private boolean canResize = false;
    private boolean canDrag = false;
    private DragContext dragContext;

    private LinkedNode prevNode;
    private LinkedNode nextNode;

    private XYChart<Number, Number> chart;
    private ContextMenu contextMenu;

    private List<ChartRemovedListener> listeners = new ArrayList<>();

    private static final class DragContext {
        double mouseAnchorX;
        double mouseAnchorY;
        double initialLayoutX;
        double initialLayoutY;
    }

    public WrappedChart(XYChart<Number, Number> chart) {
        super();
        uniqueId = UUID.randomUUID().toString();

        if (!(chart instanceof GatableChart)) {
            throw new IllegalArgumentException();
        }
        this.chart = chart;
        ((GatableChart) chart).addGateLifeCycleListener(this);
        setVgrow(chart, Priority.ALWAYS);

        getChildren().add(createTitledPane());
        getChildren().add(chart);
        getChildren().add(createBottomPane());

        setStyle("-fx-background-color: white;"
                + "-fx-border-color: dimgray;"
                + "-fx-border-width: 1;");
        setPrefWidth(300);
        setPrefHeight(220);

        hookContextMenu();
        hookAxisLabelListeners();
    }

    private Pane createTitledPane() {
        FlowPane titledPane = new FlowPane();
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
            ((Pane) getParent()).getChildren().remove(WrappedChart.this);
            // fire remove event
            listeners.forEach(ChartRemovedListener::chartRemoved);
        });
        titledPane.getChildren().add(hyperlink);
        hookDragHandlers(titledPane);
        return titledPane;
    }

    private Pane createBottomPane() {
        Pane bottomPane = new Pane();
        Rectangle resizeMarkRegion = createResizeMarkRegion();
        resizeMarkRegion.xProperty().bind(bottomPane.widthProperty()
                .subtract(resizeMarkRegion.widthProperty()));
        resizeMarkRegion.yProperty().bind(bottomPane.heightProperty()
                .subtract(resizeMarkRegion.heightProperty()));
        bottomPane.getChildren().add(resizeMarkRegion);
        return bottomPane;
    }

    private Rectangle createResizeMarkRegion() {
        final int WIDTH = 10;
        final int HEIGHT = 10;
        Rectangle region = new Rectangle(WIDTH, HEIGHT);
        region.setStyle("-fx-fill: dimgray;");
        region.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
            getScene().setCursor(Cursor.NW_RESIZE);
        });
        region.addEventHandler(MouseEvent.MOUSE_EXITED, event -> {
            getScene().setCursor(Cursor.DEFAULT);
        });
        hookResizeHandlers(region);
        return region;
    }

    private void hookDragHandlers(Pane pane) {
        dragContext = new DragContext();
        addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            if (pane.contains(event.getX(), event.getY())) {
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

    private void hookResizeHandlers(Rectangle region) {
        region.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            canResize = true;
        });
        addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
            if (canResize) {
                setPrefWidth(event.getX());
                setPrefHeight(event.getY());
            }
        });
        region.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            if (canResize) {
                canResize = false;
            }
        });
    }

    private void hookContextMenu() {
        chart.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.isPopupTrigger()) {
                if (contextMenu == null) {
                    contextMenu = new GatableChartContextMenu(gatableChart());
                }
                contextMenu.show(chart,
                        event.getScreenX(), event.getScreenY());
            }
        });
    }

    private void hookAxisLabelListeners() {
        chart.getXAxis().labelProperty().addListener((observable, oldValue, newValue) -> {
            // remove gate first, or the following propagation will be wrong.
            removeGate();
            replotChartData();
            propagateToNextChart();
        });
        chart.getYAxis().labelProperty().addListener((observable, oldValue, newValue) -> {
            removeGate();
            replotChartData();
            propagateToNextChart();
        });
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
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

    public void propagateToNextChart() {
        // clear all data from current chart to tail chart
        LinkedNode nextArrow = getNextNode();
        while (nextArrow != null) {
            WrappedChart nextChart = (WrappedChart) nextArrow.getNextNode();
            nextChart.clearAllData();
            nextArrow = nextChart.getNextNode();
        }
        if (getNextChart() != null) {
            getGatedData().forEach(getNextChart()::addData);
        }
    }

    private GatableChart<Number, Number> gatableChart() {
        return (GatableChart<Number, Number>) chart;
    }

    private WrappedChart getNextChart() {
        if (nextNode == null) {
            return null;
        }
        return(WrappedChart) nextNode.getNextNode();
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

    @Override
    public void setGate(Gate<Number, Number> gate) {
        gatableChart().setGate(gate);
    }

    @Override
    public void removeGate() {
        gatableChart().removeGate();
    }

    @Override
    public Gate<Number, Number> getGate() {
        return gatableChart().getGate();
    }

    @Override
    public void addData(KVData data) {
        Platform.runLater(() -> {
            gatableChart().addData(data);
            if (getNextChart() != null && gatableChart().isGated(data)) {
                getNextChart().addData(data);
            }
        });
    }

    @Override
    public void clearAllData() {
        gatableChart().clearAllData();
    }

    @Override
    public void replotChartData() {
        gatableChart().replotChartData();
    }

    @Override
    public List<KVData> getGatedData() {
        return gatableChart().getGatedData();
    }

    @Override
    public boolean isGated(KVData data) {
        return gatableChart().isGated(data);
    }

    public void setAxisCandidateNames(List<String> names) {
        gatableChart().setAxisCandidateNames(names);
    }

    @Override
    public void addGateLifeCycleListener(GateLifeCycleListener listener) {
        gatableChart().addGateLifeCycleListener(listener);
    }

    @Override
    public List<KVData> getKVData() {
        return gatableChart().getKVData();
    }

    @Override
    public Axis<Number> getXAxis() {
        return gatableChart().getXAxis();
    }

    @Override
    public Axis<Number> getYAxis() {
        return gatableChart().getYAxis();
    }

    public void addChartRemovedListener(ChartRemovedListener listener) {
        listeners.add(listener);
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
        return new JsonObject(getUniqueId(), type, getLayoutX(), getLayoutY(),
                getPrefWidth(), getPrefHeight(),
                AxisJsonObject.fromAxis((ValueAxis<Number>) chart.getXAxis()),
                AxisJsonObject.fromAxis((ValueAxis<Number>) chart.getYAxis()),
                GateJsonObject.fromGate(getGate()));
    }

    public static WrappedChart fromJsonObject(JsonObject json) {
        ValueAxis<Number> xAxis = json.xAxisJson.toAxis();
        ValueAxis<Number> yAxis = json.yAxisJson.toAxis();

        XYChart<Number, Number> chart;
        if (json.type.equals("Scatter")) {
            chart = new GatedScatterChart(xAxis, yAxis);
        } else if (json.type.equals("Histogram")) {
            chart = new GatedHistogram(xAxis, yAxis);
        } else {
            throw new RuntimeException("Unknown type of chart");
        }

        WrappedChart wrapper = new WrappedChart(chart);
        wrapper.setUniqueId(json.uniqueId);
        wrapper.setLayoutX(json.x);
        wrapper.setLayoutY(json.y);
        wrapper.setPrefWidth(json.width);
        wrapper.setPrefHeight(json.height);

        if (!json.gateJson.type.equals("")) {
            Gate<Number, Number> gate;
            if (json.gateJson.type.equals("Rectangle")) {
                gate = new RectangleGate<>();
            } else if (json.gateJson.type.equals("Polygon")) {
                gate = new PolygonGate<>();
            } else {
                throw new RuntimeException("Unknown type of gate");
            }
            wrapper.setGate(gate);
            json.gateJson.initGate(gate);
        }

        return wrapper;
    }

    public class JsonObject {
        public final String uniqueId;
        public final String type;
        public final double x;
        public final double y;
        public final double width;
        public final double height;
        public final AxisJsonObject xAxisJson;
        public final AxisJsonObject yAxisJson;
        public final GateJsonObject<Number, Number> gateJson;

        public JsonObject(String uniqueId, String type, double x, double y, double width, double height,
                          AxisJsonObject xAxisJson, AxisJsonObject yAxisJson,
                          GateJsonObject<Number, Number> gateJson) {
            this.uniqueId = uniqueId;
            this.type = type;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.xAxisJson = xAxisJson;
            this.yAxisJson = yAxisJson;
            this.gateJson = gateJson;
        }
    }
}
