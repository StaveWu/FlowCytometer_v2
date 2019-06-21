package application.channel.model;

import application.pandas.DataFrame;
import application.pandas.DataSeries;
import org.springframework.stereotype.Repository;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ChannelSeriesRepository {

    private String location;
    private List<ChannelSeries> channelSeriesList = new ArrayList<>();

    public void setLocation(String location) {
        this.location = location;
    }

    public void appendSeries(List<ChannelSeries> seriesList) throws IOException {
        DataFrame df = new DataFrame(seriesList.stream()
                .map(ChannelSeries::toSeries)
                .collect(Collectors.toList()));

        Path pathname = Paths.get(location);
        boolean exists = Files.exists(pathname);
        try (BufferedWriter writer = Files.newBufferedWriter(pathname,
                Charset.forName("utf-8"),
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND,
                StandardOpenOption.WRITE)) {
            if (!exists) {
                // add header
                writer.write(String.join("\t", df.getHeaders()));
            }
            for (List<Double> row :
                    df) {
                writer.newLine();
                writer.write(String.join("\t", row.stream()
                        .map(String::valueOf)
                        .collect(Collectors.toList())));
            }
        }
    }

    public List<ChannelSeries> headSeries() {
        return null;
    }

    public List<ChannelSeries> findAll() {
        if (!channelSeriesList.isEmpty()) {
            return channelSeriesList;
        }
        try {
            DataFrame df = DataFrame.load(location);
            for (String header :
                    df.getHeaders()) {
                channelSeriesList.add(ChannelSeries.fromSeries(df.of(header)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return channelSeriesList;
    }

    public void saveAll() {
        List<DataSeries> seriesList = channelSeriesList.stream()
                .map(ChannelSeries::toSeries)
                .collect(Collectors.toList());
        DataFrame df = new DataFrame(seriesList);
        try {
            df.dump(location);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
