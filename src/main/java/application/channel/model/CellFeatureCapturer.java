package application.channel.model;

import application.event.CellFeatureCapturedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.Collectors;

public class CellFeatureCapturer implements WaveCapturedHandler {

    private List<WaveWatcher> waveWatchers;

    private List<CellFeatureCapturedHandler> handlers = new ArrayList<>();

    private BlockingDeque<SamplingPoint> pointQueue = new LinkedBlockingDeque<>();

    private static final Logger log = LoggerFactory.getLogger(CellFeatureCapturer.class);

    public CellFeatureCapturer(List<ChannelMeta> metas) {
        waveWatchers = metas.stream()
                .map(meta -> {
                    WaveWatcher waveWatcher = new WaveWatcher(meta);
                    waveWatcher.registerWaveCapturedHandler(this);
                    return waveWatcher;
                })
                .collect(Collectors.toList());
        Thread captureWaveThread = new Thread(() -> {
            while (true) {
                try {
                    SamplingPoint point = pointQueue.take();
                    for (int i = 0; i < point.getChannelIds().size(); i++) {
                        waveWatchers.get(i).add(point.getCoords().get(i));
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

    @Override
    public void waveCaptured() {
        List<Float> waves = waveWatchers.stream()
                .map(WaveWatcher::getWave)
                .collect(Collectors.toList());
        log.info("wave captured: " + waves);
        handlers.forEach(handler -> handler.cellFeatureCaptured(new CellFeatureCapturedEvent(waves)));
    }

    public void registerCellFeatureCapturedHandler(CellFeatureCapturedHandler handler) {
        handlers.add(handler);
    }

    public void unregisterCellFeatureCapturedHandler(CellFeatureCapturedHandler handler) {
        handlers.remove(handler);
    }
}
