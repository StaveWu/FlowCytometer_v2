package application.channel.model;

import application.pandas.DataSeries;

import java.util.ArrayList;
import java.util.List;

public class ChannelData {

    private DataSeries series;

    public ChannelData(String id) {
        this(id, new ArrayList<>());
    }

    public ChannelData(String id, List<Double> data) {
        this.series = new DataSeries();
        this.series.setName(id);
        this.series.setData(data);
    }

    public static ChannelData fromSeries(DataSeries series) {
        return new ChannelData(series.getName(), series.getData());
    }

    public DataSeries toSeries() {
        return series;
    }

    public String getId() {
        return series.getName();
    }

    public List<Double> getData() {
        return series.getData();
    }

    public void addAll(List<Double> elements) {
        series.addAll(elements);
    }
}
