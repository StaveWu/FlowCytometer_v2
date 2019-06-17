package application.channel;

import application.channel.model.ChannelModel;
import application.channel.model.ChannelModelRepository;
import application.event.ChannelChangedEvent;
import application.event.EventBusFactory;
import application.event.SamplingPointCapturedEvent;
import application.starter.FCMRunTimeConfig;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

@Component
public class ChannelController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(ChannelController.class);
    private final EventBus eventBus = EventBusFactory.getEventBus();

    @Autowired
    private ChannelModelRepository repository;

    @FXML
    private HBox channelsHBox;

    public ChannelController() {
        eventBus.register(this);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        channelsHBox.getChildren().addListener((ListChangeListener<Node>) c ->
                eventBus.post(new ChannelChangedEvent(channelsHBox.getChildren().size())));

        repository.setLocation(FCMRunTimeConfig.getInstance()
                .getProjectConfigFolder() + File.separator + "channels.json");
        repository.findAll().forEach(this::addChannelCell);
    }

    private void addChannelCell(ChannelModel model) {
        channelsHBox.getChildren().add(new ChannelCell(this, model));
    }

    @FXML
    protected void newChannelCell() {
        ChannelModel model = new ChannelModel();
        repository.addModel(model);
        addChannelCell(model);
    }

    public void removeChannelCell(ChannelCell cell) {
        repository.removeModel(cell.getChannelModel());
        channelsHBox.getChildren().remove(cell);
    }

    public void saveChannelInformation() {
        if (repository == null) {
            return;
        }
        try {
            repository.saveAll();
        } catch (IOException e) {
            e.printStackTrace();
            UiUtils.getAlert(Alert.AlertType.ERROR, "保存通道数据失败",
                    e.getMessage()).showAndWait();
        }
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
                int start;
                if (series.getData().size() == 0) {
                    start = 0;
                } else {
                    start = (int) series.getData().get(series.getData().size() - 1).getXValue();
                }
                for (Double ele : data) {
                    // remove first and add last so that the chart will
                    // perform like a slide window
                    if (series.getData().size() > 3000) {
                        series.getData().remove(0);
                    }
                    series.getData().add(new XYChart.Data<>(start++, ele));
                    chart.requestLayout();
                }
            }
            long t2 = System.currentTimeMillis();
            System.out.println("chart画点用时：" + (t2 - t1) + "ms");
        });
    }

}
