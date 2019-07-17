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

public class CellFeatureCapturer implements WaveCapturedHandler {

    private List<WaveWatcher> waveWatchers;
    private List<CellFeatureCapturedHandler> handlers = new ArrayList<>();
    private BlockingDeque<SamplingPoint> pointQueue = new LinkedBlockingDeque<>();

    private volatile boolean stop = false;

    private static final Logger log = LoggerFactory.getLogger(CellFeatureCapturer.class);

    public CellFeatureCapturer(List<ChannelMeta> metas) {
        // init wave watchers to watch wave appearing
        waveWatchers = metas.stream()
                .map(meta -> {
                    WaveWatcher waveWatcher = new WaveWatcher(meta);
                    waveWatcher.registerWaveCapturedHandler(this);
                    return waveWatcher;
                })
                .collect(Collectors.toList());
        // start a thread to handle cell feature calculating
        Thread captureWaveThread = new Thread(() -> {
            while (!stop) {
                try {
                    SamplingPoint point = pointQueue.take();
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

    @Override
    public void waveCaptured() {
        Map<String, Float> cellFeature = new HashMap<>();
        for (WaveWatcher watcher :
                waveWatchers) {
            cellFeature.put(watcher.getName(), watcher.getWave());
        }
//        log.info("wave captured: " + cellFeature);
        handlers.forEach(handler -> handler.cellFeatureCaptured(new CellFeatureCapturedEvent(cellFeature)));
    }

    public void registerCellFeatureCapturedHandler(CellFeatureCapturedHandler handler) {
        handlers.add(handler);
    }

    public void unregisterCellFeatureCapturedHandler(CellFeatureCapturedHandler handler) {
        handlers.remove(handler);
    }
}
