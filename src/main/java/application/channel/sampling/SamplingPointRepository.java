package application.channel.sampling;

import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
public class SamplingPointRepository {

    private SamplingPointSaver saver;
    private SamplingPointCacher cacher;

    private String location;

    public SamplingPointRepository() {
        saver = new SamplingPointSaver();
        cacher = new SamplingPointCacher(800);
    }

    public List<SamplingPoint> getRecentPoints(int lookback) {
        return cacher.getRecentPoints(lookback);
    }

    public void savePoints(List<SamplingPoint> points) {
        cacher.cache(points);
        try {
            saver.save(points);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public List<Float> getDataByChannelId(String channelId) throws Exception {
        if (location == null) {
            throw new RuntimeException("找不到数据文件");
        }
        final int colId = getColumnIndex(channelId);
        if (colId == -1) {
            throw new RuntimeException("Channel Id " + channelId + " not found");
        }
        return Files.lines(Paths.get(location)).skip(1)
                .map(line -> Float.parseFloat(line.split("\t")[colId]))
                .collect(Collectors.toList());
    }

    private int getColumnIndex(String channelId) throws Exception {
        int colIndex = -1;
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(location))) {
            String line = reader.readLine();
            String[] channelIds = line.split("\t");
            for (int i = 0; i < channelIds.length; i++) {
                if (channelIds[i].equals(channelId)) {
                    colIndex = i;
                }
            }
        }
        return colIndex;
    }

    public Stream<SamplingPoint> pointsStream() throws IOException {
        // get header
        List<String> channelIds = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(location))) {
            String line = reader.readLine();
            if (line == null) {
                throw new IOException("empty file");
            }
            channelIds.addAll(Arrays.asList(line.split("\t")));
        }
        // get sampling point stream
        return Files.lines(Paths.get(location)).skip(1).map(line -> {
            List<Float> coords = new ArrayList<>();
            for (String s :
                    line.split("\t")) {
                Float f = Float.valueOf(s);
                coords.add(f);
            }
            SamplingPoint point = new SamplingPoint(channelIds, coords);
            List<SamplingPoint> pointCache = new ArrayList<>();
            pointCache.add(point);
            cacher.cache(pointCache);
            return point;
        });
    }

    public void setLocation(String location) {
        this.location = location;
        saver.setLocation(location);
    }
}
