package application.channel.model;

import application.pandas.DataFrame;
import application.pandas.DataSeries;
import org.springframework.stereotype.Repository;

import java.io.IOException;
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

    public void appendSeries(List<ChannelSeries> seriesList) {

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
