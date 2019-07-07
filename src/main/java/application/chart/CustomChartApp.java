package application.chart;

import application.chart.axis.LogarithmicAxis;
import application.chart.gate.GatedScatterChart;
import application.utils.Resource;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class CustomChartApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Scatter Chart Sample");
        final LogarithmicAxis xAxis = new LogarithmicAxis(1, 100);
        final NumberAxis yAxis = new NumberAxis(0, 100, 10);
        final ScatterChart sc = new GatedScatterChart(xAxis,yAxis);
        sc.getStylesheets().add(Resource.getStyle("chart.css"));
        xAxis.setLabel("Age (years)");
        yAxis.setLabel("Returns to date");
        sc.setTitle("Investment Overview");
//        sc.setLayoutX(10);

        XYChart.Series series1 = new XYChart.Series();
        series1.setName("Equities");
        series1.getData().add(new XYChart.Data(1, 93.2));
        series1.getData().add(new XYChart.Data(10, 33.6));
        series1.getData().add(new XYChart.Data(20, 24.8));
        series1.getData().add(new XYChart.Data(50, 14));
        series1.getData().add(new XYChart.Data(100, 26.4));

        sc.getData().addAll(series1);
        WrappedChart wrapper = new WrappedChart(sc);

        Pane root = new Pane();
        root.getChildren().add(wrapper);

        Button changeSizeBtn = new Button("更改尺寸");
        changeSizeBtn.setOnAction(event -> {
            wrapper.setPrefWidth(wrapper.getPrefWidth() - 50);
            wrapper.setPrefHeight(wrapper.getPrefHeight() - 50);
//            System.out.println(((GatedScatterChart<Number, Number>) sc).getGatedData().get(0).getData());
        });
        root.getChildren().add(changeSizeBtn);
        Scene scene  = new Scene(root, 500, 400);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
