package application.worksheet;

import application.chart.ChartWrapper;
import application.chart.gate.GatedHistogram;
import application.chart.gate.GatedScatterChart;
import application.event.CellFeatureCapturedEvent;
import application.event.EventBusFactory;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import javafx.fxml.FXML;
import javafx.scene.chart.NumberAxis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class WorksheetController {

    private static final Logger log = LoggerFactory.getLogger(WorksheetController.class);
    private final EventBus eventBus = EventBusFactory.getEventBus();

    private int delta = 0;

    @FXML
    private LinkedChartsPane chartsPane;

    public WorksheetController() {
        eventBus.register(this);
    }

    @Subscribe
    public void listen(CellFeatureCapturedEvent event) {
        log.info("cell feature received: " + event.getCellFeature());
        chartsPane.addCellFeature(new CellFeature(event.getCellFeature()));
    }

    @FXML
    protected void createScatterChart() {
        GatedScatterChart scatterChart = new GatedScatterChart(
                new NumberAxis(),
                new NumberAxis());
        ChartWrapper wrapper = new ChartWrapper(scatterChart);
        final int loc = getDelta();
        wrapper.setLayoutX(loc);
        wrapper.setLayoutY(loc);
        chartsPane.getChildren().add(wrapper);
    }

    @FXML
    protected void createHistogram() {
        GatedHistogram<Number, Number> histogram = new GatedHistogram<>(
                new NumberAxis(),
                new NumberAxis());
        ChartWrapper wrapper = new ChartWrapper(histogram);
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
