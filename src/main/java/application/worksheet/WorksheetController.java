package application.worksheet;

import application.channel.featurecapturing.ChannelMeta;
import application.chart.WrappedChart;
import application.chart.axis.LogarithmicAxis;
import application.chart.gate.GateLifeCycleListener;
import application.chart.gate.GatedHistogram;
import application.chart.gate.GatedScatterChart;
import application.event.CellFeatureCapturedEvent;
import application.event.ChannelChangedEvent;
import application.event.EventBusFactory;
import application.starter.FCMRunTimeConfig;
import application.utils.UiUtils;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.chart.Axis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;
import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class WorksheetController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(WorksheetController.class);
    private final EventBus eventBus = EventBusFactory.getEventBus();

    private Point2D chartInitLocation = new Point2D(-30, -30);
    private List<String> channelNames = new ArrayList<>();

    @FXML
    private LinkedChartsPane chartsPane;

    @Autowired
    private ChartRepository chartRepository;
    @Autowired
    private ChartChainRepository chartChainRepository;

    public WorksheetController() {
        eventBus.register(this);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        chartsPane.setAxisCandidateNames(channelNames);

        chartRepository.setLocation(FCMRunTimeConfig.getInstance()
                .getProjectConfigFolder() + File.separator + "charts.json");
        chartRepository.findAll().forEach(chart -> {
            chart.setAxisCandidateNames(channelNames);
            chartsPane.add(chart);
        });

        chartChainRepository.setLocation(FCMRunTimeConfig.getInstance()
                .getProjectConfigFolder() + File.separator + "chart_chains.json");
        chartsPane.setChartChains(chartChainRepository.findAll());

        chartsPane.addChartLifeCycleListener(new ChartLifeCycleListener() {
            @Override
            public void afterAdd() {
                log.info("afterAdd");
                saveWorksheetSnapshot();
            }

            @Override
            public void afterRemove() {
                log.info("afterRemove");
                saveWorksheetSnapshot();
            }

            @Override
            public void propertyChanged() {
                log.info("propertyChanged");
                saveWorksheetSnapshot();
            }
        });

        chartsPane.addGateLifeCycleListener(new GateLifeCycleListener() {
            @Override
            public void afterComplete() {
                log.info("Gate:afterComplete");
                saveWorksheetSnapshot();
            }

            @Override
            public void afterDestroy() {
                log.info("Gate:afterDestroy");
                saveWorksheetSnapshot();
            }
        });

        chartsPane.addChartConnectedListener(() -> {
            log.info("chart connected");
            saveWorksheetSnapshot();
        });
    }

    @Subscribe
    public void listen(CellFeatureCapturedEvent event) {
        log.info("cell feature received: " + event.getCellFeature());
        chartsPane.addCellFeature(new CellFeature(event.getCellFeature()));
    }

    @Subscribe
    public void listen(ChannelChangedEvent event) {
        log.info("channel changed");
        channelNames = event.getChannelMetas().stream()
                .map(ChannelMeta::getNameWithPolicy)
                .collect(Collectors.toList());
        if (chartsPane != null) {
            chartsPane.setAxisCandidateNames(channelNames);
        }
    }

    @FXML
    protected void createScatterChart() {
        GatedScatterChart scatterChart;
        Optional<AxisPair> res = getAxisChoiceDialog().showAndWait();
        if (res.isPresent()) {
            scatterChart = new GatedScatterChart(res.get().xAxis, res.get().yAxis);
            WrappedChart wrapper = new WrappedChart(scatterChart);
            nextChartLocation();
            wrapper.setLayoutX(chartInitLocation.getX());
            wrapper.setLayoutY(chartInitLocation.getY());
            wrapper.setAxisCandidateNames(channelNames);
            chartsPane.add(wrapper);
        }
    }

    @FXML
    protected void createHistogram() {
        GatedHistogram histogram;
        Optional<AxisPair> res = getAxisChoiceDialog().showAndWait();
        if (res.isPresent()) {
            histogram = new GatedHistogram(res.get().xAxis, res.get().yAxis);
            WrappedChart wrapper = new WrappedChart(histogram);
            nextChartLocation();
            wrapper.setLayoutX(chartInitLocation.getX());
            wrapper.setLayoutY(chartInitLocation.getY());
            wrapper.setAxisCandidateNames(channelNames);
            chartsPane.add(wrapper);
        }
    }

    @FXML
    protected void connect() {
        chartsPane.setState(LinkedChartsPane.State.ON_CONNECTING);
    }

    private class AxisPair {
        public final Axis<Number> xAxis;
        public final Axis<Number> yAxis;

        public AxisPair(String xAxisType, String yAxisType) {
            xAxis = xAxisType.equals("Linear") ? new NumberAxis() : new LogarithmicAxis();
            yAxis = yAxisType.equals("Linear") ? new NumberAxis() : new LogarithmicAxis();
        }
    }

    private Dialog<AxisPair> getAxisChoiceDialog() {
        Dialog<AxisPair> dialog = new Dialog<>();
        dialog.setTitle("图初始化");
        dialog.setHeaderText("请选择轴类型");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        ComboBox<String> xAxisCombo = new ComboBox<>();
        xAxisCombo.getItems().addAll("Linear", "Log");
        xAxisCombo.getSelectionModel().selectFirst();
        ComboBox<String> yAxisCombo = new ComboBox<>();
        yAxisCombo.getItems().addAll("Linear", "Log");
        yAxisCombo.getSelectionModel().selectFirst();

        grid.add(new Label("X轴："), 0, 0);
        grid.add(xAxisCombo, 1, 0);
        grid.add(new Label("Y轴："), 0, 1);
        grid.add(yAxisCombo, 1, 1);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return new AxisPair(xAxisCombo.getValue(), yAxisCombo.getValue());
            }
            return null;
        });
        return dialog;
    }

    public void saveWorksheetSnapshot() {
        try {
            chartRepository.saveAll(chartsPane.getCharts());
            chartChainRepository.saveAll(chartsPane.getChartChains());
        } catch (IOException e) {
            e.printStackTrace();
            UiUtils.getAlert(Alert.AlertType.ERROR, "保存数据失败",
                    e.getMessage()).showAndWait();
        }
    }

    private void nextChartLocation() {
        // Set chart's initial location misalignment, so different
        // chart can look more clearly
        Point2D old = chartInitLocation;
        if (old.getX() > 200) {
            chartInitLocation = new Point2D(10, 10);
        } else {
            chartInitLocation = new Point2D(old.getX() + 40, old.getY() + 40);
        }
    }
}
