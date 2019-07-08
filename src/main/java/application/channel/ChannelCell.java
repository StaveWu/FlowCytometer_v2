package application.channel;

import application.channel.featurecapturing.ChannelMeta;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.util.converter.NumberStringConverter;
import org.springframework.lang.NonNull;
import application.utils.Resource;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ChannelCell extends VBox implements Initializable {

    @FXML
    private TextField voltageTextField;

    @FXML
    private TextField thresholdTextField;

    @FXML
    private TextField nameTextField;

    @FXML
    private ComboBox<String> channelIdCombo;

    @FXML
    private ToggleButton areaToggle;

    @FXML
    private ToggleButton heightToggle;

    @FXML
    private ToggleButton widthToggle;

    @FXML
    private ToggleGroup peakgroup;

    @FXML
    private AreaChart<Number, Number> channelChart;

    private ChannelMeta channelMeta;
    private ChannelController parentController;
    private List<PropertyChangeHandler> handlers = new ArrayList<>();

    private final String[] channelIds = {"PMT1", "PMT2", "PMT3", "PMT4", "APD1", "APD2", "APD3", "APD4"};

    public ChannelCell(@NonNull ChannelController parentController, @NonNull ChannelMeta channelMeta) {
        this.parentController = parentController;
        this.channelMeta = channelMeta;

        FXMLLoader loader = new FXMLLoader(Resource.getFXML("channel_cell.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // init comboBox
        for (String id : channelIds) {
            channelIdCombo.getItems().add(id);
        }
        // set toggle when first load
        peakgroup.selectToggle(getSelectedToggle(channelMeta.getPeakPolicy()));

        // bind meta and hook property change handler
        nameTextField.textProperty().bindBidirectional(channelMeta.nameProperty());
        nameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            handlers.forEach(PropertyChangeHandler::propertyChanged);
        });
        voltageTextField.textProperty().bindBidirectional(channelMeta.voltageProperty(), new NumberStringConverter());
        voltageTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            handlers.forEach(PropertyChangeHandler::propertyChanged);
        });
        thresholdTextField.textProperty().bindBidirectional(channelMeta.thresholdProperty(), new NumberStringConverter());
        thresholdTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            handlers.forEach(PropertyChangeHandler::propertyChanged);
        });
        channelIdCombo.valueProperty().bindBidirectional(channelMeta.idProperty());
        channelIdCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
            handlers.forEach(PropertyChangeHandler::propertyChanged);
        });

        peakgroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            ToggleButton selectedBtn = (ToggleButton) observable.getValue();
            channelMeta.setPeakPolicy(selectedBtn.getText());
            handlers.forEach(PropertyChangeHandler::propertyChanged);
        });
        channelMeta.peakPolicyProperty().addListener((observable, oldValue, newValue) -> {
            String policy = observable.getValue();
            peakgroup.selectToggle(getSelectedToggle(policy));
        });

        // init chart
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Data flow");
        channelChart.getData().add(series);
        channelChart.setAnimated(false);
    }

    private ToggleButton getSelectedToggle(String policy) {
        if (policy == null) { // switch...case... damn throw a null pointer ??
            return areaToggle;
        }
        switch (policy) {
            case "Height":
                return heightToggle;
            case "Width":
                return widthToggle;
            default:
                return areaToggle;
        }
    }

    @FXML
    protected void closeChannel() {
        parentController.removeChannelCell(this);
    }

    public ChannelMeta getChannelMeta() {
        return channelMeta;
    }

    public XYChart<Number, Number> getChart() {
        return channelChart;
    }

    public void addPropertyChangeHandler(PropertyChangeHandler handler) {
        handlers.add(handler);
    }
}
