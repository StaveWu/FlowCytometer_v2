package application.channel.model;

import application.pandas.DataSeries;

import java.util.ArrayList;
import java.util.List;

public class ChannelSeries {

    private String id;
    private List<Double> data;

    public ChannelSeries(String id) {
        this(id, new ArrayList<>());
    }

    public ChannelSeries(String id, List<Double> data) {
        this.id = id;
        this.data = data;
    }

    public static ChannelSeries fromSeries(DataSeries series) {
        return new ChannelSeries(series.getName(), series.getData());
    }

    public DataSeries toSeries() {
        return new DataSeries(id, data);
    }

    public String getId() {
        return id;
    }

    public List<Double> getData() {
        return data;
    }
}
