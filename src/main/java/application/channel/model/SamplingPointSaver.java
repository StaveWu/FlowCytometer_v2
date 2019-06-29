package application.channel.model;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.Collectors;

public class SamplingPointSaver {

    private BlockingDeque<List<SamplingPoint>> pointsQueue = new LinkedBlockingDeque<>();
    private String location;

    public SamplingPointSaver() {
        Thread saveThread = new Thread(() -> {
            while (true) {
                try {
                    List<SamplingPoint> points = pointsQueue.take();
                    // save to file
                    Path pathname = Paths.get(location);
                    boolean exists = Files.exists(pathname);
                    try (BufferedWriter writer = Files.newBufferedWriter(pathname,
                            Charset.forName("utf-8"),
                            StandardOpenOption.CREATE,
                            StandardOpenOption.APPEND,
                            StandardOpenOption.WRITE)) {
                        if (!exists) {
                            // add header
                            writer.write(String.join("\t", points.get(0).getChannelIds()));
                        }
                        for (SamplingPoint point :
                                points) {
                            writer.newLine();
                            writer.write(String.join("\t", point.getCoords().stream()
                                    .map(String::valueOf)
                                    .collect(Collectors.toList())));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        saveThread.setDaemon(true);
        saveThread.start();
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void save(List<SamplingPoint> points) throws InterruptedException {
        pointsQueue.put(points);
    }
}
