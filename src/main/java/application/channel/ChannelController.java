package application.channel;

import application.event.EventBusFactory;
import application.event.SamplingPointCapturedEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import application.utils.Resource;
import application.utils.UiUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

@Component
public class ChannelController implements Initializable {

    private ChannelModel model;
    private final EventBus eventBus = EventBusFactory.getEventBus();

    @FXML
    private HBox channelsHBox;

    public ChannelController() {
        eventBus.register(this);
    }

    /**
     * using lazy load, to guarantee project root dir being set before
     * @return
     */
    private ChannelModel getModel() {
        if (model == null) {
            model = new ChannelModel();
        }
        return model;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        getModel().getChannelInfos().stream().forEach(info -> addChannelCell(info));
    }

    private void addChannelCell(ChannelInfo info) {
        channelsHBox.getChildren().add(new ChannelCell(this, info));
    }

    @FXML
    protected void saveChannelInfos() {
        try {
            getModel().saveInfos();
        } catch (Exception e) {
            UiUtils.getAlert(Alert.AlertType.ERROR, null,
                    "保存通道参数失败：" + e.getMessage()).showAndWait();
        }
    }

    @FXML
    protected void newChannelCell() {
        ChannelInfo info = new ChannelInfo();
        getModel().addChannelInfo(info);
        addChannelCell(info);
    }

    public void removeChannelCell(ChannelCell cell) {
        getModel().removeChannelInfo(cell.getChannelInfo());
        channelsHBox.getChildren().remove(cell);
    }

    @Subscribe
    protected void listen(SamplingPointCapturedEvent event) {
        List<List<Double>> channels = event.getSamplingPoint();

        Platform.runLater(() -> {
            for (int i = 0; i < channels.size(); i++) {
                // access channel's data
                List<Double> data = channels.get(i);

                // append data into existing series
                ChannelCell channelCell = (ChannelCell) channelsHBox.getChildren().get(i);
                XYChart<Number, Number> chart = channelCell.getChart();
                XYChart.Series<Number, Number> series = chart.getData().get(0);
                int start = series.getData().size();
                for (Double ele : data) {
                    series.getData().add(new XYChart.Data<>(start++, ele));
                }
            }
        });
    }

}
