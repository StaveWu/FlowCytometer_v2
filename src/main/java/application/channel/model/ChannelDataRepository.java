package application.channel.model;

import application.pandas.DataFrame;
import application.pandas.DataSeries;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ChannelDataRepository {

    private String location;
    private List<ChannelData> channelDataList = new ArrayList<>();

    public void setLocation(String location) {
        this.location = location;
    }

    public List<ChannelData> findAll() {
        if (!channelDataList.isEmpty()) {
            return channelDataList;
        }
        try {
            DataFrame df = DataFrame.load(location);
            for (String header :
                    df.getHeaders()) {
                channelDataList.add(ChannelData.fromSeries(df.of(header)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return channelDataList;
    }

    public void saveAll() {
        List<DataSeries> seriesList = channelDataList.stream()
                .map(ChannelData::toSeries)
                .collect(Collectors.toList());
        DataFrame df = new DataFrame(seriesList);
        try {
            df.dump(location);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
