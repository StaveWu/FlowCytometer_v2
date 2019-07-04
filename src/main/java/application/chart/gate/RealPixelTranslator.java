package application.chart.gate;

import javafx.geometry.Point2D;
import javafx.scene.chart.Axis;
import javafx.scene.chart.XYChart;

public class RealPixelTranslator {

    public static <X, Y> Point2D getDisplayPosition(XYChart.Data<X, Y> data, Axis<X> xAxis, Axis<Y> yAxis) {
        double x = xAxis.getDisplayPosition(data.getXValue());
        double y = yAxis.getDisplayPosition(data.getYValue());
        return new Point2D(x, y);
    }

    public static <X, Y> XYChart.Data<X, Y> getDataForDisplay(Point2D displayPos, Axis<X> xAxis, Axis<Y> yAxis) {
        X xValue = xAxis.getValueForDisplay(displayPos.getX());
        Y yValue = yAxis.getValueForDisplay(displayPos.getY());
        return new XYChart.Data<>(xValue, yValue);
    }
}
