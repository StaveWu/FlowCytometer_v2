package apitest;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.Comparator;

public class AreaChartSortTest extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        AreaChart<Number, Number> chart = new AreaChart<>(
                new NumberAxis(),
                new NumberAxis());
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.getData().add(new XYChart.Data<>(5, 5));
        series.getData().add(new XYChart.Data<>(2, 2));
        series.getData().add(new XYChart.Data<>(3, 3));
        series.getData().add(new XYChart.Data<>(10, 10));
        series.getData().add(new XYChart.Data<>(6, 6));

        chart.getData().add(series);
        Pane root = new Pane();
        root.getChildren().add(chart);

        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}
