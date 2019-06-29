package application.channel.model;

import javafx.scene.chart.XYChart;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SamplingPointSeriesTranslator {

    public List<XYChart.Series<Number, Number>> toSeries(List<SamplingPoint> points) {
        List<XYChart.Series<Number, Number>> res = new ArrayList<>();
        if (points.isEmpty()) {
            return res;
        }
        for (String channelId :
                points.get(0).getChannelIds()) {
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(channelId);
            res.add(series);
        }
        for (int i = 0; i < points.size(); i++) {
            for (int j = 0; j < points.get(i).getChannelIds().size(); j++) {
                res.get(j).getData().add(new XYChart.Data<>(i, points.get(i).getCoords().get(j)));
            }
        }
        return res;
    }
}
