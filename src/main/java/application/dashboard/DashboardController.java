package application.dashboard;

import application.channel.model.ChannelModel;
import application.channel.model.ChannelModelRepository;
import application.dashboard.device.CommDataParser;
import application.dashboard.device.CommDeviceEventAdapter;
import application.dashboard.device.ICommDevice;
import application.dashboard.device.UsbCommDevice;
import application.event.ChannelChangedEvent;
import application.event.EventBusFactory;
import application.event.SamplingPointCapturedEvent;
import application.utils.UiUtils;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.usb.event.UsbPipeDataEvent;
import javax.usb.event.UsbPipeErrorEvent;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@Component
public class DashboardController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(DashboardController.class);
    private final EventBus eventBus = EventBusFactory.getEventBus();

    @FXML
    private ComboBox<SampleMode> modeCombo;

    @FXML
    private ComboBox<CommunicationDevice> connectionCombo;

    @FXML
    private TextField hourTextField;
    @FXML
    private TextField miniteTextField;
    @FXML
    private TextField secondTextField;

    @FXML
    private TextField cellTextField;

    @FXML
    private Label leftLabel;

    @FXML
    private ProgressIndicator progressIndicator;

    private Service<Void> tickService;
    private ICommDevice commDevice;

    private CircuitBoard circuitBoard = new CircuitBoard();

    @Autowired
    private ChannelModelRepository repository;

    public enum SampleMode {
        TIME("按时间"),
        CELL_NUMBER("按细胞个数");

        private String modeName;

        SampleMode(String name) {
            modeName = name;
        }

        @Override
        public String toString() {
            return modeName;
        }
    }

    public enum CommunicationDevice {
        SERIAL("串口"),
        USB("USB"),
        DEVICE_CONFIG("通讯配置...");

        private String deviceName;

        CommunicationDevice(String name) {
            deviceName = name;
        }

        @Override
        public String toString() {
            return deviceName;
        }
    }

    public DashboardController() {
        eventBus.register(this);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        modeCombo.getItems().add(SampleMode.TIME);
        modeCombo.getItems().add(SampleMode.CELL_NUMBER);

        connectionCombo.getItems().add(CommunicationDevice.SERIAL);
        connectionCombo.getItems().add(CommunicationDevice.USB);
        connectionCombo.getItems().add(CommunicationDevice.DEVICE_CONFIG);

        // define ui constraint
        modeCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
            SampleMode mode = observable.getValue();
            if (mode != null) {
                switch (mode) {
                    case TIME:
                        setCellDisable(true);
                        setTimeDisable(false);
                        break;
                    case CELL_NUMBER:
                        setTimeDisable(true);
                        setCellDisable(false);
                        break;
                    default:
                        setCellDisable(true);
                        setTimeDisable(false);
                }
            }
        });

        circuitBoard.setDataReceivedHandler(dataList -> {
            List<ChannelModel> models = repository.findAll();
            for (int i = 0; i < dataList.size(); i++) {
                models.get(i).setData(dataList.get(i));
            }
        });

        // define communication device creation
        connectionCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
            CommunicationDevice device = observable.getValue();
            if (device != null) {
                switch (device) {
                    case USB:
                        circuitBoard.setCommDevice(new UsbCommDevice());
                        break;
                    case SERIAL:
                        break;
                    case DEVICE_CONFIG:
                        break;
                    default:
                        circuitBoard.setCommDevice(new UsbCommDevice());
                }
            }
        });
    }

    @Subscribe
    public void listen(ChannelChangedEvent event) {
        log.info(event.getNumChannels() + " channels open");
    }

    @FXML
    protected void connectDevice() {
        if(!checkCommDevice()) {
            return;
        }
        log.info("try connecting device...");
        try {
            circuitBoard.connect();
        } catch (Exception e) {
            e.printStackTrace();
            UiUtils.getAlert(Alert.AlertType.ERROR,
                    null, "设备连接失败: " + e.getMessage()).showAndWait();
        }
        if (circuitBoard.isConnected()) {
            log.info("device is connected");
        }
    }

    private boolean checkCommDevice() {
        if (commDevice == null) {
            UiUtils.getAlert(Alert.AlertType.WARNING,
                    null, "请先选择端口类型！").showAndWait();
            return false;
        }
        return true;
    }

    private boolean checkCommConnected() {
        if (!circuitBoard.isConnected()) {
            UiUtils.getAlert(Alert.AlertType.WARNING, null, "请先连接设备！").showAndWait();
            return false;
        }
        return true;
    }

    @FXML
    protected void startSampling() {
        if(!checkCommDevice() || !checkCommConnected()) {
            return;
        }
        tickService = getTickService();
        progressIndicator.progressProperty().unbind();
        progressIndicator.progressProperty().bind(tickService.progressProperty());
        leftLabel.textProperty().bind(tickService.messageProperty());
        tickService.setOnSucceeded(event -> {
            stopSampling();
        });
        tickService.setOnCancelled(event -> {
            progressIndicator.progressProperty().unbind();
            progressIndicator.setProgress(0);
        });

        try {
            log.info("initialize circuit board");
            initializeBoard();
            log.info("start sampling");
            tickService.start();
            circuitBoard.startSampling(repository.findAll().stream()
                    .map(ChannelModel::getId)
                    .collect(Collectors.toList()));
        } catch (Exception e) {
            e.printStackTrace();
            stopSampling();
        }
    }

    private void initializeBoard() throws Exception {
        for (ChannelModel model :
                repository.findAll()) {
            circuitBoard.setVoltage(model.getId(), model.getVoltage());
        }
        circuitBoard.setFrequency();
        circuitBoard.setValve();
        circuitBoard.setSupValve();
    }


    private Service<Void> getTickService() {
        if (modeCombo.getSelectionModel().getSelectedItem() == SampleMode.TIME) {
            return new TimeTickService(convertToSecond(Integer.valueOf(hourTextField.getText()),
                    Integer.valueOf(miniteTextField.getText()),
                    Integer.valueOf(secondTextField.getText())));
        } else {
            return new CounterTickService(Integer.valueOf(cellTextField.getText()));
        }
    }

    @FXML
    protected void stopSampling() {
        if(!checkCommDevice() || !checkCommConnected()) {
            return;
        }
        if (tickService != null) {
            tickService.cancel();
        }
        log.info("stop sampling");
        try {
            circuitBoard.stopSampling();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void resetSystem() {
        if(!checkCommDevice() || !checkCommConnected()) {
            return;
        }
        log.info("reset system");
        try {
            circuitBoard.resetSystem();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setTimeDisable(boolean disable) {
        hourTextField.setDisable(disable);
        miniteTextField.setDisable(disable);
        secondTextField.setDisable(disable);
    }

    private int convertToSecond(int h, int m, int s) {
        return 60 * h + 60 * m + s;
    }

    private void setCellDisable(boolean b) {
        cellTextField.setDisable(b);
    }

}
