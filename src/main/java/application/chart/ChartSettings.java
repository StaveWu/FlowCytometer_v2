package application.chart;

import application.utils.Resource;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ChartSettings extends VBox implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(ChartSettings.class);

    @FXML
    private ComboBox<String> xNameCombo;
    @FXML
    private CheckBox xAutoRangeCheckBox;
    @FXML
    private TextField xMinTextField;
    @FXML
    private TextField xMaxTextField;

    @FXML
    private ComboBox<String> yNameCombo;
    @FXML
    private CheckBox yAutoRangeCheckBox;
    @FXML
    private TextField yMinTextField;
    @FXML
    private TextField yMaxTextField;

    private XYChart<Number, Number> chart;

    public ChartSettings(XYChart<Number, Number> chart) {
        this.chart = chart;
        FXMLLoader loader = new FXMLLoader(Resource.getFXML("chart_settings.fxml"));
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
        // define ui constraint
        xAutoRangeCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                xMaxTextField.setDisable(true);
                xMinTextField.setDisable(true);
            } else {
                xMaxTextField.setDisable(false);
                xMinTextField.setDisable(false);
            }
        });
        yAutoRangeCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                yMaxTextField.setDisable(true);
                yMinTextField.setDisable(true);
            } else {
                yMaxTextField.setDisable(false);
                yMinTextField.setDisable(false);
            }
        });

        NumberAxis xAxis = (NumberAxis) chart.getXAxis();
        List<String> xCandidateNames = (List<String>) xAxis.getUserData();
        xNameCombo.setItems(FXCollections.observableArrayList(xCandidateNames));
        xNameCombo.getSelectionModel().select(xAxis.getLabel());
        xAutoRangeCheckBox.setSelected(xAxis.isAutoRanging());
        xMinTextField.setText("" + xAxis.getLowerBound());
        xMaxTextField.setText("" + xAxis.getUpperBound());

        NumberAxis yAxis = (NumberAxis) chart.getYAxis();
        List<String> yCandidateNames = (List<String>) yAxis.getUserData();
        yNameCombo.setItems(FXCollections.observableArrayList(yCandidateNames));
        yNameCombo.getSelectionModel().select(yAxis.getLabel());
        yAutoRangeCheckBox.setSelected(yAxis.isAutoRanging());
        yMinTextField.setText("" + yAxis.getLowerBound());
        yMaxTextField.setText("" + yAxis.getUpperBound());
    }

    @FXML
    protected void confirm() {
        NumberAxis xAxis = (NumberAxis) chart.getXAxis();
        xAxis.setLabel(xNameCombo.getValue());
        xAxis.setAutoRanging(xAutoRangeCheckBox.isSelected());
        xAxis.setLowerBound(Double.valueOf(xMinTextField.getText()));
        xAxis.setUpperBound(Double.valueOf(xMaxTextField.getText()));
        xAxis.setTickUnit(getBestTickUnit(
                Double.valueOf(xMinTextField.getText()),
                Double.valueOf(xMaxTextField.getText())));

        NumberAxis yAxis = (NumberAxis) chart.getYAxis();
        yAxis.setLabel(yNameCombo.getValue());
        yAxis.setAutoRanging(yAutoRangeCheckBox.isSelected());
        yAxis.setLowerBound(Double.valueOf(yMinTextField.getText()));
        yAxis.setUpperBound(Double.valueOf(yMaxTextField.getText()));
        yAxis.setTickUnit(getBestTickUnit(
                Double.valueOf(yMinTextField.getText()),
                Double.valueOf(yMaxTextField.getText())));

        closeSelf();
    }

    @FXML
    protected void cancel() {
        closeSelf();
    }

    private void closeSelf() {
        Stage stage = (Stage) this.getScene().getWindow();
        stage.close();
    }

    private double getBestTickUnit(double lowerBound, double upperBound) {
        double range = upperBound - lowerBound;
        if (range < 10) {
            return 1;
        } else if (range < 100) {
            return 10;
        } else if (range < 1000) {
            return 100;
        } else {
            return 1000;
        }
    }

}
