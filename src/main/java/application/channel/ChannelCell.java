package application.channel;

import application.channel.featurecapturing.ChannelMeta;
import application.channel.sampling.SamplingPointRepository;
import application.chart.gate.CursorChart;
import application.chart.gate.GateCompletedListener;
import application.chart.gate.RectangleGate;
import application.utils.MathUtils;
import application.utils.Resource;
import application.utils.UiUtils;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.converter.BooleanStringConverter;
import javafx.util.converter.NumberStringConverter;
import org.springframework.lang.NonNull;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

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
    private CheckBox eventTriggerCheckBox;

    @FXML
    private TextField meanTextField;

    @FXML
    private AreaChart<Number, Number> channelChart;

    private ChannelMeta channelMeta;
    private ChannelController parentController;
    private SamplingPointRepository samplingPointRepository;
    private List<PropertyChangeHandler> handlers = new ArrayList<>();

    private final String[] channelIds = {"PMT1", "PMT2", "PMT3", "PMT4", "APD1", "APD2", "APD3", "APD4"};

    public ChannelCell(@NonNull ChannelController parentController,
                       @NonNull ChannelMeta channelMeta,
                       @NonNull SamplingPointRepository repository) {
        this.parentController = parentController;
        this.channelMeta = channelMeta;
        this.samplingPointRepository = repository;

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

        // init chart
        channelChart.getData().add(new XYChart.Series<>());
        channelChart.setAnimated(false);

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
        eventTriggerCheckBox.selectedProperty().bindBidirectional(channelMeta.eventTriggerProperty());
        eventTriggerCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            handlers.forEach(PropertyChangeHandler::propertyChanged);
        });
        meanTextField.textProperty().bindBidirectional(channelMeta.backgroundProperty(), new NumberStringConverter());
        meanTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            handlers.forEach(PropertyChangeHandler::propertyChanged);
        });
        channelIdCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.startsWith("PMT")) {
                voltageTextField.setDisable(false);
                channelChart.getYAxis().setLabel("Voltage");
            } else if (newValue.startsWith("APD")) {
                voltageTextField.setDisable(true);
                channelChart.getYAxis().setLabel("Count");
            } else {
                throw new RuntimeException("unknown channel id");
            }
            channelChart.getData().get(0).setName(newValue);
        });
        channelIdCombo.valueProperty().bindBidirectional(channelMeta.idProperty());
        // NOTE: Do not merge this listener to the listener above, or
        // the duplicate checking function in controller will not normally work.
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
    }

    private ToggleButton getSelectedToggle(String policy) {
        if (policy == null) { // switch...case... damn throw a null pointer exception ??
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

    @FXML
    protected void correctThreshold() {
        List<Float> data;
        try {
            data = samplingPointRepository.getDataByChannelId(channelMeta.getId());
        } catch (Exception e) {
            UiUtils.getAlert(Alert.AlertType.ERROR, "获取校正窗口错误",
                    e.getMessage()).showAndWait();
            return;
        }

        Stage stage = new Stage();
        stage.setTitle(this.nameTextField.getText() + "通道平均值和阈值校正");
        CursorChart chart = new CursorChart(new NumberAxis(), new NumberAxis());
        chart.setPrefWidth(800);

        Label meanLabel = new Label("平均值：");
        Label meanValueLabel = new Label("----");
        Label stdLabel = new Label("标准差：");
        Label stdValueLabel = new Label("----");
        Label thresholdLabel = new Label("阈值：");
        Label thresholdValueLabel = new Label("----");
        thresholdLabel.setTooltip(new Tooltip("阈值 = 平均值 + 3 x 标准差"));

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(5));
        gridPane.setHgap(5);
        gridPane.setVgap(5);
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setMinWidth(10);
        col1.setPrefWidth(50);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setMinWidth(10);
        col2.setPrefWidth(50);
        ColumnConstraints col3 = new ColumnConstraints();
        col3.setMinWidth(10);
        col3.setPrefWidth(50);
        ColumnConstraints col4 = new ColumnConstraints();
        col4.setMinWidth(10);
        col4.setPrefWidth(50);
        ColumnConstraints col5 = new ColumnConstraints();
        col4.setMinWidth(10);
        col4.setPrefWidth(50);
        ColumnConstraints col6 = new ColumnConstraints();
        col4.setMinWidth(10);
        col4.setPrefWidth(50);
        col1.setHgrow(Priority.SOMETIMES);
        col2.setHgrow(Priority.SOMETIMES);
        col3.setHgrow(Priority.SOMETIMES);
        col4.setHgrow(Priority.SOMETIMES);
        col5.setHgrow(Priority.SOMETIMES);
        col6.setHgrow(Priority.SOMETIMES);
        gridPane.getColumnConstraints().addAll(col1, col2, col3, col4, col5, col6);

        RowConstraints row1 = new RowConstraints();
        row1.setMinHeight(10);
        row1.setPrefHeight(30);
        row1.setVgrow(Priority.SOMETIMES);
        gridPane.getRowConstraints().add(row1);

        gridPane.add(meanLabel, 0, 0);
        gridPane.add(meanValueLabel, 1, 0);
        gridPane.add(stdLabel, 2, 0);
        gridPane.add(stdValueLabel, 3, 0);
        gridPane.add(thresholdLabel, 4, 0);
        gridPane.add(thresholdValueLabel, 5, 0);

        Button cursorBtn = new Button("游标");
        cursorBtn.setOnAction(event -> {
            RectangleGate<Number, Number> gate = new RectangleGate<>();
            gate.addCompletedListener(() -> {
                // calculate mean, std...
                List<Float> floatData = chart.getGatedData();
                double[] doubleData = new double[floatData.size()];
                for (int i = 0; i < doubleData.length; i++) {
                    doubleData[i] = floatData.get(i).doubleValue();
                }
                double mean = MathUtils.getMean(doubleData);
                double std = MathUtils.getStdDev(doubleData);
                double threshold = mean + 3 * std;
                meanValueLabel.setText(String.format("%.3f", mean));
                stdValueLabel.setText(String.format("%.3f", std));
                thresholdValueLabel.setText(String.format("%.3f", threshold));
            });
            chart.setGate(gate);
        });

        Button okBtn = new Button("确认");
        okBtn.setOnAction(event -> {
            String text = thresholdValueLabel.getText();
            if (text.equals("NaN") || text.equals("----")) {
                UiUtils.getAlert(Alert.AlertType.ERROR, "阈值校正错误",
                        String.format("阈值“%s”是无效值", text)).showAndWait();
            } else {
                channelMeta.setThreshold(Double.parseDouble(text));
                channelMeta.setBackground(Double.parseDouble(meanValueLabel.getText()));
                stage.close();
            }
        });
        Button cancelBtn = new Button("取消");
        cancelBtn.setOnAction(event -> {
            stage.close();
        });
        ButtonBar bar = new ButtonBar();
        bar.setPadding(new Insets(10));
        ButtonBar.setButtonData(okBtn, ButtonBar.ButtonData.RIGHT);
        ButtonBar.setButtonData(cancelBtn, ButtonBar.ButtonData.RIGHT);
        ButtonBar.setButtonData(cursorBtn, ButtonBar.ButtonData.LEFT);
        bar.getButtons().addAll(cursorBtn, okBtn, cancelBtn);

        VBox vbox1 = new VBox();
        vbox1.setMinHeight(Double.NEGATIVE_INFINITY);
        vbox1.setMaxHeight(Double.NEGATIVE_INFINITY);
        vbox1.setMinWidth(Double.NEGATIVE_INFINITY);
        vbox1.setMaxWidth(Double.NEGATIVE_INFINITY);
        vbox1.setAlignment(Pos.CENTER);
        vbox1.setDisable(true);

        vbox1.getChildren().add(chart);
        vbox1.getChildren().add(gridPane);
        vbox1.getChildren().add(bar);

        ProgressIndicator pb = new ProgressIndicator();
        VBox vbox2 = new VBox(pb);
        vbox2.setMinHeight(Double.NEGATIVE_INFINITY);
        vbox2.setMaxHeight(Double.NEGATIVE_INFINITY);
        vbox2.setMinWidth(Double.NEGATIVE_INFINITY);
        vbox2.setMaxWidth(Double.NEGATIVE_INFINITY);
        vbox2.setAlignment(Pos.CENTER);

        StackPane root = new StackPane();
        root.setMinHeight(Double.NEGATIVE_INFINITY);
        root.setMaxHeight(Double.NEGATIVE_INFINITY);
        root.setMinWidth(Double.NEGATIVE_INFINITY);
        root.setMaxWidth(Double.NEGATIVE_INFINITY);
        root.getChildren().add(vbox1);
        root.getChildren().add(vbox2);

        stage.setScene(new Scene(root));
        stage.show();

        // async display data
        CompletableFuture.supplyAsync(() -> {
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            // we only display 5000 points in order to saving time
            int numToDisplay = data.size() > 5000 ? 5000 : data.size();
            for (int i = 0; i < numToDisplay; i++) {
                series.getData().add(new XYChart.Data<>(i, data.get(i)));
            }
            return series;
        }).thenAccept(series -> {
            Platform.runLater(() -> {
                chart.setData(series);
                root.getChildren().remove(vbox2);
                vbox1.setDisable(false);
            });
        });
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
