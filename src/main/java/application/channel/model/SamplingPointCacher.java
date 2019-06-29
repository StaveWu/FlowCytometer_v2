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
        LinkedList<SamplingPoint> pointsCopy = new LinkedList<>(points);
        if (pointsCopy.size() >= maxAllowedCache) {
            for (int i = 0; i < pointsCopy.size() - maxAllowedCache; i++) {
                pointsCopy.removeFirst();
            }
            cachedPoints = pointsCopy;
        } else {
            for (int i = 0; i < maxAllowedCache - pointsCopy.size(); i++) {
                cachedPoints.removeFirst();
            }
            cachedPoints.addAll(pointsCopy);
        }
    }

    public List<SamplingPoint> getCachedPoints() {
        return cachedPoints;
    }
}
