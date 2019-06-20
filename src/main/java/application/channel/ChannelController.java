package application.channel;

import application.channel.model.*;
import application.event.ChannelChangedEvent;
import application.event.EventBusFactory;
import application.event.SamplingPointsCapturedEvent;
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
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Component
public class ChannelController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(ChannelController.class);
    private final EventBus eventBus = EventBusFactory.getEventBus();

    @Autowired
    private ChannelModelRepository channelModelRepository;
    @Autowired
    private ChannelSeriesRepository channelSeriesRepository;

    private SamplingDataCache samplingDataCache;

    @FXML
    private HBox channelsHBox;

    public ChannelController() {
        eventBus.register(this);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        channelsHBox.getChildren().addListener((ListChangeListener<Node>) c -> {
            saveChannelInformation();
            eventBus.post(new ChannelChangedEvent(channelsHBox.getChildren().size()));
        });

        channelModelRepository.setLocation(FCMRunTimeConfig.getInstance()
                .getProjectConfigFolder() + File.separator + "channels.json");
        channelModelRepository.findAll().forEach(this::addChannelCell);

//        channelSeriesRepository.setLocation(FCMRunTimeConfig.getInstance()
//                .getRootDir() + File.separator + "SamplingData.txt");
//        samplingDataCache = new SamplingDataCache(channelSeriesRepository.findAll());
//        samplingDataCache.registerRowDataAddedHandler(() -> {
//            if (samplingDataCache.canSave()) {
//                CompletableFuture.runAsync(() -> {
//                    channelSeriesRepository.appendSeries(samplingDataCache.getSeriesList());
//                    samplingDataCache.clear();
//                });
//            }
//        });

        // start a thread to monitor channel series
//        Thread channelseriesMonitor = new Thread(() -> {
//            while(true) {
//                try {
//                    Thread.sleep(5000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                List<ChannelSeries> headSeriesList = channelSeriesRepository.headSeries();
//                Platform.runLater(() -> {
//                    for (int i = 0; i < headSeriesList.size(); i++) {
//                        ChannelCell channelCell = (ChannelCell) channelsHBox.getChildren().get(i);
//                        XYChart<Number, Number> chart = channelCell.getChart();
//                        XYChart.Series<Number, Number> series = chart.getData().get(0);
//                        int start;
//                        if (series.getData().size() == 0) {
//                            start = 0;
//                        } else {
//                            start = (int) series.getData().get(series.getData().size() - 1).getXValue();
//                        }
//                        series.getData().clear();
//                        for (Double ele : headSeriesList.get(i).getData()) {
//                            series.getData().add(new XYChart.Data<>(start++, ele));
//                            chart.requestLayout();
//                        }
//                    }
//                });
//            }
//        });
//        channelseriesMonitor.setDaemon(true);
//        channelseriesMonitor.start();
    }

    private void addChannelCell(ChannelModel model) {
        ChannelCell channelCell = new ChannelCell(this, model);
        channelCell.addPropertyChangeHandler(this::saveChannelInformation);
        channelsHBox.getChildren().add(channelCell);
    }

    @FXML
    protected void newChannelCell() {
        addChannelCell(new ChannelModel());
    }

    public void removeChannelCell(ChannelCell cell) {
        channelsHBox.getChildren().remove(cell);
    }

    private void saveChannelInformation() {
        try {
            channelModelRepository.saveAll(channelsHBox.getChildren().stream()
                    .map(e -> ((ChannelCell)e).getChannelModel())
                    .collect(Collectors.toList()));
        } catch (IOException e) {
            e.printStackTrace();
            UiUtils.getAlert(Alert.AlertType.ERROR, "保存通道数据失败",
                    e.getMessage()).showAndWait();
        }
    }

    @Subscribe
    protected void listen(SamplingPointsCapturedEvent event) {
        // append sampling points
        event.getSamplingPoints().forEach(samplingDataCache::add);
    }

}
