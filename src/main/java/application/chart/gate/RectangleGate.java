package application.chart.gate;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.shape.Rectangle;

public class RectangleGate<X, Y> {

    private Rectangle delegate;
    private boolean isActive = false;
    private boolean isLocaled = false;

    private DragContext dragContext;
    private static final class DragContext {
        public double mouseAnchorX;
        public double mouseAnchorY;
        public double initialTranslateX;
        public double initialTranslateY;
    }

    public RectangleGate() {
        delegate = new Rectangle();
        delegate.setStyle("-fx-fill: transparent; " +
                "-fx-stroke: black; " +
                "-fx-stroke-width: 1;");
        setDraggable();
    }

    private void setDraggable() {
        dragContext = new DragContext();
        delegate.setOnMousePressed(event -> {
            if (isLocaled) {
                Point2D mouseLoc = delegate.localToParent(event.getX(), event.getY());
                dragContext.mouseAnchorX = mouseLoc.getX();
                dragContext.mouseAnchorY = mouseLoc.getY();
                dragContext.initialTranslateX = delegate.getTranslateX();
                dragContext.initialTranslateY = delegate.getTranslateY();
            }
        });
        delegate.setOnMouseDragged(event -> {
            if (isLocaled) {
                Point2D mouseLoc = delegate.localToParent(event.getX(), event.getY());
                delegate.setTranslateX(dragContext.initialTranslateX
                        + mouseLoc.getX() - dragContext.mouseAnchorX);
                delegate.setTranslateY(dragContext.initialTranslateY
                        + mouseLoc.getY() - dragContext.mouseAnchorY);
            }
        });
    }

    public Node getDelegate() {
        return delegate;
    }

    public boolean isActive() {
        return isActive;
    }

    public boolean isLocaled() {
        return isLocaled;
    }

    public void setLocaled(boolean localed) {
        isLocaled = localed;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public void setLocation(double x, double y) {
        if (!isActive) {
            return;
        }
        if (!isLocaled) {
            delegate.setX(x);
            delegate.setY(y);
            setLocaled(true);
        } else {
            updateGateShape(x, y);
            setActive(false);
        }
    }

    public void setRunningPoint(double x, double y) {
        if (!isActive) {
            return;
        }
        updateGateShape(x, y);
    }

    private void updateGateShape(double x, double y) {
        double locx = delegate.getX();
        double locy = delegate.getY();
        delegate.setWidth(x - locx);
        delegate.setHeight(y - locy);
    }

    public double getX() {
        return delegate.getX();
    }

    public double getY() {
        return delegate.getY();
    }

    public boolean contains(double x, double y) {
        return delegate.contains(new Point2D(x, y));
    }

    public Bounds getLayoutBounds() {
        return delegate.getLayoutBounds();
    }



}
