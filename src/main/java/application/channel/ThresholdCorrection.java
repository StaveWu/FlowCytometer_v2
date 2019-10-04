package application.channel;

import application.utils.Resource;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.XYChart;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ThresholdCorrection extends VBox implements Initializable {

    @FXML
    private AreaChart<Number, Number> chart;

    public ThresholdCorrection() {
        FXMLLoader loader = new FXMLLoader(Resource.getFXML("threshold_correction.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        chart.getData().add(new XYChart.Series<>());
        chart.getData().get(0).getData().add(new XYChart.Data<>(1, 2));
        chart.getData().get(0).getData().add(new XYChart.Data<>(2, 423));
        chart.getData().get(0).getData().add(new XYChart.Data<>(3, 546));
        chart.getData().get(0).getData().add(new XYChart.Data<>(4, 333));
        chart.getData().get(0).getData().add(new XYChart.Data<>(5, 45));
        chart.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {

        });
    }
}
