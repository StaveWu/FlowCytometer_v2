package application.channel;

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
    private ComboBox<Number> channelIdCombo;

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

    private ChannelInfo channelInfo;
    private ChannelController parentController;

    public ChannelCell(@NonNull ChannelController parentController, @NonNull ChannelInfo info) {
        this.parentController = parentController;
        this.channelInfo = info;

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
        channelIdCombo.getItems().add(1);
        channelIdCombo.getItems().add(2);
        channelIdCombo.getItems().add(3);
        // set toggle when first load
        peakgroup.selectToggle(getSelectedToggle(channelInfo.getPeakPolicy()));

        // bind model
        nameTextField.textProperty().bindBidirectional(channelInfo.channelNameProperty());
        voltageTextField.textProperty().bindBidirectional(channelInfo.voltageProperty(), new NumberStringConverter());
        thresholdTextField.textProperty().bindBidirectional(channelInfo.thresholdProperty(), new NumberStringConverter());
        channelIdCombo.valueProperty().bindBidirectional(channelInfo.channelIdProperty());

        peakgroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            ToggleButton selectedbtn = (ToggleButton) observable.getValue();
            channelInfo.setPeakPolicy(selectedbtn.getText());
        });
        channelInfo.peakPolicyProperty().addListener((observable, oldValue, newValue) -> {
            String policy = observable.getValue();
            peakgroup.selectToggle(getSelectedToggle(policy));
        });

        // init chart
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Data flow");
        channelChart.getData().add(series);
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

    public ChannelInfo getChannelInfo() {
        return channelInfo;
    }

    public XYChart<Number, Number> getChart() {
        return channelChart;
    }
}
