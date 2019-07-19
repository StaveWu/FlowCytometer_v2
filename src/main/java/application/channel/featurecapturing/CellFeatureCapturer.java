package application.channel.featurecapturing;

import application.channel.sampling.SamplingPoint;
import application.event.CellFeatureCapturedEvent;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.Collectors;

public class CellFeatureCapturer implements WaveCapturedHandler {

    private List<WaveWatcher> waveWatchers;
    private List<CellFeatureCapturedHandler> handlers = new ArrayList<>();
    private volatile BlockingDeque<SamplingPoint> pointQueue = new LinkedBlockingDeque<>();

    private volatile boolean stop = false;

    private int maxBiasForTheSameCell;
    private IntegerProperty currentBias = new SimpleIntegerProperty(0);

    /**
     * Cache wave event if a point has not consumed yet.
     */
    private List<Map<String, Float>> waveEventCache = new ArrayList<>();

    private static final Logger log = LoggerFactory.getLogger(CellFeatureCapturer.class);

    public CellFeatureCapturer(List<ChannelMeta> metas, int maxBiasForTheSameCell) {
        // init wave watchers to watch wave appearing
        waveWatchers = metas.stream()
                .map(meta -> {
                    WaveWatcher waveWatcher = new WaveWatcher(meta);
                    waveWatcher.registerWaveCapturedHandler(this);
                    return waveWatcher;
                })
                .collect(Collectors.toList());
        this.maxBiasForTheSameCell = maxBiasForTheSameCell;
        // hook handler
        currentBias.addListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() == this.maxBiasForTheSameCell + 1) {
                tryPostingCellFeature();
            }
        });

        // start a thread to handle cell feature calculating
        Thread captureWaveThread = new Thread(() -> {
            while (!stop) {
                try {
                    SamplingPoint point = pointQueue.take();
                    currentBias.set(currentBias.get() + 1); // may trigger handler
                    for (int i = 0; i < point.size(); i++) {
                        waveWatchers.get(i).add(point.coordOf(i));
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        captureWaveThread.setDaemon(true);
        captureWaveThread.start();
    }

    public void addSamplingPoint(SamplingPoint point) {
        try {
            pointQueue.put(point);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        stop = true;
    }

    private void tryPostingCellFeature() {
        if (waveEventCache.isEmpty()) {
            return;
        }
        // merge wave event cache into a cell feature by averaging.
        Map<String, Float> cellFeature = new HashMap<>();
        for (String key :
                waveEventCache.get(0).keySet()) {
            float sum = 0;
            for (Map<String, Float> waveEvent :
                    waveEventCache) {
                sum += waveEvent.get(key);
            }
            cellFeature.put(key, sum / (float) waveEventCache.size());
        }
        waveEventCache.clear();
        handlers.forEach(handler -> handler.cellFeatureCaptured(
                new CellFeatureCapturedEvent(cellFeature)));
    }

    @Override
    public void waveCaptured() {
        Map<String, Float> waveEvent = new HashMap<>();
        for (WaveWatcher watcher :
                waveWatchers) {
            waveEvent.put(watcher.getName(), watcher.getWave());
        }
        // This means the first wave event has been captured
        // when wave event occurs but wave event cache is empty,
        // so reset current bias and begin to count it from here.
        if (waveEventCache.isEmpty()) {
            currentBias.set(0);
        }
        waveEventCache.add(waveEvent);
    }

    public void registerCellFeatureCapturedHandler(CellFeatureCapturedHandler handler) {
        handlers.add(handler);
    }

    public void unregisterCellFeatureCapturedHandler(CellFeatureCapturedHandler handler) {
        handlers.remove(handler);
    }
}
