package application.dashboard;

import application.channel.model.ChannelData;
import application.channel.model.ChannelDataRepository;
import application.channel.model.ChannelModel;
import application.channel.model.ChannelModelRepository;
import application.dashboard.device.UsbCommDevice;
import application.event.ChannelChangedEvent;
import application.event.EventBusFactory;
import application.utils.UiUtils;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import javafx.concurrent.Service;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    /**
     * 采样设置
     */
    @FXML
    private TextField frequencyTextField;
    @FXML
    private TextField hourTextField;
    @FXML
    private TextField miniteTextField;
    @FXML
    private TextField secondTextField;
    @FXML
    private TextField cellTextField;

    /**
     * 进度监视
     */
    @FXML
    private Label timeRemainLabel;
    @FXML
    private ProgressIndicator progressIndicator;

    /**
     * 液流系统
     */
    @FXML
    private CheckBox valveCheckBox1;
    @FXML
    private CheckBox valveCheckBox2;
    @FXML
    private CheckBox valveCheckBox3;
    @FXML
    private CheckBox valveCheckBox4;
    @FXML
    private CheckBox valveCheckBox5;
    @FXML
    private CheckBox valveCheckBox6;
    @FXML
    private TextField supValveTextField1;
    @FXML
    private TextField supValveTextField2;

    private Service<Void> tickService;

    private CircuitBoard circuitBoard = new CircuitBoard();

    @Autowired
    private ChannelModelRepository channelModelRepository;
    @Autowired
    private ChannelDataRepository channelDataRepository;

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
        USB("USB");

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

        // perform action when data received
        circuitBoard.setDataReceivedHandler(receivedList -> {
            List<ChannelData> channelDataList = channelDataRepository.findAll();
            for (int i = 0; i < receivedList.size(); i++) {
                channelDataList.get(i).addAll(receivedList.get(i));
            }
        });

        // choose communication device
        connectionCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
            CommunicationDevice device = observable.getValue();
            if (device != null) {
                switch (device) {
                    case USB:
                        circuitBoard.setCommDevice(new UsbCommDevice());
                        break;
                    case SERIAL:
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
                    "设备连接失败", e.getMessage()).showAndWait();
        }
        if (circuitBoard.isConnected()) {
            log.info("device is connected");
        }
    }

    private boolean checkCommDevice() {
        if (connectionCombo.getSelectionModel().isEmpty()) {
            UiUtils.getAlert(Alert.AlertType.WARNING,
                    "设备连接失败", "请先选择端口类型！").showAndWait();
            return false;
        }
        return true;
    }

    private boolean checkCommConnected() {
        if (!circuitBoard.isConnected()) {
            UiUtils.getAlert(Alert.AlertType.WARNING, "设备连接失败", "请先连接设备！").showAndWait();
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
        timeRemainLabel.textProperty().bind(tickService.messageProperty());
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
            circuitBoard.startSampling(channelModelRepository.findAll().stream()
                    .map(ChannelModel::getId)
                    .collect(Collectors.toList()));
        } catch (Exception e) {
            stopSampling();
            e.printStackTrace();
            UiUtils.getAlert(Alert.AlertType.ERROR, "命令发送失败",
                    e.getMessage()).showAndWait();
        }
    }

    private void initializeBoard() throws Exception {
        // set voltage
        for (ChannelModel model :
                channelModelRepository.findAll()) {
            circuitBoard.setVoltage(model.getId(), model.getVoltage());
        }
        // set sampling frequency
        circuitBoard.setFrequency(Long.parseLong(frequencyTextField.getText()));
        // set valve
        circuitBoard.setValve("V1", valveCheckBox1.isSelected());
        circuitBoard.setValve("V2", valveCheckBox2.isSelected());
        circuitBoard.setValve("V3", valveCheckBox3.isSelected());
        circuitBoard.setValve("V4", valveCheckBox4.isSelected());
        circuitBoard.setValve("V5", valveCheckBox5.isSelected());
        circuitBoard.setValve("V6", valveCheckBox6.isSelected());
        circuitBoard.setSupValve("SV1", Float.parseFloat(supValveTextField1.getText()));
        circuitBoard.setSupValve("SV2", Float.parseFloat(supValveTextField2.getText()));
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
            UiUtils.getAlert(Alert.AlertType.ERROR, "命令发送失败",
                    e.getMessage()).showAndWait();
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
            UiUtils.getAlert(Alert.AlertType.ERROR, "命令发送失败",
                    e.getMessage()).showAndWait();
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
