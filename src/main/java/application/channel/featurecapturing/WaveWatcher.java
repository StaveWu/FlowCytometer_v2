package application.channel.featurecapturing;

import java.util.ArrayList;
import java.util.List;

public class WaveWatcher {

    private ChannelMeta meta;
    private List<Float> data = new ArrayList<>();

    private boolean isOnGenerating;
    private List<WaveCapturedHandler> handlers = new ArrayList<>();

    public WaveWatcher(ChannelMeta meta) {
        this.meta = meta;
    }

    public Float getWave() {
        Float sum = 0f;
        for (Float ele : data) {
            sum += ele;
        }
        return sum;
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
