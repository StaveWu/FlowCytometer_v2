package application.worksheet;

import application.channel.featurecapturing.ChannelMeta;
import application.event.CellFeatureCapturedEvent;
import application.event.ChannelChangedEvent;
import application.event.EventBusFactory;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@Component
public class WorksheetController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(WorksheetController.class);
    private final EventBus eventBus = EventBusFactory.getEventBus();

    @FXML
    private LinkedChartsPane chartsPane;

    private List<String> channelNames = new ArrayList<>();

    public WorksheetController() {
        eventBus.register(this);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.info("initialize");
        chartsPane.setAxisCandidateNames(channelNames);
    }

    @Subscribe
    public void listen(CellFeatureCapturedEvent event) {
        log.info("cell feature received: " + event.getCellFeature());
        chartsPane.addCellFeature(new CellFeature(event.getCellFeature()));
    }

    @Subscribe
    public void listen(ChannelChangedEvent event) {
        log.info("channel changed");
        channelNames = event.getChannelMetas().stream()
                .map(ChannelMeta::getName)
                .collect(Collectors.toList());
        if (chartsPane != null) {
            chartsPane.setAxisCandidateNames(channelNames);
        }
    }

    @FXML
    protected void createScatterChart() {
        chartsPane.createScatterChart();
    }

    @FXML
    protected void createHistogram() {
        chartsPane.createHistogram();
    }

    @FXML
    protected void connect() {
        log.info("on connecting");
        chartsPane.setState(LinkedChartsPane.State.ON_CONNECTING);
    }
}
