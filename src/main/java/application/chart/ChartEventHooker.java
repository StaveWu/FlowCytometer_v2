package application.chart;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.chart.Chart;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;


public class ChartEventHooker {

    private Chart chart;
    private RegionForDrag regionForDrag;
    private RegionForResize regionForResize;

    private DragContext contextForDrag;
    private boolean canResizable = false;
    private boolean canDraggable = false;

    public ChartEventHooker(Chart chart) {
        this.chart = chart;
        regionForDrag = new RegionForDrag(chart);
        regionForResize = new RegionForResize(chart);

        contextForDrag = new DragContext();

        // Reset cursor in case the any change cursor action performed blow
        this.chart.addEventHandler(MouseEvent.MOUSE_MOVED, event -> {
            if (!regionForDrag.contains(event.getX(), event.getY())
                    && !regionForResize.contains(event.getX(), event.getY())) {
                chart.getScene().setCursor(Cursor.DEFAULT);
            }
        });
    }

    public void hookDraggableEvent() {
        chart.addEventHandler(MouseEvent.MOUSE_MOVED, event -> {
            if (regionForDrag.contains(event.getX(), event.getY())) {
                chart.getScene().setCursor(Cursor.MOVE);
            }
        });
        chart.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            if (regionForDrag.contains(event.getX(), event.getY())) {
                Point2D mouseLoc = chart.localToParent(event.getX(), event.getY());
                contextForDrag.mouseAnchorX = mouseLoc.getX();
                contextForDrag.mouseAnchorY = mouseLoc.getY();
                contextForDrag.initialTranslateX = chart.getTranslateX();
                contextForDrag.initialTranslateY = chart.getTranslateY();
                canDraggable = true;
            }
        });
        chart.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
            if (canDraggable) {
                Point2D mouseLoc = chart.localToParent(event.getX(), event.getY());
                chart.setTranslateX(contextForDrag.initialTranslateX
                        + mouseLoc.getX() - contextForDrag.mouseAnchorX);
                chart.setTranslateY(contextForDrag.initialTranslateY
                        + mouseLoc.getY() - contextForDrag.mouseAnchorY);
            }
        });
        chart.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            if (canDraggable) {
                canDraggable = false;
            }
        });
    }

    public void hookResizableEvent() {
        chart.addEventHandler(MouseEvent.MOUSE_MOVED, event -> {
            if (regionForResize.contains(event.getX(), event.getY())) {
                chart.getScene().setCursor(Cursor.NW_RESIZE);
            }
        });
        chart.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            if (regionForResize.contains(event.getX(), event.getY())) {
                canResizable = true;
            }
        });
        chart.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
            if (canResizable) {
                chart.setPrefWidth(event.getX());
                chart.setPrefHeight(event.getY());
            }
        });
        chart.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            if (canResizable) {
                canResizable = false;
            }
        });
    }

    /**
     * An inner class which used to define a region for dragging
     */
    private class RegionForDrag {
        private Rectangle region;
        private static final int HEIGHT = 30;

        RegionForDrag(Chart chart) {
            region = new Rectangle(chart.getWidth(), HEIGHT);
            region.widthProperty().bind(chart.widthProperty());
        }

        boolean contains(double x, double y) {
            return region.contains(x, y);
        }
    }

    /**
     * An inner class which used to define a region for resizing
     */
    private class RegionForResize {
        private Rectangle region;
        private static final int WIDTH = 20;
        private static final int HEIGHT = 20;

        RegionForResize(Chart chart) {
            region = new Rectangle(WIDTH, HEIGHT);
            IntegerProperty xDelta = new SimpleIntegerProperty(WIDTH);
            IntegerProperty yDelta = new SimpleIntegerProperty(HEIGHT);
            region.xProperty().bind(chart.widthProperty().subtract(xDelta));
            region.yProperty().bind(chart.heightProperty().subtract(yDelta));
        }

        boolean contains(double x, double y) {
            return region.contains(x, y);
        }
    }

    private static final class DragContext {
        double mouseAnchorX;
        double mouseAnchorY;
        double initialTranslateX;
        double initialTranslateY;
    }
}
