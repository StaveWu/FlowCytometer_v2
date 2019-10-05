package application.channel.featurecapturing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class aims to defining how many bias can wave events
 * be judged as coming from the same cell.
 */
public class CellFeatureCaptureHelper {

    private int maxBiasForTheSameCell;
    private int currentBias;
    private List<Map<String, Float>> waveEvents = new ArrayList<>();

    private CellFeatureCapturer capturer;

    public CellFeatureCaptureHelper(CellFeatureCapturer capturer, int maxBiasForTheSameCell) {
        this.maxBiasForTheSameCell = maxBiasForTheSameCell;
        this.capturer = capturer;
    }

    public void addWaveEvent(Map<String, Float> waveEvent) {
        if (waveEvents.isEmpty()) {
            currentBias = 0;
        }
        waveEvents.add(waveEvent);
    }

    public void tick() {
        currentBias++;
        if (currentBias == maxBiasForTheSameCell + 1) {
            Map<String, Float> cellFeature = mergeWaveEvents();
            waveEvents.clear();
            if (cellFeature == null) {
                return;
            }
            capturer.postCellFeature(cellFeature);
        }
    }

    private Map<String, Float> mergeWaveEvents() {
        if (waveEvents.isEmpty()) {
            return null;
        }
        // merge by averaging
        Map<String, Float> res = new HashMap<>();
        for (String key :
                waveEvents.get(0).keySet()) {
            float sum = 0;
            for (Map<String, Float> waveEvent :
                    waveEvents) {
                sum += waveEvent.get(key);
            }
            // keep three decimal places
            float mean = sum / (float) waveEvents.size();
            res.put(key, Math.round(mean * 1000f) / 1000f);
        }
        return res;
    }
}
