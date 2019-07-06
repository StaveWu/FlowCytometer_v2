package application.worksheet;

import application.chart.ArrowHead;
import application.chart.ChartRemovedListener;
import application.chart.ChartWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LinkedChartsPane extends AnchorPane {

    private ArrowHead activeArrowHead;
    private List<ChartLifeCycleListener> listeners = new ArrayList<>();

    public enum State {
        ON_CONNECTING,
        IDLE
    }
    private State state = State.IDLE;

    public boolean isOnConnecting() {
        return state == State.ON_CONNECTING;
    }

    public void setState(State state) {
        this.state = state;
    }

    public LinkedChartsPane() {
        super();
        // hook arrow head related handlers
        getChildren().addListener((ListChangeListener<Node>) c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    for (Node node :
                            c.getAddedSubList()) {
                        if (node instanceof ChartWrapper) {
                            addRecorrectArrowHeadListener((ChartWrapper) node);
                        }
                    }
                }
            }
        });
        addEventFilter(MouseEvent.ANY, event -> {
            if (isOnConnecting())
                // disable mouse events for all children
                event.consume();
        });

        addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            if (isOnConnecting()) {
                activeArrowHead = new ArrowHead(event.getX(), event.getY());
                getChildren().add(activeArrowHead);
            }
        });

        addEventFilter(MouseEvent.MOUSE_DRAGGED, event -> {
            if (isOnConnecting()) {
                activeArrowHead.setEnd(event.getX(), event.getY());
            }
        });

        addEventFilter(MouseEvent.MOUSE_RELEASED, event -> {
            if (isOnConnecting()) {
                activeArrowHead.setEnd(event.getX(), event.getY());
                setState(State.IDLE);
                ChartWrapper startChart = queryChartByPoint(activeArrowHead.getStart());
                if (startChart == null) { // arrowhead is not valid, so remove it
                    removeArrowHead();
                    return;
                }

                ChartWrapper endChart = queryChartByPoint(activeArrowHead.getEnd());
                if (endChart == null) {
                    removeArrowHead();
                    return;
                }
                bind(activeArrowHead, startChart, endChart);
                correctArrowHead(activeArrowHead, startChart, endChart);
            }
        });
    }

    private ChartWrapper queryChartByPoint(Point2D point) {
        return (ChartWrapper) getChildren().stream()
                .filter(child -> child instanceof ChartWrapper
                        && child.contains(child.parentToLocal(point)))
                .findFirst()
                .orElse(null);
    }

    private void removeArrowHead() {
        getChildren().remove(activeArrowHead);
        activeArrowHead = null;
    }

    private void bind(ArrowHead arrowHead, ChartWrapper startChart, ChartWrapper endChart) {
        // clear old arrowhead
        if (startChart.getNextNode() != null) {
            getChildren().remove(startChart.getNextNode());
        }
        if (endChart.getPrevNode() != null) {
            getChildren().remove(endChart.getPrevNode());
        }
        // bind three linked nodes
        arrowHead.setPrevNode(startChart);
        arrowHead.setNextNode(endChart);
        startChart.setNextNode(arrowHead);
        endChart.setPrevNode(arrowHead);
    }

    private void correctArrowHead(ArrowHead arrowHead, ChartWrapper startChart, ChartWrapper endChart) {
        Point2D srcCenter = getCenterPoint(startChart);
        Point2D dstCenter = getCenterPoint(endChart);

        double bestStartX, bestStartY;
        double bestEndX, bestEndY;
        //计算斜率a和截距b: y = ax + b
        if(dstCenter.getX() != srcCenter.getX()) {
            double a = (dstCenter.getY() - srcCenter.getY()) / (dstCenter.getX() - srcCenter.getX());
            double b = srcCenter.getY() - a * srcCenter.getX();
            //在两张图上各取一条与中心线相交的边界
            if(a > -1 && a < 1) {
                if(dstCenter.getX() > srcCenter.getX()) {
                    //src取右，dst取左
                    bestStartX = startChart.getLayoutX() + startChart.getWidth();
                    bestEndX = endChart.getLayoutX();
                }
                else {
                    //src取左，dst取右
                    bestStartX = startChart.getLayoutX();
                    bestEndX = endChart.getLayoutX() + endChart.getWidth();
                }
                bestStartY = a * bestStartX + b;
                bestEndY = a * bestEndX + b;
            }
            else {
                if(dstCenter.getY() > srcCenter.getY()) {
                    //src取下，dst取上
                    bestStartY = startChart.getLayoutY() + startChart.getHeight();
                    bestEndY = endChart.getLayoutY();
                }
                else {
                    //src取上，dst取下
                    bestStartY = startChart.getLayoutY();
                    bestEndY = endChart.getLayoutY() + endChart.getHeight();
                }
                bestStartX = (bestStartY - b) / a;
                bestEndX = (bestEndY - b) / a;
            }
        }
        else {
            if(dstCenter.getY() > srcCenter.getY()) {
                //src取下，dst取上
                bestStartY = startChart.getLayoutY() + startChart.getHeight();
                bestEndY = endChart.getLayoutY();

            }
            else {
                //src取上，dst取下
                bestStartY = startChart.getLayoutY();
                bestEndY = endChart.getLayoutY() + endChart.getHeight();
            }
            bestStartX = srcCenter.getX();
            bestEndX = dstCenter.getX();
        }
        arrowHead.setStart(bestStartX, bestStartY);
        arrowHead.setEnd(bestEndX, bestEndY);
    }

    private Point2D getCenterPoint(ChartWrapper chart) {
        double centerX = chart.getLayoutX() + chart.getWidth() / 2;
        double centerY = chart.getLayoutY() + chart.getHeight() / 2;
        return new Point2D(centerX, centerY);
    }

    private void addRecorrectArrowHeadListener(ChartWrapper chart) {
        chart.layoutBoundsProperty().addListener((observable, oldValue, newValue) -> {
            recorrectArrowHead(chart);
        });
        chart.layoutXProperty().addListener((observable, oldValue, newValue) -> {
            recorrectArrowHead(chart);
        });
        chart.layoutYProperty().addListener((observable, oldValue, newValue) -> {
            recorrectArrowHead(chart);
        });
    }

    private void recorrectArrowHead(ChartWrapper chart) {
        ArrowHead prevArrowHead = (ArrowHead) chart.getPrevNode();
        if (prevArrowHead != null) {
            ChartWrapper prevChart = (ChartWrapper) prevArrowHead.getPrevNode();
            correctArrowHead(prevArrowHead, prevChart, chart);
        }
        ArrowHead nextArrowHead = (ArrowHead) chart.getNextNode();
        if (nextArrowHead != null) {
            ChartWrapper nextChart = (ChartWrapper) nextArrowHead.getNextNode();
            correctArrowHead(nextArrowHead, chart, nextChart);
        }
    }

    public void addCellFeature(CellFeature cellFeature) {
        if (getHeadChart() == null) {
            return;
        }
        getHeadChart().addData(cellFeature);
    }

    private ChartWrapper getHeadChart() {
        return (ChartWrapper) getChildren().stream()
                .filter(child -> child instanceof ChartWrapper)
                .filter(child -> ((ChartWrapper)child).getPrevNode() == null)
                .findFirst()
                .orElse(null);
    }

    public void setAxisCandidateNames(List<String> names) {
        getChildren().stream()
                .filter(child -> child instanceof ChartWrapper)
                .forEach(child -> ((ChartWrapper) child).setAxisCandidateNames(names));
    }

    public List<ChartWrapper> getCharts() {
        return getChildren().stream()
                .filter(child -> child instanceof ChartWrapper)
                .map(child -> (ChartWrapper) child)
                .collect(Collectors.toList());
    }

    public void add(ChartWrapper chart) {
        hookChartPropertyChangeListener(chart);
        hookChartRemoveListener(chart);
        getChildren().add(chart);
        listeners.forEach(ChartLifeCycleListener::afterCreate);
    }

    private void hookChartRemoveListener(ChartWrapper chart) {
        chart.addChartRemovedListener(() -> listeners.forEach(ChartLifeCycleListener::afterRemove));
    }

    private void hookChartPropertyChangeListener(ChartWrapper chart) {
        chart.layoutBoundsProperty().addListener((observable, oldValue, newValue) -> {
            listeners.forEach(ChartLifeCycleListener::propertyChanged);
        });
        chart.layoutXProperty().addListener((observable, oldValue, newValue) -> {
            listeners.forEach(ChartLifeCycleListener::propertyChanged);
        });
        chart.layoutYProperty().addListener((observable, oldValue, newValue) -> {
            listeners.forEach(ChartLifeCycleListener::propertyChanged);
        });
        chart.getChart().getXAxis().labelProperty().addListener((observable, oldValue, newValue) -> {
            listeners.forEach(ChartLifeCycleListener::propertyChanged);
        });
        chart.getChart().getYAxis().labelProperty().addListener((observable, oldValue, newValue) -> {
            listeners.forEach(ChartLifeCycleListener::propertyChanged);
        });

    }

    public void addChartLifeCycleListener(ChartLifeCycleListener listener) {
        listeners.add(listener);
    }
}
