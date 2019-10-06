package application.channel.featurecapturing;

import application.channel.sampling.SamplingPoint;
import application.event.CellFeatureCapturedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.Collectors;

public class CellFeatureCaptureThread extends Thread implements WaveCapturedHandler {

    private static final Logger log = LoggerFactory.getLogger(CellFeatureCaptureThread.class);

    private List<WaveWatcher> waveWatchers;
    private List<CellFeatureCapturedHandler> handlers = new ArrayList<>();
    private volatile BlockingDeque<SamplingPoint> pointQueue = new LinkedBlockingDeque<>();

    /**
     * help to capture cell feature according to max bias. This is not thread safe but
     * it's never mind for that it is only used on one thread (capture wave thread in below).
     */
    private CellFeatureCaptureHelper helper;

    public CellFeatureCaptureThread(List<ChannelMeta> metas, int maxBiasForTheSameCell) {
        setName("CellFeatureCaptureThread");
        // init wave watchers to watch wave appearing
        waveWatchers = metas.stream()
                .map(meta -> {
                    WaveWatcher waveWatcher = new WaveWatcher(meta);
                    // capturing wave on those channels selected,
                    // others will just be ignored whenever its wave event occurs
                    if (meta.getEventTrigger()) {
                        waveWatcher.registerWaveCapturedHandler(this);
                    }
                    return waveWatcher;
                })
                .collect(Collectors.toList());
        helper = new CellFeatureCaptureHelper(this, maxBiasForTheSameCell);
    }

    @Override
    public void run() {
        super.run();
        while (!isInterrupted()) {
            try {
                SamplingPoint point = pointQueue.take();
                // tick tick once a new point come.
                helper.tick();
                for (int i = 0; i < point.size(); i++) {
                    waveWatchers.get(i).add(point.coordOf(i));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void addSamplingPoint(SamplingPoint point) {
        try {
            pointQueue.put(point);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    void postCellFeature(Map<String, Float> cellFeature) {
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
        helper.addWaveEvent(waveEvent);
    }

    public void registerCellFeatureCapturedHandler(CellFeatureCapturedHandler handler) {
        handlers.add(handler);
    }

    public void unregisterCellFeatureCapturedHandler(CellFeatureCapturedHandler handler) {
        handlers.remove(handler);
    }
}
