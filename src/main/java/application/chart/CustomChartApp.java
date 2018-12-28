package application.chart;

import application.chart.axis.LogarithmicAxis;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

public class CustomChartApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Scatter Chart Sample");
        final LogarithmicAxis xAxis = new LogarithmicAxis(1, 100);
        final NumberAxis yAxis = new NumberAxis(0, 100, 10);
        final ScatterChart<Number,Number> sc = new
                GatedScatterChart<>(xAxis,yAxis);
        xAxis.setLabel("Age (years)");
        yAxis.setLabel("Returns to date");
        sc.setTitle("Investment Overview");

        XYChart.Series series1 = new XYChart.Series();
        series1.setName("Equities");
        series1.getData().add(new XYChart.Data(1, 93.2));
        series1.getData().add(new XYChart.Data(10, 33.6));
        series1.getData().add(new XYChart.Data(20, 24.8));
        series1.getData().add(new XYChart.Data(50, 14));
        series1.getData().add(new XYChart.Data(100, 26.4));

        sc.getData().addAll(series1);
        Scene scene  = new Scene(sc, 500, 400);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
