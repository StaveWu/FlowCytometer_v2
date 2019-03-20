package application.dashboard;

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
import org.springframework.stereotype.Component;

import javax.usb.event.UsbPipeDataEvent;
import javax.usb.event.UsbPipeErrorEvent;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

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
    private boolean isOnSampling = false;
    private int numChannels = 0;

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

        // define communication device creation and handler when data received
        connectionCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
            CommunicationDevice device = observable.getValue();
            if (device != null) {
                switch (device) {
                    case USB:
                        commDevice = new UsbCommDevice();
                        commDevice.setDataReceivedHandler(new CommDeviceEventAdapter() {
                            @Override
                            public void dataEventOccurred(UsbPipeDataEvent event) {
                                // decode data
                                log.info("data received");
                                byte[] data = event.getData();
                                List<List<Double>> decoded = CommDataParser.decode(data, 2);

                                System.out.println("接收长度：" + data.length);
                                for (int i = 0; i < 2; i++) {
                                    System.out.println("CH" + i + ": " + decoded.get(i));
                                }

                                // post data to channel component
                                eventBus.post(new SamplingPointCapturedEvent(decoded));
                                log.info("data posted");

                                // go to next read cycle if dashboard still on sampling state
                                if (isOnSampling) {
                                    try {
                                        commDevice.read();
                                    } catch (Exception e) {
                                        Platform.runLater(() -> {
                                            e.printStackTrace();
                                            UiUtils.getAlert(Alert.AlertType.ERROR, null,
                                                    "读取数据失败：" + e.getMessage()).showAndWait();
                                        });
                                    }
                                }
                            }

                            @Override
                            public void errorEventOccurred(UsbPipeErrorEvent event) {
                                Platform.runLater(() -> {
                                    event.getUsbException().printStackTrace();
                                    UiUtils.getAlert(Alert.AlertType.ERROR, null,
                                            "读取数据失败：" + event.getUsbException().getMessage()).showAndWait();
                                });
                            }
                        });
                        break;
                    case SERIAL:
                        break;
                    case DEVICE_CONFIG:
                        break;
                    default:
                        commDevice = new UsbCommDevice();
                }
            }
        });
    }

    @Subscribe
    public void listen(ChannelChangedEvent event) {
        numChannels = event.getNumChannels();
        log.info(numChannels + " channels open");
    }

    @FXML
    protected void connectDevice() {
        if(!checkCommDevice()) {
            return;
        }
        log.info("try connecting device " + commDevice.getClass().getName());
        try {
            commDevice.connect();
        } catch (Exception e) {
            UiUtils.getAlert(Alert.AlertType.ERROR,
                    null, "设备连接失败: " + e.getMessage()).showAndWait();
        }
        if (commDevice.isConnected()) {
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
        if (!commDevice.isConnected()) {
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
        isOnSampling = true;
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

        log.info("start sampling");
        tickService.start();

        try {
            commDevice.write(CommDataParser.encode("SetFrequency:50"));
            commDevice.write(CommDataParser.encode("Start:CH3"));
            commDevice.read();
        } catch (Exception e) {
            e.printStackTrace();
            stopSampling();
        }
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
        isOnSampling = false;
        if (tickService != null) {
            tickService.cancel();
        }
        log.info("stop sampling");
        try {
            commDevice.write(CommDataParser.encode("Stop:"));
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
            commDevice.write(CommDataParser.encode("ResetSystem:"));
            commDevice.read();
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
