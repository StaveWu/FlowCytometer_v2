package application.worksheet;

import application.channel.featurecapturing.ChannelMeta;
import application.chart.WrappedChart;
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
import javafx.geometry.Point2D;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Alert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
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
    private ChartRepository repository;

    public WorksheetController() {
        eventBus.register(this);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        chartsPane.setAxisCandidateNames(channelNames);

        repository.setLocation(FCMRunTimeConfig.getInstance()
                .getProjectConfigFolder() + File.separator + "charts.json");
        repository.findAll().forEach(chart -> {
            chart.setAxisCandidateNames(channelNames);
            chartsPane.add(chart);
        });

        chartsPane.addChartLifeCycleListener(new ChartLifeCycleListener() {
            @Override
            public void afterCreate() {
                log.info("afterCreate");
                saveCharts();
            }

            @Override
            public void afterRemove() {
                log.info("afterRemove");
                saveCharts();
            }

            @Override
            public void propertyChanged() {
                log.info("propertyChanged");
                saveCharts();
            }
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
                .map(ChannelMeta::getName)
                .collect(Collectors.toList());
        if (chartsPane != null) {
            chartsPane.setAxisCandidateNames(channelNames);
        }
    }

    @FXML
    protected void createScatterChart() {
        GatedScatterChart scatterChart = new GatedScatterChart(
                new NumberAxis(),
                new NumberAxis());
        WrappedChart wrapper = new WrappedChart(scatterChart);
        nextChartLocation();
        wrapper.setLayoutX(chartInitLocation.getX());
        wrapper.setLayoutY(chartInitLocation.getY());
        wrapper.setAxisCandidateNames(channelNames);
        chartsPane.add(wrapper);
    }

    public void saveCharts() {
        try {
            repository.saveAll(chartsPane.getCharts());
        } catch (IOException e) {
            e.printStackTrace();
            UiUtils.getAlert(Alert.AlertType.ERROR, "保存数据失败",
                    e.getMessage()).showAndWait();
        }
    }

    @FXML
    protected void createHistogram() {
        GatedHistogram histogram = new GatedHistogram(
                new NumberAxis(),
                new NumberAxis());
        WrappedChart wrapper = new WrappedChart(histogram);
        nextChartLocation();
        wrapper.setLayoutX(chartInitLocation.getX());
        wrapper.setLayoutY(chartInitLocation.getY());
        wrapper.setAxisCandidateNames(channelNames);
        chartsPane.add(wrapper);
    }

    @FXML
    protected void connect() {
        log.info("on connecting");
        chartsPane.setState(LinkedChartsPane.State.ON_CONNECTING);
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
