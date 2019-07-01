package application.channel.sampling;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SamplingPointRepository {

    private SamplingPointSaver saver;
    private SamplingPointCacher cacher;

    public SamplingPointRepository() {
        saver = new SamplingPointSaver();
        cacher = new SamplingPointCacher(100);
    }

    public List<SamplingPoint> getRecentPoints() {
        return cacher.getCachedPoints();
    }

    public void savePoints(List<SamplingPoint> points) {
        cacher.cache(points);
        try {
            saver.save(points);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void setLocation(String location) {
        saver.setLocation(location);
    }
}
