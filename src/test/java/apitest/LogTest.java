package apitest;

import application.chart.axis.LogarithmicAxis;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

public class LogTest extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        XYChart<Number, Number> chart = new ScatterChart<>(new LogarithmicAxis(), new LogarithmicAxis());
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName(null);
        series.getData().add(new XYChart.Data<>(3, 11));
        series.getData().add(new XYChart.Data<>(3, 12));
        series.getData().add(new XYChart.Data<>(3, 13));
        series.getData().add(new XYChart.Data<>(3, 14));
        series.getData().add(new XYChart.Data<>(3, 15));
        chart.getData().add(series);

        stage.setScene(new Scene(chart));
        stage.show();
    }
}
