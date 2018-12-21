package application.dashboard;

import javafx.concurrent.Service;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import utils.Resource;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@Component
public class Dashboard extends VBox implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(Dashboard.class);

    @FXML
    private ComboBox<SampleMode> modeCombo;

    @FXML
    private ComboBox<String> connectionCombo;

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

    @Autowired
    private ConfigurableApplicationContext context;

    public enum SampleMode {
        TIME("按时间"),
        CELLNUMBER("按细胞个数");

        private String modename;

        SampleMode(String name) {
            modename = name;
        }

        @Override
        public String toString() {
            return modename;
        }
    }

    public Dashboard() {
        FXMLLoader loader = new FXMLLoader(Resource.getFXML("dash_board.fxml"));
        loader.setRoot(this);
        loader.setController(context.getBean(Dashboard.class));

        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        modeCombo.getItems().add(SampleMode.TIME);
        modeCombo.getItems().add(SampleMode.CELLNUMBER);

        connectionCombo.getItems().add("串口");
        connectionCombo.getItems().add("USB");
        connectionCombo.getItems().add("通讯配置...");

        // define ui constraint
        modeCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
            SampleMode mode = observable.getValue();
            if (mode != null) {
                switch (mode) {
                    case TIME:
                        setCellDisable(true);
                        setTimeDisable(false);
                        return;
                    case CELLNUMBER:
                        setTimeDisable(true);
                        setCellDisable(false);
                        return;
                        default:
                            setCellDisable(true);
                            setTimeDisable(false);
                            return;
                }
            }
        });
    }

    @FXML
    protected void startSampling() {
        tickService = getTickService();
        progressIndicator.progressProperty().unbind();
        progressIndicator.progressProperty().bind(tickService.progressProperty());
        leftLabel.textProperty().bind(tickService.messageProperty());
        tickService.setOnSucceeded(event -> {
            stopSampling();
        });
        log.info("start sampling");
        tickService.start();
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
        tickService.cancel();
        progressIndicator.progressProperty().unbind();
        progressIndicator.setProgress(0);
        log.info("stop sampling");
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