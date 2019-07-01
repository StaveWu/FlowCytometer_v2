package application.channel;

import application.channel.model.*;
import application.event.*;
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
import org.springframework.stereotype.Controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@Controller
public class ChannelController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(ChannelController.class);
    private final EventBus eventBus = EventBusFactory.getEventBus();

    @Autowired
    private ChannelMetaRepository channelMetaRepository;

    @Autowired
    private SamplingPointRepository samplingPointRepository;

    @Autowired
    private SamplingPointSeriesTranslator samplingPointSeriesTranslator;

    private CellFeatureCapturer cellFeatureCapturer;

    @FXML
    private HBox channelsHBox;

    public ChannelController() {
        eventBus.register(this);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        channelsHBox.getChildren().addListener((ListChangeListener<Node>) c -> {
            List<ChannelMeta> updatedMetas = channelsHBox.getChildren().stream()
                    .map(e -> ((ChannelCell)e).getChannelMeta())
                    .collect(Collectors.toList());
            saveChannelInformation(updatedMetas);
            eventBus.post(new ChannelChangedEvent(updatedMetas));
        });

        channelMetaRepository.setLocation(FCMRunTimeConfig.getInstance()
                .getProjectConfigFolder() + File.separator + "channels.json");
        channelMetaRepository.findAll().forEach(this::addChannelCell);

        // start a thread to monitor channel series
        Thread histgramUpdater = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                List<SamplingPoint> points = samplingPointRepository.getRecentPoints();
                List<XYChart.Series<Number, Number>> seriesList = samplingPointSeriesTranslator.toSeries(points);
                Platform.runLater(() -> {
                    for (int i = 0; i < seriesList.size(); i++) {
                        ChannelCell channelCell = (ChannelCell) channelsHBox.getChildren().get(i);
                        XYChart<Number, Number> chart = channelCell.getChart();
                        chart.getData().clear();
                        chart.getData().add(seriesList.get(i));
                        chart.requestLayout();
                    }
                });
            }
        });
        histgramUpdater.setDaemon(true);
        histgramUpdater.start();
    }

    private void addChannelCell(ChannelMeta model) {
        ChannelCell channelCell = new ChannelCell(this, model);
        channelCell.addPropertyChangeHandler(() -> {
            List<ChannelMeta> updatedMetas = channelsHBox.getChildren().stream()
                    .map(e -> ((ChannelCell)e).getChannelMeta())
                    .collect(Collectors.toList());
            saveChannelInformation(updatedMetas);
            eventBus.post(new ChannelChangedEvent(updatedMetas));
        });
        channelsHBox.getChildren().add(channelCell);
    }

    @FXML
    protected void newChannelCell() {
        addChannelCell(new ChannelMeta());
    }

    public void removeChannelCell(ChannelCell cell) {
        channelsHBox.getChildren().remove(cell);
    }

    private void saveChannelInformation(List<ChannelMeta> channelMetas) {
        try {
            channelMetaRepository.saveAll(channelMetas);
        } catch (IOException e) {
            e.printStackTrace();
            UiUtils.getAlert(Alert.AlertType.ERROR, "保存通道数据失败",
                    e.getMessage()).showAndWait();
        }
    }

    @Subscribe
    public void listen(StartSamplingEvent event) {
        String channelDataFileName = String.format("ChannelData_%s.txt", event.getTimeStamp());
        samplingPointRepository.setLocation(FCMRunTimeConfig.getInstance()
                .getRootDir() + File.separator + channelDataFileName);

        cellFeatureCapturer = new CellFeatureCapturer(channelMetaRepository.findAll());
        cellFeatureCapturer.registerCellFeatureCapturedHandler(eventBus::post);
    }

    @Subscribe
    protected void listen(SamplingPointsCapturedEvent event) {
        samplingPointRepository.savePoints(event.getSamplingPoints());
        event.getSamplingPoints().forEach(point -> cellFeatureCapturer.addSamplingPoint(point));
    }

}
