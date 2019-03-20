package application.channel;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import static org.junit.Assert.*;

public class ChannelControllerTest extends Application {


    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Area Chart Sample");
        final NumberAxis xAxis = new NumberAxis(1, 31, 1);
        final NumberAxis yAxis = new NumberAxis();
        final AreaChart<Number,Number> ac =
                new AreaChart<>(xAxis, yAxis);
        ac.getXAxis().setAutoRanging(true);
        ac.setAnimated(false);
        ac.setTitle("Temperature Monitoring (in Degrees C)");

        XYChart.Series<Number, Number> seriesApril= new XYChart.Series<>();
        seriesApril.setName("April");
        seriesApril.getData().add(new XYChart.Data<>(1, 4));
        seriesApril.getData().add(new XYChart.Data<>(2, 10));
        seriesApril.getData().add(new XYChart.Data<>(3, 15));
        seriesApril.getData().add(new XYChart.Data<>(4, 8));
        seriesApril.getData().add(new XYChart.Data<>(5, 5));
        seriesApril.getData().add(new XYChart.Data<>(6, 18));
        seriesApril.getData().add(new XYChart.Data<>(7, 15));
        seriesApril.getData().add(new XYChart.Data<>(8, 13));
        seriesApril.getData().add(new XYChart.Data<>(9, 19));
        seriesApril.getData().add(new XYChart.Data<>(10, 21));
        seriesApril.getData().add(new XYChart.Data<>(11, 21));

        Button btn = new Button("add");
        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                int lastIdx = (int) seriesApril.getData().get(seriesApril.getData().size() - 1).getXValue();
                for (int i = 0; i < 1000; i++) {
                    if (seriesApril.getData().size() > 11) {
                        seriesApril.getData().remove(0);
                    }
                    seriesApril.getData().add(new XYChart.Data<>(lastIdx++, 15));
                }
            }
        });

        VBox box = new VBox();
        box.getChildren().add(ac);
        box.getChildren().add(btn);
        Scene scene  = new Scene(box,800,600);
        ac.getData().add(seriesApril);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}