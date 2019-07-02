package application.worksheet;

import application.chart.ArrowHead;
import application.chart.ChartWrapper;
import application.chart.GatedScatterChart;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.NumberAxis;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
public class WorksheetController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(WorksheetController.class);

    private int delta = 0;

    private enum WorksheetState {
        ON_CONNECTING,
        ON_RECTANGE_CIRCLING,
        IDLE
    }

    private WorksheetState state = WorksheetState.IDLE;

    @FXML
    private AnchorPane chartsPane;

    private ArrowHead activeArrowHead;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        chartsPane.addEventFilter(MouseEvent.ANY, event -> {
            if (state == WorksheetState.ON_CONNECTING)
                // disable mouse events for all children
                event.consume();
        });

        chartsPane.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            if (state == WorksheetState.ON_CONNECTING) {
                activeArrowHead = new ArrowHead(event.getX(), event.getY());
                chartsPane.getChildren().add(activeArrowHead);
            }
        });

        chartsPane.addEventFilter(MouseEvent.MOUSE_DRAGGED, event -> {
            if (state == WorksheetState.ON_CONNECTING) {
                activeArrowHead.setEnd(event.getX(), event.getY());
            }
        });

        chartsPane.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> {
            if (state == WorksheetState.ON_CONNECTING) {
                activeArrowHead.setEnd(event.getX(), event.getY());
                state = WorksheetState.IDLE;
            }
        });
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
        state = WorksheetState.ON_CONNECTING;
    }
}
