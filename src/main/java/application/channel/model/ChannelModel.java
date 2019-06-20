package application.channel.model;

import java.util.ArrayList;
import java.util.List;

public class ChannelModel {
    private ChannelMeta meta;
    private List<Double> data = new ArrayList<>();

    private boolean isOnAscenting;
    private boolean isOnDescenting;

    public ChannelModel(ChannelMeta meta) {
        this.meta = meta;
    }

    public String getId() {
        return meta.getId();
    }

    public boolean isOnAscenting() {
        return isOnAscenting;
    }

    public boolean isOnDescenting() {
        return isOnDescenting;
    }

    public List<Double> getData() {
        return data;
    }

    public void setData(List<Double> data) {
        this.data = data;
    }

    public static ChannelModel of(ChannelMeta meta, ChannelSeries series) {
        ChannelModel model = new ChannelModel(meta);
        model.setData(series.getData());
        return model;
    }
}
