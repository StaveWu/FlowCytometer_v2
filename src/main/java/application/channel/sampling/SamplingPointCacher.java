package application.channel.sampling;

import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SamplingPointCacher {

    private int maxAllowedCache;
    private final LinkedList<SamplingPoint> cachedPoints = new LinkedList<>();

    public SamplingPointCacher(int maxAllowedCache) {
        this.maxAllowedCache = maxAllowedCache;
    }

    public synchronized void cache(@NonNull List<SamplingPoint> points) {
        if (points.size() > this.maxAllowedCache) {
            cachedPoints.clear();
            cachedPoints.addAll(points.subList(
                    points.size() - this.maxAllowedCache,
                    points.size()));
        } else {
            cachedPoints.addAll(points);
            while (cachedPoints.size() > this.maxAllowedCache) {
                cachedPoints.removeFirst();
            }
        }
    }

    public synchronized List<SamplingPoint> getRecentPoints(int lookback) {
        return cachedPoints.size() > lookback ?
                new ArrayList<>(cachedPoints.subList(cachedPoints.size() - lookback, cachedPoints.size()))
                : new ArrayList<>(cachedPoints);
    }
}
