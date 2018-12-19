package application.channel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Component
public class ChannelModel {

    private List<ChannelInfo> channelInfos = new ArrayList<>();

    @Autowired
    private ChannelInfoRepository repository;

    public ChannelModel() {
//        channelInfos.add(new ChannelInfo(1, "FSC", 10, 12, "Area"));
//        channelInfos.add(new ChannelInfo(2, "SSC", 10, 12, "Height"));
//        channelInfos.add(new ChannelInfo(3, "FITC", 10, 12, "Width"));
        loadInfos();
    }

    public void saveInfos() {
        repository.deleteAll();
        repository.saveAll(channelInfos);
    }

    public void loadInfos() {
        channelInfos.clear();
        repository.findAll().forEach(info -> channelInfos.add(info));
    }

    public void addChannelInfo(ChannelInfo info) {
        channelInfos.add(info);
    }

    public void removeChannelInfo(ChannelInfo info) {
        channelInfos.remove(info);
    }

    public List<ChannelInfo> getChannelInfos() {
        return channelInfos;
    }
}
