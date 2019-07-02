package application.worksheet;

import application.chart.ChartWrapper;
import application.chart.GatedScatterChart;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.NumberAxis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
public class WorksheetController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(WorksheetController.class);

    private int delta = 0;

    @FXML
    private LinkedChartsPane chartsPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @FXML
    protected void createScatterChart() {
        GatedScatterChart<Number, Number> scatterChart = new GatedScatterChart<>(new NumberAxis(),
                new NumberAxis());
        ChartWrapper wrapper = new ChartWrapper(scatterChart);
        final int loc = getDelta();
        wrapper.setLayoutX(loc);
        wrapper.setLayoutY(loc);
        chartsPane.getChildren().add(wrapper);
    }

    private int getDelta() {
        if (delta > 200) {
            delta = 10;
        } else {
            delta += 40;
        }
        return delta;
    }

    @FXML
    protected void connect() {
        log.info("on connecting");
        chartsPane.setState(LinkedChartsPane.State.ON_CONNECTING);
    }

    @FXML
    protected void gateRectangle() {
        log.info("on rectangle gating");
    }
}
