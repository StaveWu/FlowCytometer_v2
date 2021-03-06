package application.channel;

import application.channel.featurecapturing.CellFeatureCaptureThread;
import application.channel.featurecapturing.ChannelMeta;
import application.channel.featurecapturing.ChannelMetaRepository;
import application.channel.sampling.SamplingPointRepository;
import application.event.*;
import application.starter.FCMRunTimeConfig;
import application.utils.UiUtils;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class ChannelController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(ChannelController.class);
    private final EventBus eventBus = EventBusFactory.getEventBus();

    @Autowired
    private ChannelMetaRepository channelMetaRepository;

    @Autowired
    private SamplingPointRepository samplingPointRepository;

    private MonitorSamplingPointThread monitorSamplingPointThread;
    private CellFeatureCaptureThread cellFeatureCaptureThread;
    private ChannelSetting channelSetting;

    private boolean isOnSampling = false;

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
            if (isIdDuplicated(updatedMetas) || isNameDuplicated(updatedMetas)) {
                UiUtils.getAlert(Alert.AlertType.WARNING, "非法操作",
                        "通道类型或通道名不能重复").showAndWait();
                return;
            }
            saveChannelInformation(updatedMetas);
            eventBus.post(new ChannelChangedEvent(updatedMetas));
        });

        channelMetaRepository.setLocation(FCMRunTimeConfig.getInstance()
                .getProjectConfigFolder() + File.separator + "channels.json");
        channelMetaRepository.findAll().forEach(this::addChannelCell);

        channelSetting = new ChannelSetting(FCMRunTimeConfig.getInstance()
                .getProjectConfigFolder() + File.separator + "channel_setting.json");

        startMonitorPointsDaemon();
    }

    private void addChannelCell(ChannelMeta model) {
        ChannelCell channelCell = new ChannelCell(this, model, samplingPointRepository);
        channelCell.addPropertyChangeHandler(() -> {
            List<ChannelMeta> updatedMetas = channelsHBox.getChildren().stream()
                    .map(e -> ((ChannelCell)e).getChannelMeta())
                    .collect(Collectors.toList());
            if (isIdDuplicated(updatedMetas) || isNameDuplicated(updatedMetas)) {
                UiUtils.getAlert(Alert.AlertType.WARNING, "非法操作",
                        "通道类型或通道名不能重复").showAndWait();
                return;
            }
            saveChannelInformation(updatedMetas);
            eventBus.post(new ChannelChangedEvent(updatedMetas));
        });
        channelsHBox.getChildren().add(channelCell);
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

    private boolean isIdDuplicated(List<ChannelMeta> metas) {
        Set<String> lump = new HashSet<>();
        for (ChannelMeta meta :
                metas) {
            if (lump.contains(meta.getId())) {
                return true;
            }
            lump.add(meta.getId());
        }
        return false;
    }

    private boolean isNameDuplicated(List<ChannelMeta> metas) {
        Set<String> lump = new HashSet<>();
        for (ChannelMeta meta :
                metas) {
            if (lump.contains(meta.getName())) {
                return true;
            }
            lump.add(meta.getName());
        }
        return false;
    }

    @FXML
    protected void newChannelCell() {
        addChannelCell(new ChannelMeta());
    }

    @FXML
    protected void setting() {
        Optional<Settings> res = getSettingsDialog().showAndWait();
        res.ifPresent(value -> {
            try {
                channelSetting.setLookback(Integer.valueOf(value.lookback));
                channelSetting.setMaxBias(Integer.valueOf(value.maxBias));
            } catch (Exception e) {
                e.printStackTrace();
                UiUtils.getAlert(Alert.AlertType.ERROR, "设置无效",
                        e.getMessage()).showAndWait();
            }
        });
    }

    private Dialog<Settings> getSettingsDialog() {
        Dialog<Settings> dialog = new Dialog<>();
        dialog.setTitle("设置");
        dialog.setHeaderText("通道全局设置");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 10, 10, 10));

        TextField lookBackField = new TextField();
        lookBackField.setText("" + channelSetting.getLookback());
        TextField maxBiasField = new TextField();
        maxBiasField.setText("" + channelSetting.getMaxBias());

        grid.add(new Label("每次刷新的采样点数(<=800)："), 0, 0);
        grid.add(lookBackField, 1, 0);
        grid.add(new Label("判为同一细胞峰的最大允许偏置："), 0, 1);
        grid.add(maxBiasField, 1, 1);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                return new Settings(lookBackField.getText(), maxBiasField.getText());
            }
            return null;
        });
        return dialog;
    }

    private class Settings {
        public final String lookback;
        public final String maxBias;

        public Settings(String lookback, String maxBias) {
            this.lookback = lookback;
            this.maxBias = maxBias;
        }
    }

    @Subscribe
    public void listen(StartSamplingEvent event) {
        isOnSampling = true;
        String channelDataFileName = String.format("ChannelData_%s.txt", event.getTimeStamp());
        samplingPointRepository.setLocation(FCMRunTimeConfig.getInstance()
                .getRootDir() + File.separator + channelDataFileName);

        startCellFeatureCaptureDaemon();
    }
    private void startCellFeatureCaptureDaemon() {
        stopCellFeatureCaptureDaemon();
        cellFeatureCaptureThread = new CellFeatureCaptureThread(
                channelMetaRepository.findAll(),
                channelSetting.getMaxBias());
        cellFeatureCaptureThread.registerCellFeatureCapturedHandler(eventBus::post);
        cellFeatureCaptureThread.setDaemon(true);
        cellFeatureCaptureThread.start();
    }
    private void stopCellFeatureCaptureDaemon() {
        if (cellFeatureCaptureThread != null) {
            cellFeatureCaptureThread.interrupt();
            cellFeatureCaptureThread = null;
        }
    }

    private void startMonitorPointsDaemon() {
        stopMonitorPointsDaemon();
        monitorSamplingPointThread = new MonitorSamplingPointThread(channelsHBox,
                channelSetting, samplingPointRepository);
        monitorSamplingPointThread.setDaemon(true);
        monitorSamplingPointThread.start();
    }
    private void stopMonitorPointsDaemon() {
        if (monitorSamplingPointThread != null) {
            monitorSamplingPointThread.interrupt();
            monitorSamplingPointThread = null;
        }
    }

    @Subscribe
    protected void listen(StopSamplingEvent event) {
        log.info("stop sampling received");
        isOnSampling = false;
        stopCellFeatureCaptureDaemon();
    }

    @Subscribe
    protected void listen(SamplingPointsCapturedEvent event) {
        if (!isOnSampling) {
            return;
        }
        samplingPointRepository.savePoints(event.getSamplingPoints());
        event.getSamplingPoints().forEach(point -> cellFeatureCaptureThread.addSamplingPoint(point));
    }

    @Subscribe
    protected void listen(ChannelDataLoadAction action) {
        log.info("load channel data: " + action.getChannelDataPath());
        samplingPointRepository.setLocation(action.getChannelDataPath());
        startCellFeatureCaptureDaemon();

        try {
            samplingPointRepository.pointsStream().forEach(cellFeatureCaptureThread::addSamplingPoint);
        } catch (Exception e) {
            e.printStackTrace();
            UiUtils.getAlert(Alert.AlertType.ERROR, "读取文件失败",
                    e.getMessage()).showAndWait();
        }
    }

}
