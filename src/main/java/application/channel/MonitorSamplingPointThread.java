package application.channel;

import application.channel.sampling.SamplingPoint;
import application.channel.sampling.SamplingPointRepository;
import javafx.application.Platform;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.HBox;

import java.util.ArrayList;
import java.util.List;

public class MonitorSamplingPointThread extends Thread {

    private SamplingPointRepository samplingPointRepository;
    private HBox channelsHBox;
    private ChannelSetting channelSetting;

    public MonitorSamplingPointThread(HBox channelsHBox, ChannelSetting channelSetting,
                                      SamplingPointRepository repository) {
        this.channelsHBox = channelsHBox;
        this.channelSetting = channelSetting;
        this.samplingPointRepository = repository;
        this.setName("MonitorSamplingPointThread");
    }

    @Override
    public void run() {
        super.run();
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }

            // query all chart data list first
            List<List<XYChart.Data<Number, Number>>> dataLists = new ArrayList<>();
            for (int i = 0; i < channelsHBox.getChildren().size(); i++) {
                ChannelCell channelCell = (ChannelCell) channelsHBox.getChildren().get(i);
                dataLists.add(channelCell.getChart().getData().get(0).getData());
            }

            List<SamplingPoint> points = samplingPointRepository.getRecentPoints(channelSetting.getLookback());
            Platform.runLater(() -> {
                for (int i = 0; i < points.size(); i++) { // points size
                    for (int j = 0; j < points.get(i).size(); j++) { // channel number
                        List<XYChart.Data<Number, Number>> dataList = dataLists.get(j);
                        while (true) { // align data list size
                            if (dataList.size() > points.size()) {
                                dataList.remove(dataList.size() - 1);
                            }
                            if (dataList.size() < points.size()) {
                                dataList.add(new XYChart.Data<>(dataList.size(), null));
                            }
                            break;
                        }
                        dataList.get(i).setYValue(points.get(i).coordOf(j));
                    }
                }
            });
        }
    }
}
