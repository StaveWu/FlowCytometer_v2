package application.channel.model;

import application.pandas.DataSeries;

import java.util.ArrayList;
import java.util.List;

public class ChannelSeries {

    private String id;
    private List<Double> data;

    private boolean isOnAscenting;
    private boolean isOnDescenting;

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

    public boolean isOnAscenting() {
        return isOnAscenting;
    }

    public boolean isOnDescenting() {
        return isOnDescenting;
    }

    public void add(Double element) {
        data.add(element);
        // check ascenting or descenting
    }

    public void clear() {
        data.clear();
    }
}
