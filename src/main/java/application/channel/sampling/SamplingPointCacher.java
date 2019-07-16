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
    private BlockingQueue<List<SamplingPoint>> queue = new LinkedBlockingQueue<>();

    public SamplingPointCacher(int maxAllowedCache) {
        this.maxAllowedCache = maxAllowedCache;

//        Thread cacheThread = new Thread(() -> {
//            while (true) {
//                try {
//                    List<SamplingPoint> points = queue.take();
//                    synchronized (this) {
//                        if (points.size() > this.maxAllowedCache) {
//                            cachedPoints.clear();
//                            cachedPoints.addAll(points.subList(points.size()
//                                    - this.maxAllowedCache, points.size()));
//                        } else {
//                            cachedPoints.addAll(points);
//                            if (cachedPoints.size() > this.maxAllowedCache) {
//                                for (int i = 0; i < cachedPoints.size() - this.maxAllowedCache; i++) {
//                                    cachedPoints.removeFirst();
//                                }
//                            }
//                        }
////                        System.out.println("cache points: " + cachedPoints.size());
//                    }
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//        cacheThread.setDaemon(true);
//        cacheThread.start();
    }

    public synchronized void cache(@NonNull List<SamplingPoint> points) {
        if (points.size() > this.maxAllowedCache) {
            cachedPoints.clear();
            cachedPoints.addAll(points.subList(points.size()
                    - this.maxAllowedCache, points.size()));
        } else {
            cachedPoints.addAll(points);
            if (cachedPoints.size() > this.maxAllowedCache) {
                for (int i = 0; i < cachedPoints.size() - this.maxAllowedCache; i++) {
                    cachedPoints.removeFirst();
                }
            }
        }
    }

    public synchronized List<SamplingPoint> getCachedPoints() {
        return new ArrayList<>(cachedPoints);
    }
}
