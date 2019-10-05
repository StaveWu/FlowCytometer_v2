package apitest;

import javafx.scene.chart.XYChart;

public class SeriesTest {

    public static void main(String[] args) {
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        long start = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            series.getData().add(new XYChart.Data<>());
        }
        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }
}
