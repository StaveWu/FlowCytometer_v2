package application.worksheet;

import application.chart.ArrowHead;
import application.chart.ChartWrapper;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.chart.Chart;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

public class LinkedChartsPane extends AnchorPane {

    private ArrowHead activeArrowHead;

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
                correctArrowCoords(startChart, endChart);
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

    private void correctArrowCoords(ChartWrapper startChart, ChartWrapper endChart) {
        Point2D cp1 = getCenterPoint(startChart);
        Point2D cp2 = getCenterPoint(endChart);
        activeArrowHead.setStart(cp1.getX(), cp1.getY());
        activeArrowHead.setEnd(cp2.getX(), cp2.getY());
    }

    private Point2D getCenterPoint(ChartWrapper chart) {
        double centerX = chart.getLayoutX() + chart.getWidth() / 2;
        double centerY = chart.getLayoutY() + chart.getHeight() / 2;
        return new Point2D(centerX, centerY);
    }
}
