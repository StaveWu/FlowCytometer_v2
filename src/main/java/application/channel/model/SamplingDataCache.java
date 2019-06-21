package application.channel.model;

import application.channel.BeforeCacheClearedHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SamplingDataCache {

    private List<ChannelModel> models;

    private static final int MAX_ALLOWABLE_CACHE = 10;
    private static final int MIN_TRIGGERED_SAVE_CACHE = 5;

    private List<BeforeCacheClearedHandler> handlers = new ArrayList<>();

    public SamplingDataCache(List<ChannelModel> models) {
        this.models = models;
    }

    public int size() {
        return models.get(0).getData().size();
    }

    public boolean canSave() {
        if (size() < MIN_TRIGGERED_SAVE_CACHE) {
            return false;
        }
        if (size() > MAX_ALLOWABLE_CACHE) {
            return true;
        }
        boolean canSave = true;
        for (ChannelModel model:
                models) {
            if (model.isOnAscenting() || model.isOnDescenting()) {
                canSave = false;
            }
        }
        return canSave;
    }

    public void add(List<Double> rowData) {
        for (int i = 0; i < models.size(); i++) {
            models.get(i).getData().add(rowData.get(i));
        }
        // fire event
        if (canSave()) {
            handlers.forEach(BeforeCacheClearedHandler::beforeClear);
            clear();
        }

    }

    public void registerBeforeClearHandler(BeforeCacheClearedHandler handler) {
        handlers.add(handler);
    }

    public void clear() {
        models.forEach(e -> e.getData().clear());
    }

    public List<ChannelSeries> getSeriesList() {
        return models.stream()
                .map(e -> new ChannelSeries(e.getId(), new ArrayList<>(e.getData())))
                .collect(Collectors.toList());
    }

    public static SamplingDataCache of(List<ChannelMeta> metas) {
        return new SamplingDataCache(metas.stream()
                .map(ChannelModel::new)
                .collect(Collectors.toList()));
    }
}
