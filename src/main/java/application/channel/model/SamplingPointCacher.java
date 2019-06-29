package application.channel.model;

import org.springframework.lang.NonNull;

import java.util.LinkedList;
import java.util.List;

public class SamplingPointCacher {

    private int maxAllowedCache;
    private LinkedList<SamplingPoint> cachedPoints = new LinkedList<>();

    public SamplingPointCacher(int maxAllowedCache) {
        this.maxAllowedCache = maxAllowedCache;
    }

    public void cache(@NonNull List<SamplingPoint> points) {
        cachedPoints.addAll(points);
        // Remove elements that exceed the maximum cache limit
        if (cachedPoints.size() > maxAllowedCache) {
            for (int i = 0; i < cachedPoints.size() - maxAllowedCache; i++) {
                cachedPoints.removeFirst();
            }
        }
    }

    public List<SamplingPoint> getCachedPoints() {
        return cachedPoints;
    }
}
