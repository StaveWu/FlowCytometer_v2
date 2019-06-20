package application.channel.model;

import application.channel.RowDataAddedHandler;

import java.util.ArrayList;
import java.util.List;

public class SamplingDataCache {

    private List<ChannelSeries> seriesList;

    private static final int MAX_ALLOWABLE_CACHE = 10;
    private static final int MIN_TRIGGERED_SAVE_CACHE = 5;

    private List<RowDataAddedHandler> handlers = new ArrayList<>();

    public SamplingDataCache(List<ChannelSeries> seriesList) {
        this.seriesList = seriesList;
    }

    public int size() {
        return seriesList.size();
    }

    public boolean canSave() {
        if (size() < MIN_TRIGGERED_SAVE_CACHE) {
            return false;
        }
        if (size() > MAX_ALLOWABLE_CACHE) {
            return true;
        }
        boolean canSave = true;
        for (ChannelSeries series:
                seriesList) {
            if (series.isOnAscenting() || series.isOnDescenting()) {
                canSave = false;
            }
        }
        return canSave;
    }

    public void add(List<Double> rowData) {
        for (int i = 0; i < seriesList.size(); i++) {
            seriesList.get(i).add(rowData.get(i));
        }
        // fire event
        handlers.forEach(RowDataAddedHandler::rowDataAdded);


    }

    public void registerRowDataAddedHandler(RowDataAddedHandler handler) {
        handlers.add(handler);
    }

    public void clear() {
        seriesList.forEach(ChannelSeries::clear);
    }

    public List<ChannelSeries> getSeriesList() {
        return seriesList;
    }
}
