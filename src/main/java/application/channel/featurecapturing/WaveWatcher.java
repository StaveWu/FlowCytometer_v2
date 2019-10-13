package application.channel.featurecapturing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WaveWatcher {

    private ChannelMeta meta;
    private List<Float> data = new ArrayList<>();
    private FeatureCalculationStrategy strategy;

    private Map<String, RecordData> recordMap = new HashMap<>();

    private boolean isOnGenerating;
    private List<WaveCapturedHandler> waveCapturedHandlers = new ArrayList<>();
    private List<WaveRaisedHandler> waveRaisedHandlers = new ArrayList<>();

    public WaveWatcher(ChannelMeta meta) {
        this.meta = meta;
        switch (meta.getPeakPolicy()) {
            case "Area":
                strategy = FeatureCalculation.AREA;
                break;
            case "Height":
                strategy = FeatureCalculation.HEIGHT;
                break;
            case "Width":
                strategy = FeatureCalculation.WIDTH;
                break;
            default:
                throw new RuntimeException("can not reach here");
        }
    }

    public String getId() {
        return meta.getId();
    }

    public String getName() {
        return meta.getNameWithPolicy();
    }

    public Float getWave() {
        return strategy.getFeature(data);
    }

    public Float getRecord(String channelId) {
        return strategy.getFeature(recordMap.get(channelId) == null ?
                new ArrayList<>() : recordMap.get(channelId).getData());
    }

    public void startRecording(String channelId) {
        RecordData recordData = recordMap.get(channelId);
        if (recordData == null) {
            recordData = new RecordData();
            recordMap.put(channelId, recordData);
        }
        recordData.startRecording();
    }

    public void stopRecording(String channelId) {
        recordMap.get(channelId).stopRecording();
    }

    public void add(Float maybeWaveValue) {
        // subtract background before considering wave
        final float valueWithoutBackground = maybeWaveValue < meta.getBackground() ?
                0 : maybeWaveValue - (float)meta.getBackground();

        recordMap.forEach((channelId, recordData) ->
                recordData.add(valueWithoutBackground));

        if (maybeWaveValue < meta.getThreshold()) {
            if (isOnGenerating) {
                isOnGenerating = false;
                fireWaveCapturedEvent();
                data = new ArrayList<>();
            }
        }
        else {
             if (!isOnGenerating) {
                 isOnGenerating = true;
                 fireWaveRaisedEvent();
             }
             data.add(valueWithoutBackground);
        }
    }

    private void fireWaveRaisedEvent() {
        waveRaisedHandlers.forEach(handler -> handler.waveRaised(new WaveRaiseEvent(meta.getId())));
    }

    private void fireWaveCapturedEvent() {
        waveCapturedHandlers.forEach(handler -> handler.waveCaptured(new WaveCapturedEvent(meta.getId())));
    }

    public void registerWaveCapturedHandler(WaveCapturedHandler handler) {
        waveCapturedHandlers.add(handler);
    }

    public void unRegisterWaveCapturedHandler(WaveCapturedHandler handler) {
        waveCapturedHandlers.remove(handler);
    }


    public void registerWaveRaisedHandler(WaveRaisedHandler handler) {
        waveRaisedHandlers.add(handler);
    }

    public void unregisterWaveRaisedHandler(WaveRaisedHandler handler) {
        waveRaisedHandlers.remove(handler);
    }
}
