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
        if (meta.getPeakPolicy().equals("Area")) {
            strategy = FeatureCalculation.AREA;
        } else if (meta.getPeakPolicy().equals("Height")) {
            strategy = FeatureCalculation.HEIGHT;
        } else if (meta.getPeakPolicy().equals("Width")) {
            strategy = FeatureCalculation.WIDTH;
        } else {
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
