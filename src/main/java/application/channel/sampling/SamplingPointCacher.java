package application.channel.sampling;

import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

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
                cachedPoints.subList(cachedPoints.size() - lookback, cachedPoints.size())
                : new ArrayList<>(cachedPoints);
    }
}
