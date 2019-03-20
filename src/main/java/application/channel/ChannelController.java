package application.channel;

import application.event.ChannelChangedEvent;
import application.event.EventBusFactory;
import application.event.SamplingPointCapturedEvent;
import application.utils.UiUtils;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.layout.HBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

@Component
public class ChannelController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(ChannelController.class);
    private ChannelModel model;
    private final EventBus eventBus = EventBusFactory.getEventBus();

    @FXML
    private HBox channelsHBox;

    public ChannelController() {
        eventBus.register(this);
    }

    /**
     * using lazy load, to guarantee project root dir being set before
     * @return
     */
    private ChannelModel getModel() {
        if (model == null) {
            model = new ChannelModel();
        }
        return model;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        channelsHBox.getChildren().addListener((ListChangeListener<Node>) c ->
                eventBus.post(new ChannelChangedEvent(channelsHBox.getChildren().size())));
        getModel().getChannelInfos().stream().forEach(info -> addChannelCell(info));
    }

    private void addChannelCell(ChannelInfo info) {
        channelsHBox.getChildren().add(new ChannelCell(this, info));
    }

    @FXML
    protected void saveChannelInfos() {
        try {
            getModel().saveInfos();
        } catch (Exception e) {
            UiUtils.getAlert(Alert.AlertType.ERROR, null,
                    "保存通道参数失败：" + e.getMessage()).showAndWait();
        }
    }

    @FXML
    protected void newChannelCell() {
        ChannelInfo info = new ChannelInfo();
        getModel().addChannelInfo(info);
        addChannelCell(info);
    }

    public void removeChannelCell(ChannelCell cell) {
        getModel().removeChannelInfo(cell.getChannelInfo());
        channelsHBox.getChildren().remove(cell);
    }

    @Subscribe
    protected void listen(SamplingPointCapturedEvent event) {
        Platform.runLater(() -> {
            log.info("on adding sample points to chart");
            long t1 = System.currentTimeMillis();
            List<List<Double>> channels = event.getSamplingPoint();
            for (int i = 0; i < channels.size(); i++) {
                // access channel's data
                List<Double> data = channels.get(i);

                // append data into existing series
                ChannelCell channelCell = (ChannelCell) channelsHBox.getChildren().get(i);
                XYChart<Number, Number> chart = channelCell.getChart();
                XYChart.Series<Number, Number> series = chart.getData().get(0);
                int start = series.getData().size();
                for (Double ele : data) {
                    // remove first and add last so that chart will perform like a slide window
                    if (series.getData().size() > 100) {
                        series.getData().remove(0);
                    }
                    series.getData().add(new XYChart.Data<>(start++, ele));
                }
            }
            long t2 = System.currentTimeMillis();
            System.out.println("chart画点用时：" + (t2 - t1) + "ms");
        });
    }

}
