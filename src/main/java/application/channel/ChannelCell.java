package application.channel;

import application.channel.model.ChannelModel;
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

    private ChannelModel channelModel;
    private ChannelController parentController;

    private final String[] channelIds = {"PMT1", "PMT2", "PMT3", "PMT4", "APD1", "APD2", "APD3", "APD4"};

    public ChannelCell(@NonNull ChannelController parentController, @NonNull ChannelModel model) {
        this.parentController = parentController;
        this.channelModel = model;

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
        peakgroup.selectToggle(getSelectedToggle(channelModel.getPeakPolicy()));

        // bind model
        nameTextField.textProperty().bindBidirectional(channelModel.nameProperty());
        voltageTextField.textProperty().bindBidirectional(channelModel.voltageProperty(), new NumberStringConverter());
        thresholdTextField.textProperty().bindBidirectional(channelModel.thresholdProperty(), new NumberStringConverter());
        channelIdCombo.valueProperty().bindBidirectional(channelModel.idProperty());

        peakgroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            ToggleButton selectedBtn = (ToggleButton) observable.getValue();
            channelModel.setPeakPolicy(selectedBtn.getText());
        });
        channelModel.peakPolicyProperty().addListener((observable, oldValue, newValue) -> {
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
            case "Area":
                return areaToggle;
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

    public ChannelModel getChannelModel() {
        return channelModel;
    }

    public XYChart<Number, Number> getChart() {
        return channelChart;
    }
}
