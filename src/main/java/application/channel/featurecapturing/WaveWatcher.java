package application.channel.featurecapturing;

import java.util.ArrayList;
import java.util.List;

public class WaveWatcher {

    private ChannelMeta meta;
    private List<Float> data = new ArrayList<>();
    private FeatureCalculationStrategy strategy;

    private boolean isOnGenerating;
    private List<WaveCapturedHandler> handlers = new ArrayList<>();

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

    public Float getWave() {
        return strategy.getFeature(data);
    }

    public void add(Float maybeWaveValue) {
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
             }
             data.add(maybeWaveValue);
        }
    }

    private void fireWaveCapturedEvent() {
        handlers.forEach(WaveCapturedHandler::waveCaptured);
    }

    public void registerWaveCapturedHandler(WaveCapturedHandler handler) {
        handlers.add(handler);
    }

    public void unRegisterWaveCapturedHandler(WaveCapturedHandler handler) {
        handlers.remove(handler);
    }

}
