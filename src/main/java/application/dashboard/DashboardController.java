package application.dashboard;

import application.channel.featurecapturing.ChannelMeta;
import application.dashboard.device.CommunicationDevice;
import application.dashboard.device.SimulationCommDevice;
import application.dashboard.device.UsbCommDevice;
import application.event.*;
import application.starter.FCMRunTimeConfig;
import application.utils.UiUtils;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import javafx.concurrent.Service;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import javafx.util.converter.NumberStringConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@Controller
public class DashboardController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(DashboardController.class);
    private final EventBus eventBus = EventBusFactory.getEventBus();


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
    @FXML
    private ComboBox<SampleMode> modeCombo;

    /**
     * 进度监视
     */
    @FXML
    private Label timeRemainLabel;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private Label speedLabel;
    @FXML
    private Label totalEventLabel;

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

    /**
     * A service that used for ticking when dashboard on sampling.
     */
    private Service<Void> tickService;

    /**
     * A service to monitor event occurring speed.
     */
    private SpeedService speedService;

    /**
     * A service to count total events.
     */
    private EventCountService eventCountService;

    /**
     * A virtual board corresponding to the real board of flow cytometry.
     */
    private CircuitBoard circuitBoard = new CircuitBoard();

    private List<ChannelMeta> channelMetas = new ArrayList<>();

    /**
     * Do not replace this field to local variable, or persistence will not work.
     */
    private DashboardSetting dashboardSetting;

    public DashboardController() {
        eventBus.register(this);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // init comboBox
        modeCombo.getItems().add(SampleMode.TIME);
        modeCombo.getItems().add(SampleMode.CELL_NUMBER);
        connectionCombo.getItems().add(CommunicationDevice.SIMULATION);
        connectionCombo.getItems().add(CommunicationDevice.USB);

        // define ui constraint
        modeCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
            SampleMode mode = observable.getValue();
            if (mode != null) {
                if (mode == SampleMode.CELL_NUMBER) {
                    setTimeDisable(true);
                    setCellDisable(false);
                } else {
                    setCellDisable(true);
                    setTimeDisable(false);
                }
            }
        });

        // post to handlers when data receiving
        circuitBoard.setDataReceivedHandler(samplingPoints ->
                eventBus.post(new SamplingPointsCapturedEvent(samplingPoints)));

        // define communication device listener
        connectionCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
            CommunicationDevice device = observable.getValue();
            if (device != null) {
                if (device == CommunicationDevice.SIMULATION) {
                    circuitBoard.setCommDevice(new SimulationCommDevice());
                } else {
                    circuitBoard.setCommDevice(new UsbCommDevice());
                }
            }
        });

        // bind model
        dashboardSetting = new DashboardSetting(FCMRunTimeConfig.getInstance()
                .getProjectConfigFolder() + File.separator + "dashboard.json");
        connectionCombo.valueProperty().bindBidirectional(dashboardSetting.deviceProperty());
        frequencyTextField.textProperty().bindBidirectional(dashboardSetting.frequencyProperty(),
                new NumberStringConverter());
        modeCombo.valueProperty().bindBidirectional(dashboardSetting.sampleModeProperty());
        // NumberStringConverter converts 100000 to 100,000, which cause error when converting back,
        // so we use following custom converter instead.
        cellTextField.textProperty().bindBidirectional(dashboardSetting.cellNumberProperty(),
                new StringConverter<Number>() {
                    @Override
                    public String toString(Number object) {
                        return "" + object.longValue();
                    }

                    @Override
                    public Number fromString(String string) {
                        return Long.valueOf(string);
                    }
                });
        hourTextField.textProperty().bindBidirectional(dashboardSetting.hourProperty(),
                new NumberStringConverter());
        miniteTextField.textProperty().bindBidirectional(dashboardSetting.minuteProperty(),
                new NumberStringConverter());
        secondTextField.textProperty().bindBidirectional(dashboardSetting.secondProperty(),
                new NumberStringConverter());
        valveCheckBox1.selectedProperty().bindBidirectional(dashboardSetting.valve1Property());
        valveCheckBox2.selectedProperty().bindBidirectional(dashboardSetting.valve2Property());
        valveCheckBox3.selectedProperty().bindBidirectional(dashboardSetting.valve3Property());
        valveCheckBox4.selectedProperty().bindBidirectional(dashboardSetting.valve4Property());
        valveCheckBox5.selectedProperty().bindBidirectional(dashboardSetting.valve5Property());
        valveCheckBox6.selectedProperty().bindBidirectional(dashboardSetting.valve6Property());
        supValveTextField1.textProperty().bindBidirectional(dashboardSetting.supValve1Property(),
                new NumberStringConverter());
        supValveTextField2.textProperty().bindBidirectional(dashboardSetting.supValve2Property(),
                new NumberStringConverter());
    }

    @Subscribe
    public void listen(ChannelChangedEvent event) {
        log.info("channel meta changed");
        this.channelMetas = event.getChannelMetas();
    }

    @Subscribe
    public void listen(CellFeatureCapturedEvent event) {
        if (tickService != null && tickService instanceof CounterTickService) {
            ((CounterTickService) tickService).countDown();
        }
        if (speedService != null) {
            speedService.speedUp();
        }
        if (eventCountService != null) {
            eventCountService.count();
        }
    }

    @FXML
    protected void connectDevice() {
        if(!checkCommDeviceSelected()) {
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

    @FXML
    protected void startSampling() {
        if(!checkCommDeviceSelected() || !checkCommDeviceConnected()) {
            return;
        }
        tickService = newTickService();
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
        speedService = new SpeedService();
        speedLabel.textProperty().bind(speedService.messageProperty());
        eventCountService = new EventCountService();
        totalEventLabel.textProperty().bind(eventCountService.messageProperty());

        try {
            log.info("initialize circuit board");
            initializeBoard();
            eventBus.post(new StartSamplingEvent());
            log.info("start sampling");
            tickService.start();
            speedService.start();
            eventCountService.start();
            circuitBoard.startSampling(channelMetas.stream()
                    .map(ChannelMeta::getId)
                    .collect(Collectors.toList()));
        } catch (Exception e) {
            stopSampling();
            e.printStackTrace();
            UiUtils.getAlert(Alert.AlertType.ERROR, "命令发送失败",
                    e.getMessage()).showAndWait();
        }
    }

    @FXML
    protected void stopSampling() {
        if(!checkCommDeviceSelected() || !checkCommDeviceConnected()) {
            return;
        }
        if (tickService != null) {
            tickService.cancel();
        }
        if (speedService != null) {
            speedService.cancel();
        }
        if (eventCountService != null) {
            eventCountService.cancel();
        }
        eventBus.post(new StopSamplingEvent());
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
        if(!checkCommDeviceSelected() || !checkCommDeviceConnected()) {
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

    private void initializeBoard() throws Exception {
        // set channel
        for (ChannelMeta meta :
                channelMetas) {
            circuitBoard.setVoltage(meta.getId(), "" + meta.getVoltage());
        }
        // set sampling frequency
        circuitBoard.setFrequency(frequencyTextField.getText());
        // set valve
        circuitBoard.setValve("V1", valveCheckBox1.isSelected());
        circuitBoard.setValve("V2", valveCheckBox2.isSelected());
        circuitBoard.setValve("V3", valveCheckBox3.isSelected());
        circuitBoard.setValve("V4", valveCheckBox4.isSelected());
        circuitBoard.setValve("V5", valveCheckBox5.isSelected());
        circuitBoard.setValve("V6", valveCheckBox6.isSelected());
        circuitBoard.setVoltage("SV1", supValveTextField1.getText());
        circuitBoard.setVoltage("SV2", supValveTextField2.getText());
    }


    private Service<Void> newTickService() {
        if (modeCombo.getSelectionModel().getSelectedItem() == SampleMode.TIME) {
            return new TimeTickService(new TimeLimit(Integer.valueOf(hourTextField.getText()),
                    Integer.valueOf(miniteTextField.getText()),
                    Integer.valueOf(secondTextField.getText())).totalSeconds());
        } else {
            return new CounterTickService(Integer.valueOf(cellTextField.getText()));
        }
    }

    private void setTimeDisable(boolean disable) {
        hourTextField.setDisable(disable);
        miniteTextField.setDisable(disable);
        secondTextField.setDisable(disable);
    }

    private void setCellDisable(boolean b) {
        cellTextField.setDisable(b);
    }

    private boolean checkCommDeviceSelected() {
        if (connectionCombo.getSelectionModel().isEmpty()) {
            UiUtils.getAlert(Alert.AlertType.WARNING,
                    "设备连接失败", "请先选择端口类型！").showAndWait();
            return false;
        }
        return true;
    }

    private boolean checkCommDeviceConnected() {
        if (!circuitBoard.isConnected()) {
            UiUtils.getAlert(Alert.AlertType.WARNING, "设备连接失败",
                    "请先连接设备！").showAndWait();
            return false;
        }
        return true;
    }

}
